/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.cron.datasource.driven.scheduled.serialization;

import top.osjf.cron.core.util.AssertUtils;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;

/**
 * Abstract base class for data source operations of scheduled task elements based on configuration format.
 *
 * <p>This class implements {@link ConfigFormatProvider} and {@link DatasourceTaskElementsOperation},
 * providing a common foundation for reading and writing task configurations in various formats
 * (e.g., YAML, JSON, TEXT). Subclasses extend this class to implement concrete data access logic
 * while inheriting format-aware behavior.
 *
 * <p>The key role is to hold and expose a {@link ConfigFormat} instance, indicating the format used
 * by the operation. This enables the system to route or process task data appropriately based on format,
 * supporting multi-format interoperability.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class ConfigFormatDatasourceTaskElementsOperation
        implements ConfigFormatProvider, DatasourceTaskElementsOperation {

    private final ConfigFormat configFormat;

    public ConfigFormatDatasourceTaskElementsOperation(ConfigFormat configFormat) {
        AssertUtils.assertNotNull(configFormat, "configFormat not be null");
        this.configFormat = configFormat;
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return configFormat;
    }
}
