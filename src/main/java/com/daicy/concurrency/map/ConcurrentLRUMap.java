package com.daicy.concurrency.map;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiFunction;

/**
 * @author: create by daichangya
 * @version: v1.0
 * @description: com.xcar.nvwa.common.utils
 * @date:19-12-16
 */
public class ConcurrentLRUMap<K, V> extends LinkedHashMap<K, V> implements ConcurrentMap<K, V> {

    /**
     * The lock protecting all mutators
     */
    private final ReadWriteLock readWriteLock;
    private final Lock readLock;
    private final Lock writeLock;

    private final int maxEntries;

    public ConcurrentLRUMap(final int maxEntries) {
        super(maxEntries + 1, 1.0f, true);
        this.maxEntries = maxEntries;
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = readWriteLock.readLock();
        this.writeLock = readWriteLock.writeLock();
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        return super.size() > maxEntries;
    }

    @Override
    public V put(final K key, final V value) {
        writeLock.lock();
        try {
            return (V) super.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V putIfAbsent(final K key, final V value) {
        writeLock.lock();
        try {
            return (V) super.putIfAbsent(key, value);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V get(final Object key) {
        readLock.lock();
        try {
            return (V) super.get(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return super.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return super.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        readLock.lock();
        try {
            return super.containsKey(key);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        readLock.lock();
        try {
            return super.containsValue(value);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        writeLock.lock();
        try {
            return (V) super.remove(key);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void putAll(Map m) {
        writeLock.lock();
        try {
            super.putAll(m);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public void clear() {
        writeLock.lock();
        try {
            super.clear();
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        readLock.lock();
        try {
            return new HashSet(super.keySet());
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Collection values() {
        readLock.lock();
        try {
            return super.values();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        readLock.lock();
        try {
            return super.entrySet();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public V computeIfPresent(K key,
                              BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        writeLock.lock();
        try {
            return super.computeIfPresent(key, remappingFunction);
        } finally {
            writeLock.unlock();
        }
    }

}
