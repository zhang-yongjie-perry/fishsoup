package com.fishsoup.fishweb.service.impl;

import com.fishsoup.entity.creation.Creation;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.enums.CreationClassifyEnum;
import com.fishsoup.fishweb.annotation.FootstepLog;
import com.fishsoup.fishweb.enums.ArtworkTypeEnum;
import com.fishsoup.fishweb.service.CreationService;
import com.fishsoup.fishweb.util.UserUtils;
import com.fishsoup.util.CollectionUtils;
import com.fishsoup.util.DateUtils;
import com.fishsoup.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RKeys;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.KeysOptions;
import org.redisson.api.options.KeysScanOptions;
import org.redisson.api.options.OptionalOptions;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

@Slf4j
@Service
public class CreationServiceImpl implements CreationService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    @Qualifier("commonExecutorService")
    private ExecutorService commonExecutorService;

    @Override
    public String saveCreation(Creation creation) throws BusinessException {
        if (!StringUtils.hasText(creation.getContent())) {
            throw new BusinessException("请输入正文内容");
        }
        if (!StringUtils.hasText(creation.getTitle())) {
            throw new BusinessException("请输入标题");
        }
        if (!StringUtils.hasText(creation.getSummary())) {
            throw new BusinessException("请输入概述内容");
        }
        creation.setUpdateBy(UserUtils.getLoginName());
        creation.setUpdateTime(DateUtils.now());
        String creationId;
        if (StringUtils.hasText(creation.getId())) {
            Query query = new Query(Criteria.where("_id").is(creation.getId()));
            Update update = new Update();
            if (StringUtils.hasText(creation.getTitle())) {
                update.set("title", creation.getTitle());
            }
            if (StringUtils.hasText(creation.getAuthor())) {
                update.set("author", creation.getAuthor());
            }
            if (Objects.nonNull(creation.getClassify())) {
                update.set("classify", creation.getClassify());
            }
            if (Objects.nonNull(creation.getVisibleRange())) {
                update.set("visibleRange", creation.getVisibleRange());
            }
            if (StringUtils.hasText(creation.getSummary())) {
                update.set("summary", creation.getSummary());
            }
            if (StringUtils.hasText(creation.getContent())) {
                update.set("content", creation.getContent());
            }
            if (Objects.nonNull(creation.getCreateTime())) {
                update.set("time", creation.getCreateTime());
            }
            if (CollectionUtils.isEmpty(creation.getTags())) {
                update.set("tags", new ArrayList<String>());
            } else {
                update.set("tags", creation.getTags());
            }
            update.set("updateBy", creation.getUpdateBy());
            update.set("updateTime", creation.getUpdateTime());
            mongoTemplate.updateFirst(query, update, Creation.class);
            creationId = creation.getId();
        } else {
            creation.setCreateBy(creation.getUpdateBy());
            creation.setCreateTime(creation.getUpdateTime());
            creation.setTime(creation.getCreateTime());
            creationId = mongoTemplate.insert(creation).getId();
        }
        commonExecutorService.submit(() -> rabbitTemplate.convertAndSend("amq.topic", "cache.creation", creationId));
        return creationId;
    }

    /**
     * 注意此处使用 #id 会报错, 所以使用 #p0 获取第一个参数
     * @param id 文章id
     * @return 文章
     */
    @Override
    @FootstepLog(ArtworkTypeEnum.CREATION)
    @Cacheable(value = "creation", key = "#p0")
    public Creation getCreationById(String id) {
        RLock fairLock = redissonClient.getFairLock("getCreationById:" + id);
        try {
            boolean success = fairLock.tryLock();
            if (!success) {
                throw new BusinessException("当前请求人数过多, 请稍后再试");
            }
            Query query = new Query(Criteria.where("_id").is(id));
            return mongoTemplate.findOne(query, Creation.class);
        } finally {
            if (fairLock.isLocked()) {
                fairLock.unlock();
            }
        }
    }

    @Override
    @Cacheable(value = "creations", key = "#p0.classify + ':' + #p0 + #p1 + #p2")
    public List<Creation> listCreations(Creation conditions, int pageNum, int pageSize) {
        RLock fairLock = redissonClient.getFairLock("listCreations:" + conditions + ":" + pageNum + ":" + pageSize);
        try {
            boolean success = fairLock.tryLock();
            if (!success) {
                throw new BusinessException("当前请求人数过多, 请稍后再试");
            }
            // 创建分页请求
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
            Query query = new Query().with(pageable);
            Criteria criteria = new Criteria();
            if (Objects.nonNull(conditions) && conditions.getVisibleRange() != null) {
                criteria.and("visibleRange").is(conditions.getVisibleRange());
            }
            if (Objects.nonNull(conditions) && conditions.getClassify() != null) {
                criteria.and("classify").is(conditions.getClassify());
            }
            if (Objects.nonNull(conditions) && StringUtils.hasText(conditions.getAuthor())) {
                criteria.and("author").is(conditions.getAuthor());
            }
            if (Objects.nonNull(conditions) && StringUtils.hasText(conditions.getContent())) {
                criteria.and("content").regex(Pattern.compile("^.*" + conditions.getContent() + ".*$", Pattern.CASE_INSENSITIVE));
            }
            if (!CollectionUtils.isEmpty(conditions.getTags())) {
                criteria.and("tags").all(conditions.getTags());
            }
            query.addCriteria(criteria);
            query.with(Sort.by(Sort.Order.desc("time")));
            query.fields().include("_id", "title", "author", "time", "summary", "classify", "visibleRange", "tags");

            // 查询所有用户
            return mongoTemplate.find(query, Creation.class);
        } finally {
            if (fairLock.isLocked()) {
                fairLock.unlock();
            }
        }
    }

    @Override
    public void deleteCreationsCache(CreationClassifyEnum classify) {
        // 因为此处将什么分类的文章改成了什么分类的文章不易获取, 所以简单的做法是将三种类型的的文章缓存全部清空
        RKeys rkeys = redissonClient.getKeys();
        KeysScanOptions options = KeysScanOptions.defaults()
            .pattern("creations::*");
        Iterable<String> keys = rkeys.getKeys(options);
        for (String key : keys) {
            redissonClient.getBucket(key).delete();
        }
        log.debug("清空文章缓存: {}", classify);
    }
}
