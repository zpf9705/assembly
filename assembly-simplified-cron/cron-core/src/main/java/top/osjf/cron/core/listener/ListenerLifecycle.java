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


package top.osjf.cron.core.listener;

import top.osjf.cron.core.lang.Nullable;

/**
 * The enumeration class is used to describe the execution lifecycle of {@code CronListener},
 * where each cycle has its own consumption function {@link Consumer}, and the methods of this
 * declaration cycle stage are executed based on the given parameters.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public enum ListenerLifecycle {

    /**
     * When the {@link CronListener#start} stage is executed.
     */
    START((cronListener, listenerContext, e) -> cronListener.start(listenerContext)),

    /**
     * When the {@link CronListener#success} stage is executed.
     */
    SUCCESS((cronListener, listenerContext, e) -> cronListener.success(listenerContext)),

    /**
     * When the {@link CronListener#failed} stage is executed.
     */
    FAILED(CronListener::failed);

    final Consumer consumer;

    /**
     * At the beginning stage, a {@link ListenerContext} instance will be generated based
     * on the provided {@code ListenerContext} type and related parameters. This instance
     * will be retained in {@link ThreadLocal} and deleted after the {@link #SUCCESS} or
     * {@link #FAILED} stage.
     */
    private static final ThreadLocal<ListenerContext> CONTEXT_LOCAL = new ThreadLocal<>();

    ListenerLifecycle(Consumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Perform phased execution on the provided {@link Consumer} based on the current enumerated
     * {@link CronListener} behavior.
     *
     * <p>{@link ListenerContext} will only be created when the state is {@link #START}, and then
     * retained until {@link #SUCCESS} or {@link #FAILED} is used up before being deleted.
     *
     * @param sourceContext     the original context object provided by the framework used
     *                          for executing scheduled tasks.
     * @param e                 error type object thrown during task execution only when failed.
     * @param collector         manage instance objects for listeners.
     */
    void consumerListeners(Object sourceContext, @Nullable Throwable e, CronListenerCollector collector) {
        if (START == this) {
            ListenerContext listenerContext = ListenerContextSupport.createListenerContext(collector, sourceContext);
            CONTEXT_LOCAL.set(listenerContext);
        }
        ListenerContext listenerContext = CONTEXT_LOCAL.get();
        if (listenerContext != null) {
            try {
                for (CronListener cronListener : collector.getCronListeners()) {
                    consumer.accept(cronListener, listenerContext, e);
                }
            }
            finally {
                if (SUCCESS == this || FAILED == this) {
                    CONTEXT_LOCAL.remove();
                }
            }
        }
    }

    /**
     * Provide {@link CronListener} consumption related parameters to execute the function interface
     * during the listening cycle phase.
     */
    @FunctionalInterface
    interface Consumer {
        void accept(CronListener cronListener, ListenerContext listenerContext, @Nullable Throwable e);
    }
}
