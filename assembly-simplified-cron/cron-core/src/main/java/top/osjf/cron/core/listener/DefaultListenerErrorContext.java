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

import top.osjf.commons.util.Assert;
import top.osjf.cron.core.repository.RepositoryContext;

/**
 * Default implementation of {@link ListenerErrorContext}.
 *
 * <p>This context encapsulates the scene information when a single cron listener throws an exception
 * during execution. Extended based on the global task {@link ListenerContext}, it additionally
 * records the lifecycle stage where the exception occurred and the target listener instance that
 * threw the exception.
 *
 * <p>Usage Scenario: It is only used when a {@link CronListener} throws an exception in its lifecycle
 * callback method, applied to the local exception callback {@link CronListener#failed} of the current
 * listener. Different from {@link ListenerContext} which is used for global task failure and broadcasts
 * failure events to all registered listeners.
 *
 * <p>You can obtain the global unique task ID, business source context, repository operation context
 * from this context, and precisely locate which lifecycle and which cron listener triggered the execution
 * exception.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class DefaultListenerErrorContext implements ListenerErrorContext {

    private final ListenerContext listenerContext;

    private final ListenerLifecycle errorListenerLifecycle;

    private final CronListener errorCronListener;


    /**
     * Construct an instance of listener exception context.
     *
     * @param listenerContext         global original context of current cron task
     * @param errorListenerLifecycle  lifecycle enum when the exception occurred
     * @param errorCronListener       target cron listener that threw execution exception
     */
    public DefaultListenerErrorContext(ListenerContext listenerContext,
                                       ListenerLifecycle errorListenerLifecycle, CronListener errorCronListener) {

        Assert.notNull(listenerContext, "listenerContext must not be null");
        Assert.notNull(errorListenerLifecycle, "errorListenerLifecycle must not be null");
        Assert.notNull(errorCronListener, "errorCronListener must not be null");

        this.listenerContext = listenerContext;
        this.errorListenerLifecycle = errorListenerLifecycle;
        this.errorCronListener = errorCronListener;
    }

    @Override
    public String getID() {
        return listenerContext.getID();
    }

    @Override
    public Object getSourceContext() {
        return listenerContext.getSourceContext();
    }

    @Override
    public RepositoryContext getRepositoryContext() {
        return listenerContext.getRepositoryContext();
    }

    @Override
    public ListenerLifecycle getErrorListenerLifecycle() {
        return errorListenerLifecycle;
    }

    @Override
    public CronListener getErrorCronListener() {
        return errorCronListener;
    }
}
