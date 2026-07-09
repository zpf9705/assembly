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
import top.osjf.commons.util.Assert;

/**
 * Common utility for datasource driven scheduler.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class Utils {

    /**
     * Safely load configuration value via {@link DataSourceConfigLoader}.
     * If any throwable occurs during loading or the config value is null,
     * the provided default value will be returned instead of throwing an exception.
     *
     * @param loader       Config loader instance, cannot be {@literal null}
     * @param configKey    Target configuration unique key, cannot be blank
     * @param type         Target value conversion type, cannot be {@literal null}
     * @param defaultValue Fallback value returned when config missing or load failed
     * @return Parsed config value or specified default fallback value
     * @param <T> Generic type of target configuration value
     */
    @Nullable
    public static <T> T getConfigSafe(DataSourceConfigLoader loader,
                                      String configKey,
                                      Class<T> type,
                                      @Nullable T defaultValue) {
        Assert.notNull(loader, "DataSourceConfigLoader cannot be null");
        Assert.hasText(configKey, "configKey cannot be null or blank");
        Assert.notNull(type, "Target config value type cannot be null");

        try {
            T configValue = loader.getConfig(configKey, type);
            // Config exists but value is null, use default
            return configValue != null ? configValue : defaultValue;
        } catch (Throwable e) {
            // Any loading error, fallback to default value
            return defaultValue;
        }
    }
}
