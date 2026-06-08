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


package top.osjf.cron.datasource.driven.scheduled.external.file;

import org.slf4j.LoggerFactory;
import top.osjf.cron.core.lang.NotNull;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread-safe implementation of {@link ReadWriteLock} for file operations,
 * combining file-level locking with thread-level synchronization.
 * Implements {@link Closeable} for resource management.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class FileReadWriteLock implements ReadWriteLock, Closeable {

    /**
     * The underlying file channel for locking operations.
     */
    private final FileChannel channel;

    /**
     * The write lock instance providing exclusive access.
     */
    private final FileChannelLock writeLock;

    /**
     * The read lock instance providing shared access.
     */
    private final FileChannelLock readLock;

    /**
     * Constructs a {@code FileReadWriteLock} with default non-fair ordering policy.
     *
     * @param file the file to lock
     * @throws IOException if the file cannot be opened or accessed
     */
    public FileReadWriteLock(File file) throws IOException {
        this(file, false);
    }

    /**
     * Constructs a {@code FileReadWriteLock} with specified fairness policy.
     *
     * @param file the file to lock
     * @param fair true for fair ordering, false for non-fair
     * @throws IOException if the file cannot be opened or accessed
     */
    public FileReadWriteLock(File file, boolean fair) throws IOException {
        this(file.toPath(), fair);
    }

    /**
     * Core constructor that initializes the file channel and {@code FileChannelLock} instances.
     *
     * @param path the path to the file to lock
     * @param fair true for fair ordering, false for non-fair
     * @throws IOException if the file cannot be opened or accessed
     */
    public FileReadWriteLock(Path path, boolean fair) throws IOException {
        channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE);
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock(fair);
        writeLock = new FileChannelLockImpl(channel, readWriteLock.writeLock());
        readLock = new FileChannelLockImpl(channel, readWriteLock.readLock());
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Lock writeLock() {
        return writeLock;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    public Lock readLock() {
        return readLock;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        channel.close();
    }

    /**
     * The {@link FileChannel} process Lock.
     */
    public interface FileChannelLock extends Lock {

        /**
         * Performs the actual file locking operation.
         * @throws IllegalStateException if file lock acquisition fails
         */
        void lockProcess() throws IllegalStateException;

        /**
         * Attempts to acquire the file lock without blocking.
         * @return true if the lock was acquired, false otherwise
         * @throws IllegalStateException if file lock acquisition fails
         */
        boolean tryLockProcess() throws IllegalStateException;
    }

    /**
     * A thread-safe implementation of {@link Lock} combining file-level locking with
     * thread-level locking.
     * This class provides both exclusive and shared file locking capabilities based
     * on the underlying thread lock type.
     */
    private static class FileChannelLockImpl implements FileChannelLock {

        private final ThreadLocal<UnlockCallbackRegistrant> unlockCallbackLocal = ThreadLocal
                .withInitial(UnlockCallbackRegistrant::new);

        /** The file channel associated with this lock. */
        private final FileChannel fileChannel;

        /** The thread-level lock used for synchronization.*/
        private final Lock threadLock;

        /**
         * Flag indicating whether this is a read lock (shared) or write lock (exclusive).
         * {@code true} if the thread lock is a {@link ReentrantReadWriteLock.ReadLock}.
         */
        private final boolean isThreadRead;

        /**
         * Constructs a new {@code FileChannelLockImpl} with the specified file channel and thread lock.
         *
         * @param fileChannel the file channel to lock
         * @param threadLock the thread-level lock to coordinate with
         */
        public FileChannelLockImpl(FileChannel fileChannel, Lock threadLock) {
            this.fileChannel = fileChannel;
            this.threadLock = threadLock;
            isThreadRead = threadLock instanceof ReentrantReadWriteLock.ReadLock;
        }

        /**
         * Performs the actual file locking operation.
         * Acquires either a shared or exclusive lock on the entire file based on {@code isThreadRead}.
         * The acquired lock is stored in thread-local storage.
         *
         * @throws IllegalStateException if file lock acquisition fails
         */
        @Override
        public void lockProcess() {
            try {
                FileLock fileLock;
                if (isThreadRead) {
                    fileLock = fileChannel.lock(0, Long.MAX_VALUE, true);
                }
                else {
                    fileLock = fileChannel.lock();
                }
                registerUnlockCallback(fileLock);
            }
            catch (IOException ex) {
                throw new IllegalStateException("File lock acquisition failed", ex);
            }
        }

        /**
         * Attempts to acquire the file lock without blocking.
         * @return true if the lock was acquired, false otherwise
         * @throws IllegalStateException if file lock acquisition fails
         */
        @Override
        public boolean tryLockProcess() {
            try {
                FileLock fileLock;
                if (isThreadRead) {
                    fileLock = fileChannel.tryLock(0, Long.MAX_VALUE, true);
                }
                else {
                    fileLock = fileChannel.tryLock();
                }
                if (fileLock != null) {
                    registerUnlockCallback(fileLock);
                    return true;
                }
                return false;
            }
            catch (IOException ex) {
                throw new IllegalStateException("File lock acquisition failed", ex);
            }
        }

        @Override
        public void lock() {
            threadLock.lock();
            lockProcess();
            registerUnlockCallback(threadLock);
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            threadLock.lockInterruptibly();
            lockProcess();
            registerUnlockCallback(threadLock);
        }

        @Override
        public boolean tryLock() {
            if (!threadLock.tryLock()) {
                return false;
            }
            return tryLock0();
        }

        @Override
        public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
            if (!threadLock.tryLock(time, unit)) {
                return false;
            }
            return tryLock0();
        }

        /**
         * @return result of {@code tryLock}
         * @since 3.0.2
         */
        private boolean tryLock0() {
            if (!tryLockProcess()) {
                threadLock.unlock();
                return false;
            }
            registerUnlockCallback(threadLock);
            return true;
        }

        @Override
        public void unlock() {
            try {
                unlockCallbackLocal.get().callback();
            }
            catch (Exception ex) {
                LoggerFactory.getLogger(FileReadWriteLock.class)
                        .error("Failed to execute unlock callbacks", ex);
            }
            finally {
                unlockCallbackLocal.remove();
            }
        }

        /**
         * @since 3.0.2
         */
        private void registerUnlockCallback(Object callbackObj) {
            unlockCallbackLocal.get().registerUnlockCallback(callbackObj);
        }

        /**
         * @since 3.0.2
         */
        private interface Callback {
            void callback() throws Exception;
        }

        /**
         * @since 3.0.2
         */
        private static class UnlockCallbackRegistrant implements Callback {
            private final List<Callback> callbacks = new ArrayList<>();
            private boolean threadRegistered;
            private boolean fileRegistered;
            public void registerUnlockCallback(Object callbackObj) {
                if (!threadRegistered && callbackObj instanceof Lock) {
                    callbacks.add(() -> ((Lock) callbackObj).unlock());
                    threadRegistered = true;
                }
                else if (!fileRegistered && callbackObj instanceof FileLock) {
                    callbacks.add(() -> ((FileLock) callbackObj).release());
                    fileRegistered = true;
                }
            }
            @Override
            public void callback() throws Exception {
                for (Callback callback : callbacks) {
                    callback.callback();
                }
            }
        }

        /**
         * Throws {@code UnsupportedOperationException} as conditions are not supported.
         *
         * @return nothing (always throws)
         * @throws UnsupportedOperationException always
         */
        @Override
        @NotNull
        public Condition newCondition() {
            throw new UnsupportedOperationException();
        }
    }
}
