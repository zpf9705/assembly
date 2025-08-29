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
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.cron4j.repository.Cron4jCronTaskRepository;
import top.osjf.cron.datasource.driven.scheduled.Constants;
import top.osjf.cron.hutool.repository.HutoolCronTaskRepository;
import top.osjf.cron.spring.datasource.driven.scheduled.DataSource;
import top.osjf.cron.spring.datasource.driven.scheduled.SpringDatasourceDrivenScheduled;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Cron schedule properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
@ConfigurationProperties(prefix = "spring.schedule.cron")
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

    /**
     * @since 1.0.4
     */
    private ScheduledDriven scheduledDriven = new ScheduledDriven();

    public ClientType getClientType() {
        return clientType;
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

    public ScheduledDriven getScheduledDriven() {
        return scheduledDriven;
    }

    public void setScheduledDriven(ScheduledDriven scheduledDriven) {
        this.scheduledDriven = scheduledDriven;
    }

    /**
     * Hutool client properties.
     */
    public static class Hutool implements Supplier<SuperiorProperties> {

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
        public SuperiorProperties get() {
            SuperiorProperties properties = SuperiorProperties.of();
            properties.addProperty(HutoolCronTaskRepository.PROPERTY_NAME_OF_MATCH_SECOND, isMatchSecond);
            properties.addProperty(HutoolCronTaskRepository.PROPERTY_NAME_OF_DAEMON, isDaemon);
            properties.addProperty(HutoolCronTaskRepository.PROPERTY_NAME_OF_TIMEZONE, timezone);
            properties.addProperty(HutoolCronTaskRepository.PROPERTY_NAME_OF_IF_STOP_CLEAR_TASK, ifStopClearTasks);
            return properties;
        }
    }

    /**
     * Quartz client properties.
     */
    public static class Quartz implements Supplier<SuperiorProperties> {

        /**
         * Additional Quartz Scheduler properties.
         *
         * @see org.quartz.impl.StdSchedulerFactory
         * @see org.quartz.impl.DirectSchedulerFactory
         * @see top.osjf.cron.quartz.repository.QuartzCronTaskRepository#PROP_NAME_OF_FACTORY_CLASS
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
        public SuperiorProperties get() {
            SuperiorProperties superiorProperties = SuperiorProperties.of();
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                superiorProperties.addProperty(entry.getKey(), entry.getValue());
            }
            return superiorProperties;
        }
    }

    /**
     * Cron4j client properties.
     */
    public static class Cron4j implements Supplier<SuperiorProperties> {

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
        public SuperiorProperties get() {
            SuperiorProperties properties = SuperiorProperties.of();
            properties.addProperty(Cron4jCronTaskRepository.PROPERTY_NAME_OF_TIMEZONE, timezone);
            properties.addProperty(Cron4jCronTaskRepository.PROPERTY_NAME_OF_DAEMON, daemon);
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
     * Properties related to dynamic task management.
     * @since 1.0.4
     */
    public static class ScheduledDriven {

        /**
         * Whether to enable dynamic data source configuration management scheduled tasks.
         */
        private boolean enable = false;

        /**
         * The datasource-driven matched profiles.
         * @see org.springframework.core.env.Profiles
         */
        private String activeProfilesMatched;

        /**
         * Select the data source type for dynamically enabling data source configuration.
         *
         * <p>The configuration takes effect when {@link #enable} is {@code true}.
         */
        private DataSource dataSource;

        /**
         * The name of the tool for printing logs related to dynamic data source driven
         * task scheduling (for example {@link org.slf4j.Logger}).
         * {@code SpringDatasourceDrivenScheduled#getLogger()}
         * @since 3.0.1
         */
        private String loggerName;

        /**
         * Specify the main task ID array when scheduling tasks driven by dynamic data sources.
         * @see SpringDatasourceDrivenScheduled#getManagerTaskUniqueIdentifiers()
         * @since 3.0.1
         */
        private String[] mainTaskUniqueIds;

        /**
         * The definition of this cron expression is used to determine the execution frequency
         * of using the default main task management when no relevant information about the
         * main task is provided.
         * {@code SpringDatasourceDrivenScheduled#getManagerTaskCheckFrequencyCronExpress()}
         * @since 3.0.1
         */
        private String defaultMainTaskExpress = Constants.MANAGER_TASK_CHECK_FREQUENCY_CRON;

        /**
         * @see Yaml
         */
        private Yaml yaml = new Yaml();

        /**
         * @see WebRequestAuthentication
         */
        private WebRequestAuthentication webRequestAuthentication = new WebRequestAuthentication();

        public boolean isEnable() {
            return enable;
        }

        public void setEnable(boolean enable) {
            this.enable = enable;
        }

        public String getActiveProfilesMatched() {
            return activeProfilesMatched;
        }

        public void setActiveProfilesMatched(String activeProfilesMatched) {
            this.activeProfilesMatched = activeProfilesMatched;
        }

        public DataSource getDataSource() {
            return dataSource;
        }

        public void setDataSource(DataSource dataSource) {
            this.dataSource = dataSource;
        }

        public String getLoggerName() {
            return loggerName;
        }

        public void setLoggerName(String loggerName) {
            this.loggerName = loggerName;
        }

        public String[] getMainTaskUniqueIds() {
            return mainTaskUniqueIds;
        }

        public void setMainTaskUniqueIds(String[] mainTaskUniqueIds) {
            this.mainTaskUniqueIds = mainTaskUniqueIds;
        }

        public String getDefaultMainTaskExpress() {
            return defaultMainTaskExpress;
        }

        public void setDefaultMainTaskExpress(String defaultMainTaskExpress) {
            this.defaultMainTaskExpress = defaultMainTaskExpress;
        }

        public Yaml getYaml() {
            return yaml;
        }

        public void setYaml(Yaml yaml) {
            this.yaml = yaml;
        }

        public WebRequestAuthentication getWebRequestAuthentication() {
            return webRequestAuthentication;
        }

        public void setWebRequestAuthentication(WebRequestAuthentication webRequestAuthentication) {
            this.webRequestAuthentication = webRequestAuthentication;
        }

        /**
         * Yaml's data source dynamically drives task scheduling configuration.
         * @since 3.0.1
         */
        public static class Yaml {

            /**
             * The base directory path for resolving dynamic configuration files.
             * @see top.osjf.cron.datasource.driven.scheduled.yaml.YamlTaskElementLoader#setBaseDir
             */
            private String baseDir;

            /**
             * The yml config file name for resolving dynamic configuration files.
             * @see top.osjf.cron.datasource.driven.scheduled.yaml.YamlTaskElementLoader#setConfigYamlFileName
             */
            private String configFileName = "task-config.yml";

            /**
             * The number of milliseconds cached since the last modification interval.
             * @see top.osjf.cron.datasource.driven.scheduled.yaml.YamlTaskElementLoader#setIntervalMillAfterModified
             */
            private long intervalMillAfterModified = 2000;

            public String getBaseDir() {
                return baseDir;
            }

            public void setBaseDir(String baseDir) {
                this.baseDir = baseDir;
            }

            public String getConfigFileName() {
                return configFileName;
            }

            public void setConfigFileName(String configFileName) {
                this.configFileName = configFileName;
            }

            public long getIntervalMillAfterModified() {
                return intervalMillAfterModified;
            }

            public void setIntervalMillAfterModified(long intervalMillAfterModified) {
                this.intervalMillAfterModified = intervalMillAfterModified;
            }
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
}
