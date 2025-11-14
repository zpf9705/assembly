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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String serialize(List<NacosConfigTaskElement> elements) throws IOException {
        List<JSONNacosConfigTaskElement> jElements
                = elements.stream().map(JSONNacosConfigTaskElement::new).collect(Collectors.toList());
        return objectMapper.writeValueAsString(jElements);
    }

    @Override
    public List<NacosConfigTaskElement> deserialize(String configInfo) throws IOException {
        return objectMapper.readValue(configInfo, new TypeReference<List<NacosConfigTaskElement>>() { });
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.JSON;
    }

    static class JSONNacosConfigTaskElement extends NacosConfigTaskElement {

        private static final long serialVersionUID = -826552181140068263L;

        public JSONNacosConfigTaskElement(NacosConfigTaskElement source) {
            setId(source.getId());
            setTaskId(source.getTaskId());
            setTaskName(source.getTaskName());
            setProfiles(source.getProfiles());
            setTaskDescription(source.getTaskDescription());
            setStatus(source.getStatus());
            setStatusDescription(source.getStatusDescription());
            setExpression(source.getExpression());
            setUpdateSign(source.getUpdateSign());
        }

        @Override
        @JsonIgnore
        public boolean isAfterUpdate() {
            return super.isAfterUpdate();
        }

        @Override
        @JsonIgnore
        public boolean isAfterInsert() {
            return super.isAfterInsert();
        }
    }
}
