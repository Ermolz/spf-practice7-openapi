package com.example.openapi.config;

import com.example.openapi.cache.CustomCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Primary
    public CustomCacheManager cacheManager() {
        return new CustomCacheManager();
    }
}

