/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.cron.spring;

import cn.hutool.cron.TaskExecutor;
import cn.hutool.cron.listener.TaskListener;

/**
 * The monitoring interface for timed task start, end, and exception
 * mainly relies on {@link TaskListener},which can be registered through
 * {@link CronTaskManager#addCronListeners(CronListener...)} and used for task
 * monitoring during startup.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface CronListener extends TaskListener {

    /**
     * Triggered when a scheduled task starts.
     * @since 2.2.5
     * @param executor Job executor.
     */
    void onStart(TaskExecutor executor);

    /**
     * Triggered when the task successfully ends.
     * @since 2.2.5
     * @param executor Job executor.
     */
    void onSucceeded(TaskExecutor executor);

    /**
     * Triggered when the task fails to start.
     * @since 2.2.5
     * @param executor Job executor.
     * @param exception The specific exception captured by the
     *                  listener when a scheduled task is abnormal.
     */
    void onFailed(TaskExecutor executor, Throwable exception);
}
