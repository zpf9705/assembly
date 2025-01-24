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


package top.osjf.cron.spring.scheduler;

import top.osjf.cron.core.listener.ListenerContext;

/**
 * The listening context object of spring scheduler.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ListenerContextImpl implements ListenerContext {

    private final String id;
    private final ListenableRunnable listenableRunnable;

    /**
     * Creates a {@code ListenerContextImpl} by given {@code ListenableRunnable}.
     * @param listenableRunnable the scheduler listener {@code ListenableRunnable} obj.
     */
    public ListenerContextImpl(ListenableRunnable listenableRunnable) {
        this.id = listenableRunnable.getId();
        this.listenableRunnable = listenableRunnable;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public Object getSourceContext() {
        return listenableRunnable;
    }
}
