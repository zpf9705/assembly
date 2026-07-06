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


package top.osjf.cron.spring;

import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * Unified interface for storing key constants related to cron scheduled
 * task configuration files.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface CronTaskPropertyKey {

    /**
     * Cron scheduled task configuration global root prefix
     */
    String PREFIX = "spring.schedule.cron";

    /**
     * Switch to enable web task list query interface
     */
    String KEY_WEB_QUERY_TASK_LIST_ENABLE = PREFIX + ".enable-web-query-task-list";

    /**
     * Prefix for web request authentication configuration
     */
    String WEB_AUTH_PREFIX = PREFIX + ".web-request-authentication";

    /**
     * Switch to control whether {@link CronTaskRepository} start automatically on application boot.
     */
    String AUTO_STARTUP = PREFIX + ".auto-startup";

    /**
     * Switch to enable web request authentication
     */
    String KEY_WEB_AUTH_ENABLE = WEB_AUTH_PREFIX + ".enable";

    /**
     * Configuration key for web authentication token
     */
    String KEY_OF_AUTHENTICATION_TOKEN = WEB_AUTH_PREFIX + ".token";


}
