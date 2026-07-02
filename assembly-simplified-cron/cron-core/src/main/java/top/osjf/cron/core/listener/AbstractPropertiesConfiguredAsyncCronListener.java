/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.core.listener;

import top.osjf.commons.lang.NotNull;
import top.osjf.cron.core.lifecycle.InitializeProperties;
import top.osjf.cron.core.repository.PropertiesParsedThreadPoolExecutor;

import java.util.concurrent.ExecutorService;

/**
 * Abstract base class for asynchronous timer task listeners initialized based on configuration
 *
 * <p>Implement {@link AsyncCronListener} to uniformly encapsulate the thread pool creation logic:
 * Based on the passed-in {@link InitializeProperties} configuration parameters, construct a custom
 * thread pool implementation class, namely {@link PropertiesParsedThreadPoolExecutor}.
 *
 * <p>Subclasses do not need to repeatedly implement the {@link AsyncCronListener#get()} method.
 * They can directly pass in the thread pool configuration through the constructor, You can quickly
 * acquire the ability to asynchronously execute scheduled tasks with callback listeners, unify
 * thread pool configuration specifications, and avoid repeatedly creating thread pool template code.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class AbstractPropertiesConfiguredAsyncCronListener implements AsyncCronListener {

    /** A thread pool object created with the given configuration {@link InitializeProperties}.*/
    private final PropertiesParsedThreadPoolExecutor executorService;

    /**
     * Creates a new {@code AbstractPropertiesConfiguredAsyncCronListener} with the given initial
     * {@link InitializeProperties} to initialize a new {@link PropertiesParsedThreadPoolExecutor}.
     * @param initializeProperties the initialization configuration parameters related to thread pool.
     */
    public AbstractPropertiesConfiguredAsyncCronListener(InitializeProperties initializeProperties) {
        this.executorService = new PropertiesParsedThreadPoolExecutor(initializeProperties);
    }

    /**
     * Return a custom thread pool instance based on the configuration properties, which is used
     * to asynchronously execute the scheduled task callback of the current listener.
     * @return a custom thread pool {@link PropertiesParsedThreadPoolExecutor} instance.
     */
    @NotNull
    @Override
    public ExecutorService get() {
        return executorService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        executorService.close();
    }
}
