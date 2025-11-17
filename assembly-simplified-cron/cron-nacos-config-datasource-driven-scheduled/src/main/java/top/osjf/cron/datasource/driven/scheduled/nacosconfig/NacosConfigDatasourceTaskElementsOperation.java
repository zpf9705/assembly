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

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.util.AssertUtils;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigFormat;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigFormatDatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigurableTaskElement;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * A data source operation implementation based on the Nacos configuration center.
 *
 * <p>This class implements the {@link DatasourceTaskElementsOperation} interface, using Nacos
 * as the backend storage to manage a set of dynamically updatable task elements (e.g., scheduled
 * job configurations). It supports: reading from remote Nacos configs, listening for changes,
 * and persisting updates back to Nacos.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class NacosConfigDatasourceTaskElementsOperation extends ConfigFormatDatasourceTaskElementsOperation {

    /**
     * Single-thread pool. Once the thread pool is blocked, we throw up the old task.
     */
    private final ExecutorService pool = new ThreadPoolExecutor(1, 1, 0, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(1), new NameThreadFactory("cron-nacos-datasource-update"),
            new ThreadPoolExecutor.DiscardOldestPolicy());

    private static final int DEFAULT_TIMEOUT = 3000;

    private ConfigRefreshListener configListener;

    private final String groupId;
    private final String dataId;
    private final Properties properties;

    /**
     * Note: The Nacos config might be null if its initialization failed.
     */
    private ConfigService configService;

    /**
     * Constructs a {@code NacosConfigDatasourceTaskElementsOperation} to init {@link ConfigService} with
     * any nacos setting.
     * @param serverAddr   server address of Nacos, cannot be empty
     * @param groupId      group ID, cannot be empty
     * @param dataId       data ID, cannot be empty
     * @param configFormat config format, cannot be null.
     */
    public NacosConfigDatasourceTaskElementsOperation(final String serverAddr, final String groupId,
                                                      final String dataId, final ConfigFormat configFormat) {
        this(buildProperties(serverAddr), groupId, dataId, configFormat);
    }

    /**
     * Constructs a {@code NacosConfigDatasourceTaskElementsOperation} to init {@link ConfigService} with
     * any nacos setting.
     * @param properties properties for construct {@link ConfigService} using
     *                   {@link NacosFactory#createConfigService(Properties)}
     * @param groupId    group ID, cannot be empty
     * @param dataId     data ID, cannot be empty
     * @param configFormat config format, cannot be null.
     */
    public NacosConfigDatasourceTaskElementsOperation(final Properties properties, final String groupId,
                                                      final String dataId, final ConfigFormat configFormat) {
        super(configFormat);
        AssertUtils.assertNotBlank(groupId, "Bad argument: groupId=[%s]" + groupId);
        AssertUtils.assertNotBlank(dataId, "Bad argument: groupId=[%s]" + dataId);
        AssertUtils.assertNotNull(properties,
                "Nacos properties must not be null, you could put some keys from PropertyKeyConst");
        this.groupId = groupId;
        this.dataId = dataId;
        this.properties = properties;
        initNacosListener();
    }

    private static Properties buildProperties(String serverAddr) {
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
        return properties;
    }

    private void initNacosListener() {
        try {
            this.configService = NacosFactory.createConfigService(this.properties);
        }
        catch (Exception ex) {
            logger.warn("[NacosConfigDatasourceTaskElementsOperation] Error occurred when initializing " +
                    "Nacos data source", ex);
        }
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
    public List<TaskElement> getDatasourceTaskElements() {
        return Collections.unmodifiableList(getElements());
    }

    @Override
    public void afterStart(List<TaskElement> fulledDatasourceTaskElement) {

        updateConfig(fulledDatasourceTaskElement);
    }

    @Override
    public List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {

        List<ConfigurableTaskElement> elements = getElements();

        if (elements.isEmpty()) {
            return Collections.emptyList();
        }

        elements = elements.stream()
                .filter(t -> Objects.equals(t.getUpdateSign(), 1)
                        || (Objects.equals(t.getUpdateSign(), 1) && t.getTaskId() == null))
                .collect(Collectors.toList());

        return Collections.unmodifiableList(elements);
    }

    @Override
    public void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement) {

        updateConfig(runtimeCheckedDatasourceTaskElement);
    }

    @Nullable
    @Override
    public TaskElement getElementById(String id) {
        List<ConfigurableTaskElement> elements = getElements();
        return elements.isEmpty() ? null : elements.stream()
                .filter(e-> Objects.equals(e.getTaskId(), id)).findFirst().orElse(null);
    }

    @Override
    public boolean registerDefaultIfMainTaskInfoNotProvided() {
        return false;
    }

    @Override
    public void close() {
        if (configService != null) {
            if (configListener != null) {
                configService.removeListener(dataId, groupId, configListener);
            }
            try {
                configService.shutDown();
            }
            catch (Exception ex) {
                logger.warn("[NacosConfigDatasourceTaskElementsOperation] Error occurred when closing " +
                        "Nacos data source", ex);
            }
        }
        pool.shutdownNow();
    }

    @Override
    public void notifyMainTaskInfoNotProvidedAndNoDefaultUsed() {
        configListener = new ConfigRefreshListener();
        try {
            // Add config listener.
            configService.addListener(dataId, groupId, new ConfigRefreshListener());
        }
        catch (NacosException ex) {
            logger.warn("[NacosConfigDatasourceTaskElementsOperation] Error occurred when add " +
                    "config listener", ex);

            throw new DataSourceDrivenException("Failed to add configuration and refresh listener", ex);
        }
    }

    /**
     * Gets the current list of task elements, preferring cached version from listener,
     * falling back to remote fetch if needed.
     *
     * @return Current valid list of task elements
     */
    private List<ConfigurableTaskElement> getElements() {
        if (configListener != null) {
            return configListener.elements;
        }
        return getRemoteElements();
    }

    /**
     * Fetches the latest config string from Nacos and deserializes it into a list of task elements.
     * @return Deserialized list of task elements
     */
    private List<ConfigurableTaskElement> getRemoteElements() {
        String configInfo;
        try {
            configInfo = configService.getConfig(dataId, groupId, DEFAULT_TIMEOUT);
        }
        catch (NacosException ex) {
            logger.warn("[NacosConfigDatasourceTaskElementsOperation] Error occurred when get " +
                    "config info ", ex);

            throw new DataSourceDrivenException("[NacosConfigDatasourceTaskElementsOperation] Error occurred when get " +
                    "config info ", ex);
        }
        return deserialize(configInfo);
    }

    /**
     * Updates configuration: synchronizes the given task elements to Nacos.
     * @param elements Collection of task elements to update
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
     * Publishes new configuration to Nacos.
     *
     * @param nacosConfigTaskElements List of task elements to publish
     * @throws DataSourceDrivenException if serialization or publishing fails
     */
    private void publishConfig(List<ConfigurableTaskElement> nacosConfigTaskElements) {

        String newConfig = null;

        try {
            newConfig = serialize(nacosConfigTaskElements);

            configService.publishConfig(dataId, groupId, newConfig);
        }
        catch (NacosException ex) {

            logger.warn("[NacosConfigDatasourceTaskElementsOperation] Error occurred when " +
                    "publish config {}", newConfig, ex);

            throw new DataSourceDrivenException("[NacosConfigDatasourceTaskElementsOperation] Error occurred when " +
                    "publish config " + newConfig, ex);
        }
    }

    /**
     * Inner class: Listener for Nacos configuration changes.
     *
     * <p>When the remote config changes, receives the latest content and reparses it into a task list,
     * ensuring the local view stays consistent with Nacos.
     */
    private class ConfigRefreshListener implements Listener {

       volatile List<ConfigurableTaskElement> elements;

        public ConfigRefreshListener() {
            elements = getRemoteElements();
        }

        @Override
        public Executor getExecutor() {
            return pool;
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
            logger.info("[NacosDataSource] New property value received for (properties: {}) " +
                    "(dataId: {}, groupId: {}): {}", properties, dataId, groupId, configInfo);
            elements = deserialize(configInfo);
        }
    }
}
