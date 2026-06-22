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

import top.osjf.commons.lang.NotNull;
import top.osjf.cron.core.lifecycle.InitializeProperties;
import top.osjf.cron.core.util.ExecutorUtils;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@code SuperiorPropertiesParsedThreadPoolExecutor} is an inheritance implementation
 * class of {@link ThreadPoolExecutor}, which uses {@link InitializeProperties}'s configuration
 * loading to configure a thread pool instance.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class PropertiesParsedThreadPoolExecutor extends ThreadPoolExecutor implements AutoCloseable {

    /**
     * The {@link InitializeProperties} configuration attribute name of {@code ThreadPoolExecutor#corePoolSize}
     */
    public static final String PROPERTY_OF_CORE_SIZE = "corePoolSize";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@code ThreadPoolExecutor#maximumPoolSize}
     */
    public static final String PROPERTY_OF_MAX_SIZE = "maximumPoolSize";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@code ThreadPoolExecutor#keepAliveTime}
     */
    public static final String PROPERTY_OF_KEEP_ALIVE = "keepAlive";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@code ThreadPoolExecutor#keepAliveTime}
     */
    public static final String PROPERTY_OF_KEEP_ALIVE_UNIT = "keepAliveUnit";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@code ThreadPoolExecutor#workQueue}
     */
    public static final String PROPERTY_OF_QUEUE_CAPACITY = "queueCapacity";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@code ThreadPoolExecutor#threadFactory}
     */
    public static final String PROPERTY_OF_THREAD_NAME_PREFIX = "threadNamePrefix";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@code ThreadPoolExecutor#allowCoreThreadTimeOut}
     */
    public static final String PROPERTY_OF_ALLOW_CORE_THREAD_TIMEOUT = "allowCoreThreadTimeout";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@link ThreadPoolExecutor#awaitTermination}
     */
    public static final String PROPERTY_OF_AWAIT_TERMINATION = "awaitTermination";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@link ThreadPoolExecutor#awaitTermination}
     */
    public static final String PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT = "awaitTerminationTimeout";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@link ThreadPoolExecutor#awaitTermination}
     */
    public static final String PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT_UNIT = "awaitTerminationTimeoutUnit";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@code ThreadPoolExecutor#handler}
     */
    public static final String PROPERTY_OF_REJECT_RETRY_TIMEOUT = "rejectRetryTimeout";

    /**
     * The {@link InitializeProperties} configuration attribute name of {@code ThreadPoolExecutor#handler}
     */
    public static final String PROPERTY_OF_REJECT_RETRY_TIMEOUT_UNIT = "rejectRetryTimeoutUnit";

    private boolean awaitTermination = false;

    private long awaitTerminationTimeout = 10L;

    private TimeUnit awaitTerminationTimeoutUnit = TimeUnit.SECONDS;

    /**
     * Creates a new {@code SuperiorPropertiesParsedThreadPoolExecutor} with the given initial
     * parameter.
     * @param properties the initial {@link top.osjf.cron.core.lifecycle.InitializeProperties}.
     */
    public PropertiesParsedThreadPoolExecutor(InitializeProperties properties) {
        super(
                properties.getInteger(PROPERTY_OF_CORE_SIZE, Runtime.getRuntime().availableProcessors()),
                properties.getInteger(PROPERTY_OF_MAX_SIZE, Runtime.getRuntime().availableProcessors() + 1),
                properties.getLong( PROPERTY_OF_KEEP_ALIVE, 60L),
                properties.getEnum(PROPERTY_OF_KEEP_ALIVE_UNIT, TimeUnit.class, TimeUnit.SECONDS),
                new ArrayBlockingQueue<>(properties.getInteger(PROPERTY_OF_QUEUE_CAPACITY, 1000)),
                new PropertiesParsedThreadFactoryImpl(properties),
                new PropertiesParsedRejectedExecutionHandler(properties)
        );
        allowCoreThreadTimeOut(properties.getBoolean(PROPERTY_OF_ALLOW_CORE_THREAD_TIMEOUT, true));
        this.awaitTermination
                = properties.getBoolean(PROPERTY_OF_AWAIT_TERMINATION, this.awaitTermination);
        this.awaitTerminationTimeout
                = properties.getLong(PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT, this.awaitTerminationTimeout);
        this.awaitTerminationTimeoutUnit
                = properties.getEnum(PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT_UNIT, TimeUnit.class, this.awaitTerminationTimeoutUnit);
    }

    @Override
    public void close() {
        ExecutorUtils.shutdownExecutor(this, awaitTermination, awaitTerminationTimeout, awaitTerminationTimeoutUnit);
    }

    /**
     * The {@link ThreadFactory} implementation class for parsing configuration of {@link InitializeProperties}.
     */
    private static class PropertiesParsedThreadFactoryImpl implements ThreadFactory {
        private final AtomicLong counter = new AtomicLong(0);

        private final String threadNamePrefix;

        public PropertiesParsedThreadFactoryImpl(InitializeProperties properties) {
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
     * The {@link RejectedExecutionHandler} implementation class for parsing configuration of {@link InitializeProperties}.
     */
    private static class PropertiesParsedRejectedExecutionHandler implements RejectedExecutionHandler {

        private final long rejectRetryTimeout;
        private final TimeUnit rejectRetryTimeoutUnit;

        public PropertiesParsedRejectedExecutionHandler(InitializeProperties properties) {
            this.rejectRetryTimeout = properties.getLong(PROPERTY_OF_REJECT_RETRY_TIMEOUT, 10L);
            this.rejectRetryTimeoutUnit = properties.getEnum(PROPERTY_OF_REJECT_RETRY_TIMEOUT_UNIT, TimeUnit.class,
                    TimeUnit.SECONDS);
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor pool) {
            if (pool.isShutdown()) {
                return;
            }
            try {
                if (!pool.getQueue().offer(r, rejectRetryTimeout, rejectRetryTimeoutUnit)) {
                    throw new RunningTimeoutException("Rejected running entity re queued timeout");
                }
            }
            catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new RunningException("Thread interrupt");
            }
        }
    }
}
