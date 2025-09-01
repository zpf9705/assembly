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


package top.osjf.cron.datasource.driven.scheduled;

import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.core.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * The abstract task element loading class aims to provide thread safe operations
 * and common operation methods for non file class loading when operating on unique
 * files.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class AbstractTaskElementLoader<T extends TaskElement> {

    /** The path to the base directory.*/
    @Nullable
    private String baseDir;

    /** The path to the configuration file name.*/
    private String configFileName;

    /** The {@link File} object to {@link #configFileName} and {@link #baseDir}.*/
    private File configFile;

    /** The timestamp of the last modification.*/
    private Long lastModifiedMill;

    /** The boolean flag indicates first loading. */
    private volatile boolean loadingFlag;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /** The list of {@link TaskElement} loading through {@link #loadingInternal(InputStream)}. */
    private List<T> taskElements;

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
    public void setBaseDir(@Nullable String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Sets the config file name for resolving dynamic configuration files.
     *
     * <p><strong>Important Note:</strong> The configuration file specified by {@code configFileName}
     * must be dynamically modifiable at runtime and should NOT be placed under the {@code resources}
     * directory.
     *
     * @param configFileName the path to the configuration file. The file must exist in a writable
     *                       external directory (e.g., the working directory or an absolute path) to allow
     *                       runtime updates.This parameter cannot be {@code null}.
     */
    public void setConfigFileName(@Nullable String configFileName) {
        this.configFileName = configFileName;
    }

    /**
     * Clean the data of the loaded task element list.
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
            for (T taskElement : taskElements) {
                if (purgeInternal(taskElement) && !updateFlag) {
                    updateFlag = true;
                }
            }
            if (updateFlag) {
                refresh();
            }
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Adapt and update batch task elements.
     * @param updateElements Collection of task elements to be updated.
     */
    public void update(List<T> updateElements) {

        loading(null);

        final Lock writeLock = readWriteLock.writeLock();
        writeLock.lock();
        try {
            for (T updateElement : updateElements) {
                updateInternal(updateElement);
            }

            refresh();
        }
        finally {
            writeLock.unlock();
        }
    }

    /**
     * Internal method for cleaning specified task elements.
     * @param taskElement Task elements to be cleaned.
     * @return The cleaning Boolean result of task elements, if {@code true}
     * represents cleaning completion, then {@code false} is an irregular task
     * element.
     */
    protected abstract boolean purgeInternal(T taskElement);

    /**
     * Update the internal implementation method of task elements.
     * @param updateElement Task elements to be updated.
     */
    protected abstract void updateInternal(T updateElement);

    /**
     * Refresh the latest content of the configuration file.
     * @throws DataSourceDrivenException If the refresh fails to occur.
     */
    protected abstract void refresh() throws DataSourceDrivenException;

    /**
     * Load the specified configuration file and filter the data.
     * @param loadingElementsFilterFunction the {@link Function} that filter the loaded data.
     * @return The dataset that has been loaded and filtered.
     */
    public List<T> loading(Function<List<T>, List<T>> loadingElementsFilterFunction) {
        final Lock readLock = readWriteLock.readLock();
        readLock.lock();

        try {
            if (loadingFlag && !isModifiedRecently()) {

                return Optional.ofNullable(loadingElementsFilterFunction).map(lf -> lf.apply(taskElements))
                        .orElse(Collections.emptyList());
            }
            else {

                try (InputStream is = new FileInputStream(getConfigFile())) {

                    taskElements = loadingInternal(is);

                    if (!loadingFlag) {
                        loadingFlag = true;
                    }

                    return Optional.ofNullable(loadingElementsFilterFunction).map(lf -> lf.apply(taskElements))
                            .orElse(Collections.emptyList());
                }
                catch (ClassCastException ex) {
                    throw new DataSourceDrivenException("Failed to load task list type conversion.", ex);
                }
                catch (DataSourceDrivenException ex) {
                    throw ex;
                }
                catch (FileNotFoundException ex) {
                    throw new DataSourceDrivenException("Missing Yaml file " + getConfigFile().getName());
                }
                catch (Throwable ex) {
                    throw new DataSourceDrivenException("Failed to load Yaml file : " + getConfigFile().getName(), ex);
                }
            }
        }

        finally {
            readLock.unlock();
        }
    }

    /**
     * The method for implementing the internal loading results of a class.
     * @param is the configuration file {@link InputStream}.
     * @return Load the conversion {@link TaskElement} list of configuration content.
     */
    protected abstract List<T> loadingInternal(InputStream is);

    /**
     * Retrieve the {@link File} object after configuration {@link #baseDir} and {@link #configFileName}.
     * @return the {@link File} object after configuration {@link #baseDir} and {@link #configFileName}.
     */
    protected File getConfigFile() {
        if (configFile == null) {
            String configFileName = getConfigFileName();
            configFile = baseDir != null ? new File(baseDir, configFileName) : new File(configFileName);
        }
        return configFile;
    }

    /**
     * @return The configuration file name.
     */
    private String getConfigFileName() {
        if (StringUtils.isBlank(configFileName)) {
            configFileName = defaultConfigFileName();
        }
        return configFileName;
    }

    /**
     * @return The default configuration file name.
     */
    protected abstract String defaultConfigFileName();

    /**
     * Check if there have been any changes to {@link #getConfigFile()} that are
     * inconsistent with {@link #lastModifiedMill}.
     * @return {@code true} indicate changes have occurred,{@code false} otherwise.
     */
    private boolean isModifiedRecently() {
        try {
            long modifiedMillis = Files.getLastModifiedTime(getConfigFile().toPath()).toMillis();
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
}
