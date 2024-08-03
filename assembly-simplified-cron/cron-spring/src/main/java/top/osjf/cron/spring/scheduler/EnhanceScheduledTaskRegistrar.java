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
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * The enhanced version of {@link ScheduledTaskRegistrar} added {@link SchedulingListener}
 * callbacks to the task execution cycle in version 1.0.0.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class EnhanceScheduledTaskRegistrar extends ScheduledTaskRegistrar {

    /*** Scheduling repository.*/
    private final SchedulingRepository repository;

    /*** {@code ScheduledTaskRegistrar#unresolvedTasks}.*/
    private Map<Task, ScheduledTask> unresolvedTasks;

    /*** Constructor for {@link ScheduledTask}.*/
    private Constructor<ScheduledTask> constructor;

    public EnhanceScheduledTaskRegistrar(SchedulingRepository repository) {
        this.repository = repository;
    }

    @Nullable
    @Override
    public ScheduledTask scheduleTriggerTask(@NonNull TriggerTask task) {
        if (getScheduler() == null) {
            ScheduledTask scheduledTask = getScheduledTask(task);
            top.osjf.cron.spring.scheduler.task.TriggerTask triggerTask = repository.newTriggerTask(task);
            getParentUnresolvedTasks().put(triggerTask, scheduledTask);
            addTriggerTask(triggerTask);
            return scheduledTask;
        }
        ScheduledTask scheduledTask = getParentUnresolvedTasks().get(task);
        super.scheduleTriggerTask(task);
        return registerScheduledTask(task, scheduledTask);
    }

    @Nullable
    @Override
    public ScheduledTask scheduleCronTask(@NonNull CronTask task) {
        if (getScheduler() == null) {
            ScheduledTask scheduledTask = getScheduledTask(task);
            top.osjf.cron.spring.scheduler.task.CronTask cronTask = repository.newCronTask(task);
            getParentUnresolvedTasks().put(cronTask, scheduledTask);
            addCronTask(cronTask);
            return scheduledTask;
        }
        ScheduledTask scheduledTask = getParentUnresolvedTasks().get(task);
        super.scheduleCronTask(task);
        return registerScheduledTask(task, scheduledTask);
    }

    @Nullable
    @Override
    public ScheduledTask scheduleFixedRateTask(@NonNull org.springframework.scheduling.config.FixedRateTask task) {
        if (getScheduler() == null) {
            ScheduledTask scheduledTask = getScheduledTask(task);
            top.osjf.cron.spring.scheduler.task.FixedRateTask fixedRateTask = repository.newFixedRateTask(task);
            getParentUnresolvedTasks().put(fixedRateTask, scheduledTask);
            addFixedRateTask(fixedRateTask);
            return scheduledTask;
        }
        ScheduledTask scheduledTask = getParentUnresolvedTasks().get(task);
        super.scheduleFixedRateTask(task);
        return registerScheduledTask(task, scheduledTask);
    }

    @Nullable
    @Override
    public ScheduledTask scheduleFixedDelayTask(@NonNull org.springframework.scheduling.config.FixedDelayTask task) {
        if (getScheduler() == null) {
            ScheduledTask scheduledTask = getScheduledTask(task);
            top.osjf.cron.spring.scheduler.task.FixedDelayTask fixedDelayTask = repository.newFixedDelayTask(task);
            getParentUnresolvedTasks().put(fixedDelayTask, scheduledTask);
            addFixedDelayTask(fixedDelayTask);
            return scheduledTask;
        }
        ScheduledTask scheduledTask = getParentUnresolvedTasks().get(task);
        super.scheduleFixedDelayTask(task);
        return registerScheduledTask(task, scheduledTask);
    }

    /**
     * Return a new {@link ScheduledTask} using reflect {@code ScheduledTask#ScheduledTask}.
     *
     * @param task spring task.
     * @return Spring scheduling task.
     */
    private ScheduledTask getScheduledTask(Task task) {
        ScheduledTask scheduledTask = getParentUnresolvedTasks().remove(task);
        if (scheduledTask == null) {
            try {
                Constructor<ScheduledTask> constructor = getScheduledTaskConstructor();
                ReflectionUtils.makeAccessible(constructor);
                scheduledTask = constructor.newInstance(task);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return scheduledTask;
    }

    /**
     * Return a {@link ScheduledTask} default constructor using reflect.
     *
     * @return default constructor {@link ScheduledTask}.
     */
    private Constructor<ScheduledTask> getScheduledTaskConstructor() {
        if (constructor == null) {
            try {
                constructor = ScheduledTask.class.getDeclaredConstructor(Task.class);
            } catch (Exception ignored) {
            }
        }
        return constructor;
    }

    /**
     * Return the private property field {@code unresolvedTasks } inherited
     * from the parent class {@link ScheduledTaskRegistrar}.
     *
     * @return unresolved Tasks.
     */
    @SuppressWarnings("unchecked")
    private Map<Task, ScheduledTask> getParentUnresolvedTasks() {
        if (unresolvedTasks == null) {
            Field field = ReflectionUtils.findField(getClass().getSuperclass(), "unresolvedTasks");
            if (field != null) {
                ReflectionUtils.makeAccessible(field);
                unresolvedTasks = (Map<Task, ScheduledTask>) ReflectionUtils.getField(field, this);
            }
        }
        return unresolvedTasks;
    }

    /**
     * Register a {@link ScheduledTask} within {@code key} is {@link Task}.
     *
     * @param task          Spring  task.
     * @param scheduledTask Spring scheduling task.
     * @return Spring scheduling task.
     */
    private ScheduledTask registerScheduledTask(Task task, ScheduledTask scheduledTask) {
        if (task instanceof SchedulingInfoCapable) {
            SchedulingInfo schedulingInfo = ((SchedulingInfoCapable) task).getSchedulingInfo();
            repository.registerScheduledTask(schedulingInfo.getId(), scheduledTask);
        }
        return scheduledTask;
    }
}
