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
 * Base generic repository interface for scheduling task registration.
 *
 * <p>This interface defines the fundamental capability for registering cron tasks,
 * which must be implemented directly by underlying components. It supports registering
 * tasks with a cron expression paired with multiple types of task execution carriers,
 * including {@link Runnable}, {@link CronMethodRunnable}, {@link TaskBody},
 * and the integrated metadata wrapper {@link CronTask}.
 *
 * <p>Each registration method returns a globally unique task ID. This identifier can be
 * used to query task details via {@link ListableRepository} and modify or remove tasks
 * via {@link ModifiableRepository}.
 *
 * <p>Sample usage:
 * <pre>{@code
 * repository.register("0 0 12 * * ?", () -> System.out.println("Execute at 12:00 noon"));
 * }</pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see Repository
 * @see RegistrationCallback
 * @see ListableRepository
 * @see ModifiableRepository
 */
public interface GeneralRegistrarRepository extends Repository, RegistrationCallback {


    /**
     * Registers a new scheduled task with the specified cron expression and {@link Runnable}.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   the task logic to execute when the cron trigger fires
     * @return unique task registration ID for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(String expression, Runnable runnable) throws CronInternalException;


    /**
     * Registers a new scheduled task with the specified cron expression and {@link CronMethodRunnable}.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param runnable   method-wrapped task logic to execute when the cron trigger fires
     * @return unique task registration ID for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(String expression, CronMethodRunnable runnable) throws CronInternalException;


    /**
     * Registers a new scheduled task with the specified cron expression and {@link RunnableTaskBody}.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       encapsulated runnable task body to execute when the cron trigger fires
     * @return unique task registration ID for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(String expression, RunnableTaskBody body) throws CronInternalException;


    /**
     * Registers a new scheduled task with the specified cron expression and custom {@link TaskBody}.
     *
     * <p>{@link TaskBody} allows developers to implement custom task execution logic
     * with self-defined business parameters.
     *
     * @param expression valid cron expression defining the task trigger rule
     * @param body       custom encapsulated task body to execute when the cron trigger fires
     * @return unique task registration ID for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(String expression, TaskBody body) throws CronInternalException;


    /**
     * Registers a new scheduled task using the encapsulated {@link CronTask} metadata.
     *
     * @param task encapsulated cron task metadata containing trigger rule and task execution body
     * @return unique task registration ID for subsequent update and delete operations
     * @throws CronInternalException if cron parsing fails, registration conflict occurs
     *                               or any internal scheduling exception is thrown
     */
    String register(CronTask task) throws CronInternalException;
}
