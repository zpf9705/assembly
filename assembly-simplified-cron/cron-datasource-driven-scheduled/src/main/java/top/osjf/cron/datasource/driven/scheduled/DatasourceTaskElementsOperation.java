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
 *   <li>Callback after {@link AbstractDatasourceDrivenScheduled#inspect()} to update source data</li>
 * </ul>
 *
 * <p>Implementations should define cleanup logic and task information retrieval strategies according
 * to business requirements.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface DatasourceTaskElementsOperation extends DatasourceTaskElementsQueryOperation,
        AbstractDatasourceDrivenScheduled.TaskScheduleMonitorStartAction, AutoCloseable {

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
     * This method is a callback method for {@link DatasourceDrivenScheduledLifecycle#start()}.
     * After the task data source driver starts the method, the registered updated task data
     * {@link TaskElement} collection is called back to the data source for update operation.
     *
     * @param fulledDatasourceTaskElement After task registration, the updated data set needs to
     *                                    be mapped with additional entries (such as {@link TaskElement#getTaskId()}).
     */
    void afterStart(List<TaskElement> fulledDatasourceTaskElement);

    /**
     * This method is used for patrolling and monitoring activities {@link AbstractDatasourceDrivenScheduled#inspect()}
     * to check the method callback after execution.
     *
     * <p>According to the developer's modification prompts for {@link TaskElement#getUpdateSign()},
     * the task is dynamically checked and updated during runtime (see method {@link AbstractDatasourceDrivenScheduled#inspect()}).
     * After the changes and updates are completed, the data source needs to be updated and returned
     * to the update.
     *
     * @param runtimeCheckedDatasourceTaskElement Dynamically check and update the {@link TaskElement} collection
     *                                            of tasks during runtime.
     */
    void afterInspect(List<TaskElement> runtimeCheckedDatasourceTaskElement);

    /**
     * Set the task scheduling data source management instance object {@link AbstractDatasourceDrivenScheduled
     * AbstractDatasourceDrivenScheduled } to the {@link DatasourceTaskElementsOperation
     * DatasourceTaskElementsOperation } instance in order to implement related extended operations
     * in a specific way, and the specific behavior depends on the requirements of the implementation class.
     * @param scheduled task scheduling data source management instance object.
     * @since 3.0.2
     */
    default void setAbstractDatasourceDrivenScheduled(AbstractDatasourceDrivenScheduled scheduled) {
    }

    /**
     * If necessary, close the resources occupied by this operation implementation.
     * Periodic closure management has been performed in {@link AbstractDatasourceDrivenScheduled},
     * and developers do not need to manually call it.
     * @throws Exception if this data source cannot be closed
     * @since 3.0.2
     */
    @Override
    default void close() throws Exception {}
}
