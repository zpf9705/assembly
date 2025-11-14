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


package top.osjf.cron.datasource.driven.scheduled.nacosconfig;

import java.io.IOException;
import java.util.List;

/**
 * Nacos configuration task element serializer interface, responsible for serializing and
 * deserializing lists of configuration task elements to and from string representation.
 * This interface extends {@code ConfigFormatProvider}, indicating that each serialization
 * implementation corresponds to a specific configuration format.
 *
 * <p>Implementations must provide logic for serialization and deserialization based on a
 * specific format (e.g., JSON, XML).
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface NacosConfigTaskElementSerializer extends ConfigFormatProvider {

    /**
     * Serializes the list of Nacos configuration task elements into a string.
     *
     * @param elements the list of configuration task elements to serialize, must not be {@literal null}.
     * @return the serialized string representation, not {@literal null}.
     * @throws IOException if io error occur when serialized.
     */
    String serialize(List<NacosConfigTaskElement> elements) throws IOException;

    /**
     * Deserializes a string into a list of Nacos configuration task elements.
     *
     * @param configInfo the string content to deserialize, must not be {@literal null} or empty.
     * @return the deserialized list of configuration task elements, not {@literal null}.
     * @throws IOException if io error occur when deserialized.
     */
    List<NacosConfigTaskElement> deserialize(String configInfo) throws IOException;
}
