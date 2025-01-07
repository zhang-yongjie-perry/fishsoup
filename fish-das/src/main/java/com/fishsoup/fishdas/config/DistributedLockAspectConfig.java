package com.fishsoup.fishdas.config;

import com.fishsoup.aspect.DistributedLockAspect;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistributedLockAspectConfig {

    @Bean
    public DistributedLockAspect distributedLockAspect(RedissonClient redisson) {
        return new DistributedLockAspect(redisson);
    }
}
