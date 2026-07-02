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

import top.osjf.cron.core.exception.CronInternalException;

/**
 * Extension of {@link Repository} that supports task execution timeout control.
 *
 * <p>Inherits the capabilities of {@link GeneralRegistrarRepository} and
 * {@link RunTimesRegistrarRepository}, adding {@link RunningTimeout} parameter
 * to limit the maximum execution duration of a single scheduled task.
 * Combined with fixed-run times capability, it provides fine-grained control
 * for temporary scheduled task scenarios.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see GeneralRegistrarRepository
 * @see RunTimesRegistrarRepository
 * @see RunTimes
 * @see RunningTimeout
 * @see RunTimeout
 */
public interface RunTimeoutRegistrarRepository
        extends Repository, RunTimesRegistrarRepository, GeneralRegistrarRepository {

    /**
     * Register a scheduled task with the specified cron expression, {@link Runnable}
     * and task execution timeout configuration.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   the task logic to execute when the cron trigger fires
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID used for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(String expression, Runnable runnable, RunningTimeout timeout)
            throws CronInternalException;

    /**
     * Register a scheduled task with the specified cron expression, {@link CronMethodRunnable}
     * and task execution timeout configuration.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   method-wrapped task logic to execute when the cron trigger fires
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID used for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(String expression, CronMethodRunnable runnable, RunningTimeout timeout)
            throws CronInternalException;

    /**
     * Register a scheduled task with the specified cron expression, {@link RunnableTaskBody}
     * and task execution timeout configuration.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       encapsulated runnable task body to execute when the cron trigger fires
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID used for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(String expression, RunnableTaskBody body, RunningTimeout timeout)
            throws CronInternalException;

    /**
     * Register a scheduled task with the specified cron expression, custom {@link TaskBody}
     * and task execution timeout configuration.
     *
     * <p>{@link TaskBody} allows developers to implement custom task execution logic
     * with self-defined parameters.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       custom encapsulated task body to execute when the cron trigger fires
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID used for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(String expression, TaskBody body, RunningTimeout timeout) throws CronInternalException;

    /**
     * Register a scheduled task using the encapsulated {@link CronTask} metadata
     * and task execution timeout configuration.
     *
     * @param task     encapsulated cron task metadata containing trigger and task body
     * @param timeout  timeout configuration for a single task execution
     * @return unique task registration ID used for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(CronTask task, RunningTimeout timeout) throws CronInternalException;

    /**
     * Register a one-time scheduled task that will be automatically unregistered
     * after its first successful execution.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   the task logic to execute when the cron trigger fires
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the one-time execution constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(String expression, Runnable runnable, RunningTimeout timeout)
            throws CronInternalException {
        return registerRunTimes(expression, runnable, 1, timeout);
    }

    /**
     * Register a one-time scheduled task that will be automatically unregistered
     * after its first successful execution.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   method-wrapped task logic to execute when the cron trigger fires
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the one-time execution constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(String expression, CronMethodRunnable runnable, RunningTimeout timeout)
            throws CronInternalException {
        return registerRunTimes(expression, runnable, 1, timeout);
    }

    /**
     * Register a one-time scheduled task that will be automatically unregistered
     * after its first successful execution.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       encapsulated runnable task body to execute when the cron trigger fires
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the one-time execution constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(String expression, RunnableTaskBody body, RunningTimeout timeout)
            throws CronInternalException {
        return registerRunTimes(expression, body, 1, timeout);
    }

    /**
     * Register a one-time scheduled task that will be automatically unregistered
     * after its first successful execution.
     *
     * <p>{@link TaskBody} allows developers to implement custom task execution logic
     * with self-defined parameters.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       custom encapsulated task body to execute when the cron trigger fires
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the one-time execution constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(String expression, TaskBody body, RunningTimeout timeout)
            throws CronInternalException {
        return registerRunTimes(expression, body, 1, timeout);
    }

    /**
     * Register a one-time scheduled task that will be automatically unregistered
     * after its first successful execution.
     *
     * @param task    encapsulated cron task metadata containing trigger and task body
     * @param timeout timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the one-time execution constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(CronTask task, RunningTimeout timeout) throws CronInternalException {
        return registerRunTimes(task, 1, timeout);
    }

    /**
     * Register a scheduled task that will be automatically unregistered after
     * the specified number of executions.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   the task logic to execute when the cron trigger fires
     * @param times      maximum allowed execution times, must be a positive integer
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the predefined execution limit.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(String expression, Runnable runnable, int times, RunningTimeout timeout)
            throws CronInternalException;

    /**
     * Register a scheduled task that will be automatically unregistered after
     * the specified number of executions.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   method-wrapped task logic to execute when the cron trigger fires
     * @param times      maximum allowed execution times, must be a positive integer
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the predefined execution limit.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(String expression, CronMethodRunnable runnable, int times, RunningTimeout timeout)
            throws CronInternalException;

    /**
     * Register a scheduled task that will be automatically unregistered after
     * the specified number of executions.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       encapsulated runnable task body to execute when the cron trigger fires
     * @param times      maximum allowed execution times, must be a positive integer
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the predefined execution limit.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(String expression, RunnableTaskBody body, int times, RunningTimeout timeout)
            throws CronInternalException;

    /**
     * Register a scheduled task that will be automatically unregistered after
     * the specified number of executions.
     *
     * <p>{@link TaskBody} allows developers to implement custom task execution logic
     * with self-defined parameters.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       custom encapsulated task body to execute when the cron trigger fires
     * @param times      maximum allowed execution times, must be a positive integer
     * @param timeout    timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the predefined execution limit.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(String expression, TaskBody body, int times, RunningTimeout timeout)
            throws CronInternalException;

    /**
     * Register a scheduled task that will be automatically unregistered after
     * the specified number of executions.
     *
     * @param task     encapsulated cron task metadata containing trigger and task body
     * @param times    maximum allowed execution times, must be a positive integer
     * @param timeout  timeout configuration for a single task execution
     * @return unique task registration ID for later task management
     * <p><strong>Note:</strong> Task update operations may reset the remaining run count,
     * which could invalidate the predefined execution limit.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(CronTask task, int times, RunningTimeout timeout) throws CronInternalException;
}