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
 * Abstract base implementation of {@link ListenerContext}.
 *
 * <p>Encapsulates the two core context objects required for scheduled task listener callbacks:
 * the original task execution source context and the task repository operation context.
 * Subclasses inherit this abstract class and reuse the field initialization and getter implementations,
 * avoiding repetitive definition of context member variables and access methods.
 *
 * @param <T> the type of source context.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class AbstractListenerContext<T> implements ListenerContext {

    private final T sourceContext;

    private final RepositoryContext repositoryContext;

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
