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
        return taskElementService.getById(getManagerTaskId());
    }

    @NotNull
    @Override
    protected List<TaskElement> getDatasourceTaskElements() {
        return new ArrayList<>(taskElementService.lambdaQuery().ne(DatabaseTaskElement::getId, getManagerTaskId()).list());
    }

    @Override
    protected List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {
        return new ArrayList<>(taskElementService.lambdaQuery()
                .and(w -> w.eq(DatabaseTaskElement::getUpdateSign, 1)
                        .or(w2 -> w2.eq(DatabaseTaskElement::getUpdateSign, 0)
                                .isNull(DatabaseTaskElement::getTaskId))).list());
    }

    /**
     * Return the task ID of the main task, which defaults to {@link Constants#MANAGER_TASK_ID}.
     * @return the task ID of the main task.
     */
    protected String getManagerTaskId() {
        return Constants.MANAGER_TASK_ID;
    }
}
