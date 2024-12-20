package com.fishsoup.fishdas.config;

import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OkHttpConfig {

    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient().newBuilder()
            .connectTimeout(Duration.ofSeconds(60 * 5))
            .connectionPool(new ConnectionPool(1000, 30, TimeUnit.SECONDS))
            .build();
    }
}
