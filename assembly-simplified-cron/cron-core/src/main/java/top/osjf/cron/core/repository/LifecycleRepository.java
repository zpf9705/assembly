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

/**
 * The {@code LifecycleRepository} interface extends the Lifecycle interface, providing
 * lifecycle management functionality for repository components.
 *
 * <p>The design of the Lifecycle Repository interface aims to meet the needs of application
 * scenarios that require flexible management of the lifecycle of repository components.
 * For example, in scenarios such as database connection pools, message queue clients, or
 * file storage systems, warehouse components may need to be frequently started and stopped
 * in response to changes in application requirements or resource limitations. By implementing
 * the Lifecycle Repository interface, these components can handle lifecycle events more
 * elegantly, improving system stability and reliability.
 *
 * <p>Used to define components with clear lifecycle management capabilities, their lifecycle
 * management involves key stages such as component initialization, startup, shutdown, and
 * destruction.
 * <p><strong>Code case:</strong></p>
 * <pre>
 *     {@code
 *     LifecycleRepository repository = new ExampleLifecycleRepository();
 *     repository.start();
 *     if(repository.isStarted()){
 *         //... do any things.
 *     }
 *     Thread.sleep(50000);
 *     try{
 *        repository.restart();
 *     } catch(UnsupportedOperationException e){
 *         if(repository.isStarted()){
 *             repository.stop();
 *         }
 *     }
 *    }
 * </pre>
 * <p>Developers need to be particularly careful to avoid issues such as resource leaks or data
 * inconsistencies during the stop and restart process. Certain types of repository components
 * (such as database connection pools) may require re establishing connections with backend
 * services or reloading configuration information during restart.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface LifecycleRepository extends Lifecycle {

    /**
     * A default method used to restart the repository component.
     *
     * <p>The default method is to determine whether to restart based on different
     * scenarios. If the implementation framework has a special restart plan, please
     * override this method. If restart is not supported, you can override this method
     * to throw a {@link top.osjf.cron.core.exception.UnsupportedLifecycleException}
     * exception.
     *
     * @throws ReStartedUnsupportedException if reStart operation cannot be supported.
     */
    void reStart();
}
