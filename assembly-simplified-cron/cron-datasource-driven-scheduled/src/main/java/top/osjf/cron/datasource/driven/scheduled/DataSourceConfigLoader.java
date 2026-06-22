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

import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.StringUtils;

/**
 * An interface based on dynamic configuration of data sources.
 *
 * <p>This interface is adapted to obtain dynamic configuration of data sources to meet
 * real-time updates of flexible configurations related to dynamic task management.
 * It mainly relies on {@link DatasourceTaskElementsOperation}'s data source management
 * to meet different configuration needs in the adaptation framework.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface DataSourceConfigLoader {

    /**
     * Retrieve configuration values based on the specified key. If the retrieval is
     * empty, return {@literal null}; otherwise, convert to the expected {@link Class type}
     * and return.
     *
     * @param configKey the config key to obtain.
     * @param type      obtain the type of expected conversion for configuration.
     * @param <T>       the expected type.
     * @return the obtain the configuration and convert the object value to the desired type.
     * @throws Throwable          If an error occurs during the retrieval process.
     * @throws ClassCastException if the object is not null and is not assignable to
     *                            the type {@code T}.
     */
    @Nullable
    default <T> T getConfig(String configKey, Class<T> type) throws Throwable {
        String configValue = getConfig(configKey);
        if (StringUtils.isBlank(configValue)) {
            return null;
        }
        return type.cast(configValue);
    }

    /**
     * Retrieve the configuration value based on the specified key. If the retrieval is empty,
     * return {@literal null}.
     *
     * @param configKey the config key to obtain.
     * @return the {@code String} configuration value based on the specified key.
     * @throws Throwable If an error occurs during the retrieval process.
     */
    @Nullable
    String getConfig(String configKey) throws Throwable;
}
