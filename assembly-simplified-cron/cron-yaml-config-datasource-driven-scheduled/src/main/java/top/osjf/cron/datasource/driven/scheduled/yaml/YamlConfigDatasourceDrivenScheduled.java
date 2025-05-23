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
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.datasource.driven.scheduled.AbstractDatasourceDrivenScheduled;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

/**
 * Abstract class for YAML-configured datasource-driven scheduled tasks.
 *
 * <p>This class extends {@link AbstractDatasourceDrivenScheduled} and provides YAML-based task configuration
 * management.
 * Key features include:
 * <ul>
 *   <li>Loading task configurations from YAML files</li>
 *   <li>Parsing/serializing configurations using SnakeYAML</li>
 *   <li>Managing persistent configuration updates</li>
 *   <li>Providing interfaces for task element cleanup and retrieval</li>
 * </ul>
 *
 * <p>Supports custom YAML parser injection via {@link #setYaml(Yaml)}, with SnakeYAML as default.
 * Task configurations are encapsulated by {@link YamlTaskElement}, enabling runtime configuration batch updates.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public abstract class YamlConfigDatasourceDrivenScheduled extends AbstractDatasourceDrivenScheduled {

    private static final Yaml DEFAULT_YAML_PARSER = new Yaml();
    private Yaml yaml = DEFAULT_YAML_PARSER;
    private final String configYamlFileName;

    /**
     * Constructs a new {@code YamlConfigDatasourceDrivenScheduled} with {@code CronTaskRepository}
     * as its task Manager and a yaml configuration file name.
     *
     * @param cronTaskRepository the Task management resource explorer.
     * @param configYamlFileName the path to the YAML configuration file.
     */
    public YamlConfigDatasourceDrivenScheduled(CronTaskRepository cronTaskRepository, String configYamlFileName) {
        super(cronTaskRepository);
        this.configYamlFileName = requireNonNull(configYamlFileName,"configYamlFileName == null");;
    }

    /**
     * Sets the Yaml instance to be used for parsing and serialization.
     *
     * @param yaml The Yaml instance to set. This instance will be used for YAML
     *             parsing and serialization operations.
     */
    public void setYaml(@NotNull Yaml yaml) {
        this.yaml = yaml;
    }

    @Override
    protected void purgeDatasourceTaskElements() {
        new YamlTaskElementLoader().purge();
    }

    @Override
    protected List<TaskElement> getDatasourceTaskElements() {
        return new YamlTaskElementLoader().getDatasourceTaskElements();
    }

    @Override
    protected void afterStart(List<TaskElement> fulledDatasourceTaskElement) {
        updateBatchElementToYamlConfigFile(fulledDatasourceTaskElement);
    }

    @Override
    protected void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement) {
        updateBatchElementToYamlConfigFile(runtimeCheckedDatasourceTaskElement);
    }

    /**
     * Batch update configuration to the current {@link #configYamlFileName} YAML configuration file.
     *
     * @param updateElements Updated task information items.
     */
    private void updateBatchElementToYamlConfigFile(List<TaskElement> updateElements) {
        Map<String, Map<String, String>> sourceUpdateMap = new HashMap<>();
        for (TaskElement updateElement : updateElements) {
            if (updateElement instanceof YamlTaskElement) {
                sourceUpdateMap.put(updateElement.getId(), ((YamlTaskElement) updateElement).getSourceYamlConfig());
            }
        }
        updateYamlConfigFile(sourceUpdateMap);
    }

    /**
     * Update the latest map format configuration to the YAML file of the configuration.
     *
     * @param updateDrivenTaskYamlConfig The configuration of the map format that needs to be updated.
     */
    private void updateYamlConfigFile(Map<String, Map<String, String>> updateDrivenTaskYamlConfig) {
        try (Writer writer = new FileWriter(configYamlFileName)) {
            yaml.dump(updateDrivenTaskYamlConfig, writer);
        } catch (IOException ex) {
            getLogger().error("Error writing to file: " + configYamlFileName, ex);
            throw new DataSourceDrivenException("Error writing to file: " + configYamlFileName, ex);
        }
    }

    private class YamlTaskElementLoader {

        private Map<String, Map<String, String>> drivenTaskYamlConfig;
        private List<YamlTaskElement> taskElements;

        /**
         * Construct an empty {@code YamlTaskElementLoader} to load the relevant configuration
         * information of the specified file.
         */
        public YamlTaskElementLoader() {
            loading();
        }

        /**
         * Load the relevant configuration information of the specified file.
         */
        private void loading() {
            try (InputStream inputStream = getConfigYamlFileInputStream()) {
                drivenTaskYamlConfig = yaml.load(inputStream);
            }
            catch (IOException e) {
                getLogger().error("Failed to load yaml file : " + configYamlFileName, e);
                throw new DataSourceDrivenException("Failed to load yaml file : " + configYamlFileName, e);
            }
            taskElements = drivenTaskYamlConfig.values().stream().map(YamlTaskElement::new).collect(Collectors.toList());
        }

        /**
         * Retrieve the information flow of the specified file.
         *
         * @return the {@link InputStream} of the specified file.
         * @throws IOException if file not found.
         */
        private InputStream getConfigYamlFileInputStream() throws IOException {
            return Files.newInputStream(Paths.get(configYamlFileName));
        }

        /**
         * @see YamlTaskElement#purge(Map)
         */
        public void purge() {
            if (CollectionUtils.isEmpty(taskElements)) {
                return;
            }
            if (taskElements.stream().anyMatch(element -> element.purge(drivenTaskYamlConfig))) {
                updateYamlConfigFile(drivenTaskYamlConfig);
            }
        }

        /**
         * @return The collection of information for running tasks.
         */
        public List<TaskElement> getDatasourceTaskElements() {
            return new ArrayList<>(taskElements);
        }
    }
}
