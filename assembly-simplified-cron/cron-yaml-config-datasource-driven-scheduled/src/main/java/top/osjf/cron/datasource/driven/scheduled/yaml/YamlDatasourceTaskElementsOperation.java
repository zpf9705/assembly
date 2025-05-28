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
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

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
 * <p>Supports custom YAML parser injection via {@link #setYaml(Yaml)}, with SnakeYAML as default.
 * Task configurations are encapsulated by {@link YamlTaskElement}, enabling runtime configuration batch updates.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class YamlDatasourceTaskElementsOperation implements DatasourceTaskElementsOperation {

    private static final Yaml DEFAULT_YAML_PARSER = new Yaml();
    private static final String USER_DIR = System.getProperty("user.dir");
    private Yaml yaml = DEFAULT_YAML_PARSER;
    private String configYamlFileName;

    /**
     * Constructs a new {@code YamlDatasourceTaskElementsOperation} with a YAML configuration file name.
     *
     * @param configYamlFileName the yaml config name.
     * @throws NullPointerException if {@code configYamlFileName} is {@code null}.
     */
    public YamlDatasourceTaskElementsOperation(String configYamlFileName) {
        this.configYamlFileName = requireNonNull(configYamlFileName, "configYamlFileName == null");
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
    public void purgeDatasourceTaskElements() {
        new YamlTaskElementLoader().purge();
    }

    @Override
    public List<TaskElement> getDatasourceTaskElements() {
        return new YamlTaskElementLoader().getDatasourceTaskElements();
    }

    @Override
    public void afterStart(List<TaskElement> fulledDatasourceTaskElement) {
        updateBatchElementToYamlConfigFile(fulledDatasourceTaskElement);
    }

    @Override
    public List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {
        return getDatasourceTaskElements().stream()
                .filter(t -> Objects.equals(t.getUpdateSign(), 1)
                        || (Objects.equals(t.getUpdateSign(), 1) && t.getTaskId() == null))
                .collect(Collectors.toList());
    }

    @Override
    public void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement) {
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
        try (Writer writer
                     = new OutputStreamWriter
                (Files.newOutputStream(Paths.get(ClassLoader.getSystemResource(configYamlFileName).toURI())))) {
            yaml.dump(updateDrivenTaskYamlConfig, writer);
        }
        catch (Throwable ex) {
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
            catch (DataSourceDrivenException ex){
                throw ex;
            }
            catch (Throwable ex) {
                throw new DataSourceDrivenException("Failed to load yaml file : " + configYamlFileName, ex);
            }
            taskElements = drivenTaskYamlConfig.values().stream().map(YamlTaskElement::new).collect(Collectors.toList());
        }

        /**
         * Retrieve the information flow of the specified file.
         *
         * @return the {@link InputStream} of the specified file.
         * @throws  IOException
         *          if an I/O error occurs
         */
        private InputStream getConfigYamlFileInputStream() throws IOException {
            try {
                return new FileInputStream(configYamlFileName);
            }
            catch (FileNotFoundException ex){
                if (configYamlFileName.startsWith(USER_DIR)){
                    throw ex;
                }
                configYamlFileName = configYamlFileName.startsWith(File.separator) ?
                        USER_DIR + configYamlFileName :
                        USER_DIR + File.separator + configYamlFileName;
                return getConfigYamlFileInputStream();
            }
            catch (Throwable ex){
                throw new DataSourceDrivenException("Failed to get config yaml file inputStream", ex);
            }
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
            return Collections.unmodifiableList(taskElements);
        }
    }
}
