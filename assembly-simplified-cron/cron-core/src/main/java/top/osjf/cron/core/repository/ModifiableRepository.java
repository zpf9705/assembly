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
 * Repository interface for modifying and terminating registered scheduled tasks.
 *
 * <p>Provides capabilities to update task cron expressions, delete single or all tasks,
 * and interrupt ongoing executing task threads without altering task scheduling metadata.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public interface ModifiableRepository extends Repository {

    /**
     * Updates the cron expression of a registered scheduled task.
     *
     * @param id            unique identifier of the target registered task
     * @param newExpression valid new cron expression to replace the original trigger rule
     * @throws CronInternalException if the task cannot be found, cron parsing fails
     *                               or any internal scheduling exception occurs
     */
    void update(String id, String newExpression) throws CronInternalException;

    /**
     * Deletes the registered scheduled task matching the specified task ID.
     *
     * @param id unique identifier of the target registered task
     * @throws CronInternalException if the task cannot be found or any internal scheduling exception occurs
     */
    void remove(String id) throws CronInternalException;

    /**
     * Deletes all registered scheduled tasks and releases all occupied scheduling resources.
     *
     * @throws CronInternalException if any internal scheduling exception occurs during batch deletion
     * @since 3.0.2
     */
    void removeAll() throws CronInternalException;

    /**
     * Immediately terminates the currently running execution thread of the specified task.
     *
     * <p>The task's cron configuration and metadata remain unchanged. The task will still be
     * scheduled and triggered normally at its next scheduled time; only the ongoing running thread
     * will be interrupted.
     *
     * @param id unique identifier of the target registered task
     * @throws CronInternalException if the task cannot be found or any internal scheduling exception occurs
     * @since 3.0.2
     * @see ListableRepository#isTaskRunning(String)
     */
    void terminate(String id) throws CronInternalException;

    /**
     * Terminates all currently running task execution threads in the current repository.
     *
     * <p>All task metadata and cron trigger configurations are preserved, and subsequent scheduled
     * executions will continue to take effect normally.
     *
     * @throws CronInternalException if any internal scheduling exception occurs during batch termination
     * @since 3.0.2
     */
    void terminateAll() throws CronInternalException;
}