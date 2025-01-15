package com.fishsoup.fishfms.listener;

import com.fishsoup.entity.creation.Creation;
import com.fishsoup.entity.exception.BusinessException;
import com.fishsoup.fishfms.service.RedisCacheService;
import com.fishsoup.util.StringUtils;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@SuppressWarnings("all")
public class RMQListener {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private RedisCacheService redisCacheService;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(name = "cache.creation"),
        exchange = @Exchange(name = "amq.topic", type = ExchangeTypes.TOPIC),
        key = "cache.creation"
    ))
    public void listenCacheUpdate(String creationId) {
        if (!StringUtils.hasText(creationId)) {
            throw new MessageConversionException("ID参数为空");
        }
        // 先更新
        redisCacheService.putCreationCache(creationId);
        // 再获取置缓存
        Creation creationCache = redisCacheService.getCreationCache(creationId);
        Creation creation = mongoTemplate.findOne(new Query(Criteria.where("_id").is(creationId)), Creation.class);
        if (!Objects.equals(creationCache, creation)) {
            throw new BusinessException("缓存不一致异常");
        }
    }
}
