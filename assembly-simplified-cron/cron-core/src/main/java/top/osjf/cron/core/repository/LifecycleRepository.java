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

package top.osjf.cron.core.repository;

import top.osjf.cron.core.lifecycle.Lifecycle;
import top.osjf.cron.core.exception.UnsupportedLifecycleException;

/**
 * {@link Repository} extension interface that adds lifecycle management capabilities
 * by inheriting {@link Lifecycle}.
 *
 * <p>This interface is designed to flexibly manage the startup, shutdown and restart
 * lifecycle of repository components. Typical applicable scenarios include components
 * such as database connection pools, message queue clients and file storage modules,
 * which require frequent startup and shutdown in response to application changes or
 * resource constraints. Implementing this interface allows components to gracefully
 * handle lifecycle events and enhance overall system stability.
 *
 * <p>It standardizes core lifecycle phases including initialization, startup, shutdown
 * and destruction for scheduled task repository implementations.
 *
 * <p><strong>Code Example:</strong>
 * <pre>{@code
 * LifecycleRepository repository = new ExampleLifecycleRepository();
 * repository.start();
 * if (repository.isStarted()) {
 *     // Execute business logic
 * }
 * Thread.sleep(50000);
 * try {
 *     repository.reStart();
 * } catch (UnsupportedLifecycleException e) {
 *     if (repository.isStarted()) {
 *         repository.stop();
 *     }
 * }
 * }</pre>
 *
 * <p>Developers must pay attention to avoiding resource leaks and data inconsistency
 * during stop and restart operations. Some components like database connection pools
 * may need to rebuild backend connections or reload configuration when restarting.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 * @see Lifecycle
 * @see Repository
 */
public interface LifecycleRepository extends Lifecycle, Repository {

    /**
     * Restarts the current repository component.
     *
     * <p>The default implementation provides general restart logic. Framework implementations
     * with custom restart policies may override this method. If restart is not supported,
     * the implementation should throw {@link UnsupportedLifecycleException}.
     *
     * @throws UnsupportedLifecycleException if the restart operation is not supported
     */
    void reStart();
}