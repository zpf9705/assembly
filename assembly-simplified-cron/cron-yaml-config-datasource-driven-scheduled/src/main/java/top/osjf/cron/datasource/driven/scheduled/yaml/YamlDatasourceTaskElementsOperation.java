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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

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
    private static final String DEFAULT_CONFIG_FILE_NAME = "task-config.yml";
    private static final String DEFAULT_BASE_DIR= System.getProperty("user.dir");

    private Yaml yaml = DEFAULT_YAML_PARSER;
    private String baseDir = DEFAULT_BASE_DIR;
    private String configYamlFileName = DEFAULT_CONFIG_FILE_NAME;

    /**
     * Sets the base directory path for resolving dynamic configuration files.
     *
     * <p><strong>Usage Note:</strong> This method is typically used to specify a writable directory
     * (e.g., the working directory or an absolute path) where dynamically modifiable configuration files
     * (e.g., YAML, properties) are stored.
     *
     * @param baseDir the path to the base directory. This parameter can be an absolute path or a path relative
     *                to the working directory. It should point to a location with appropriate write permissions
     *                to allow runtime configuration updates.
     */
    public void setBaseDir(@NotNull String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Sets the yml config file name for resolving dynamic configuration files.
     *
     * <p><strong>Important Note:</strong> The configuration file specified by {@code configYamlFileName}
     * must be dynamically modifiable at runtime and should NOT be placed under the {@code resources} directory.
     *
     * @param configYamlFileName the path to the YAML configuration file. The file must exist in a writable
     *                           external directory (e.g., the working directory or an absolute path) to allow
     *                           runtime updates.This parameter cannot be {@code null}.
     */
    public void setConfigYamlFileName(@NotNull String configYamlFileName) {
        this.configYamlFileName = configYamlFileName;
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
        Map<String, Map<String, String>> drivenTaskYamlConfig = new YamlTaskElementLoader().drivenTaskYamlConfig;
        for (TaskElement updateElement : updateElements) {
            if (updateElement instanceof YamlTaskElement) {
                drivenTaskYamlConfig.put(updateElement.getId(), ((YamlTaskElement) updateElement).getSourceYamlConfig());
            }
        }
        updateYamlConfigFile(drivenTaskYamlConfig);
    }

    /**
     * Update the latest map format configuration to the YAML file of the configuration.
     *
     * @param updateDrivenTaskYamlConfig The configuration of the map format that needs to be updated.
     */
    private void updateYamlConfigFile(Map<String, Map<String, String>> updateDrivenTaskYamlConfig) {
        try (Writer writer = new FileWriter(configYamlFileName)) {
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
            catch (DataSourceDrivenException ex) {
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
         */
        private InputStream getConfigYamlFileInputStream() {
            final String configFileName =
                    isNull(baseDir) ? configYamlFileName : baseDir + File.separator + configYamlFileName;
            try {
                return new FileInputStream(configFileName);
            }
            catch (FileNotFoundException ex) {
                throw new DataSourceDrivenException("Failed to found yaml config " + configFileName);
            }
            catch (Throwable ex) {
                throw new DataSourceDrivenException("Failed to get config yaml file inputStream", ex);
            }
        }

        /**
         * @see YamlTaskElement#purge()
         */
        public void purge() {
            if (CollectionUtils.isEmpty(taskElements)) {
                return;
            }
            boolean updateFlag = false;
            for (YamlTaskElement taskElement : taskElements) {
                if (taskElement.purge() && !updateFlag) {
                    updateFlag = true;
                }
            }
            if (updateFlag) {
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
