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
 * Text-format scheduled task element serializer based on delegation.
 *
 * <p>This class does not implement standalone serialization logic; instead, it delegates all actual
 * serialization and deserialization tasks to a concrete instance of
 * {@link ConfigTaskElementSerializer}. Since most configuration storage systems (e.g., Nacos,
 * Apollo, databases) store content as plain text, this serializer acts as a generic wrapper that
 * treats structured data (like YAML or JSON) as plain text at the interface level.
 *
 * <p>The core design principle is: avoid inventing custom formats. Reuse existing format implementations
 * (such as YAML or JSON), while declaring the format as TEXT for interoperability. This is useful in
 * scenarios where the system only requires "text-based" configuration without enforcing strict format types.
 *
 * <p>By default, it uses YAML as the underlying format — meaning the actual content remains valid YAML,
 * even though the reported format is TEXT. Users can inject alternative serializers (e.g., JSON)
 * via constructor to customize behavior at runtime.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultTextConfigTaskElementSerializer implements ConfigTaskElementSerializer {


    private final ConfigTaskElementSerializer serializer;

    /**
     * Constructs a new instance with the default delegate serializer.
     *
     * <p>Uses {@link DefaultYamlConfigTaskElementSerializer} as the underlying implementation,
     * meaning that although the format is declared as TEXT, the actual content is encoded in YAML.
     */
    public DefaultTextConfigTaskElementSerializer() {
        serializer = new DefaultYamlConfigTaskElementSerializer();
    }

    /**
     *
     * Constructs a new instance with a specified delegate serializer.
     *
     * @param serializer The actual serializer responsible for serialization/deserialization; must not be null.
     *                   Can be used to inject implementations such as JSON, YAML, or custom formats.
     */
    public DefaultTextConfigTaskElementSerializer(ConfigTaskElementSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public String serialize(List<ConfigurableTaskElement> elements) throws IOException {
        return serializer.serialize(elements);
    }

    @Override
    public List<ConfigurableTaskElement> deserialize(String configInfo) throws IOException {
        return serializer.deserialize(configInfo);
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.TEXT;
    }
}
