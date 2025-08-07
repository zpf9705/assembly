/*
 * Copyright 2025-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package top.osjf.filewatch;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Thread-safe container for managing wait-creation/modification/deletion configurations with path context.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see FileWatchService#registerWaitConfiguration(String, String, WaitConfiguration)
 */
public final class WaitConfigurations {

    private final ReadWriteLock lock;
    private final Map<Path, Map<Path, WaitConfiguration>> pathContextWaitCreateConfigurationMap;

    /**
     * Constructs with default initial capacity (16).
     */
    public WaitConfigurations() {
        this(16);
    }

    /**
     * Constructs with specified initial capacity.
     *
     * @param initialCapacity initial capacity for the parent path map.
     */
    public WaitConfigurations(int initialCapacity) {
        lock = new ReentrantReadWriteLock();
        pathContextWaitCreateConfigurationMap = new HashMap<>(initialCapacity);
    }

    /**
     * Registers a wait-create/modify/delete configuration under specified parent path and context.
     * @param parent        the parent directory path.
     * @param pathContext   the context path for watching.
     * @param configuration the configuration to register.
     * @throws NullPointerException if any parameter is {@literal null}.
     */
    public void registerWaitConfiguration(Path parent, Path pathContext, WaitConfiguration configuration) {
        if (parent == null || pathContext == null || configuration == null) {
            throw new NullPointerException("parent or pathContext or configuration");
        }
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            Map<Path, WaitConfiguration> map
                    = pathContextWaitCreateConfigurationMap.computeIfAbsent(parent, k -> new HashMap<>(16));
            map.put(pathContext, configuration);
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Checks if there is a wait-create/modify/delete configuration under specified path.
     * @param parent      the parent directory path.
     * @param pathContext the context path for watching.
     * @return the configuration if found, {@literal null} otherwise.
     * @throws NullPointerException if any parameter is {@literal null}.
     */
    public boolean hasWaitConfiguration(Path parent, Path pathContext) {
        if (parent == null || pathContext == null) {
            throw new NullPointerException("parent or pathContext");
        }
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            Map<Path, WaitConfiguration> map = pathContextWaitCreateConfigurationMap.get(parent);
            if (map != null) {
                return map.containsKey(pathContext);
            }
            return false;
        }
        finally {
            readLock.unlock();
        }
    }

    /**
     * Retrieves wait-create/modify/delete configuration by parent path and context.
     * @param parent      the parent directory path.
     * @param pathContext the context path for watching.
     * @return the configuration if found, {@literal null} otherwise.
     * @throws NullPointerException if any parameter is {@literal null}.
     */
    public WaitConfiguration getWaitConfiguration(Path parent, Path pathContext) {
        if (parent == null || pathContext == null) {
            throw new NullPointerException("parent or pathContext");
        }
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            Map<Path, WaitConfiguration> map = pathContextWaitCreateConfigurationMap.get(parent);
            if (map != null) {
                return map.get(pathContext);
            }
            return null;
        }
        finally {
            readLock.unlock();
        }
    }
}
