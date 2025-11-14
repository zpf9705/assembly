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

import top.osjf.cron.core.lang.NotNull;

import java.io.IOException;
import java.util.List;

/**
 * A text-format Nacos configuration task element serializer based on delegation.
 *
 * <p>This class does not implement standalone serialization logic; instead, it delegates all
 * actual serialization and deserialization tasks to another concrete implementation of
 * {@link NacosConfigTaskElementSerializer}. Since all configuration formats (e.g., YAML, JSON)
 * are fundamentally stored as plain text in Nacos, this serializer serves as a generic wrapper
 * for treating structured content under the {@link ConfigFormat#TEXT} type.</p>
 *
 * <p>The core design principle is: avoid defining custom formats, and instead reuse existing
 * format implementations (like YAML), simply declaring the format as TEXT at the interface level.</p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultTextNacosConfigTaskElementSerializer implements NacosConfigTaskElementSerializer {


    private final NacosConfigTaskElementSerializer serializer;

    /**
     * Constructs a new instance with the default delegate serializer.
     *
     * <p>By default, uses {@link DefaultYamlNacosConfigTaskElementSerializer} as the underlying
     * implementation, meaning that although the format is declared as TEXT, the actual content
     * is encoded in YAML.</p>
     */
    public DefaultTextNacosConfigTaskElementSerializer() {
        serializer = new DefaultYamlNacosConfigTaskElementSerializer();
    }

    /**
     * Constructs a new instance with a specified delegate serializer.
     *
     * @param serializer The actual serializer responsible for serialization/deserialization; must not be null.
     *                   Can be used to inject implementations such as JSON, YAML, or custom formats.
     */
    public DefaultTextNacosConfigTaskElementSerializer(NacosConfigTaskElementSerializer serializer) {
        this.serializer = serializer;
    }

    @Override
    public String serialize(@NotNull List<NacosConfigTaskElement> elements) throws IOException {
        return serializer.serialize(elements);
    }

    @Override
    public List<NacosConfigTaskElement> deserialize(@NotNull String configInfo) throws IOException {
        return serializer.deserialize(configInfo);
    }

    @Override
    public ConfigFormat getConfigFormat() {
        return ConfigFormat.TEXT;
    }
}
