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


package top.osjf.cron.datasource.driven.scheduled.external.file;

import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract
class ExternalFileDatasourceTaskElementsOperation<T extends TaskElement> implements DatasourceTaskElementsOperation {

    private final ExternalFileTaskElementLoader<T> loader;

    /**
     * Constructs an {@code ExternalFileDatasourceTaskElementsOperation} with the given
     * {@link ExternalFileTaskElementLoader}.
     * @param loader the given {@link ExternalFileTaskElementLoader} instance.
     */
    public ExternalFileDatasourceTaskElementsOperation(ExternalFileTaskElementLoader<T> loader) {
        this.loader = loader;
        this.loader.init();
    }

    /**
     * @return the task element loader by provider.
     */
    public ExternalFileTaskElementLoader<T> getLoader() {
        return loader;
    }

    /**
     * {@inheritDoc}
     *
     * {@link #purgeDatasourceTaskElements} by {@link #loader}.
     */
    @Override
    public void purgeDatasourceTaskElements() {
        loader.purge();
    }

    /**
     * {@inheritDoc}
     *
     * {@link #getDatasourceTaskElements} by {@link #loader}.
     */
    @Override
    public List<TaskElement> getDatasourceTaskElements() {
        return Collections.unmodifiableList(loader.loading(Function.identity()));
    }

    /**
     * {@inheritDoc}
     *
     * {@link #afterStart} by {@link #loader}.
     */
    @Override
    public void afterStart(List<TaskElement> fulledDatasourceTaskElement) {
        loader.checkedUpdate(fulledDatasourceTaskElement);
    }

    /**
     * {@inheritDoc}
     *
     * {@link #getRuntimeNeedCheckDatasourceTaskElements} by {@link #loader}.
     */
    @Override
    public List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {

        List<T> filteredDatasourceTaskElements = loader.loading(yamlTaskElements -> yamlTaskElements.stream()
                .filter(t -> Objects.equals(t.getUpdateSign(), 1)
                        || (Objects.equals(t.getUpdateSign(), 1) && t.getTaskId() == null))
                .collect(Collectors.toList()));

        return Collections.unmodifiableList(filteredDatasourceTaskElements);
    }

    /**
     * {@inheritDoc}
     *
     * {@link #afterRun} by {@link #loader}.
     */
    @Override
    public void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement) {
        loader.checkedUpdate(runtimeCheckedDatasourceTaskElement);
    }

    /**
     * {@inheritDoc}
     *
     * {@link #getElementById} by {@link #loader}.
     */
    @Nullable
    @Override
    public TaskElement getElementById(String id) {
        return Optional.ofNullable(loader.loading(yamlTaskElements -> yamlTaskElements.stream()
                        .filter(element -> Objects.equals(id, element.getTaskId()))
                        .collect(Collectors.toList())))
                .map(l -> l.get(0))
                .orElse(null);
    }
}
