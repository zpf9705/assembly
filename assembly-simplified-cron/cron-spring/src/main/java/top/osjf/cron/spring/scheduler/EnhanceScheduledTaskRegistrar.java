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

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.config.*;

/**
 * The enhanced version of {@link ScheduledTaskRegistrar} added {@link SchedulingListener}
 * callbacks to the task execution cycle in version 1.0.0.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class EnhanceScheduledTaskRegistrar extends ScheduledTaskRegistrar {

    private final SchedulingRepository repository;

    public EnhanceScheduledTaskRegistrar(SchedulingRepository repository) {
        this.repository = repository;
    }

    @Nullable
    @Override
    public ScheduledTask scheduleTriggerTask(@NonNull TriggerTask task) {
        top.osjf.cron.spring.scheduler.task.TriggerTask triggerTask = repository.newTriggerTask(task);
        ScheduledTask scheduledTask;
        if ((scheduledTask = super.scheduleTriggerTask(triggerTask)) != null) {
            registerScheduledTask(triggerTask, scheduledTask);
        }
        return scheduledTask;
    }

    @Nullable
    @Override
    public ScheduledTask scheduleCronTask(@NonNull CronTask task) {
        top.osjf.cron.spring.scheduler.task.CronTask cronTask = repository.newCronTask(task);
        ScheduledTask scheduledTask;
        if ((scheduledTask = super.scheduleCronTask(cronTask)) != null) {
            registerScheduledTask(cronTask, scheduledTask);
        }
        return scheduledTask;
    }

    @Nullable
    @Override
    public ScheduledTask scheduleFixedRateTask(@NonNull org.springframework.scheduling.config.FixedRateTask task) {
        top.osjf.cron.spring.scheduler.task.FixedRateTask fixedRateTask = repository.newFixedRateTask(task);
        ScheduledTask scheduledTask;
        if ((scheduledTask = super.scheduleFixedRateTask(fixedRateTask)) != null) {
            registerScheduledTask(fixedRateTask, scheduledTask);
        }
        return scheduledTask;
    }

    @Nullable
    @Override
    public ScheduledTask scheduleFixedDelayTask(@NonNull org.springframework.scheduling.config.FixedDelayTask task) {
        top.osjf.cron.spring.scheduler.task.FixedDelayTask fixedDelayTask = repository.newFixedDelayTask(task);
        ScheduledTask scheduledTask;
        if ((scheduledTask = super.scheduleFixedDelayTask(fixedDelayTask)) != null) {
            registerScheduledTask(fixedDelayTask, scheduledTask);
        }
        return scheduledTask;
    }

    /**
     * Register a {@link ScheduledTask} within {@code key} is {@link Task}.
     *
     * @param task          Spring  task.
     * @param scheduledTask Spring scheduling task.
     */
    private void registerScheduledTask(Task task, ScheduledTask scheduledTask) {
        if (task instanceof SchedulingInfoSupplier) {
            SchedulingInfo schedulingInfo = ((SchedulingInfoSupplier) task).get();
            repository.registerScheduledTask(schedulingInfo.getId(), scheduledTask);
        }
    }
}
