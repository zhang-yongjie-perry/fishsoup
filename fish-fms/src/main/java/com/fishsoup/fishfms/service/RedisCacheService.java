package com.fishsoup.fishfms.service;

import com.fishsoup.entity.creation.Creation;

public interface RedisCacheService {

    Creation getCreationCache(String creationId);
    Creation putCreationCache(String creationId);
    void deleteCreationCache(String creationId);
}
