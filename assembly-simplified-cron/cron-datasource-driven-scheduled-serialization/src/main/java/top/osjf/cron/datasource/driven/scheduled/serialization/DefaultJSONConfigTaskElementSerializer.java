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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import top.osjf.cron.core.lang.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * JSON-formatted serializer for scheduled task elements.
 *
 * <p>This implementation uses Jackson's {@link ObjectMapper} to perform bidirectional conversion
 * between a list of task elements and JSON strings. It supports serialization of complex object
 * structures and is suitable for persisting or transmitting scheduling metadata across various
 * data sources, such as configuration centers, databases, or remote APIs.
 *
 * <p>During serialization, the entire {@link List} is converted into a standard JSON array.
 * During deserialization, {@link TypeReference} is used to preserve generic type information,
 * preventing runtime casting errors due to Java type erasure. Users can inject a customized
 * {@link ObjectMapper} (e.g., with specific features enabled/disabled) via constructor to meet
 * different operational requirements.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultJSONConfigTaskElementSerializer extends NullableResolvedConfigTaskElementSerializer {

    private final ObjectMapper objectMapper;

    /**
     * Constructs a new {@code DefaultJSONConfigTaskElementSerializer} with a default-configured
     * {@code ObjectMapper}.
     */
    public DefaultJSONConfigTaskElementSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper = objectMapper;
    }

    /**
     * Constructs a new {@code DefaultJSONConfigTaskElementSerializer} with the given ObjectMapper.
     *
     * <p>Allows the caller to pass in a pre-configured ObjectMapper (e.g., with registered modules,
     * disabled failure-handling features), enabling more flexible JSON processing behavior.
     *
     * @param objectMapper the provided ObjectMapper instance, must not be {@literal null}.
     *
     */
    public DefaultJSONConfigTaskElementSerializer(@NotNull ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serializeInternal(@NotNull List<ConfigurableTaskElement> elements) throws IOException {
        return objectMapper.writeValueAsString(elements);
    }

    @Override
    public List<ConfigurableTaskElement> deserializeInternal(@NotNull String configInfo) throws IOException {
        return objectMapper.readValue(configInfo, new TypeReference<List<ConfigurableTaskElement>>() { });
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.JSON;
    }
}
