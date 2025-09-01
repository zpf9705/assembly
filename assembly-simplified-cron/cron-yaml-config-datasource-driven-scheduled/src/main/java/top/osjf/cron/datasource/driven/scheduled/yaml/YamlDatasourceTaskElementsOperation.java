/*
 * Copyright 2024-? the original author or authors.
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


package top.osjf.cron.datasource.driven.scheduled.yaml;

import org.yaml.snakeyaml.Yaml;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.external.file.ExternalFileDatasourceTaskElementsOperation;

/**
 * Abstract class for YAML-configured of scheduled task datasource operation.
 *
 * <p>This class implements {@link DatasourceTaskElementsOperation} and provides YAML-based task configuration
 * management.
 * Key features include:
 * <ul>
 *   <li>Loading task configurations from YAML files</li>
 *   <li>Parsing/serializing configurations using SnakeYAML</li>
 *   <li>Managing persistent configuration updates</li>
 *   <li>Providing interfaces for task element cleanup and retrieval</li>
 * </ul>
 *
 * <p>Supports custom YAML parser injection via {@link YamlTaskElementLoader#setYaml(Yaml)}, with SnakeYAML
 * as default.
 * Task configurations are encapsulated by {@link YamlTaskElement}, enabling runtime configuration
 * batch updates.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class YamlDatasourceTaskElementsOperation extends ExternalFileDatasourceTaskElementsOperation<YamlTaskElement> {

    /**
     * Constructs an empty {@code YamlDatasourceTaskElementsOperation} and init
     * an {@link YamlTaskElementLoader} instance.
     */
    public YamlDatasourceTaskElementsOperation() {
        super(new YamlTaskElementLoader());
    }

    /**
     * @return The {@link YamlTaskElement} loader.
     */
    public YamlTaskElementLoader getLoader() {
        return (YamlTaskElementLoader) super.getLoader();
    }
}
