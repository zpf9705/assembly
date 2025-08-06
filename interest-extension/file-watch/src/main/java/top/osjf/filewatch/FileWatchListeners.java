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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread-safe container for managing file watch listeners.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public final class FileWatchListeners {

    private final ReadWriteLock lock;
    private final List<FileWatchListener> listeners;

    /**
     * Constructs with default initial capacity (16).
     */
    public FileWatchListeners() {
        this(16);
    }
    /**
     * Constructs with specified initial capacity.
     * @param initialCapacity the initial capacity of listener list.
     */
    public FileWatchListeners(int initialCapacity) {
        lock = new ReentrantReadWriteLock();
        listeners = new ArrayList<>(initialCapacity);
    }

    /**
     * Registers or updates a {@code FileWatchListener} (removes existing one first).
     * @param listener the {@code FileWatchListener} to register.
     * @throws NullPointerException if {@code FileWatchListener} null.
     */
    public void registerListener(FileWatchListener listener) {
        if (listener == null) {
            throw new NullPointerException("listener");
        }
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            listeners.remove(listener);
            listeners.add(listener);
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes specified {@code FileWatchListener}.
     * @param listener the {@code FileWatchListener} to remove.
     * @throws IllegalArgumentException if {@code FileWatchListener} not registered.
     */
    public void removeListener(FileWatchListener listener) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (!listeners.contains(listener)) {
                throw new IllegalArgumentException("Unregistered listener " + listener);
            }
            listeners.remove(listener);
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes {@code FileWatchListener} by index.
     * @param index the position in {@code FileWatchListener} list.
     * @throws IndexOutOfBoundsException if index invalid.
     */
    public void removeListener(int index) {
        if (index < 0 || index > getListenerSize() - 1) {
            throw new IndexOutOfBoundsException("Size:" + getListenerSize() + ",Index:"+ getListenerSize());
        }
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            listeners.remove(index);
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Gets current registered {@code FileWatchListener} count.
     * @return number of registered listeners.
     */
    public int getListenerSize() {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return listeners.size();
        }
        finally {
            readLock.unlock();
        }
    }

    /**
     * Gets {@code FileWatchListener} by index.
     * @param index the position in listener list.
     * @return the listener at specified position.
     * @throws IndexOutOfBoundsException if index invalid.
     */
    public FileWatchListener getListener(int index) {
        if (index < 0 || index > getListenerSize() - 1) {
            throw new IndexOutOfBoundsException("Size:" + getListenerSize() + ",Index:"+ getListenerSize());
        }
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return listeners.get(index);
        }
        finally {
            readLock.unlock();
        }
    }

    /**
     * Gets unmodifiable view of all {@code FileWatchListener}.
     * @return immutable list containing all {@code FileWatchListener}.
     */
    public List<FileWatchListener> getListeners() {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return Collections.unmodifiableList(listeners);
        }
        finally {
            readLock.unlock();
        }
    }
}
