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
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ScheduledDrivenPropertyKey {

    String PREFIX = CronTaskPropertyKey.PREFIX + ".scheduled-driven";

    String PREFIX_EXTERNAL = PREFIX + ".external";

    String KEY_BASE_DIR = PREFIX_EXTERNAL + ".base-dir";

    String KEY_CONFIG_FILE_NAME = PREFIX_EXTERNAL + ".config-file-name";

    String PREFIX_NACOS = PREFIX + ".nacos-config";

    String KEY_NACOS_SERVER_ADDR = PREFIX_NACOS + ".server-addr";

    String KEY_NACOS_GROUP_ID = PREFIX_NACOS + ".group-id";

    String KEY_NACOS_DATA_ID = PREFIX_NACOS + ".data-id";

    String KEY_NACOS_CONFIG_FORMAT = PREFIX_NACOS + ".config-format";

    String PREFIX_REDIS = "spring.schedule.cron.scheduled-driven.redis";

    String KEY_RULE_KEY = PREFIX_REDIS + ".rule-key";

    String KEY_CHANNEL = PREFIX_REDIS + ".channel";

    String KEY_CONFIG_FORMAT = PREFIX_REDIS + ".config-format";

    String KEY_LOGGER_NAME = PREFIX + ".logger-name";

   String KEY_MONITOR_CHECK_INTERNAL = PREFIX + ".monitor-check-internal";
}
