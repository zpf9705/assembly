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
import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.util.Assert;
import top.osjf.cron.datasource.driven.scheduled.DataSourceDrivenException;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigFormat;
import top.osjf.cron.datasource.driven.scheduled.serialization.remote.RemoteDatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.serialization.remote.RemoteListener;

import java.io.Closeable;
import java.util.Properties;
import java.util.concurrent.*;

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
public class NacosConfigDatasourceTaskElementsOperation extends RemoteDatasourceTaskElementsOperation {

    private static final int DEFAULT_TIMEOUT = 3000;

    private final String groupId;
    private final String dataId;
    private final Properties properties;
    private final ConfigType configType;

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
        this.configType = toNacosConfigType(configFormat);
        Assert.hasText(groupId, String.format("Bad argument: groupId=[%s]", groupId));
        Assert.hasText(dataId, String.format("Bad argument: dataId=[%s]", dataId));
        Assert.notNull(properties,
                "Nacos properties must not be null, you could put some keys from PropertyKeyConst");
        this.groupId = groupId;
        this.dataId = dataId;
        this.properties = properties;
        initNacosConfigService();
        setLazyListener(()-> new ConfigRefreshListener(this));
    }

    private ConfigType toNacosConfigType(ConfigFormat configFormat) {
        switch (configFormat) {
            case PROPERTIES: return ConfigType.PROPERTIES;
            case XML: return ConfigType.XML;
            case JSON: return ConfigType.JSON;
            case TEXT: return ConfigType.TEXT;
            case HTML: return ConfigType.HTML;
            case YAML: return ConfigType.YAML;
            default: return ConfigType.UNSET;
        }
    }

    private static Properties buildProperties(String serverAddr) {
        Assert.hasText(serverAddr, String.format("Bad argument: serverAddr=[%s]", serverAddr));
        Properties properties = new Properties();
        properties.setProperty(PropertyKeyConst.SERVER_ADDR, serverAddr);
        return properties;
    }

    private void initNacosConfigService() {
        try {
            this.configService = NacosFactory.createConfigService(this.properties);
        }
        catch (Exception ex) {
            logger.warn("[NacosConfigDatasourceTaskElementsOperation] Error occurred when initializing " +
                    "Nacos data source", ex);
        }
    }

    @Override
    public void close() {
        if (configService != null) {
            ConfigRefreshListener listener = getListener();
            if (listener != null) {
                configService.removeListener(dataId, groupId, listener);
                listener.close();
            }
            try {
                configService.shutDown();
            }
            catch (Exception ex) {
                logger.warn("[NacosConfigDatasourceTaskElementsOperation] Error occurred when closing " +
                        "Nacos data source", ex);
            }
        }
    }

    @Override
    public void elseMonitorStartAction() {
        super.elseMonitorStartAction();
        try {
            // Add config listener.
            configService.addListener(dataId, groupId, getListener());
        }
        catch (NacosException ex) {
            logger.warn("[NacosConfigDatasourceTaskElementsOperation] Error occurred when add " +
                    "config listener", ex);

            throw new DataSourceDrivenException("Failed to add configuration and refresh listener", ex);
        }
    }

    @Override
    @NotNull
    protected String getRemoteConfigInfo() throws Throwable {
        return configService.getConfig(dataId, groupId, DEFAULT_TIMEOUT);
    }

    @Override
    protected void publishConfig(@NotNull String configInfo) throws Throwable {
        configService.publishConfig(dataId, groupId, configInfo, configType.getType());
    }

    /**
     * Listener for Nacos configuration changes, used to receive remote config updates and trigger hot-reload of
     * task elements.
     * <p>
     * Implements Nacos's {@link Listener} interface and extends {@link RemoteListener},
     * asynchronously processes received configuration strings via a dedicated thread pool,
     * invoking the parent's {@link RemoteListener#refresh(String)} method to update the in-memory list of task
     * elements, enabling dynamic configuration reload without restarting the application.
     * </p>
     * <p>
     * Uses a single-threaded executor with a bounded queue (size=1) and {@code DiscardOldestPolicy} to ensure:
     * <ul>
     *   <li>Configuration events are processed sequentially, avoiding race conditions.</li>
     *   <li>If the system is overwhelmed, older pending updates are discarded, keeping only the latest config —
     *   preventing backlog and ensuring freshness.</li>
     * </ul>
     * </p>
     * <p>
     * Implements {@link Closeable} so that underlying resources (thread pool) can be properly released when no
     * longer needed.
     * </p>
     *
     * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
     * @since 3.0.2
     */
    private static class ConfigRefreshListener extends RemoteListener implements Listener, Closeable {

        /**
         * Single-thread pool. Once the thread pool is blocked, we throw up the old task.
         */
        private final ExecutorService pool = new ThreadPoolExecutor(1, 1, 0,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(1), new NameThreadFactory("cron-nacos-datasource-update"),
                new ThreadPoolExecutor.DiscardOldestPolicy());

        public ConfigRefreshListener(NacosConfigDatasourceTaskElementsOperation operation) {
            super(operation);
        }

        @Override
        public Executor getExecutor() {
            return pool;
        }

        @Override
        public void receiveConfigInfo(String configInfo) {
            NacosConfigDatasourceTaskElementsOperation nacosRemoteOperation
                    = (NacosConfigDatasourceTaskElementsOperation) remoteOperation;
            logger.info("[NacosDataSource] New property value received for (properties: {}) (dataId: {}, groupId: {}): {}",
                    nacosRemoteOperation.properties , nacosRemoteOperation.dataId, nacosRemoteOperation.groupId, configInfo);
            refresh(configInfo);
        }

        @Override
        public void close() {
            pool.shutdownNow();
        }
    }
}
