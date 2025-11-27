package com.example.openapi.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCacheManager implements CacheManager {

    private final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, CustomCache::new);
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.unmodifiableSet(cacheMap.keySet());
    }

    public void clearCache(String cacheName) {
        Cache cache = cacheMap.get(cacheName);
        if (cache != null) {
            cache.clear();
        }
    }

    public void clearAllCaches() {
        cacheMap.values().forEach(Cache::clear);
    }
}

