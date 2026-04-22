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

import top.osjf.cron.core.lang.Nullable;

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
    List<TaskElement> getElementByTaskName(String taskName);

    /**
     * Return list of {@link TaskElement} based on {@link TaskElement#getStatus()}.
     * @param status {@link TaskElement#getStatus()}.
     * @return list of {@link TaskElement} based on {@link TaskElement#getStatus()}.
     */
    List<TaskElement> getElementByTaskStatus(Status status);
}
