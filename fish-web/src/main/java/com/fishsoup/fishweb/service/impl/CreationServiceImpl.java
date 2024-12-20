package com.fishsoup.fishweb.service.impl;

import com.fishsoup.fishweb.annotation.FootstepLog;
import com.fishsoup.fishweb.domain.Creation;
import com.fishsoup.fishweb.enums.ArtworkTypeEnum;
import com.fishsoup.fishweb.exception.BusinessException;
import com.fishsoup.fishweb.service.CreationService;
import com.fishsoup.fishweb.util.DateUtils;
import com.fishsoup.fishweb.util.SecurityUtils;
import com.fishsoup.fishweb.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CreationServiceImpl implements CreationService {

    private final MongoTemplate mongoTemplate;

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
        creation.setUpdateBy(SecurityUtils.getLoginName());
        creation.setUpdateTime(DateUtils.now());
        creation.setTime(creation.getUpdateTime());
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
            update.set("time", creation.getTime());
            update.set("updateBy", creation.getUpdateBy());
            update.set("updateTime", creation.getUpdateTime());
            mongoTemplate.updateFirst(query, update, Creation.class);
            return creation.getId();
        }
        creation.setCreateBy(creation.getUpdateBy());
        creation.setCreateTime(creation.getUpdateTime());
        return mongoTemplate.insert(creation).getId();
    }

    @Override
    @FootstepLog(ArtworkTypeEnum.CREATION)
    public Creation getCreationById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.findOne(query, Creation.class);
    }

    @Override
    public List<Creation> listCreations(Creation conditions, int pageNum, int pageSize) {
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
        query.addCriteria(criteria);
        query.with(Sort.by(Sort.Order.desc("time")));
        query.fields().include("_id", "title", "author", "time", "summary", "classify", "visibleRange");

        // 查询所有用户
        return mongoTemplate.find(query, Creation.class);
    }
}
