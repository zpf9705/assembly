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
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.datasource.driven.scheduled.AbstractDatasourceDrivenScheduled;
import top.osjf.cron.datasource.driven.scheduled.Constants;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis-Plus integrated implementation of datasource-driven scheduled task management.
 *
 * <p>This class extends {@link AbstractDatasourceDrivenScheduled} to provide concrete
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
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public abstract class MybatisPlusDatasourceDrivenScheduled extends AbstractDatasourceDrivenScheduled {

    private final IService<DatabaseTaskElement> taskElementService;

    /**
     * Constructs a new {@code MybatisPlusDatasourceDrivenScheduled} with {@code CronTaskRepository}
     * as its task Manager and {@code IService<DatabaseTaskElement>} as its task information storage.
     *
     * @param cronTaskRepository the Task management resource explorer.
     * @param taskElementService the task information storage service.
     */
    public MybatisPlusDatasourceDrivenScheduled(CronTaskRepository cronTaskRepository,
                                                IService<DatabaseTaskElement> taskElementService) {
        super(cronTaskRepository);
        this.taskElementService = taskElementService;
    }

    @Override
    protected void purgeDatasourceTaskElements() {
        if (taskElementService.lambdaQuery().isNotNull(DatabaseTaskElement::getTaskId).exists()) {
            taskElementService.lambdaUpdate()
                    .set(DatabaseTaskElement::getTaskId, "")
                    .set(DatabaseTaskElement::getUpdateSign, 0)
                    .set(DatabaseTaskElement::getStatusDescription, "")
                    .update();
        }
    }

    @Nullable
    @Override
    protected TaskElement getManagerDatasourceTaskElement() {
        return taskElementService.getById(getManagerTaskUniqueId());
    }

    @NotNull
    @Override
    protected List<TaskElement> getDatasourceTaskElements() {
        return new ArrayList<>(taskElementService.lambdaQuery()
                .ne(DatabaseTaskElement::getId, getManagerTaskUniqueId()).list());
    }

    @Override
    protected void afterStart(List<TaskElement> fulledDatasourceTaskElement) {
        updateBatchElements(fulledDatasourceTaskElement);
    }

    @Override
    protected List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {
        return new ArrayList<>(taskElementService.lambdaQuery()
                .and(w -> w.eq(DatabaseTaskElement::getUpdateSign, 1)
                        .or(w2 -> w2.eq(DatabaseTaskElement::getUpdateSign, 0)
                                .isNull(DatabaseTaskElement::getTaskId))).list());
    }

    @Override
    protected void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement) {
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

    /**
     * Return the task unique ID of the main task, which defaults to {@link Constants#MANAGER_TASK_UNIQUE_ID}.
     *
     * @return the task ID of the main task.
     */
    protected String getManagerTaskUniqueId() {
        return Constants.MANAGER_TASK_UNIQUE_ID;
    }
}
