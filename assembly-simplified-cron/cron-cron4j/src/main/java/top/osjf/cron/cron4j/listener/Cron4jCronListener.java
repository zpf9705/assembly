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

package top.osjf.cron.cron4j.listener;

import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.TaskExecutor;
import top.osjf.cron.core.listener.CronListener;

/**
 * The Cron4j cron task {@link CronListener},reply to {@link SchedulerListener}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface Cron4jCronListener extends SchedulerListener, CronListener<TaskExecutor> {
    @Override
    default void taskLaunching(TaskExecutor executor) {
        onStart(executor);
    }

    @Override
    default void taskSucceeded(TaskExecutor executor) {
        onSucceeded(executor);
    }

    @Override
    default void taskFailed(TaskExecutor executor, Throwable exception) {
        onFailed(executor, exception);
    }

    @Override
    void onStart(TaskExecutor executor);

    @Override
    void onSucceeded(TaskExecutor executor);

    @Override
    void onFailed(TaskExecutor executor, Throwable exception);
}
