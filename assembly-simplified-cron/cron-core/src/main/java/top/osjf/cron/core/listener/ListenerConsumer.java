/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.core.listener;

import top.osjf.cron.core.lang.Nullable;

/**
 * Functional interface used to carry the execution logic of cron task lifecycle events,
 * acting as a consumer that receives task listener, runtime context and exception information.
 *
 * <p>This interface is matched with {@link ListenerLifecycle} enumeration, each lifecycle node
 * will trigger the corresponding {@code accept} method to execute custom event callback logic.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ListenerConsumer {

    /**
     * Consume the cron task lifecycle event data and execute custom callback logic.
     *
     * @param cronListener    The target cron listener instance bound to the current task,
     *                         used to trigger the corresponding lifecycle listening method.
     * @param listenerContext  Runtime context of the current cron task, storing task unique ID,
     *                         cron expression, execution timestamp and other core task metadata.
     * @param e                Exception thrown during task execution, marked {@link Nullable}:
     *                         <ul>
     *                             <li>{@code null}: Task executed normally without error (START/SUCCESS event)</li>
     *                             <li>Non-null: Task execution failed, carries specific abnormal stack info (FAILED event)</li>
     *                         </ul>
     */
    void accept(CronListener cronListener, ListenerContext listenerContext, @Nullable Throwable e);
}
