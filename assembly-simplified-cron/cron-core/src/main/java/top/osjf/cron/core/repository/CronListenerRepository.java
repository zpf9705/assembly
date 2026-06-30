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

import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.listener.CronListener;

import java.util.List;

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
     */
    void addListener(CronListener listener);

    /**
     * Add a task listener {@code CronListener} instance at the beginning.
     * <p>Task listeners are used to execute specific logic before and after task execution.
     * <p>This method takes a task listener {@code CronListener} object as an input parameter
     * and adds it to the listener list.
     * @param listener the task listener {@code CronListener} object to be added.
     */
    void addFirstListener(CronListener listener);

    /**
     * Add a task listener {@code CronListener} instance at the ending.
     * <p>Task listeners are used to execute specific logic before and after task execution.
     * <p>This method takes a task listener {@code CronListener} object as an input parameter
     * and adds it to the listener list.
     * @param listener the task listener {@code CronListener} object to be added.
     */
    void addLastListener(CronListener listener);

    /**
     * Return a {@code Boolean} flag that the input {@code CronListener} already added.
     * @param listener the task listener object to check.
     * @return if {@code true} already added,{@code false} otherwise.
     */
    boolean hasListener(CronListener listener);

    /**
     * Remove a task listener {@code CronListener} instance.
     * <p>This method takes a task listener {@code CronListener} object as an input parameter
     * and removes it from the listener list.
     * @param listener the task listener object to be removed.
     * @return {@code true} if the listener existed and was successfully removed;
     *         {@code false} if the listener was not found in the registry.
     */
    boolean removeListener(CronListener listener);

    /**
     * Removes the registered cron task listener by {@link CronListener#getName() its unique listener name}.
     * @param listenerName the unique name of the listener to remove.
     * @return {@code true} if the listener existed and was successfully removed;
     *         {@code false} if the listener was not found in the registry.
     * @since 3.0.2
     */
    boolean removeListener(String listenerName);

    /**
     * Returns the registered cron task listener by listener name.
     * @param listenerName the unique name of the target listener
     * @return the matched {@code CronListener} instance, returns {@code null} if not found.
     * @since 3.0.2
     */
    @Nullable
    CronListener getListener(String listenerName);

    /**
     * Gets the total number of currently registered cron task listeners.
     * @return registered listener count.
     * @since 3.0.2
     */
    long getListenerSize();

    /**
     * Returns an immutable list of all currently added cron task listeners.
     * <p>The returned collection cannot be modified to avoid unintended changes to the internal
     * listener list.
     * @return all registered {@code CronListener} instances.
     * @since 3.0.2
     */
    List<CronListener> getAllListeners();
}
