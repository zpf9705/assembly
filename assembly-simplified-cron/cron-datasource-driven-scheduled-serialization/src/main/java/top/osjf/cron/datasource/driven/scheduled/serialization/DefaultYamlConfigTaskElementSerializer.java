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
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import top.osjf.commons.util.Assert;

import java.io.IOException;
import java.util.List;

/**
 * Default YAML serializer/deserializer for {@link ConfigurableTaskElement} objects.
 *
 * <p>This class provides a complete implementation of {@link ConfigTaskElementSerializer} that converts
 * between a list of {@code ConfigurableTaskElement} instances and their YAML representation.
 * It uses the Jackson library's {@code jackson-dataformat-yaml} module to ensure robust, efficient,
 * and standards-compliant YAML processing.</p>
 *
 * <h3>Default ({@link #DefaultYamlConfigTaskElementSerializer() DefaultYamlConfigTaskElementSerializer})
 * Supported YAML Format</h3>
 * <p>The serializer supports and generates YAML in the following format:</p>
 * <pre>
 * - id: "task-001"
 *   taskId: ""
 *   taskName: "health-check-task"
 *   profiles: "dev"
 *   taskDescription: "Performs periodic health checks on services"
 *   status: "ACTIVE"
 *   statusDescription: "Currently running without issues"
 *   expression: "0 0/5 * * * ?"
 *   updateSign: 0
 * - id: "task-002"
 *   taskId: ""
 *   taskName: "log-purge-task"
 *   profiles: "prod"
 *   taskDescription: "Cleans up logs older than 7 days"
 *   status: "INACTIVE"
 *   statusDescription: "Paused until next maintenance window"
 *   expression: "0 0 1 * * ?"
 *   updateSign: 0
 * </pre>
 *
 * <h3>Usage Example</h3>
 * <pre>{@code
 * List<ConfigurableTaskElement> tasks = Arrays.asList(task1, task2);
 * DefaultYamlConfigTaskElementSerializer serializer = new DefaultYamlConfigTaskElementSerializer();
 *
 * // Serialize to YAML
 * String yaml = serializer.serialize(tasks);
 *
 * // Deserialize from YAML
 * List<ConfigurableTaskElement> parsedTasks = serializer.deserialize(yaml);
 * }</pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultYamlConfigTaskElementSerializer extends NullableResolvedConfigTaskElementSerializer {

    private final ObjectMapper objectMapper;

    /**
     * Constructs a new {@code DefaultYamlConfigTaskElementSerializer} to init a {@link ObjectMapper}
     * with any default settings.
     */
    public DefaultYamlConfigTaskElementSerializer() {
        YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        objectMapper = new ObjectMapper(yamlFactory);
    }

    /**
     * Constructs a new {@code DefaultYamlConfigTaskElementSerializer} by given {@link ObjectMapper}.
     * @param objectMapper the given {@link ObjectMapper}.
     */
    public DefaultYamlConfigTaskElementSerializer(ObjectMapper objectMapper) {

        Assert.isTrue(objectMapper.getFactory() instanceof YAMLFactory,
                "objectMapper.getFactory() must instanceof YAMLFactory");

        this.objectMapper = objectMapper;
    }

    @Override
    public String serializeInternal(List<ConfigurableTaskElement> elements) throws IOException {
        return objectMapper.writeValueAsString(elements);
    }

    @Override
    public List<ConfigurableTaskElement> deserializeInternal(String configInfo) throws IOException {
        return objectMapper.readValue(configInfo, new TypeReference<List<ConfigurableTaskElement>>() {});
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.YAML;
    }
}
