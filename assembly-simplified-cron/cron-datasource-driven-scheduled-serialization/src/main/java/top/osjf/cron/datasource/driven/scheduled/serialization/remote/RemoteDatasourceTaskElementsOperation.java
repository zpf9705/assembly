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


package top.osjf.cron.datasource.driven.scheduled.serialization.remote;

import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.datasource.driven.scheduled.AbstractDatasourceDrivenScheduled;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigFormat;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigFormatDatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigurableTaskElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

/**
 * Abstract base class for operating scheduled task elements stored in a remote data source (e.g., Nacos, Redis).
 *
 * <p>Extends {@link ConfigFormatDatasourceTaskElementsOperation}, providing common logic for reading,
 * writing, and synchronizing task configurations from/to remote configuration centers using various formats
 * (JSON, YAML, etc.). Subclasses implement concrete data access while reusing standardized serialization
 * and lifecycle callback mechanisms.
 *
 * <p>Key features:
 * <ul>
 *   <li>Loads task configurations from remote sources and deserializes them into {@link ConfigurableTaskElement}s.
 *   </li>
 *   <li>Automatically republishes updated task states after lifecycle events (e.g., start, run).</li>
 *   <li>Supports lazy initialization of a {@link RemoteListener} to react to remote config changes.</li>
 *   <li>Wraps remote I/O exceptions into {@link DataSourceDrivenException} for consistent error handling.</li>
 * </ul>
 *
 * <p>Subclasses must implement:
 * <ul>
 *   <li>{@link #getRemoteConfigInfo()}: Retrieves raw configuration string from the remote source.</li>
 *   <li>{@link #publishConfig(String)}: Publishes serialized configuration back to the remote source.</li>
 * </ul>
 *
 * <p>Typical use cases involve integrating with centralized configuration systems like Nacos or Apollo
 * to enable dynamic task updates and cluster-wide synchronization in distributed scheduling environments.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class RemoteDatasourceTaskElementsOperation extends ConfigFormatDatasourceTaskElementsOperation {

    /** Lazy listener provider that only initializes when needed .*/
    @Nullable private Supplier<RemoteListener> lazyListener;

    @Nullable private RemoteListener listener;

    /**
     * Constructs an instance with the specified configuration format.
     * @param configFormat the configuration format (e.g., JSON, YAML), must not be null
     */
    public RemoteDatasourceTaskElementsOperation(ConfigFormat configFormat) {
        super(configFormat);
    }

    /**
     * Sets a supplier for lazy initialization of the remote listener.
     * <p>
     * The listener will be instantiated only upon first use, avoiding premature resource allocation.
     * @param lazyListener the supplier that creates the listener on demand
     */
    public void setLazyListener(Supplier<RemoteListener> lazyListener) {
        this.lazyListener = lazyListener;
    }

    /**
     * Gets the current listener instance, allowing generic casting.
     * @param <L> the expected listener type
     * @return the current listener instance, or null if not initialized
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <L extends RemoteListener> L getListener() {
        return (L) listener;
    }

    @Override
    public void purgeDatasourceTaskElements() {
        List<ConfigurableTaskElement> elements = getElements();

        for (ConfigurableTaskElement element : elements) {
            element.purge();
        }
        publishConfig(elements);
    }

    @Override
    public void afterStart(List<TaskElement> fulledDatasourceTaskElement) {

        updateConfig(fulledDatasourceTaskElement);
    }

    @Override
    public void afterInspect(List<TaskElement> runtimeCheckedDatasourceTaskElement) {

        updateConfig(runtimeCheckedDatasourceTaskElement);
    }

    @Override
    protected List<TaskElement> getBeFilteredTaskElements() {
        return Collections.unmodifiableList(getElements());
    }

    @Override
    public void setAbstractDatasourceDrivenScheduled(AbstractDatasourceDrivenScheduled scheduled) {
        Supplier<RemoteListener> origin = this.lazyListener;
        if (origin != null) {
            this.lazyListener = () -> {
                RemoteListener remoteListener = origin.get();
                remoteListener.setAbstractDatasourceDrivenScheduled(scheduled);
                return remoteListener;
            };
        }
    }

    @Override
    public void elseMonitorStartAction() {
        if (lazyListener != null) {
            listener = lazyListener.get();
        }
    }

    /**
     * Retrieves the current list of configurable task elements.
     * <p>
     * Prefers cached elements from the listener if available; otherwise fetches from the remote source.
     * @return the list of current task elements
     */
    private List<ConfigurableTaskElement> getElements() {
        if (listener != null) {
            return listener.elements;
        }

        return getRemoteElements();
    }

    /**
     * Updates the remote configuration with the given list of task elements.
     * <p>
     * Only processes elements of type {@link ConfigurableTaskElement}.
     * @param elements the list of task elements to update
     */
    private void updateConfig(List<TaskElement> elements) {
        List<ConfigurableTaskElement> nacosConfigTaskElements = new ArrayList<>();

        for (TaskElement taskElement : elements) {
            if (taskElement instanceof ConfigurableTaskElement) {
                nacosConfigTaskElements.add((ConfigurableTaskElement) taskElement);
            }
        }

        if (nacosConfigTaskElements.isEmpty()) {
            logger.warn("There is no {} instance in the update item collection, so the configuration " +
                    "cannot be updated.", ConfigurableTaskElement.class.getName());
            return;
        }

        publishConfig(nacosConfigTaskElements);
    }

    /**
     * Serializes the given list of task elements and publishes them to the remote data source.
     * <p>
     * On failure, logs a warning and wraps the exception into a {@link DataSourceDrivenException}.
     * @param elements the list of task elements to publish
     */
    private void publishConfig(List<ConfigurableTaskElement> elements) {

        String newConfig = null;

        try {
            newConfig = serialize(elements);

            publishConfig(newConfig);
        }
        catch (Throwable ex) {

            logger.warn("[{}}] Error occurred when publish config {}", getClass().getName(), newConfig, ex);

            throw new DataSourceDrivenException("[" + getClass().getName() + "] Error occurred when " +
                    "publish config " + newConfig, ex);
        }
    }

    /**
     * Fetches configuration from the remote source and deserializes it into a list of task elements
     * <p>
     * On failure, logs a warning and wraps the exception into a {@link DataSourceDrivenException}.
     * @return the deserialized list of configurable task elements
     */
    protected List<ConfigurableTaskElement> getRemoteElements() {
        String configInfo;
        try {
            configInfo = getRemoteConfigInfo();
        }
        catch (Throwable ex) {
            logger.warn("[{}] Error occurred when get config info ", getClass().getName(), ex);

            throw new DataSourceDrivenException("[" + getClass().getName() + "] Error occurred when get " +
                    "config info ", ex);
        }
        return deserialize(configInfo);
    }

    /**
     * Retrieve raw configuration content from the remote data source.
     *
     * @return the configuration string retrieved from the remote source
     * @throws Throwable if an error occurs during retrieval (e.g., network issues, authentication failure)
     */
    protected abstract String getRemoteConfigInfo() throws Throwable;

    /**
     * Publish the given configuration string back to the remote data source.
     *
     * @param configInfo the configuration content to publish
     * @throws Throwable if an error occurs during publishing
     */
    protected abstract void publishConfig(String configInfo) throws Throwable;
}
