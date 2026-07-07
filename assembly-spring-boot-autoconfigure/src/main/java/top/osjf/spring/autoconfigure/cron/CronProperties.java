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

package top.osjf.spring.autoconfigure.cron;

import com.cronutils.model.CronType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.osjf.cron.core.lifecycle.InitializeProperties;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.repository.RunTimeoutRegistrarRepository;
import top.osjf.cron.cron4j.repository.Cron4jCronTaskRepository;
import top.osjf.cron.hutool.repository.HutoolCronTaskRepository;
import top.osjf.cron.spring.CronTaskPropertyKey;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static top.osjf.cron.core.repository.PropertiesParsedThreadPoolExecutor.*;

/**
 * Cron schedule properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
@ConfigurationProperties(prefix = CronTaskPropertyKey.PREFIX)
public class CronProperties {

    /**
     * Type of client to use. By default, auto-detected according to the classpath.
     */
    private ClientType clientType;

    private final Hutool hutool = new Hutool();

    private final Quartz quartz = new Quartz();

    private final Cron4j cron4j = new Cron4j();

    /**
     * @since 1.0.4
     */
    private final Simple simple = new Simple();

    public ClientType getClientType() {
        return clientType;
    }

    /**
     * @see WebRequestAuthentication
     */
    private WebRequestAuthentication webRequestAuthentication = new WebRequestAuthentication();

    /**
     * @see RunTimeoutMonitoring
     */
    private RunTimeoutMonitoring runTimeoutMonitoring = new RunTimeoutMonitoring();

    /**
     * Whether to automatically start the cron task container after all tasks and listeners are registered.
     * Defaults to true, set to false if you need to manually call {@link CronTaskRepository#start()} later.
     * @since 3.0.2
     */
    private boolean autoStartup = true;

    /**
     * Get the configuration of the specified {@link ClientType}.
     * @param clientType the input {@link ClientType}.
     * @return the {@link InitializeProperties} created by {@link ClientType}.
     * @since 3.0.1
     */
    public InitializeProperties getClientProperties(ClientType clientType) {
        InitializeProperties properties = null;
        if (clientType == ClientType.HUTOOL) {
            properties = hutool.get();
        }
        else if (clientType == ClientType.CRON4J) {
            properties = cron4j.get();
        }
        else if (clientType == ClientType.QUARTZ) {
            properties = quartz.get();
        }

        if (properties != null) {
            properties.mergeFrom(runTimeoutMonitoring.get());
        }

        return properties;
    }

    public void setClientType(ClientType clientType) {
        this.clientType = clientType;
    }

    public Hutool getHutool() {
        return hutool;
    }

    public Quartz getQuartz() {
        return quartz;
    }

    public Cron4j getCron4j() {
        return cron4j;
    }

    public Simple getSimple() {
        return simple;
    }

    public WebRequestAuthentication getWebRequestAuthentication() {
        return webRequestAuthentication;
    }

    public void setWebRequestAuthentication(WebRequestAuthentication webRequestAuthentication) {
        this.webRequestAuthentication = webRequestAuthentication;
    }

    public RunTimeoutMonitoring getRunTimeoutMonitoring() {
        return runTimeoutMonitoring;
    }

    public void setRunTimeoutMonitoring(RunTimeoutMonitoring runTimeoutMonitoring) {
        this.runTimeoutMonitoring = runTimeoutMonitoring;
    }

    public boolean isAutoStartup() {
        return autoStartup;
    }

    public void setAutoStartup(boolean autoStartup) {
        this.autoStartup = autoStartup;
    }

    /**
     * Hutool client properties.
     */
    public static class Hutool implements Supplier<InitializeProperties> {

        /**
         * Set whether to support second matching.
         * <p>This method is used to define whether to use the second matching mode.
         * If it is true, the first digit in the timed task expression is seconds,
         * otherwise it is minutes, and the default is minutes.
         */
        private boolean isMatchSecond = true;

        /**
         * Whether to start as a daemon thread.
         * <p>If true, the scheduled task executed immediately after calling the
         * {@link top.osjf.cron.core.repository.CronTaskRepository#stop()} method will end.
         * Otherwise, it will wait for the execution to complete before ending.
         */
        private boolean isDaemon = false;

        /**
         * The time zone applied by the scheduler.
         *
         * @since 1.0.3
         */
        private TimeZone timezone = TimeZone.getDefault();

        /**
         * The boolean flag of when stop clear tasks.
         * <p>When {@link #isDaemon} is set to true, this tag setting is invalid, otherwise
         * it can be set as needed.
         *
         * @since 1.0.3
         */
        private boolean ifStopClearTasks = true;

        public boolean isMatchSecond() {
            return isMatchSecond;
        }

        public void setMatchSecond(boolean matchSecond) {
            isMatchSecond = matchSecond;
        }

        public boolean isDaemon() {
            return isDaemon;
        }

        public void setDaemon(boolean daemon) {
            isDaemon = daemon;
        }

        public TimeZone getTimezone() {
            return timezone;
        }

        public void setTimezone(TimeZone timezone) {
            this.timezone = timezone;
        }

        public boolean isIfStopClearTasks() {
            return ifStopClearTasks;
        }

        public void setIfStopClearTasks(boolean ifStopClearTasks) {
            this.ifStopClearTasks = ifStopClearTasks;
        }

        @Override
        public InitializeProperties get() {
            InitializeProperties properties = InitializeProperties.empty();
            properties.setProperty(HutoolCronTaskRepository.PROPERTY_NAME_OF_MATCH_SECOND, isMatchSecond);
            properties.setProperty(HutoolCronTaskRepository.PROPERTY_NAME_OF_DAEMON, isDaemon);
            properties.setProperty(HutoolCronTaskRepository.PROPERTY_NAME_OF_TIMEZONE, timezone.getID());
            properties.setProperty(HutoolCronTaskRepository.PROPERTY_NAME_OF_IF_STOP_CLEAR_TASK, ifStopClearTasks);
            return properties;
        }
    }

    /**
     * Quartz client properties.
     */
    public static class Quartz implements Supplier<InitializeProperties> {

        /**
         * Additional Quartz Scheduler properties.
         *
         * @see org.quartz.impl.StdSchedulerFactory
         * @see org.quartz.impl.DirectSchedulerFactory
         * @see top.osjf.cron.quartz.repository.QuartzCronTaskRepository#PROP_NAME_OF_IF_STOP_WAIT_JOB_COMPLETE
         */
        private final Map<String, String> properties = new HashMap<>();

        public Map<String, String> getProperties() {
            return properties;
        }

        /**
         * Non Java doc.
         *
         * @since 1.0.3
         */
        @Override
        public InitializeProperties get() {
            return InitializeProperties.copyOf(properties);
        }
    }

    /**
     * Cron4j client properties.
     */
    public static class Cron4j implements Supplier<InitializeProperties> {

        /**
         * The daemon flag. If true the scheduler and its spawned threads acts like
         * daemons.
         */
        private boolean daemon = false;

        /**
         * The time zone applied by the scheduler.
         */
        private TimeZone timezone = TimeZone.getDefault();

        public boolean isDaemon() {
            return daemon;
        }

        public void setDaemon(boolean daemon) {
            this.daemon = daemon;
        }

        public TimeZone getTimezone() {
            return timezone;
        }

        public void setTimezone(TimeZone timezone) {
            this.timezone = timezone;
        }

        @Override
        public InitializeProperties get() {
            InitializeProperties properties = InitializeProperties.empty();
            properties.setProperty(Cron4jCronTaskRepository.PROPERTY_NAME_OF_TIMEZONE, timezone.getID());
            properties.setProperty(Cron4jCronTaskRepository.PROPERTY_NAME_OF_DAEMON, daemon);
            return properties;
        }
    }

    /**
     * Properties related to simple cron client.
     * @since 1.0.4
     */
    public static class Simple {

        /**
         * Maximum allowed number of threads.
         */
        private int poolCoreSize = 6;

        /**
         * Select the supported types for cron expression parsing.
         */
        private CronType cronType = CronType.QUARTZ;

        /**
         * When closing {@link java.util.concurrent.ScheduledExecutorService},
         * do you wait in the pool for the task to complete.
         */
        private boolean awaitTermination = true;

        /**
         * The delay time waiting for the completion of tasks in the pool.
         */
        private long awaitTerminationTimeout = 10;

        /**
         * The delay time unit for waiting for the completion of tasks in the pool.
         */
        private TimeUnit awaitTerminationTimeoutUnit = TimeUnit.SECONDS;

        public int getPoolCoreSize() {
            return poolCoreSize;
        }

        public void setPoolCoreSize(int poolCoreSize) {
            this.poolCoreSize = poolCoreSize;
        }

        public CronType getCronType() {
            return cronType;
        }

        public void setCronType(CronType cronType) {
            this.cronType = cronType;
        }

        public boolean isAwaitTermination() {
            return awaitTermination;
        }

        public void setAwaitTermination(boolean awaitTermination) {
            this.awaitTermination = awaitTermination;
        }

        public long getAwaitTerminationTimeout() {
            return awaitTerminationTimeout;
        }

        public void setAwaitTerminationTimeout(long awaitTerminationTimeout) {
            this.awaitTerminationTimeout = awaitTerminationTimeout;
        }

        public TimeUnit getAwaitTerminationTimeoutUnit() {
            return awaitTerminationTimeoutUnit;
        }

        public void setAwaitTerminationTimeoutUnit(TimeUnit awaitTerminationTimeoutUnit) {
            this.awaitTerminationTimeoutUnit = awaitTerminationTimeoutUnit;
        }
    }

    /**
     * Authentication configuration for relevant open interfaces.
     * @see top.osjf.cron.spring.auth.WebRequestAuthenticationInterceptor
     * @since 3.0.1
     */
    public static class WebRequestAuthentication {

        /**
         * Whether to enable authentication of exposed interfaces.
         */
        private boolean enable = false;

        /**
         * Provide authentication verification tokens.
         */
        private String token;

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    /**
     * Copy from {@link org.springframework.boot.autoconfigure.task.TaskExecutionProperties} and make slight modifications.
     * <p>Support monitoring thread pool configuration object for {@link RunTimeoutRegistrarRepository} API.
     * @since 3.0.1
     */
    public static class RunTimeoutMonitoring implements Supplier<InitializeProperties> {
        private final Pool pool = new Pool();
        private final Shutdown shutdown = new Shutdown();

        /**
         * The thread name prefix.
         */
        private String threadNamePrefix = "Monitoring-task-";

        public Pool getPool() {
            return this.pool;
        }

        public Shutdown getShutdown() {
            return this.shutdown;
        }

        public String getThreadNamePrefix() {
            return this.threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }

        @Override
        public InitializeProperties get() {
            InitializeProperties properties = InitializeProperties.empty();
            properties.setProperty(PROPERTY_OF_CORE_SIZE, pool.coreSize);
            properties.setProperty(PROPERTY_OF_MAX_SIZE, pool.maxSize);
            properties.setProperty(PROPERTY_OF_KEEP_ALIVE, pool.keepAlive);
            properties.setProperty(PROPERTY_OF_KEEP_ALIVE_UNIT, pool.keepAliveUnit);
            properties.setProperty(PROPERTY_OF_QUEUE_CAPACITY, pool.queueCapacity);
            properties.setProperty(PROPERTY_OF_THREAD_NAME_PREFIX, threadNamePrefix);
            properties.setProperty(PROPERTY_OF_ALLOW_CORE_THREAD_TIMEOUT, pool.allowCoreThreadTimeout);
            properties.setProperty(PROPERTY_OF_AWAIT_TERMINATION, shutdown.awaitTermination);
            properties.setProperty(PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT, shutdown.awaitTerminationTimeout);
            properties.setProperty(PROPERTY_OF_AWAIT_TERMINATION_TIMEOUT_UNIT, shutdown.awaitTerminationTimeoutUnit);
            properties.setProperty(PROPERTY_OF_REJECT_RETRY_TIMEOUT, pool.rejectRetryTimeout);
            properties.setProperty(PROPERTY_OF_REJECT_RETRY_TIMEOUT_UNIT, pool.rejectRetryTimeoutUnit);
            return properties;
        }

        /**
         * Configure the behavior of thread pool when closed.
         * @since 3.0.1
         */
        public static class Shutdown {

            /**
             * This {@code Boolean} value is used to control whether to wait for the tasks
             * in the thread pool to complete before completing the shutdown operation.
             */
            private boolean awaitTermination;

            /**
             * Specify the maximum time (numerical part) to wait for the thread pool to
             * terminate. This value only takes effect when awaitTermination is {@code true},
             * default value: 10 (representing 10 time units).
             */
            private long awaitTerminationTimeout = 10L;

            /**
             * Used in conjunction with awaitTerminationTimeout, specify the time unit.
             * Default value: {@code TimeUnit.SECONDS} → indicates that the above 10
             * is 10 seconds.
             */
            private TimeUnit awaitTerminationTimeoutUnit = TimeUnit.SECONDS;

            public boolean isAwaitTermination() {
                return this.awaitTermination;
            }

            public void setAwaitTermination(boolean awaitTermination) {
                this.awaitTermination = awaitTermination;
            }

            public long getAwaitTerminationTimeout() {
                return awaitTerminationTimeout;
            }

            public void setAwaitTerminationTimeout(long awaitTerminationTimeout) {
                this.awaitTerminationTimeout = awaitTerminationTimeout;
            }

            public TimeUnit getAwaitTerminationTimeoutUnit() {
                return awaitTerminationTimeoutUnit;
            }

            public void setAwaitTerminationTimeoutUnit(TimeUnit awaitTerminationTimeoutUnit) {
                this.awaitTerminationTimeoutUnit = awaitTerminationTimeoutUnit;
            }
        }

        /**
         * @since 3.0.1
         */
        public static class Pool {
            /**
             * Specify the maximum capacity of the task queue used by the thread pool.
             */
            private int queueCapacity = 1000;

            /**
             * Set the core thread count for the thread pool.
             * <p>
             * The default value is the number of CPU cores in the current system.
             */
            private int coreSize = Runtime.getRuntime().availableProcessors();

            /**
             * Set the maximum pool size for threads allowed in the thread pool.
             * <p>
             * The default value is the number of CPU cores plus 1
             */
            private int maxSize = Runtime.getRuntime().availableProcessors() + 1;

            /**
             * Allow core threads to time out and exit when idle.
             */
            private boolean allowCoreThreadTimeout = true;

            /**
             * The idle survival time values of non-core threads and (when {@link #allowCoreThreadTimeout} =
             * {@code true}) core threads.
             */
            private long keepAlive = 60L;

            /**
             * Used in conjunction with {@link #keepAlive}, specify its time unit.
             */
            private TimeUnit keepAliveUnit = TimeUnit.SECONDS;

            /**
             * The total waiting time for attempting to resubmit a task after it has been rejected.
             */
            private long rejectRetryTimeout = 10L;

            /**
             * Specify the time unit for {@link #rejectRetryTimeout}.
             */
            private TimeUnit rejectRetryTimeoutUnit = TimeUnit.SECONDS;

            public int getQueueCapacity() {
                return this.queueCapacity;
            }

            public void setQueueCapacity(int queueCapacity) {
                this.queueCapacity = queueCapacity;
            }

            public int getCoreSize() {
                return this.coreSize;
            }

            public void setCoreSize(int coreSize) {
                this.coreSize = coreSize;
            }

            public int getMaxSize() {
                return this.maxSize;
            }

            public void setMaxSize(int maxSize) {
                this.maxSize = maxSize;
            }

            public boolean isAllowCoreThreadTimeout() {
                return this.allowCoreThreadTimeout;
            }

            public void setAllowCoreThreadTimeout(boolean allowCoreThreadTimeout) {
                this.allowCoreThreadTimeout = allowCoreThreadTimeout;
            }

            public long getKeepAlive() {
                return keepAlive;
            }

            public void setKeepAlive(long keepAlive) {
                this.keepAlive = keepAlive;
            }

            public TimeUnit getKeepAliveUnit() {
                return keepAliveUnit;
            }

            public void setKeepAliveUnit(TimeUnit keepAliveUnit) {
                this.keepAliveUnit = keepAliveUnit;
            }

            public long getRejectRetryTimeout() {
                return rejectRetryTimeout;
            }

            public void setRejectRetryTimeout(long rejectRetryTimeout) {
                this.rejectRetryTimeout = rejectRetryTimeout;
            }

            public TimeUnit getRejectRetryTimeoutUnit() {
                return rejectRetryTimeoutUnit;
            }

            public void setRejectRetryTimeoutUnit(TimeUnit rejectRetryTimeoutUnit) {
                this.rejectRetryTimeoutUnit = rejectRetryTimeoutUnit;
            }
        }
    }
}
