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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.util.AssertUtils;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;

import java.io.IOException;
import java.util.List;

/**
 * Abstract base class for data source operations of scheduled task elements with built-in serialization support.
 * <p>
 * This class implements {@link ConfigFormatProvider}, {@link DatasourceTaskElementsOperation},
 * and {@link ConfigTaskElementSerializer}, providing a common foundation for reading,
 * writing, and transforming task configurations in various formats (e.g., YAML, JSON, TEXT).
 * Subclasses can focus on implementing data source-specific logic (e.g., Nacos, DB) while reusing
 * standardized serialization behavior.
 * </p>
 * <p>
 * Key responsibilities include:
 * <ul>
 *   <li>Holding and exposing an immutable {@link ConfigFormat} instance that indicates the current format.</li>
 *   <li>Providing default implementations of {@code serialize} and {@code deserialize}
 *       methods, delegating to {@link ConfigTaskElementSerializerManager} for actual format processing.</li>
 *   <li>Standardizing exception handling by catching IOExceptions, logging details,
 *       and wrapping them into {@link DataSourceDrivenException} for consistent error propagation.</li>
 * </ul>
 * </p>
 * <p>
 * Typical use cases involve loading task lists from configuration centers, persisting modified tasks,
 * or transferring task configurations across systems. By combining data source operations with
 * format serialization capabilities, this class promotes separation of concerns and improves code reuse.
 * </p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class ConfigFormatDatasourceTaskElementsOperation
        implements ConfigFormatProvider, DatasourceTaskElementsOperation, ConfigTaskElementSerializer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ConfigFormat configFormat;

    public ConfigFormatDatasourceTaskElementsOperation(ConfigFormat configFormat) {
        AssertUtils.assertNotNull(configFormat, "configFormat not be null");
        this.configFormat = configFormat;
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return configFormat;
    }

    /**
     * Serializes a list of configurable task elements into a string in the specified format.
     * <p>
     * Uses {@link ConfigTaskElementSerializerManager} to serialize the given list of task elements
     * according to the current configuration format (e.g., JSON, YAML). If an IOException occurs
     * during serialization, it logs the error and wraps it into a {@link DataSourceDrivenException}
     * for consistent exception handling at higher layers.
     * </p>
     * <p>
     * This method is typically used to convert in-memory task configurations into a storable
     * or transferable string representation, such as writing to a configuration center,
     * database, or file system.
     * </p>
     *
     * @param elements the list of task elements to serialize, must not be {@literal null}
     * @return the serialized string in the configured format
     * @throws DataSourceDrivenException if serialization fails due to I/O error
     */
    public String serialize(@NotNull List<ConfigurableTaskElement> elements) {
        try {
            return ConfigTaskElementSerializerManager.serialize(configFormat, elements);
        }
        catch (IOException ex) {

            logger.error("Failed to deserialize {} using {} format", configFormat, getConfigFormat(), ex);

            throw new DataSourceDrivenException("Failed to deserialize " + configFormat +
                    " using " + getConfigFormat() + " format", ex);
        }
    }

    /**
     * Deserializes a configuration string into a list of configurable task elements.
     * <p>
     * Uses {@link ConfigTaskElementSerializerManager} to parse the input configuration string
     * according to the current format (e.g., JSON, YAML). If an IOException occurs during
     * deserialization, it logs the error and throws a wrapped {@link DataSourceDrivenException}
     * to support uniform data source exception handling.
     * </p>
     * <p>
     * This method is commonly used after reading raw configuration data from external sources
     * (e.g., configuration centers, databases), converting it back into a usable list of
     * task element objects for scheduling or management purposes.
     * </p>
     *
     * @param configInfo the configuration string to deserialize, must not be {@literal null}
     * @return the list of deserialized task elements
     * @throws DataSourceDrivenException if deserialization fails due to I/O error
     */
    public List<ConfigurableTaskElement> deserialize(@NotNull String configInfo) {
        try {
            return ConfigTaskElementSerializerManager.deserialize(getConfigFormat(), configInfo);
        }
        catch (IOException ex) {

            logger.error("Failed to deserialize {} using {} format", configInfo, configFormat, ex);

            throw new DataSourceDrivenException("Failed to deserialize " + configInfo +
                    " using " + configFormat + " format", ex);
        }
    }
}
