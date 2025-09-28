/*
 * Copyright org.apache.commons.collections4.
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

import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.core.util.ExecutorUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class SuperiorPropertiesParsedThreadPoolExecutor extends ThreadPoolExecutor implements AutoCloseable {

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #corePoolSize}
     */
    public static final String PROPERTY_OF_CORE_SIZE = "corePoolSize";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #maximumPoolSize}
     */
    public static final String PROPERTY_OF_MAX_SIZE = "maximumPoolSize";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #keepAliveTime}
     */
    public static final String PROPERTY_OF_KEEP_ALIVE = "keepAlive";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #keepAliveTime}
     */
    public static final String PROPERTY_OF_KEEP_ALIVE_UNIT = "keepAliveUnit";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #workQueue}
     */
    public static final String PROPERTY_OF_QUEUE_CAPACITY = "queueCapacity";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #threadFactory}
     */
    public static final String PROPERTY_OF_THREAD_NAME_PREFIX = "threadNamePrefix";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #allowCoreThreadTimeOut}
     */
    public static final String PROPERTY_OF_ALLOW_CORE_THREAD_TIMEOUT = "allowCoreThreadTimeout";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #awaitTermination(long, TimeUnit)}
     */
    public static final String PROPERTY_OF_AWAIT_TERMINATION = "awaitTermination";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #awaitTermination(long, TimeUnit)}
     */
    public static final String PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT = "awaitTerminationTimeout";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #awaitTermination(long, TimeUnit)}
     */
    public static final String PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT_UNIT = "awaitTerminationTimeoutUnit";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #handler}
     */
    public static final String PROPERTY_OF_REJECT_RETRY_TIMEOUT = "rejectRetryTimeout";

    /**
     * The {@link SuperiorProperties} configuration attribute name of {@code #handler}
     */
    public static final String PROPERTY_OF_REJECT_RETRY_TIMEOUT_UNIT = "rejectRetryTimeoutUnit";

    private boolean awaitTermination = false;

    private long awaitTerminationTimeout = 10;

    private TimeUnit awaitTerminationTimeoutUnit = TimeUnit.SECONDS;

    /**
     * Creates a new {@code SuperiorPropertiesParsedThreadPoolExecutor} with the given initial
     * parameter.
     * @param properties the initial {@link SuperiorProperties}.
     */
    public SuperiorPropertiesParsedThreadPoolExecutor(SuperiorProperties properties) {
        super(
                getProperty(properties, PROPERTY_OF_CORE_SIZE, Runtime.getRuntime().availableProcessors()),
                getProperty(properties, PROPERTY_OF_MAX_SIZE, Runtime.getRuntime().availableProcessors() + 1),
                getProperty(properties, PROPERTY_OF_KEEP_ALIVE, 60),
                getProperty(properties, PROPERTY_OF_KEEP_ALIVE_UNIT, TimeUnit.SECONDS),
                new ArrayBlockingQueue<>(getProperty(properties, PROPERTY_OF_QUEUE_CAPACITY, 1000)),
                new SuperiorPropertiesParsedThreadFactoryImpl(properties),
                new SuperiorPropertiesParsedRejectedExecutionHandler(properties)
        );
        allowCoreThreadTimeOut(getProperty(properties, PROPERTY_OF_ALLOW_CORE_THREAD_TIMEOUT, true));
        this.awaitTermination
                = getProperty(properties, PROPERTY_OF_AWAIT_TERMINATION, this.awaitTermination);
        this.awaitTerminationTimeout
                = getProperty(properties, PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT, this.awaitTerminationTimeout);
        this.awaitTerminationTimeoutUnit
                = getProperty(properties, PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT_UNIT, this.awaitTerminationTimeoutUnit);
    }

    private static <T> T getProperty(SuperiorProperties properties, String propertyName, T def) {
        return properties.getProperty(propertyName, def);
    }

    @Override
    public void close() {
        ExecutorUtils.shutdownExecutor(this, awaitTermination, awaitTerminationTimeout, awaitTerminationTimeoutUnit);
    }

    /**
     * The {@link ThreadFactory} implementation class for parsing configuration of {@link SuperiorProperties}.
     */
    private static class SuperiorPropertiesParsedThreadFactoryImpl implements ThreadFactory {
        private final AtomicLong counter = new AtomicLong(0);

        private final String threadNamePrefix;

        public SuperiorPropertiesParsedThreadFactoryImpl(SuperiorProperties properties) {
            threadNamePrefix = properties.getProperty(PROPERTY_OF_THREAD_NAME_PREFIX, "monitor-task-");
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(threadNamePrefix + counter.getAndIncrement());
            return thread;
        }
    }

    /**
     * The {@link RejectedExecutionHandler} implementation class for parsing configuration of {@link SuperiorProperties}.
     */
    private static class SuperiorPropertiesParsedRejectedExecutionHandler implements RejectedExecutionHandler {

        private final long rejectRetryTimeout;
        private final TimeUnit rejectRetryTimeoutUnit;

        public SuperiorPropertiesParsedRejectedExecutionHandler(SuperiorProperties properties) {
            this.rejectRetryTimeout = properties.getProperty(PROPERTY_OF_REJECT_RETRY_TIMEOUT, 10);
            this.rejectRetryTimeoutUnit = properties.getProperty(PROPERTY_OF_REJECT_RETRY_TIMEOUT_UNIT, TimeUnit.SECONDS);
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor pool) {
            if (pool.isShutdown()) {
                return;
            }
            try {
                if (!pool.getQueue().offer(r, rejectRetryTimeout, rejectRetryTimeoutUnit)) {
                    // no op
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
