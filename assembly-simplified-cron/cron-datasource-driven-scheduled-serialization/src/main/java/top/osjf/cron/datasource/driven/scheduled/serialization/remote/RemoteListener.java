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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.Assert;
import top.osjf.cron.datasource.driven.scheduled.AbstractDatasourceDrivenScheduled;
import top.osjf.cron.datasource.driven.scheduled.serialization.ConfigurableTaskElement;

import java.util.List;

/**
 * Abstract base class for listening to remote configuration changes and refreshing task elements dynamically.
 * <p>
 * Works in conjunction with {@link RemoteDatasourceTaskElementsOperation}, encapsulating common logic for
 * processing configuration updates received from remote sources (e.g., Nacos, Apollo). When a configuration
 * change is detected, subclasses trigger the {@link #refresh(String)} method to deserialize and update
 * the local cache of task elements.
 * </p>
 * <p>
 * The {@code elements} field is marked as volatile to ensure visibility across threads — after an asynchronous
 * update in a listener callback, all other threads will immediately see the most recent version of the task list.
 * </p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class RemoteListener {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Holds the current list of configurable task elements.
     */
    protected volatile List<ConfigurableTaskElement> elements;

    /**
     * Reference to the associated remote operation that handles deserialization and shared config format.
     * <p>
     * This allows the listener to reuse existing serialization/deserialization logic and maintain consistency
     * with the operation's state and configuration handling.
     * </p>
     */
    protected final RemoteDatasourceTaskElementsOperation remoteOperation;

    @Nullable private AbstractDatasourceDrivenScheduled scheduled;

    /**
     * Constructs a new remote listener with the given operation instance.
     * <p>
     * Initializes the internal task element list by fetching the current configuration from the remote source
     * via the provided operation. This ensures the listener starts with up-to-date data.
     * </p>
     *
     * @param remoteOperation the associated remote operation instance, must not be null
     *                        used to initialize the initial task elements and provide deserialization capability
     */
    public RemoteListener(RemoteDatasourceTaskElementsOperation remoteOperation) {
        Assert.notNull(remoteOperation, "RemoteDatasourceTaskElementsOperation must not be null");
        this.remoteOperation = remoteOperation;
        elements = remoteOperation.getRemoteElements();
    }

    /**
     * Set up a data source dynamic management instance for the remote listener to trigger data
     * detection at {@link #refresh}.
     * @param scheduled task scheduling data source management instance object.
     */
    protected void setAbstractDatasourceDrivenScheduled(AbstractDatasourceDrivenScheduled scheduled) {
        Assert.notNull(remoteOperation, "AbstractDatasourceDrivenScheduled must not be null");
        this.scheduled = scheduled;
    }

    /**
     * Refreshes the local cache of task elements upon receiving updated configuration.
     * <p>
     * Deserializes the provided configuration string using the bound {@link RemoteDatasourceTaskElementsOperation}
     * and updates the {@code elements} reference, enabling transparent hot-reload for consumers.
     * </p>
     * <p>
     * Note: This method is intended to be called by subclasses when a remote configuration change is received,
     * e.g., within a Nacos ConfigListener's onReceive callback.
     * </p>
     * <p>
     * After refreshing and deserializing, promptly call the main manager to perform task checking operations.
     * </p>
     * @param configInfo Latest configuration content string (such as JSON or YAML format)
     */
    protected void refresh(String configInfo) {
        this.elements = remoteOperation.deserialize(configInfo);
        if (scheduled != null) scheduled.inspect();
    }
}
