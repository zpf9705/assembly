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

import java.io.IOException;
import java.util.List;

/**
 * Generic serializer interface for scheduled task element data, responsible for serializing and
 * deserializing lists of task elements to and from string representation.
 *
 * <p>This interface extends {@code ConfigFormatProvider}, indicating that each implementation
 * corresponds to a specific data format (e.g., JSON, XML), and can be used to persist or transmit
 * scheduling configuration across various data sources.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ConfigTaskElementSerializer extends ConfigFormatProvider {

    /**
     * Serializes a list of configurable task elements into a formatted string.
     *
     * <p>The resulting string should conform to the data format implemented by this
     * serializer (e.g., JSON array, YAML list). The input list must not be null, but
     * may be empty.
     *
     * @param elements the list of task elements to serialize, must not be {@literal null}.
     * @return the serialized string representation, not {@literal null}.
     * @throws IOException if an I/O error occurs during serialization
     *
     */
    String serialize(List<ConfigurableTaskElement> elements) throws IOException;

    /**
     * Deserializes a string into a list of configurable task elements.
     *
     * <p>The input string must conform to the format supported by this implementation
     * (e.g., valid JSON).The returned list must not be null; return an empty list instead
     * if no valid data is present.
     *
     * @param configInfo the string content to deserialize, must not be {@literal null} or empty.
     * @return the deserialized list of task elements, not {@literal null}.
     * @throws IOException if an I/O error occurs during deserialization.
     *
     */
    List<ConfigurableTaskElement> deserialize(String configInfo) throws IOException;
}
