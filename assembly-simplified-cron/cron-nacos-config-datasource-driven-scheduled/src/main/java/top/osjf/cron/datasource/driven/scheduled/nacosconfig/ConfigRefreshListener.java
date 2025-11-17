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

import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.common.executor.NameThreadFactory;
import top.osjf.cron.datasource.driven.scheduled.serialization.remote.RemoteListener;

import java.io.Closeable;
import java.util.concurrent.*;

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
public class ConfigRefreshListener extends RemoteListener implements Listener, Closeable {

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
        refresh(configInfo);
    }

    @Override
    public void close() {
        pool.shutdownNow();
    }
}
