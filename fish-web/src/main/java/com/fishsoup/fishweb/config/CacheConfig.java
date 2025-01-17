package com.fishsoup.fishweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {

        // 创建默认的 RedisCacheConfiguration，并设置全局缓存过期时间
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
            // 默认全局缓存过期时间为2小时
            .entryTtl(Duration.ofHours(2))
            // 禁止缓存 null 值
//            .disableCachingNullValues()
            .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        // 创建 RedisCacheManager，加载自定义的缓存配置
        return RedisCacheManager.builder(redisConnectionFactory)
            // 设置默认的缓存配置
            .cacheDefaults(defaultCacheConfig)
            .build();
    }
}
