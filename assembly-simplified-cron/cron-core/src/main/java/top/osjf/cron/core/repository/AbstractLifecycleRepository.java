/*
 * Copyright 2024-? the original author or authors.
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


package top.osjf.cron.core.repository;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract lifecycle repository that provides basic lifecycle management (start/stop).
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public abstract class AbstractLifecycleRepository implements LifecycleRepository {

    /**
     * Atomic flag to track whether the repository is started.
     */
    private final AtomicBoolean isStarted = new AtomicBoolean(false);

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws Exception {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (isStarted.compareAndSet(false, true)) {
            return;
        }
        throw new IllegalStateException("this repository has started");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (isStarted.compareAndSet(true, false)) {
            return;
        }
        throw new IllegalStateException("this repository has stopped");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return isStarted.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reStart() {
        throw new ReStartedUnsupportedException();
    }

    /**
     * ensure the repository has been startup before providing service.
     */
    protected void ensureStarted() {
        if (!isStarted()) {
            throw new IllegalStateException(String.format(
                    "Repository(%s) has not been started yet, please startup first!", getClass()
                            .getSimpleName()));
        }
    }
}
