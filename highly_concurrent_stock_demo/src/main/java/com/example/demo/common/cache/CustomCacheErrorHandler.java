package com.example.demo.common.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class CustomCacheErrorHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        System.out.println("cache get error");
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        System.out.println("cache put error");
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        System.out.println("cache evict error");
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        System.out.println("cache clear error");
    }
}
