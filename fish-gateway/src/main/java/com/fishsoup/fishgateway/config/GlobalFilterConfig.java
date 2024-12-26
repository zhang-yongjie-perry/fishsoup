package com.fishsoup.fishgateway.config;

import com.fishsoup.fishgateway.filter.JwtAuthenticationFilter;
import org.redisson.api.RedissonClient;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalFilterConfig {

    @Bean
    public GlobalFilter jwtAuthenticationFilter(RedissonClient redisson) {
        return new JwtAuthenticationFilter(redisson);
    }
}
