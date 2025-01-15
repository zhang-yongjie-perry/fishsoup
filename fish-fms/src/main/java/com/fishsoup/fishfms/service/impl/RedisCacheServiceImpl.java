package com.fishsoup.fishfms.service.impl;

import com.fishsoup.entity.creation.Creation;
import com.fishsoup.fishfms.service.RedisCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

@Service
public class RedisCacheServiceImpl implements RedisCacheService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Cacheable(value = "creation", key = "#p0")
    public Creation getCreationCache(String creationId) {
        Creation creation = mongoTemplate.findOne(new Query(Criteria.where("_id").is(creationId)), Creation.class);
        return creation == null ? new Creation() : creation;
    }

    @Override
    @CachePut(value = "creation", key = "#p0")
    public Creation putCreationCache(String creationId) {
        Creation creation = mongoTemplate.findOne(new Query(Criteria.where("_id").is(creationId)), Creation.class);
        return creation == null ? new Creation() : creation;
    }

    @Override
    @CacheEvict(value = "creation", key = "#p0")
    public void deleteCreationCache(String creationId) {
    }
}
