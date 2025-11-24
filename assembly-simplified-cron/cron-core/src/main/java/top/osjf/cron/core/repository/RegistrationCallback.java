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

/**
 * A callback interface used for registering task scheduling events with various types of runnable bodies.
 *
 * This interface defines multiple default methods that allow implementors to hook into task registration
 * without being forced to implement all variations. Each method corresponds to a specific type of task
 * or runnable being registered, identified by a cron expression, task ID, and the associated execution logic.
 *
 * The use of {@code default} methods makes this interface highly extensible — classes implementing it
 * can choose which callbacks they care about, while new methods can be added in the future without breaking
 * existing implementations (especially useful in SPI - Service Provider Interface scenarios).
 *
 * Methods are provided for different combinations of:
 * - Cron expressions and generic {@link Runnable}
 * - Cron expressions and framework-specific runnables like {@link CronMethodRunnable}, {@link RunnableTaskBody},
 * {@link TaskBody}
 * - Full {@link CronTask} objects representing scheduled tasks
 *
 * All methods are no-ops by default, meaning they do nothing unless overridden. This allows lightweight
 * implementation where only relevant callbacks are customized.
 *
 * @see CronTask - represents a fully configured cron-driven task
 * @see CronMethodRunnable - typically wraps a method-level runnable defined via annotation or configuration
 * @see RunnableTaskBody / TaskBody - higher-level abstractions for executable task logic
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface RegistrationCallback {

    /**
     * Called when a simple {@link Runnable} is registered with a cron expression and task ID.
     *
     * @param expression the cron expression defining the execution schedule
     * @param runnable   the {@link Runnable} instance to execute
     * @param id         unique identifier for the task
     */
    default void call(String expression, Runnable runnable, String id) { }

    /**
     * Called when a {@link CronMethodRunnable} is registered.
     *
     * @param expression the cron expression controlling execution frequency
     * @param runnable   the method-wrapping runnable instance
     * @param id         task identifier
     */
    default void call(String expression, CronMethodRunnable runnable, String id) { }

    /**
     * Called when a {@link RunnableTaskBody} is registered.
     *
     * @param expression the triggering cron expression
     * @param body       the task body containing execution logic
     * @param id         the task's unique ID
     */
    default void call(String expression, RunnableTaskBody body, String id) { }

    /**
     * Called when a general {@link TaskBody} is registered.
     *
     * @param expression the cron expression scheduling this task
     * @param body       the task implementation
     * @param id         identifier for the registered task
     */
    default void call(String expression, TaskBody body, String id) { }

    /**
     * Called when a full {@link CronTask} is registered.
     *
     * @param task the fully constructed {@link CronTask}
     * @param id   the assigned task identifier
     */
    default void call(CronTask task, String id) { }
}
