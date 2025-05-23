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


package top.osjf.cron.datasource.driven.scheduled.mp;

import com.baomidou.mybatisplus.extension.service.IService;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * MyBatis-Plus integrated implementation of  scheduled task datasource operation.
 *
 * <p>This class implements {@link DatasourceTaskElementsOperation} to provide concrete
 * implementation using MyBatis-Plus as the data persistence layer. It manages scheduled
 * tasks stored in database through MyBatis-Plus's {@link IService} interface.
 *
 * <h2>Key Features:</h2>
 * <ul>
 *   <li>MyBatis-Plus Integration: Uses {@link IService} for CRUD operations on {@link DatabaseTaskElement}</li>
 *   <li>Task Persistence: Stores task configurations in relational database</li>
 *   <li>Optimized Queries: Implements efficient database queries for task management</li>
 *   <li>Status Management: Maintains task status synchronization between memory and database</li>
 * </ul>
 *
 * <h2>Implementation Details:</h2>
 * <ul>
 *   <li>Data Purge: {@link #purgeDatasourceTaskElements()} resets task IDs and status flags
 *       in batch using MyBatis-Plus's lambda update</li>
 *   <li>Task Retrieval: {@link #getDatasourceTaskElements()} fetches tasks excluding the management task</li>
 *   <li>Runtime Checks: {@link #getRuntimeNeedCheckDatasourceTaskElements()} implements optimized
 *       database query for update detection</li>
 *   <li>Timely update, maintenance, and callback after data changes.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class MybatisPlusDatasourceTaskElementsOperation implements DatasourceTaskElementsOperation {

    private final IService<DatabaseTaskElement> taskElementService;

    /**
     * Constructs a new {@code MybatisPlusDatasourceTaskElementsOperation}{@code IService<DatabaseTaskElement>}
     * as its task information storage.
     *
     * @param taskElementService the task information storage service.
     */
    public MybatisPlusDatasourceTaskElementsOperation(IService<DatabaseTaskElement> taskElementService) {
        this.taskElementService = taskElementService;
    }

    @Override
    public void purgeDatasourceTaskElements() {
        if (taskElementService.lambdaQuery().isNotNull(DatabaseTaskElement::getTaskId).exists()) {
            taskElementService.lambdaUpdate()
                    .set(DatabaseTaskElement::getTaskId, "")
                    .set(DatabaseTaskElement::getUpdateSign, 0)
                    .set(DatabaseTaskElement::getStatusDescription, "")
                    .update();
        }
    }

    @Override
    public List<TaskElement> getDatasourceTaskElements() {
        return Collections.unmodifiableList(taskElementService.lambdaQuery().list());
    }

    @Override
    public void afterStart(List<TaskElement> fulledDatasourceTaskElement) {
        updateBatchElements(fulledDatasourceTaskElement);
    }

    @Override
    public List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {
        return new ArrayList<>(taskElementService.lambdaQuery()
                .and(w -> w.eq(DatabaseTaskElement::getUpdateSign, 1)
                        .or(w2 -> w2.eq(DatabaseTaskElement::getUpdateSign, 0)
                                .isNull(DatabaseTaskElement::getTaskId))).list());
    }

    @Override
    public void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement) {
        updateBatchElements(runtimeCheckedDatasourceTaskElement);
    }

    /**
     * The batch update process has been completed {@link TaskElement}.
     *
     * @param processedElements Process processed {@link TaskElement}.
     */
    private void updateBatchElements(List<TaskElement> processedElements) {
        List<DatabaseTaskElement> updatesElements = new ArrayList<>();
        for (TaskElement taskElement : processedElements) {
            if (taskElement instanceof DatabaseTaskElement) {
                updatesElements.add((DatabaseTaskElement) taskElement);
            }
        }
        if (!updatesElements.isEmpty()) {
            taskElementService.updateBatchById(updatesElements);
        }
    }
}
