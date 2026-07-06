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


package top.osjf.spring.autoconfigure.cron.datasouce.driven.scheduled;

import org.springframework.boot.context.properties.ConfigurationProperties;
import top.osjf.cron.datasource.driven.scheduled.Constants;
import top.osjf.cron.spring.datasource.driven.scheduled.DataSource;
import top.osjf.cron.spring.datasource.driven.scheduled.ScheduledDrivenPropertyKey;
import top.osjf.cron.spring.datasource.driven.scheduled.SubstituteConfigFormat;

/**
 * Cron datasource driven properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@ConfigurationProperties(prefix = ScheduledDrivenPropertyKey.PREFIX)
public class CronDatasourceDrivenProperties {

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
     */
    private String loggerName;

    /**
     * The millisecond of polling interval of the task monitor thread.
     */
    private Long monitorCheckInternal = Constants.MONITOR_CHECK_INTERNAL;

    /**
     * @see External
     */
    private External external = new External();

    /**
     * @see External
     */
    private NacosConfig nacosConfig = new NacosConfig();

    /**
     * @see Redis
     */
    private Redis redis = new Redis();

    /**
     * @see ConfigLoader
     */
    private ConfigLoader configLoader = new ConfigLoader();

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

    public Long getMonitorCheckInternal() {
        return monitorCheckInternal;
    }

    public void setMonitorCheckInternal(Long monitorCheckInternal) {
        this.monitorCheckInternal = monitorCheckInternal;
    }

    public External getExternal() {
        return external;
    }

    public void setExternal(External external) {
        this.external = external;
    }

    public NacosConfig getNacosConfig() {
        return nacosConfig;
    }

    public void setNacosConfig(NacosConfig nacosConfig) {
        this.nacosConfig = nacosConfig;
    }

    public Redis getRedis() {
        return redis;
    }

    public void setRedis(Redis redis) {
        this.redis = redis;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public void setConfigLoader(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    /**
     * Public configuration items for external configuration driven data sources.
     * <p>
     * This configuration is applicable to:
     * <ul>
     * <li>{@link DataSource#YAML_CONFIG}</li>
     * <li>{@link DataSource#EXCEL_CONFIG}</li>
     * </ul>
     */
    public static class External {

        /**
         * The external base directory path for resolving dynamic configuration files.
         */
        private String baseDir;

        /**
         * The external config file name for resolving dynamic configuration files.
         */
        private String configFileName;

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
    }

    /**
     * Nacos config configuration.
     */
    public static class NacosConfig {

        /**
         * The server address of Nacos, used to connect to the configuration center.
         * Format is host:port, for example: localhost:8848.
         */
        private String serverAddr = "localhost:8848";

        /**
         * Configuration group ID, used for logically grouping and managing configurations.
         * Configurations of the same business module are usually placed under the same groupId.
         */
        private String groupId = "DEFAULT_GROUP";

        /**
         * The data ID of the configuration item, serving as a unique identifier for the configuration.
         * In Nacos, dataId usually corresponds to the specific configuration file name.
         */
        private String dataId;

        private SubstituteConfigFormat configFormat = SubstituteConfigFormat.JSON;

        public String getServerAddr() {
            return serverAddr;
        }

        public void setServerAddr(String serverAddr) {
            this.serverAddr = serverAddr;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getDataId() {
            return dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }

        public SubstituteConfigFormat getConfigFormat() {
            return configFormat;
        }

        public void setConfigFormat(SubstituteConfigFormat configFormat) {
            this.configFormat = configFormat;
        }
    }

    /**
     * Redis config configuration.
     * @see RedisConnectionConfigBuilderCustomizer
     */
    public static class Redis {

        /**
         * The Redis key used to store rule or configuration data. Used for reading and writing
         * remote configuration content. All instances should share the same key to ensure consistency.
         */
        private String ruleKey;

        /**
         * The Redis Pub/Sub channel name used to publish and subscribe to configuration change events.
         * When a configuration update is published, a message is sent to this channel, triggering
         * real-time reloads on other nodes.
         */
        private String channel;

        private SubstituteConfigFormat configFormat = SubstituteConfigFormat.JSON;

        public String getRuleKey() {
            return ruleKey;
        }

        public void setRuleKey(String ruleKey) {
            this.ruleKey = ruleKey;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public SubstituteConfigFormat getConfigFormat() {
            return configFormat;
        }

        public void setConfigFormat(SubstituteConfigFormat configFormat) {
            this.configFormat = configFormat;
        }
    }

    /**
     * Some configurations about
     * {@link top.osjf.cron.datasource.driven.scheduled.DataSourceConfigLoader} loading.
     */
    public static class ConfigLoader {

        /**
         * @see JavaxDatasource
         */
        private JavaxDatasource javaxDatasource = new JavaxDatasource();

        public JavaxDatasource getJavaxDatasource() {
            return javaxDatasource;
        }

        public void setJavaxDatasource(JavaxDatasource javaxDatasource) {
            this.javaxDatasource = javaxDatasource;
        }

        /**
         * Some configurations about
         * {@link top.osjf.cron.datasource.driven.scheduled.JdkDataSourceConfigLoader} loading.
         */
        public static class JavaxDatasource {

            /**
             * Configure properties for
             * {@code top.osjf.cron.datasource.driven.scheduled.JdkDataSourceConfigLoader#queryConfigSql}
             */
            private String queryConfigSql;

            /**
             * Configure properties for
             * {@code top.osjf.cron.datasource.driven.scheduled.JdkDataSourceConfigLoader#configValueColumnName}
             */
            private String configValueColumnName;

            public String getQueryConfigSql() {
                return queryConfigSql;
            }

            public void setQueryConfigSql(String queryConfigSql) {
                this.queryConfigSql = queryConfigSql;
            }

            public String getConfigValueColumnName() {
                return configValueColumnName;
            }

            public void setConfigValueColumnName(String configValueColumnName) {
                this.configValueColumnName = configValueColumnName;
            }
        }
    }
}
