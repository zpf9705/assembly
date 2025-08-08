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


package top.osjf.cron.datasource.driven.scheduled.jpa;

import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>{@code JpaDatasourceTaskElementsOperation} is a class that implements data source task
 * operations, based on JPA (Java Persistence API) for managing {@link DatabaseTaskElement}.
 *
 * <p>This class manages the persistence of task data through the injected {@link DatabaseTaskElementJpaRepository}.
 * Key functionalities include retrieving, clearing, and updating task elements, as well as
 * performing actions before and after task execution.</p>
 *
 * <b>Key Features:</b>
 * <ul>
 *     <li>Batch update of task elements</li>
 *     <li>Retrieve all task elements</li>
 *     <li>Clear task elements</li>
 *     <li>Get a single task element by ID</li>
 * </ul>
 *
 * <b>Key Methods:</b>
 * <ul>
 *     <li>{@link #purgeDatasourceTaskElements()} - Purges all task elements</li>
 *     <li>{@link #getDatasourceTaskElements()} - Retrieves all task elements</li>
 *     <li>{@link #afterStart(List)} - Batch updates task elements after startup</li>
 *     <li>{@link #getRuntimeNeedCheckDatasourceTaskElements()} - Retrieves task elements that need to be
 *     checked at runtime</li>
 *     <li>{@link #afterRun(List)} - Batch updates task elements after execution</li>
 *     <li>{@link #getElementById(String)} - Retrieves a task element by ID</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class JpaDatasourceTaskElementsOperation implements DatasourceTaskElementsOperation {

    private final DatabaseTaskElementJpaRepository jpaRepository;

    /**
     * Constructs a new {@code JpaDatasourceTaskElementsOperation} and let {@code DatabaseTaskElementJpaRepository}
     * as its task information storage.
     *
     * @param jpaRepository the task information storage repository.
     */
    public JpaDatasourceTaskElementsOperation(DatabaseTaskElementJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void purgeDatasourceTaskElements() {
        if (jpaRepository.existsByTaskIdIsNotNull()) {
            jpaRepository.clearRegisteredScheduledInfo();
        }
    }

    @Override
    public List<TaskElement> getDatasourceTaskElements() {
        return Collections.unmodifiableList(jpaRepository.findAll());
    }

    @Override
    public void afterStart(List<TaskElement> fulledDatasourceTaskElement) {
        updateBatchElements(fulledDatasourceTaskElement);
    }

    @Override
    public List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {
        return Collections.unmodifiableList(jpaRepository.findElementsByUpdateSignAndTaskId());
    }

    @Override
    public void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement) {
        updateBatchElements(runtimeCheckedDatasourceTaskElement);
    }

    @Nullable
    @Override
    public TaskElement getElementById(String id) {
        return jpaRepository.findById(id).orElse(null);
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
            jpaRepository.saveAll(updatesElements);
        }
    }
}
