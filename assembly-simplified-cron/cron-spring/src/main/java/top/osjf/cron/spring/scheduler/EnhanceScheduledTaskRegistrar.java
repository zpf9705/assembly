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
        return registerScheduledTask(task, super.scheduleTriggerTask(task));
    }

    @Nullable
    @Override
    public ScheduledTask scheduleCronTask(@NonNull CronTask task) {
        return registerScheduledTask(task, super.scheduleCronTask(task));
    }

    @Nullable
    @Override
    public ScheduledTask scheduleFixedRateTask(@NonNull org.springframework.scheduling.config.FixedRateTask task) {
        return registerScheduledTask(task, super.scheduleFixedRateTask(task));
    }

    @Nullable
    @Override
    public ScheduledTask scheduleFixedDelayTask(@NonNull org.springframework.scheduling.config.FixedDelayTask task) {
        return registerScheduledTask(task, super.scheduleFixedDelayTask(task));
    }

    ScheduledTask registerScheduledTask(Task task, ScheduledTask scheduledTask) {
        if (task instanceof SchedulingInfoCapable) {
            SchedulingInfo schedulingInfo = ((SchedulingInfoCapable) task).getSchedulingInfo();
            repository.registerScheduledTask(schedulingInfo.getId(), scheduledTask);
        }
        return scheduledTask;
    }

    @Override
    public void addTriggerTask(@NonNull TriggerTask task) {
        super.addTriggerTask(repository.newTriggerTask(task));
    }

    @Override
    public void addCronTask(@NonNull CronTask task) {
        super.addCronTask(repository.newCronTask(task));
    }

    @Override
    public void addFixedRateTask(@NonNull IntervalTask task) {
        super.addFixedRateTask(repository.newFixedRateTask((FixedRateTask) task));
    }

    @Override
    public void addFixedDelayTask(@NonNull IntervalTask task) {
        super.addFixedDelayTask(repository.newFixedDelayTask((FixedDelayTask) task));
    }
}
