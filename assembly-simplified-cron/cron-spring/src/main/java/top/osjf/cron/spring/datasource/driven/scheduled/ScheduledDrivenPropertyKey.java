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


package top.osjf.cron.spring.datasource.driven.scheduled;

import top.osjf.cron.spring.CronTaskPropertyKey;

/**
 * Constant keys for scheduled driven configuration properties
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ScheduledDrivenPropertyKey {

    /**
     * Root prefix of all scheduled driven configurations
     */
    String PREFIX = CronTaskPropertyKey.PREFIX + ".scheduled-driven";

    /**
     * Prefix for external file data source configurations
     */
    String PREFIX_EXTERNAL = PREFIX + ".external";

    /**
     * Base directory path of external configuration files
     */
    String KEY_BASE_DIR = PREFIX_EXTERNAL + ".base-dir";

    /**
     * Filename of external configuration file
     */
    String KEY_CONFIG_FILE_NAME = PREFIX_EXTERNAL + ".config-file-name";

    /**
     * Prefix for Nacos config data source configurations
     */
    String PREFIX_NACOS = PREFIX + ".nacos-config";

    /**
     * Nacos server address
     */
    String KEY_NACOS_SERVER_ADDR = PREFIX_NACOS + ".server-addr";

    /**
     * Nacos configuration group id
     */
    String KEY_NACOS_GROUP_ID = PREFIX_NACOS + ".group-id";

    /**
     * Nacos configuration data id
     */
    String KEY_NACOS_DATA_ID = PREFIX_NACOS + ".data-id";

    /**
     * Format of Nacos configuration content
     */
    String KEY_NACOS_CONFIG_FORMAT = PREFIX_NACOS + ".config-format";

    /**
     * Prefix for Redis data source configurations
     */
    String PREFIX_REDIS = PREFIX + ".redis";

    /**
     * Redis rule storage key
     */
    String KEY_RULE_KEY = PREFIX_REDIS + ".rule-key";

    /**
     * Redis publish/subscribe channel name
     */
    String KEY_CHANNEL = PREFIX_REDIS + ".channel";

    /**
     * Format of Redis stored configuration content
     */
    String KEY_CONFIG_FORMAT = PREFIX_REDIS + ".config-format";

    /**
     * Custom logger name for scheduled driven module
     */
    String KEY_LOGGER_NAME = PREFIX + ".logger-name";

    /**
     * Interval for task monitor inspection
     */
   String KEY_MONITOR_CHECK_INTERNAL = PREFIX + ".monitor-check-internal";
}
