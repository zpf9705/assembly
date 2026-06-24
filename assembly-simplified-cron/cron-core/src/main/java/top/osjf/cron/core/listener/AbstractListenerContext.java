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


package top.osjf.cron.core.listener;

import top.osjf.cron.core.repository.RepositoryContext;

/**
 /**
 * Abstract base implementation of {@link ListenerContext}, encapsulates the common runtime context
 * of scheduled tasks, including original business source context and repository operation context.
 *
 * <p>It is recommended to use subclasses of this abstract class {@link ListenerContext} for inheritance,
 * which can standardize and accommodate the dynamic creation of methods
 * {@code ListenerLifecycle#createListenerContext(CronListenerCollector, Object, RepositoryContext)}
 * related to {@link ListenerContext}, adapting the creation methods of Setter and constructor methods,
 * and reducing the complexity of context search.
 *
 * @param <T> generic type of original scheduled task business source context.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class AbstractListenerContext<T> implements ListenerContext {

    private T sourceContext;

    private RepositoryContext repositoryContext;

    /**
     * Create an abstract listener context without args.
     */
    public AbstractListenerContext() {
    }

    /**
     * Create an abstract listener context with specified repository context and source execution context.
     * @param sourceContext     original business context of the scheduled task trigger
     * @param repositoryContext task repository operation context
     */
    public AbstractListenerContext(T sourceContext, RepositoryContext repositoryContext) {
        this.sourceContext = sourceContext;
        this.repositoryContext = repositoryContext;
    }

    /**
     * Set the original business source context of current scheduled task.
     * @param sourceContext scheduled task trigger original business context
     */
    public void setSourceContext(T sourceContext) {
        this.sourceContext = sourceContext;
    }

    /**
     * Set the repository operation context for task data persistence.
     * @param repositoryContext task repository operation context instance
     */
    public void setRepositoryContext(RepositoryContext repositoryContext) {
        this.repositoryContext = repositoryContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSourceContext() {
        return sourceContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RepositoryContext getRepositoryContext() {
        return repositoryContext;
    }
}
