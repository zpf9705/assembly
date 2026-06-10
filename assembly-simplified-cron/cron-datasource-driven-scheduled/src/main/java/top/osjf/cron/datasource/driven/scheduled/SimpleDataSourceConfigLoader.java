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


package top.osjf.cron.datasource.driven.scheduled;

import top.osjf.cron.core.lang.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple implementation of {@link DataSourceConfigLoader}, using thread-safe {@link ConcurrentHashMap}
 * to cache key-value data source configuration items in memory.
 *
 * <p>Supports manual filling of configuration via {@link #setConfig(String, String)} and reading
 * configuration values through the standard {@link #getConfig(String)} interface method.
 * Suitable for lightweight memory data source configuration storage scenarios.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class SimpleDataSourceConfigLoader implements DataSourceConfigLoader {

    private final Map<String, String> configMap = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public String getConfig(String configKey) {
        return configMap.get(configKey);
    }

    /**
     * Manually write or overwrite a single data source configuration item to memory cache.
     *
     * @param configKey   Non-null unique identifier of the configuration item
     * @param configValue Non-null string value corresponding to the configuration key
     * @throws NullPointerException if configKey or configValue is null
     */
    public void setConfig(String configKey, String configValue) {
        configMap.put(configKey, configValue);
    }
}
