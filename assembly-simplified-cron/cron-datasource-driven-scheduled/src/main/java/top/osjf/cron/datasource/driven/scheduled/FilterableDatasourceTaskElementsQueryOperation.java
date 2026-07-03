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
import top.osjf.commons.util.CollectionUtils;
import top.osjf.commons.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Filtering data source task element {@link TaskElement }query operation abstract base class.
 *
 * <p>Based on the template method pattern, encapsulate universal memory filtering logic, and
 * unify the multidimensional query capability of task elements.The subclass only needs to implement
 * {@link #getBeFilteredTaskElements()} to provide a complete dataset of task elements to be filtered,
 * It can automatically have the ability to query based on conditions such as primary
 *
 * <ul><li>key ID {@link #getElementById(String)}</li></ul>
 * <ul><li>key task ID {@link #getElementByTaskId(String)}</li></ul>
 * <ul><li>key task name {@link #getElementsByTaskName(String)}</li></ul>
 * <ul><li>key task status {@link #getElementsByTaskStatus(Status)}</li></ul>
 * etc.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class FilterableDatasourceTaskElementsQueryOperation
        extends ElseMonitorTaskScheduleMonitorStartAction implements DatasourceTaskElementsQueryOperation {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskElement> getDatasourceTaskElements() {
        return getBeFilteredTaskElements();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {
        return filterList(t -> Objects.equals(t.getUpdateSign(), 1)
                || (Objects.equals(t.getUpdateSign(), 0) && StringUtils.isBlank(t.getTaskId())));
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public TaskElement getElementById(String id) {
        return filterSingle(t-> Objects.equals(t.getId(), id));
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public TaskElement getElementByTaskId(String taskId) {
        return filterSingle(t-> Objects.equals(t.getTaskId(), taskId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskElement> getElementsByTaskName(String taskName) {
        return filterList(t-> Objects.equals(t.getTaskName(), taskName));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<TaskElement> getElementsByTaskStatus(Status status) {
        return filterList(t-> Objects.equals(t.getStatus(), status.name()));
    }

    /**
     * Filter the {@link TaskElement} single data based on condition {@code filter}
     * in {@link #getBeFilteredTaskElements()}.
     * @param filter the loading {@link Predicate Filter}.
     * @return Filter individual results loaded.
     */
    @Nullable
    private TaskElement filterSingle(Predicate<TaskElement> filter) {
        List<TaskElement> taskElements = filterList(filter);
        return taskElements.isEmpty() ? null : taskElements.get(0);
    }

    /**
     * Filter the {@link TaskElement} list data based on condition {@code filter}
     * in {@link #getBeFilteredTaskElements()}.
     * @param filter the loading {@link Predicate Filter}.
     * @return Filter the results of the loaded list.
     */
    private List<TaskElement> filterList(Predicate<TaskElement> filter) {
        List<TaskElement> elements = getBeFilteredTaskElements();
        if (CollectionUtils.isEmpty(elements)) {
            return Collections.emptyList();
        }
        return elements.stream().filter(filter).collect(Collectors.toList());
    }

    /**
     * @return Return all {@link TaskElement} list data waiting for the filtered conditions to be executed.
     */
    protected abstract List<TaskElement> getBeFilteredTaskElements();
}
