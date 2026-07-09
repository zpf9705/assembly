/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.datasource.driven.scheduled;


import top.osjf.commons.lang.Nullable;

import java.util.List;

/**
 * This interface is the information query operation interface for data source
 * task {@link TaskElement}.
 *
 * <p>This interface provides diversified query operations and supports unified
 * queries of {@link TaskElement} multiple information, such as ID, task ID,
 * status {@link Status}, task name, etc. It aims to provide relevant dynamic
 * queries after registration is completed to meet different business service needs.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface DatasourceTaskElementsQueryOperation {

    /**
     * Query all full task metadata records from the data source.
     *
     * <p>Scenarios where the returned task list is applied:
     * <ul>
     *   <li>New task registration: add qualified tasks to cron scheduler container</li>
     *   <li>Runtime dynamic inspection: global verification of task status and configuration</li>
     * </ul>
     *
     * <p><b>Important Constraint:</b>
     * The returned list may be a direct reference to the internal original data structure.
     * External write operations on this collection will tamper with the internal cached state.
     * It is highly recommended for implementors to return deep copies or immutable list instances.
     *
     * @return Full {@link TaskElement} list queried from datasource.
     */
    List<TaskElement> getDatasourceTaskElements();

    /**
     * Query the list of task metadata that need registration or update check at runtime.
     * This interface is used by the scheduled monitor thread to fetch tasks requiring dynamic operation.
     *
     * <p>Difference from {@link #getDatasourceTaskElements()}:
     * The returned collection only contains tasks that meet the update/add criteria, developers should filter
     raw data according to the actual data source business rules.
     *
     * <p>General filtering rules for eligible TaskElement:
     * <ul>
     * <li>Rule 1: {@link TaskElement#getUpdateSign()} = 1, task pending to be updated;</li>
     * <li>Rule 2: {@link TaskElement#getUpdateSign()} = 0 and {@link TaskElement#getTaskId()} is empty,
     dynamically newly added task without registered task id.</li>
     * </ul>
     *
     * <p><b>Important Constraint:</b>
     * To avoid concurrent modification exceptions during data callback operations, it is strongly recommended
     * to return an immutable collection/list for the query result.
     *
     * @return Filtered {@link TaskElement} collection requiring runtime registration/update verification
     */
    List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements();

    /**
     * Return a {@link TaskElement} based on {@link TaskElement#getId()}.
     * @param id {@link TaskElement#getId()}.
     * @return a {@link TaskElement} based on {@link TaskElement#getId()}.
     */
    @Nullable
    TaskElement getElementById(String id);

    /**
     * Return a {@link TaskElement} based on {@link TaskElement#getTaskId()}.
     * @param taskId {@link TaskElement#getTaskId()}.
     * @return a {@link TaskElement} based on {@link TaskElement#getTaskId()}.
     */
    @Nullable
    TaskElement getElementByTaskId(String taskId);

    /**
     * Return list of {@link TaskElement} based on {@link TaskElement#getTaskName()}.
     * @param taskName {@link TaskElement#getTaskName()}.
     * @return list of {@link TaskElement} based on {@link TaskElement#getTaskName()}.
     */
    List<TaskElement> getElementsByTaskName(String taskName);

    /**
     * Return list of {@link TaskElement} based on {@link TaskElement#getStatus()}.
     * @param status {@link TaskElement#getStatus()}.
     * @return list of {@link TaskElement} based on {@link TaskElement#getStatus()}.
     */
    List<TaskElement> getElementsByTaskStatus(Status status);
}
