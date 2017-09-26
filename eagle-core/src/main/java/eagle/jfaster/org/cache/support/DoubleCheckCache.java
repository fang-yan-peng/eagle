/*
 * Copyright 2017 eagle.jfaster.org.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package eagle.jfaster.org.cache.support;

import eagle.jfaster.org.cache.CacheLoader;

import eagle.jfaster.org.cache.LoadingCache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fangyanpeng on 2017/8/22.
 */
public class DoubleCheckCache<K, V> implements LoadingCache<K, V> {

    private final CacheLoader<K, V> loader;

    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<K, V>();

    private final ConcurrentHashMap<K, Object> locks = new ConcurrentHashMap<K, Object>();

    public DoubleCheckCache(CacheLoader<K, V> loader) {
        this.loader = loader;
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        if (value == null) {
            synchronized (getLock(key)) {
                value = cache.get(key);
                if (value == null) {
                    value = loader.load(key);
                    if (value != null) {
                        cache.put(key, value);
                    }
                }
            }
        }
        return value;
    }

    private Object getLock(K key) {
        Object lock = locks.get(key);
        if (lock == null) {
            lock = new Object();
            Object old = locks.putIfAbsent(key, lock);
            if (old != null) { // 已经存在lock
                lock = old;
            }
        }
        return lock;
    }

}
