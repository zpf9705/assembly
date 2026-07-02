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
import top.osjf.cron.core.listener.CronListener;

/**
 * Extension of {@link Repository} for registering scheduled tasks with execution count limits.
 *
 * <p>This interface inherits capabilities from {@link GeneralRegistrarRepository},
 * {@link ModifiableRepository} and {@link CronListenerRepository}. It adds execution
 * count restriction for each registered task: once the predefined maximum execution
 * count is reached, the task will be automatically deleted and will no longer be scheduled.
 * It provides convenient overload methods for one-time execution and custom limited execution
 * scenarios, mainly designed for temporary scheduled task registration.
 *
 * <p>The execution count limit is implemented via a built-in {@link CronListener},
 * which tracks task execution times. When the maximum run count is exhausted, the task
 * will be unregistered automatically to terminate subsequent scheduling.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see GeneralRegistrarRepository
 * @see ModifiableRepository
 * @see CronListenerRepository
 * @see CronListener
 * @see RunTimes
 */
public interface RunTimesRegistrarRepository
        extends Repository, GeneralRegistrarRepository, ModifiableRepository, CronListenerRepository {

    /**
     * Registers a one-time scheduled task that will be automatically unregistered
     * after its first execution.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   the task logic to execute when the cron trigger fires
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(String expression, Runnable runnable) throws CronInternalException {
        return registerRunTimes(expression, runnable, 1);
    }

    /**
     * Registers a one-time scheduled task that will be automatically unregistered
     * after its first execution.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   method-wrapped task logic to execute when the cron trigger fires
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(String expression, CronMethodRunnable runnable) throws CronInternalException {
        return registerRunTimes(expression, runnable, 1);
    }

    /**
     * Registers a one-time scheduled task that will be automatically unregistered
     * after its first execution.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       encapsulated runnable task body to execute when the cron trigger fires
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(String expression, RunnableTaskBody body) throws CronInternalException {
        return registerRunTimes(expression, body, 1);
    }

    /**
     * Registers a one-time scheduled task that will be automatically unregistered
     * after its first execution.
     *
     * <p>{@link TaskBody} allows developers to implement custom task execution logic
     * with self-defined parameters.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       custom encapsulated task body to execute when the cron trigger fires
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(String expression, TaskBody body) throws CronInternalException {
        return registerRunTimes(expression, body, 1);
    }

    /**
     * Registers a one-time scheduled task that will be automatically unregistered
     * after its first execution.
     *
     * @param task encapsulated cron task metadata containing trigger rule and task body
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    default String registerRunOnce(CronTask task) throws CronInternalException {
        return registerRunTimes(task, 1);
    }

    /**
     * Registers a scheduled task that will be automatically unregistered once
     * the specified maximum execution count is reached.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   the task logic to execute when the cron trigger fires
     * @param times      maximum allowed execution times, must be a positive integer
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(String expression, Runnable runnable, int times) throws CronInternalException;

    /**
     * Registers a scheduled task that will be automatically unregistered once
     * the specified maximum execution count is reached.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   method-wrapped task logic to execute when the cron trigger fires
     * @param times      maximum allowed execution times, must be a positive integer
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(String expression, CronMethodRunnable runnable, int times)
            throws CronInternalException;

    /**
     * Registers a scheduled task that will be automatically unregistered once
     * the specified maximum execution count is reached.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       encapsulated runnable task body to execute when the cron trigger fires
     * @param times      maximum allowed execution times, must be a positive integer
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(String expression, RunnableTaskBody body, int times)
            throws CronInternalException;

    /**
     * Registers a scheduled task that will be automatically unregistered once
     * the specified maximum execution count is reached.
     *
     * <p>{@link TaskBody} allows developers to implement custom task execution logic
     * with self-defined parameters.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       custom encapsulated task body to execute when the cron trigger fires
     * @param times      maximum allowed execution times, must be a positive integer
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(String expression, TaskBody body, int times) throws CronInternalException;

    /**
     * Registers a scheduled task that will be automatically unregistered once
     * the specified maximum execution count is reached.
     *
     * @param task  encapsulated cron task metadata containing trigger rule and task body
     * @param times maximum allowed execution times, must be a positive integer
     * @return unique task registration ID used for subsequent update and delete operations
     * <p><strong>Note:</strong> Task update operations may reset the remaining execution count,
     * which will invalidate the predefined execution limit constraint.
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String registerRunTimes(CronTask task, int times) throws CronInternalException;
}