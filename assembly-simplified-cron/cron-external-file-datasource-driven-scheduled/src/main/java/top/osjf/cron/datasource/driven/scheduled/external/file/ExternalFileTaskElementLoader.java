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


package top.osjf.cron.datasource.driven.scheduled.external.file;

import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.InitializeAble;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import javax.annotation.concurrent.ThreadSafe;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;

/**
 * A functional abstract class that persistently loads changes to a
 * specified type of external configuration file.
 *
 * <p>This class can set specific paths {@link #baseDir} and file names
 * {@link #configFileName}, without distinguishing between specific file
 * types, resolved based on subclass generics {@code T extends TaskElement}.
 *
 * <p>The introduction of this abstract class is mainly aimed at solving
 * the related operations introduced by file class configuration (based on
 * {@link DatasourceTaskElementsOperation}), centralizing its operation API,
 * and mainly including the following functions:
 * <ul>
 * <li>{@link #purge()} corresponding to
 * {@link DatasourceTaskElementsOperation#purgeDatasourceTaskElements()} needs.</li>
 * <li>{@link #update(List)} corresponding to
 * {@link DatasourceTaskElementsOperation#afterStart(List)} and
 * {@link DatasourceTaskElementsOperation#afterStart(List)}</li>
 * <li>{@link #loading(Function)} corresponding to
 * {@link DatasourceTaskElementsOperation#getDatasourceTaskElements()} and
 * {@link DatasourceTaskElementsOperation#getRuntimeNeedCheckDatasourceTaskElements()} and
 * {@link DatasourceTaskElementsOperation#getElementById(String)}
 * </li>
 * </ul>
 *
 * <p>The aggregation implementation class needs to sequentially implement
 * internal write operations ({@link #purgeInternal(TaskElement)} and
 * {@link #updateInternal(TaskElement)} and {@link #refresh()}) based on the
 * implementation method of the domain, while this abstract class only completes
 * process level file operations and JVM access thread safety control (according
 * to {@link FileReadWriteLock}).
 *
 * @param <T> the type of task elements this operation handles, must extend {@link TaskElement}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@ThreadSafe
public abstract class ExternalFileTaskElementLoader<T extends TaskElement> implements InitializeAble, Closeable {

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

    private ReadWriteLock readWriteLock;

    /** The list of {@link TaskElement} loading through {@link #loadingInternal(InputStream)}. */
    protected List<T> taskElements;

    private Class<T> rawType;

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
     * Obtain and verify the existence of the configuration file and initialize the
     * file lock for the initialization operation.
     */
    @Override
    public void initialize() {
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            throw new DataSourceDrivenException("Missing file " + configFile.getPath());
        }

        try {
            lastModifiedMill = Files.getLastModifiedTime(configFile.toPath()).toMillis();
            readWriteLock = new FileReadWriteLock(configFile);
        }
        catch (IOException ex) {
            throw new DataSourceDrivenException("Failed to initialize " + getClass(), ex);
        }
    }

    /**
     * Release relevant file resources promptly when the JVM is shut down.
     */
    @Override
    public void close() {
        if (readWriteLock instanceof FileReadWriteLock) {
            try {
                ((FileReadWriteLock) readWriteLock).close();
            }
            catch (IOException ignored) {
            }
        }
    }

    /**
     * @return The {@link FileReadWriteLock} instance.
     */
    ReadWriteLock getReadWriteLock() {
        if (readWriteLock == null) {
            throw new DataSourceDrivenException("Not initialized.");
        }
        return readWriteLock;
    }

    /**
     * Clean the data of the loaded task element list.
     */
    public void purge() {

        loading(null);

        final Lock writeLock = getReadWriteLock().writeLock();
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
     * Adapt and update batch task elements by checked using {@link #rawType}.
     * @param updateElements Collection of task elements to be updated.
     */
    @SuppressWarnings("unchecked")
    public void checkedUpdate(List<TaskElement> updateElements) {

        obtainRawType();

        List<T> elements = new ArrayList<>();
        for (TaskElement updateElement : updateElements) {
            if (rawType.isInstance(updateElement)) {
                elements.add((T) updateElement);
            }
        }

        update(elements);
    }

    /**
     * Sets a {@link TaskElement} raw type.
     * @param rawType a {@link TaskElement} raw type.
     */
    protected void setRawType(Class<T> rawType) {
        this.rawType = rawType;
    }

    /**
     * Dynamically obtain the primitive type of {@link TaskElement} when {@link #setRawType(Class)}
     * is not actively set.
     */
    @SuppressWarnings("unchecked") void obtainRawType() {
        if (rawType == null) {
            try {
                rawType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
            }
            catch (Exception ex) {
                throw new DataSourceDrivenException("Failed to obtain raw type", ex);
            }
        }
    }

    /**
     * Adapt and update batch task elements.
     * @param updateElements Collection of task elements to be updated.
     */
    public void update(List<T> updateElements) {

        final Lock writeLock = getReadWriteLock().writeLock();
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
    protected boolean purgeInternal(T taskElement) {
        if (taskElement instanceof ExternalFileDatasourceTaskElement) {
            return ((ExternalFileDatasourceTaskElement) taskElement).purge();
        }
        return false;
    }

    /**
     * Update the internal implementation method of task elements.
     * @param updateElement Task elements to be updated.
     */
    protected void updateInternal(T updateElement) {
    }

    /**
     * Refresh the latest content of the configuration file.
     * @throws DataSourceDrivenException If the refresh fails to occur.
     */
    protected abstract void refresh() throws DataSourceDrivenException;

    /**
     * Load the specified configuration file and filter the data.
     * @param loadingElementsFilterFunction the {@link Function} that filter the loaded data.
     * @return The dataset that has been loaded and filtered.
     * @throws DataSourceDrivenException if loading fails to occur.
     */
    public List<T> loading(@Nullable Function<List<T>, List<T>> loadingElementsFilterFunction) {
        final Lock readLock = getReadWriteLock().readLock();
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
                    throw new DataSourceDrivenException("Missing Yaml file " + getConfigFile().getPath());
                }
                catch (Throwable ex) {
                    throw new DataSourceDrivenException("Failed to load Yaml file : " + getConfigFile().getPath(), ex);
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
     * @throws Throwable if loading fails to occur.
     */
    protected abstract List<T> loadingInternal(InputStream is) throws Throwable;

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
            if (lastModifiedMill.compareTo(modifiedMillis) != 0) {
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
