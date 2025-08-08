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


package top.osjf.cron.datasource.driven.scheduled;

import top.osjf.cron.core.lang.Nullable;

import java.util.List;

/**
 * Interface defining operations for datasource task elements management.
 * Provides functionality to clean up and retrieve task information.
 *
 * <p>This interface is primarily used for managing scheduled task configurations from a datasource, including:
 * <ul>
 *   <li>Cleaning up invalid or expired task data to prevent dirty data during registration</li>
 *   <li>Retrieving task information sets for registration and runtime dynamic checks</li>
 *   <li>Callback after {@link DatasourceDrivenScheduledLifecycle#start()} to update source data</li>
 *   <li>Retrieving runtime specific conditions require a dataset that is specifically checked by the
 *   main task.</li>
 *   <li>Callback after {@link AbstractDatasourceDrivenScheduled#run()} to update source data</li>
 * </ul>
 *
 * <p>Implementations should define cleanup logic and task information retrieval strategies according
 * to business requirements.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface DatasourceTaskElementsOperation {

    /**
     * Cleans up task information data in the datasource to prevent dirty data during registration.
     *
     * <p>Developers should define cleanup logic based on business needs. Typical scenarios include:
     * <ul>
     *   <li>Removing invalid or expired task configurations</li>
     *   <li>Resetting task states to unregistered</li>
     *   <li>Validating task configuration integrity</li>
     * </ul>
     */
    void purgeDatasourceTaskElements();

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
     * This method is a callback method for {@link DatasourceDrivenScheduledLifecycle#start()}.
     * After the task data source driver starts the method, the registered updated task data
     * {@link TaskElement} collection is called back to the data source for update operation.
     *
     * @param fulledDatasourceTaskElement After task registration, the updated data set needs to
     *                                    be mapped with additional entries (such as {@link TaskElement#getTaskId()}).
     */
    void afterStart(List<TaskElement> fulledDatasourceTaskElement);

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
     * This method is for the main management task to drive the callback of protective gear after
     * the scheduled inspection of {@link AbstractDatasourceDrivenScheduled#run()}.
     *
     * <p>According to the developer's modification prompts for {@link TaskElement#getUpdateSign()},
     * the task is dynamically checked and updated during runtime (see method {@link AbstractDatasourceDrivenScheduled#run()}).
     * After the changes and updates are completed, the data source needs to be updated and returned
     * to the update.
     *
     * @param runtimeCheckedDatasourceTaskElement Dynamically check and update the {@link TaskElement} collection
     *                                            of tasks during runtime.
     */
    void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement);

    /**
     * Searches for and returns the task element with the specified unique identifier.
     *
     * @param id The unique identifier of the task element to find
     * @return The instance of the matching TaskElement, or null if no element is found
     * @see TaskElement#getId() Method to retrieve the ID of a task element
     */
    @Nullable
    TaskElement getElementById(String id);
}
