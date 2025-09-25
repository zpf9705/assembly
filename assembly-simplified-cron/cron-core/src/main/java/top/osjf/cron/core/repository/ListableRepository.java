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
import top.osjf.cron.core.lang.Nullable;

import java.util.List;

/**
 * Repository interface for querying registered scheduled task information.
 * Provides operations to check task existence, retrieve a single task's details,
 * and obtain a list of all registered tasks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface ListableRepository extends Repository {

    /**
     * Return a {@code Boolean} tag indicating whether there is a corresponding
     * scheduled task {@link CronTaskInfo} for the given ID.
     *
     * @param id the unique identifier of the registered cron task.
     * @return if {@code true} prove this id's task exist,{@code false} otherwise.
     */
    boolean hasCronTaskInfo(@NotNull String id);

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
     * @throws NullPointerException if input id is {@literal null}.
     */
    @Nullable
    CronTaskInfo getCronTaskInfo(@NotNull String id);

    /**
     * Retrieves information for all registered cron tasks.
     *
     * <p>This method returns a list of information for all registered cron tasks in the system.
     * If no cron tasks are registered in the system,it returns an empty list.
     *
     * @return A list containing information for all registered cron tasks. If the list is empty,
     * it indicates that no cron tasks are registered.
     */
    List<CronTaskInfo> getAllCronTaskInfo();
}
