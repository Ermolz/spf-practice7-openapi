package com.example.openapi.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueRetrievalException;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCache implements Cache {

    private final String name;
    private final ConcurrentHashMap<Object, Object> store = new ConcurrentHashMap<>();

    public CustomCache(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this.store;
    }

    @Override
    public ValueWrapper get(Object key) {
        Object value = store.get(key);
        return value != null ? new SimpleValueWrapper(value) : null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Object value = store.get(key);
        if (value != null && type != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = store.get(key);
        if (value != null) {
            try {
                return (T) value;
            } catch (ClassCastException e) {
                return null;
            }
        }
        
        try {
            T loadedValue = valueLoader.call();
            if (loadedValue != null) {
                store.put(key, loadedValue);
            }
            return loadedValue;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        if (value != null) {
            store.put(key, value);
        }
    }

    @Override
    public void evict(Object key) {
        store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    public int size() {
        return store.size();
    }
}

