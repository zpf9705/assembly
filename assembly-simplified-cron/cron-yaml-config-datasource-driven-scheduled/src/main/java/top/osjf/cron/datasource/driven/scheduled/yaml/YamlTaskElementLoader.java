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


package top.osjf.cron.datasource.driven.scheduled.yaml;

import org.yaml.snakeyaml.Yaml;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The Yaml loader for {@link YamlTaskElement} loading.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class YamlTaskElementLoader {

    /** Default {@link #configYamlFileName} named task-config.yml */
    private static final String DEFAULT_CONFIG_FILE_NAME = "task-config.yml";

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /** The map of loading result. */
    private Map<Object, Map<Object, Object>> loadingResult;

    /** The list of {@link YamlTaskElement} through {@link #loadingResult} conversion. */
    private List<YamlTaskElement> taskElements;

    private String baseDir;
    private String configYamlFileName = DEFAULT_CONFIG_FILE_NAME;

    private File yamlFile;
    private Yaml yaml;

    private Long lastModifiedMill;

    /** The boolean flag indicates first loading. */
    private volatile boolean loadingFlag;

    /**
     * Constructs an empty {@code YamlTaskElementLoader} and init a {@link Yaml} instance.
     */
    public YamlTaskElementLoader() {
        this.yaml = new Yaml();
    }

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

    /**
     * Clean the relevant initialization configuration items that may exist in
     * the specified YAML file.
     */
    public void purge() {

        loading(null);

        final Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
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
                dump();
            }
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Write the existing updates to the Yaml configuration file.
     * @param updateElements Update item collection.
     */
    public void dump(List<TaskElement> updateElements) {

        loading(null);

        final Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            for (TaskElement updateElement : updateElements) {
                if (updateElement instanceof YamlTaskElement) {
                    Object sourceId
                            = ((YamlTaskElement) updateElement).getSourceYamlConfig(YamlTaskElement.ID_KEY_NAME);
                    loadingResult.put(sourceId,
                            ((YamlTaskElement) updateElement).getSourceYamlConfig());
                }
            }

            dump();
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Load the specified Yaml configuration file and filter the data.
     * @param loadingElementsFilterFunction the {@link Function} that filter the loaded data.
     * @return The dataset that has been loaded and filtered.
     */
    public List<YamlTaskElement> loading(Function<List<YamlTaskElement>, List<YamlTaskElement>>
                                                 loadingElementsFilterFunction) {
        final Lock readLock = readWriteLock.readLock();
        readLock.lock();

        try {
            if (loadingFlag && !isModifiedRecently()) {

                return Optional.ofNullable(loadingElementsFilterFunction).map(lf -> lf.apply(taskElements))
                        .orElse(Collections.emptyList());
            }
            else {

                try (InputStream is = new FileInputStream(getYamlFile())) {

                    loadingResult = yaml.load(is);

                    if (loadingResult == null) {
                        loadingResult = new HashMap<>();
                        taskElements = new ArrayList<>();

                    }
                    else {
                        taskElements = loadingResult.values()
                                .stream().map(YamlTaskElement::new).collect(Collectors.toList());
                    }

                    if (!loadingFlag) {
                        loadingFlag = true;
                    }

                    return Optional.ofNullable(loadingElementsFilterFunction).map(lf -> lf.apply(taskElements))
                            .orElse(Collections.emptyList());
                }
                catch (DataSourceDrivenException ex) {
                    throw ex;
                }
                catch (FileNotFoundException ex) {
                    throw new DataSourceDrivenException("Missing Yaml file " + yamlFile.getName());
                }
                catch (Throwable ex) {
                    throw new DataSourceDrivenException("Failed to load Yaml file : " + yamlFile.getName(), ex);
                }
            }
        }

        finally {
            readLock.unlock();
        }
    }

    /**
     * Check if there have been any changes to {@link #getYamlFile()} that are
     * inconsistent with {@link #lastModifiedMill}.
     * @return {@code true} indicate changes have occurred,{@code false} otherwise.
     */
    private boolean isModifiedRecently() {
        try {
            long modifiedMillis = Files.getLastModifiedTime(getYamlFile().toPath()).toMillis();
            if (lastModifiedMill == null || lastModifiedMill.compareTo(modifiedMillis) != 0) {
                lastModifiedMill = modifiedMillis;
                return true;
            }
            return false;
        }
        catch (IOException ex) {
            return true;
        }
    }

    /**
     * Modify Yaml file based on {@link #loadingResult}.
     */
    private void dump() {
        try (Writer writer = new FileWriter(getYamlFile())) {
            yaml.dump(loadingResult, writer);
        }
        catch (Throwable ex) {
            throw new DataSourceDrivenException("Failed to dump file : " + configYamlFileName, ex);
        }
    }

    /**
     * Retrieve the {@link File} object after configuration {@link #baseDir} and {@link #configYamlFileName}.
     * @return the {@link File} object after configuration {@link #baseDir} and {@link #configYamlFileName}.
     */
    private File getYamlFile() {
        if (yamlFile == null) {
            yamlFile = baseDir != null ? new File(baseDir, configYamlFileName) : new File(configYamlFileName);
        }
        return yamlFile;
    }
}
