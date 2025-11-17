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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import top.osjf.cron.core.lang.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * JSON format serializer for Nacos configuration task elements.
 *
 * <p>This implementation uses {@link ObjectMapper} from the Jackson library to serialize
 * a list of configuration task elements into a JSON string, or deserialize a JSON string
 * back into a list of elements. It supports conversion of complex object structures and ensures type safety.
 *
 * <p>During serialization, the entire {@link List} is converted into a standard JSON array.
 * During deserialization, {@link TypeReference} is used to preserve generic type information,
 * preventing conversion errors due to Java's type erasure.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultJSONNacosConfigTaskElementSerializer implements NacosConfigTaskElementSerializer {

    private final ObjectMapper objectMapper;

    /**
     * Constructs a new {@code DefaultJSONNacosConfigTaskElementSerializer} to init a {@link ObjectMapper}
     * with any default settings.
     */
    public DefaultJSONNacosConfigTaskElementSerializer() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Constructs a new {@code DefaultJSONNacosConfigTaskElementSerializer} by given {@link ObjectMapper}.
     * @param objectMapper the given {@link ObjectMapper}.
     */
    public DefaultJSONNacosConfigTaskElementSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public String serialize(@NotNull List<NacosConfigTaskElement> elements) throws IOException {
        return objectMapper.writeValueAsString(elements);
    }

    @Override
    public List<NacosConfigTaskElement> deserialize(@NotNull String configInfo) throws IOException {
        return objectMapper.readValue(configInfo, new TypeReference<List<NacosConfigTaskElement>>() { });
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.JSON;
    }
}
