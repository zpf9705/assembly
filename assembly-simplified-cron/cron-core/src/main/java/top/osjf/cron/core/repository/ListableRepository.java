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
 * Repository interface for querying registered scheduled task information.
 * Provides operations to check task existence, retrieve a single task's details,
 * and obtain a list of all registered tasks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public interface ListableRepository extends Repository {

    /**
     * Return a {@code Boolean} tag indicating whether there is a corresponding
     * scheduled task {@link CronTaskInfo} for the given ID.
     *
     * @param id the unique identifier of the registered cron task.
     * @return if {@code true} prove this id's task exist,{@code false} otherwise.
     */
    boolean hasCronTaskInfo(String id);

    /**
     * Retrieves cron task information based on a given unique identifier.
     *
     * <p>This method is used to query the information of a registered cron task that matches
     * the specified ID. If a cron task with this ID exists in the system,it returns the task's
     * information; otherwise, it returns null.
     *
     * @param id the unique identifier of the registered cron task.
     * @return The cron task information object that matches the given ID (if exists); otherwise,
     * returns {@literal null}.
     */
    @Nullable
    CronTaskInfo getCronTaskInfo(String id);

    /**
     * Retrieves information for all registered cron tasks.
     *
     * <p>This method returns a list of information for all registered cron tasks in the system.
     * If no cron tasks are registered in the system,it returns an empty list.
     *
     * @return A list containing information for all registered cron tasks. If the list is empty,
     * it indicates that no cron tasks are registered.
     */
    List<CronTaskInfo> getAllCronTaskInfos();

    /**
     * Return all the scheduled task IDs that have been successfully registered
     * in the system.
     * @return all the scheduled task IDs that have been successfully registered
     *         in the system.
     * @since 3.0.2
     */
    List<String> getAllRegisteredTaskIds();

    /**
     * Check whether the specified task is currently running.
     * @param id the unique identifier of the registered cron task.
     * @return {@code true} if task is executing, otherwise {@code false}
     * @since 3.0.2
     */
    boolean isTaskRunning(String id);

    /**
     * Return all currently running scheduled task IDs.
     * @return all currently running scheduled task IDs.
     * @since 3.0.2
     */
    List<String> getAllRunningTaskIds();

    /**
     * Returns the next scheduled execution time of the specified task.
     *
     * @param id the unique identifier of the registered cron task.
     * @return next execution timestamp in milliseconds; return {@code null}
     * if the task no longer has subsequent triggers.
     */
    @Nullable
    Long getNextExecuteTime(String id);

    /**
     * Return the results of querying the next execution time based on the
     * task ID in batch, returned in the format of {@link Map K:taskId,V:nextExecuteTime}.
     *
     * @param ids collection of task unique identifiers
     * @return a map with taskId as key and the next execution time (timestamp
     *         in milliseconds) as value; tasks without subsequent execution will
     *         not be included in the returned map.
     */
    Map<String, Long> getNextExecuteTimes(Collection<String> ids);
}
