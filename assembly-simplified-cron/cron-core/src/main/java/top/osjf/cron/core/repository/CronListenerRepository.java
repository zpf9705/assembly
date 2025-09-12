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


package top.osjf.cron.core.repository;

import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.listener.CronListener;

/**
 * This interface provides functionalities for managing cron task listeners
 * {@code CronListener},including adding, removing, and checking the existence
 * of listeners. It allows the registration of custom logic to be executed before
 * and after the execution of scheduled tasks.
 *
 * <p>By implementing this interface, developers can flexibly control the order
 * and execution logic of listeners {@code CronListener},thereby enhancing the
 * extensibility and flexibility of task scheduling.
 *
 * <p>The API of modifying the interface is derived from {@link CronTaskRepository},
 * which is an independent extension in version 3.0.1 to provide more powerful and
 * detailed API support.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public interface CronListenerRepository extends Repository {

    /**
     * Add a task listener {@code CronListener} instance.
     * <p>Task listeners are used to execute specific logic before and after task execution.
     * <p>This method takes a task listener {@code CronListener} object as an input parameter
     * and adds it to the listener list.
     * @param listener the task listener {@code CronListener} object to be added.
     * @throws NullPointerException if input listener is {@literal null}.
     */
    void addListener(@NotNull CronListener listener);

    /**
     * Add a task listener {@code CronListener} instance at the beginning.
     * <p>Task listeners are used to execute specific logic before and after task execution.
     * <p>This method takes a task listener {@code CronListener} object as an input parameter
     * and adds it to the listener list.
     * @param listener the task listener {@code CronListener} object to be added.
     * @throws NullPointerException if input listener is {@literal null}.
     */
    void addFirstListener(@NotNull CronListener listener);

    /**
     * Add a task listener {@code CronListener} instance at the ending.
     * <p>Task listeners are used to execute specific logic before and after task execution.
     * <p>This method takes a task listener {@code CronListener} object as an input parameter
     * and adds it to the listener list.
     * @param listener the task listener {@code CronListener} object to be added.
     * @throws NullPointerException if input listener is {@literal null}.
     */
    void addLastListener(@NotNull CronListener listener);

    /**
     * Return a {@code Boolean} flag that the input {@code CronListener} already added.
     * @param listener the task listener object to check.
     * @return if {@code true} already added,{@code false} otherwise.
     * @throws NullPointerException if input listener is {@literal null}.
     */
    boolean hasCronListener(CronListener listener);

    /**
     * Remove a task listener {@code CronListener} instance.
     * <p>This method takes a task listener {@code CronListener} object as an input parameter
     * and removes it from the listener list.
     * @param listener the task listener object to be removed.
     * @throws NullPointerException if input listener is {@literal null}.
     */
    void removeListener(@NotNull CronListener listener);
}
