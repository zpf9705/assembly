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


package top.osjf.cron.hutool.repository;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.cron.Scheduler;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.CronTask;
import cn.hutool.cron.task.InvokeTask;
import cn.hutool.cron.task.RunnableTask;
import cn.hutool.cron.task.Task;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.repository.CronMethodRunnable;
import top.osjf.cron.core.repository.CronTaskInfo;

import java.lang.reflect.Method;

/**
 * The building util of hutool {@link CronTaskInfo}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class CronTaskInfoBuildUtils {

    /**
     * Build a new {@link CronTaskInfo} by given id and {@link Scheduler}.
     *
     * @param id                    the given task id.
     * @param scheduler             the input {@link Scheduler} instance.
     * @param remainingNumberOfRuns the remaining number of runs for this task.
     * @return a new {@link CronTaskInfo}.
     */
    @Nullable
    public static CronTaskInfo buildCronTaskInfo(String id, Scheduler scheduler, long remainingNumberOfRuns) {
        Task task = scheduler.getTask(id);
        CronPattern pattern = scheduler.getPattern(id);
        if (task == null || pattern == null) {
            return null;
        }
        Runnable runnable = null;
        Object target = null;
        Method method = null;
        try {
            if (task instanceof RunnableTask) {
                runnable = (Runnable) ReflectUtil.getFieldValue(task, "runnable");
                if (runnable instanceof CronMethodRunnable) {
                    CronMethodRunnable cronMethodRunnable = (CronMethodRunnable) runnable;
                    target = cronMethodRunnable.getTarget();
                    method = cronMethodRunnable.getMethod();
                }
            } else if (task instanceof InvokeTask) {
                target = ReflectUtil.getFieldValue(task, "obj");
                method = (Method) ReflectUtil.getFieldValue(task, "method");
            } else if (task instanceof CronTask) {
                CronTask cronTask = (CronTask) task;
                Task raw = cronTask.getRaw();
                if (raw instanceof RunnableTask) {
                    runnable = (Runnable) ReflectUtil.getFieldValue(task, "runnable");
                    if (runnable instanceof CronMethodRunnable) {
                        CronMethodRunnable cronMethodRunnable = (CronMethodRunnable) runnable;
                        target = cronMethodRunnable.getTarget();
                        method = cronMethodRunnable.getMethod();
                    }
                } else if (raw instanceof InvokeTask) {
                    target = ReflectUtil.getFieldValue(task, "obj");
                    method = (Method) ReflectUtil.getFieldValue(task, "method");
                }
            }
        } catch (Exception e) {
            runnable = task::execute;
        }
        if (runnable == null) {
            runnable = task::execute;
        }
        return new CronTaskInfo(id, pattern.toString(), runnable, target, method, remainingNumberOfRuns);
    }
}
