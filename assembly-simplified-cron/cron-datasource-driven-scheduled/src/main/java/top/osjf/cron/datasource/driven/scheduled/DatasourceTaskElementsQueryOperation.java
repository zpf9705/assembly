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
     * Retrieves the current set of task information from the datasource, which may include the primary task.
     *
     * <p>The returned task information is used for:
     * <ul>
     *   <li>Task registration (e.g., adding to a scheduler)</li>
     *   <li>Runtime dynamic information checks (e.g., task state validation)</li>
     * </ul>
     *
     * <p><b>Note:</b> The returned collection might be a reference to the original data.
     * External modifications could affect internal state. Implementations are encouraged
     * to return copies or immutable collections.
     *
     * @return Current set of task information from the datasource (may include primary task)
     */
    List<TaskElement> getDatasourceTaskElements();

    /**
     * Returning to dynamic operation requires the main task to check and update the relevant
     * {@link TaskElement} dataset of the entry.
     *
     * <p>The return collection data of this method is not equivalent to {@link #getDatasourceTaskElements()},
     * and developers need to filter it according to the actual situation of the data source.
     *
     * <p>However, in general, the situation is: {@link TaskElement#getUpdateSign()} is 1 (i.e. waiting to be
     * updated) or {@link TaskElement#getUpdateSign()} is 0 and there is no {@link TaskElement#getTaskId()}
     * (this situation is a dynamically added task).
     *
     * <p><b>Note:</b>Given the modification callback for obtaining data, it is recommended that the query
     * return an immutable collection list.
     *
     * @return the main task to check and update the relevant {@link TaskElement} dataset of the entry.
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
