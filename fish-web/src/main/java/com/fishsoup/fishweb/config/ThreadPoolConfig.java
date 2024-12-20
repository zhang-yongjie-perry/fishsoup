package com.fishsoup.fishweb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ThreadPoolConfig {

    @Bean("commonExecutorService")
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
            16,
            32,
            30,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(100, false),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean("scheduledExecutorService")
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(16);
    }
}
