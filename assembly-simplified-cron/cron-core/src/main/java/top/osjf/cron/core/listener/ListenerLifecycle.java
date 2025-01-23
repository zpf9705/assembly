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

import java.util.List;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public enum ListenerLifecycle {

    START((cronListener, listenerContext, e) -> cronListener.start(listenerContext)),

    SUCCESS((cronListener, listenerContext, e) -> cronListener.success(listenerContext)),

    FAILED(CronListener::failed);

    final Consumer consumer;

    private static final ThreadLocal<ListenerContext> CONTEXT_LOCAL = new ThreadLocal<>();

    ListenerLifecycle(Consumer consumer) {
        this.consumer = consumer;
    }

    void consumerListeners(List<CronListener> cronListeners, Object sourceContext, @Nullable Throwable e,
                           CronListenerCollector collector) {
        if (START == this) {
            ListenerContext listenerContext = ListenerContextSupport.createListenerContext(collector, sourceContext);
            CONTEXT_LOCAL.set(listenerContext);
        }
        ListenerContext listenerContext = CONTEXT_LOCAL.get();
        if (listenerContext != null) {
            try {
                for (CronListener cronListener : cronListeners) {
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

    interface Consumer {
        void accept(CronListener cronListener, ListenerContext listenerContext, Throwable e);
    }
}
