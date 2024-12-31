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

package top.osjf.cron.spring.scheduler.task;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.config.FixedDelayTask;
import org.springframework.scheduling.config.FixedRateTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import top.osjf.cron.spring.scheduler.SchedulingRunnable;

/**
 * {@code ManageableTaskSupport} is an abstract class that provides support for
 * Spring scheduled task management.
 *
 * <p>By obtaining a {@link ScheduledTaskRegistrar} for managing scheduled tasks
 * and using a factory method to transform the original runtime {@link Runnable}
 * into a manageable {@link SchedulingRunnable} instance, tasks can be dynamically
 * added and managed at runtime.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class ManageableTaskSupport {

    /**
     * Return a {@code ScheduledTaskRegistrar} instance for registering and
     * managing scheduled tasks.
     *
     * @return a {@code ScheduledTaskRegistrar} instance.
     */
    protected abstract ScheduledTaskRegistrar getScheduledTaskRegistrar();

    /**
     * Return a manageable {@code SchedulingRunnable} instance that
     * encapsulates {@link Runnable}.
     *
     * @param runnable raw runnable.
     * @return new {@code SchedulingRunnable} instance.
     */
    protected abstract SchedulingRunnable newSchedulingRunnable(Runnable runnable);

    /**
     * Schedule a new trigger task.
     *
     * @param runnable the underlying task to execute.
     * @param trigger  specifies when the task should be executed.
     */
    public void scheduleTriggerTask(Runnable runnable, Trigger trigger) {
        getScheduledTaskRegistrar().scheduleTriggerTask(new TriggerTask(newSchedulingRunnable(runnable), trigger));
    }

    /**
     * Schedule a new fixed delay task.
     *
     * @param runnable     the underlying task to execute.
     * @param interval     how often in milliseconds the task should be executed.
     * @param initialDelay the initial delay before first execution of the task.
     */
    public void scheduleFixedDelayTask(Runnable runnable, long interval, long initialDelay) {
        getScheduledTaskRegistrar().scheduleFixedDelayTask(new FixedDelayTask(newSchedulingRunnable(runnable),
                interval, initialDelay));
    }

    /**
     * Schedule a new fixed rate task.
     *
     * @param runnable     the underlying task to execute.
     * @param interval     how often in milliseconds the task should be executed.
     * @param initialDelay the initial delay before first execution of the task.
     */
    public void scheduleFixedRateTask(Runnable runnable, long interval, long initialDelay) {
        getScheduledTaskRegistrar().scheduleFixedRateTask(new FixedRateTask(newSchedulingRunnable(runnable),
                interval, initialDelay));
    }
}
