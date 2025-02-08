package com.cerebra.fileprocessor.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {

            return new CaffeineCacheManager() {
                public com.github.benmanes.caffeine.cache.Cache<Object, Object> createNativeCache(String name) {
                    return Caffeine.newBuilder()
                            .expireAfterWrite(10, TimeUnit.MINUTES)
                            .maximumSize(100)
                            .build();
                }
            };
    }

}

