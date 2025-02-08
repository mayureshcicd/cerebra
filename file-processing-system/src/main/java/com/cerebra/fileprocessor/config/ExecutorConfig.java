package com.cerebra.fileprocessor.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {
    private final ConfigProperties configProperties;

    @Bean(name = "fileProcessingExecutor")
    public Executor fileProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(configProperties.getCorePoolSize());
        executor.setMaxPoolSize(configProperties.getMaxPoolSize());
        executor.setQueueCapacity(configProperties.getQueueCapacity());
        executor.setThreadNamePrefix(configProperties.getThreadNamePrefix());
        executor.initialize();
        return executor;
    }
}
