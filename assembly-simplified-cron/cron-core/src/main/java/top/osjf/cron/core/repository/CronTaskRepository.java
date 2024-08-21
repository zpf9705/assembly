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

/**
 * Cron Task Repository Interface, used for managing scheduled tasks based on
 * Cron expressions.
 *
 * <p>This interface extends from CronListenerRepository,allowing users to register,
 * update, and remove Cron tasks.
 *
 * <p>It utilizes generics to support different types of task IDs, task execution
 * bodies (BODY),and custom Cron task listeners (CronListener).
 *
 * @param <ID>   The type of unique ID for scheduled tasks.
 * @param <BODY> The type of scheduled task running entity.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface CronTaskRepository<ID, BODY> {

    /**
     * Registers a new Cron task.
     *
     * @param cronExpression The Cron expression that defines the schedule for the task.
     * @param runsBody       The data body used by the task during execution, the specific type is
     *                       specified by the BODY generic.
     * @return The unique identifier of the registered task, the specific type is specified by the ID generic.
     * @throws Exception Thrown if any error occurs during registration.
     */
    ID register(String cronExpression, BODY runsBody) throws Exception;

    /**
     * Updates an existing Cron task.
     *
     * @param id                The unique identifier of the task to be updated.
     * @param newCronExpression The new Cron expression that updates the task's schedule.
     * @throws Exception Thrown if any error occurs during the update process.
     */
    void update(ID id, String newCronExpression) throws Exception;

    /**
     * Removes the specified Cron task.
     *
     * @param id The unique identifier of the task to be removed.
     * @throws Exception Thrown if any error occurs during the removal process.
     */
    void remove(ID id) throws Exception;
}
