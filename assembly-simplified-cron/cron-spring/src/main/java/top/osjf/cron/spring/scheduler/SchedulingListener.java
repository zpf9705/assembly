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

import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.ListenerContext;

/**
 * The spring scheduling cron task listener {@link CronListener}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class SchedulingListener implements CronListener {

    @Override
    public void start(ListenerContext context) {
        CronListener.super.start(context);
    }

    @Override
    public void success(ListenerContext context) {
        CronListener.super.success(context);
    }

    @Override
    public void failed(ListenerContext context, Throwable exception) {
        CronListener.super.failed(context, exception);
    }

    @Override
    public void startWithId(String id) {
    }

    @Override
    public void successWithId(String id) {
    }

    @Override
    public void failedWithId(String id, Throwable exception) {
    }
}
