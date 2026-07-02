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

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Repository interface for querying metadata of registered scheduled tasks.
 *
 * <p>Supports common query capabilities including task existence verification,
 * single task detail query, full task list retrieval, running status judgment,
 * and next scheduled execution time acquisition.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public interface ListableRepository extends Repository {

    /**
     * Checks whether a scheduled task exists for the given task ID.
     *
     * @param id unique identifier of the registered cron task
     * @return {@code true} if the task exists; {@code false} otherwise
     */
    boolean hasCronTaskInfo(String id);

    /**
     * Retrieves the detailed metadata of the scheduled task corresponding to the specified ID.
     *
     * @param id unique identifier of the registered cron task
     * @return the {@link CronTaskInfo} of the target task if exists; {@code null} otherwise
     */
    @Nullable
    CronTaskInfo getCronTaskInfo(String id);

    /**
     * Retrieves metadata for all registered scheduled tasks.
     *
     * @return a list containing all registered task metadata; returns an empty list
     *         when no tasks have been registered
     */
    List<CronTaskInfo> getAllCronTaskInfos();

    /**
     * Obtains all unique identifiers of successfully registered scheduled tasks.
     *
     * @return list of all registered task IDs
     * @since 3.0.2
     */
    List<String> getAllRegisteredTaskIds();

    /**
     * Checks whether the specified task is currently being executed.
     *
     * @param id unique identifier of the registered cron task
     * @return {@code true} if the task is running; {@code false} otherwise
     * @since 3.0.2
     */
    boolean isTaskRunning(String id);

    /**
     * Obtains unique identifiers of all currently executing scheduled tasks.
     *
     * @return list of task IDs for all running tasks
     * @since 3.0.2
     */
    List<String> getAllRunningTaskIds();

    /**
     * Gets the next scheduled execution timestamp of the specified task.
     *
     * @param id unique identifier of the registered cron task
     * @return next execution timestamp in milliseconds; returns {@code null}
     *         if the task has no subsequent trigger schedule
     */
    @Nullable
    Long getNextExecuteTime(String id);

    /**
     * Batch queries the next scheduled execution timestamp for multiple tasks.
     *
     * @param ids collection of target task unique identifiers
     * @return a {@link Map} with task ID as key and next execution timestamp (ms) as value;
     *         tasks without subsequent trigger schedules will not be included in the result map
     */
    Map<String, Long> getNextExecuteTimes(Collection<String> ids);
}