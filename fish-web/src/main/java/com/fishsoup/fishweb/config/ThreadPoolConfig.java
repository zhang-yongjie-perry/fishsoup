package com.fishsoup.fishweb.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Setter
@Getter
@Configuration
@ConfigurationProperties("fish.threads")
public class ThreadPoolConfig {

    private CommonThreadPool common = new CommonThreadPool();

    private ScheduledThreadPool scheduled = new ScheduledThreadPool();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommonThreadPool {
        private Integer corePoolSize;
        private Integer maxPoolSize;
        private Long keepAliveTime;
        private Integer queueCapacity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduledThreadPool {
        private Integer corePoolSize;
    }

    @Bean("commonExecutorService")
    public ExecutorService executorService() {
        return new ThreadPoolExecutor(
            common.getCorePoolSize(),
            common.getMaxPoolSize(),
            common.getKeepAliveTime(),
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(common.getQueueCapacity(), false),
            new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Bean("scheduledExecutorService")
    public ScheduledExecutorService scheduledExecutorService() {
        return new ScheduledThreadPoolExecutor(scheduled.getCorePoolSize());
    }
}
