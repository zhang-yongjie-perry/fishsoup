package com.fishsoup.fishdas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    @Bean("myThreadPoolExecutor")
    public ExecutorService threadPoolExecutor() {
        return new ThreadPoolExecutor(
            16,
            100,
            1,
            TimeUnit.MINUTES,
            new LinkedBlockingQueue<>(),
            new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
