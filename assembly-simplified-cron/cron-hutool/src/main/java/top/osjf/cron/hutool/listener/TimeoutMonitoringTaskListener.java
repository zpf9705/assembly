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


package top.osjf.cron.hutool.listener;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.cron.TaskExecutor;
import cn.hutool.cron.listener.TaskListener;
import cn.hutool.cron.task.CronTask;
import cn.hutool.cron.task.RunnableTask;
import cn.hutool.cron.task.Task;
import top.osjf.cron.core.repository.TimeoutMonitoringRunnable;
import top.osjf.cron.core.repository.TimeoutMonitoringRunnableContext;

/**
 * {@code TimeoutMonitoringTaskListener} is a listener function during a single execution
 * lifecycle, designed to add contextual {@link TimeoutMonitoringRunnable} information
 * to timeout monitoring mechanisms.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class TimeoutMonitoringTaskListener implements TaskListener {

    @Override
    public void onStart(TaskExecutor executor) {
        CronTask cronTask = executor.getCronTask();
        Task raw = cronTask.getRaw();
        if (raw instanceof RunnableTask) {
            Object value = ReflectUtil.getFieldValue(raw, "runnable");
            if (value instanceof TimeoutMonitoringRunnable) {
                TimeoutMonitoringRunnableContext.set((TimeoutMonitoringRunnable) value);
            }
        }
    }

    @Override
    public void onSucceeded(TaskExecutor executor) {
        TimeoutMonitoringRunnableContext.set(null);
    }

    @Override
    public void onFailed(TaskExecutor executor, Throwable exception) {
        TimeoutMonitoringRunnableContext.set(null);
    }
}
