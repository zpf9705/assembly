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

/**
 * This interface serves as an extension interface for {@link DataSourceConfigLoader}, enabling
 * configurable related capabilities.
 *
 * <p>This interface extends {@link DataSourceConfigLoader} to provide dynamic configuration
 * management capabilities. It allows runtime modification of data source configuration properties
 * without requiring application restart.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ConfigurableDataSourceConfigLoader extends DataSourceConfigLoader {

    /**
     * Sets a configuration property with the specified key and value.
     *
     * <p>This method allows dynamic configuration of data source properties at runtime.
     * The configuration key should follow standard naming conventions (e.g., "database.url",
     * "connection.timeout", etc.) and the value should be in appropriate format for the
     * corresponding configuration property.</p>
     *
     * <strong>Note:</strong> Configuration changes may take effect immediately or require
     * subsequent data source reinitialization depending on the implementation.
     *
     * @param configKey the configuration property key.
     * @param configValue the configuration property value.
     */
    void setConfig(String configKey, String configValue);
}
