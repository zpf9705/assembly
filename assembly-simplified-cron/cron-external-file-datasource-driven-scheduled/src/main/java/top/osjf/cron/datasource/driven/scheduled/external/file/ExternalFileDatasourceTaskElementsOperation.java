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

import com.sun.nio.file.SensitivityWatchEventModifier;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.InitializeAble;
import top.osjf.cron.datasource.driven.scheduled.AbstractDatasourceDrivenScheduled;
import top.osjf.cron.datasource.driven.scheduled.DatasourceTaskElementsOperation;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.filewatch.AmapleWatchEvent;
import top.osjf.filewatch.AmpleFileWatchListener;
import top.osjf.filewatch.FileWatchService;
import top.osjf.filewatch.TriggerKind;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * An abstract base implementation of {@link DatasourceTaskElementsOperation} that
 * provides file-based persistence for task elements. This class serves as an adapter
 * between the task element operation interface and file-based storage through an
 * {@link ExternalFileTaskElementLoader}.
 *
 * <p>Key features:
 * <ul>
 *   <li>Abstract template for file-based task element operations
 *   {@code <T extends TaskElement>}</li>
 *   <li>Delegates actual file I/O to configured loader implementation
 *   {@link ExternalFileTaskElementLoader#loading(Function)}</li>
 *   <li>Provides thread-safe operation through loader synchronization</li>
 *   <li>Supports both batch and individual element operations
 *   {@link #getDatasourceTaskElements()} and {@link #getElementById(String)}</li>
 * </ul>
 *
 * <p>Typical usage:
 * <pre>{@code
 * // Create concrete implementation
 * class MyFileOperation extends ExternalFileDatasourceTaskElementsOperation<MyTaskElement> {
 *     // implement abstract methods
 * }
 *
 * // Initialize with appropriate loader
 * FileTaskElementLoader<MyTaskElement> loader = new YamlTaskElementLoader<>(file);
 * DatasourceTaskElementsOperation op = new MyFileOperation(loader);
 * op.initialize()
 * }</pre>
 *
 * <p>If the file timing task data source does not provide the main task information
 * check, the default value of {@link #registerDefaultIfMainTaskInfoNotProvided()}
 * in this abstract class is {@code false}, which means that the trigger of the main
 * task {@link AbstractDatasourceDrivenScheduled#run()} is handed over to {@link #fileWatchService}
 * for execution, and the original logic remains unchanged. We recommend this usage when
 * there is no autonomous check logic.
 *
 * @param <T> the type of task elements this operation handles, must extend {@link TaskElement}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see DatasourceTaskElementsOperation
 * @see ExternalFileTaskElementLoader
 * @see TaskElement
 */
public abstract
class ExternalFileDatasourceTaskElementsOperation<T extends TaskElement> implements DatasourceTaskElementsOperation,
        InitializeAble , Closeable {

    private final ExternalFileTaskElementLoader<T> loader;

    /**
     * @since 3.0.2
     */
    private FileWatchService fileWatchService;

    /**
     * @since 3.0.2
     */
    private AbstractDatasourceDrivenScheduled scheduled;

    /**
     * Constructs an {@code ExternalFileDatasourceTaskElementsOperation} with the given
     * {@link ExternalFileTaskElementLoader}.
     * @param loader the given {@link ExternalFileTaskElementLoader} instance.
     */
    public ExternalFileDatasourceTaskElementsOperation(ExternalFileTaskElementLoader<T> loader) {
        this.loader = loader;
    }

    /**
     * @return the task element loader by provider.
     */
    public ExternalFileTaskElementLoader<T> getLoader() {
        return loader;
    }

    /**
     * Init for {@link ExternalFileTaskElementLoader}.
     */
    @PostConstruct
    @Override
    public void initialize() {
        loader.initialize();
        fileWatchService = new FileWatchService();
        File configFile = loader.getConfigFile();
        fileWatchService.registerWatch(configFile.getParent(),
                false, SensitivityWatchEventModifier.MEDIUM, TriggerKind.ENTRY_MODIFY);
        fileWatchService.registerListener(new ExternalFileModifyListener());
        fileWatchService.start();
    }

    /**
     * Destroy for {@link ExternalFileTaskElementLoader}.
     */
    @PreDestroy
    public void destroy() throws IOException {
        close();
    }

    /**
     * Close {@link #loader} and {@link #fileWatchService}.
     * @since 3.0.2
     */
    @Override
    public void close() throws IOException {
        loader.close();
        fileWatchService.close();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean registerDefaultIfMainTaskInfoNotProvided() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAbstractDatasourceDrivenScheduled(AbstractDatasourceDrivenScheduled scheduled) {
        this.scheduled = scheduled;
    }

    /**
     * The {@link top.osjf.filewatch.FileWatchListener} listener implementation class for external
     * configuration file changes.
     * @since 3.0.2
     */
    private class ExternalFileModifyListener extends AmpleFileWatchListener {

        @Override
        public boolean supports(AmapleWatchEvent event) {
            return loader.getConfigFile().equals(event.getFile());
        }

        @Override
        public void onWatchEvent(AmapleWatchEvent event) {
            scheduled.run();
        }
    }

}
