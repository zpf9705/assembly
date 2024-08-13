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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.NamedThreadLocal;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronExpression;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronTaskNoExistException;
import top.osjf.cron.core.repository.CronListenerRepository;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.spring.scheduler.task.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Integrate the Spring scheduled task resource class of {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class SchedulingRepository extends AnyTaskSupport implements CronTaskRepository<String, Runnable>,
        TaskEnhanceConvertFactory, CronListenerRepository<SchedulingListener>, ApplicationContextAware, InitializingBean {

    /*** Internally, {@link ScheduledTask} is assigned an actual ID storage map to
     * record the unique ID of the task, in order to facilitate the implementation
     * of {@link CronTaskRepository}'s related operations.*/
    private final Map<String, ScheduledTask> scheduledTaskMap = new ConcurrentHashMap<>();

    /*** Used to temporarily store the ID of the current thread operation task.*/
    private final ThreadLocal<String> ID = new NamedThreadLocal<>("ID LOCAL");

    /*** Collection of scheduled task listeners.*/
    private final List<SchedulingListener> schedulingListeners = new ArrayList<>();

    /*** Enhanced version of timed task registration class.*/
    private final EnhanceScheduledTaskRegistrar taskRegistrar = new EnhanceScheduledTaskRegistrar(this);

    /*** spring context*/
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        schedulingListeners.addAll(applicationContext.getBeansOfType(SchedulingListener.class).values());
    }

    /**
     * Register the {@link ScheduledTask} task, assign a unique ID,
     * and in subsequent {@link CronTaskRepository} operations, update,
     * delete, and perform other operations based on the ID.
     *
     * @param id            unique ID of {@link ScheduledTask}.
     * @param scheduledTask spring scheduledTask.
     */
    public void registerScheduledTask(String id, ScheduledTask scheduledTask) {
        scheduledTaskMap.putIfAbsent(id, scheduledTask);
    }

    /**
     * Return enhanced version of timed task registration class.
     *
     * @return {@link EnhanceScheduledTaskRegistrar}.
     */
    public EnhanceScheduledTaskRegistrar getTaskRegistrar() {
        return taskRegistrar;
    }

    /**
     * Return a list of immutable listener collections.
     *
     * @return list of immutable listener collections.
     */
    public List<SchedulingListener> getSchedulingListeners() {
        return Collections.unmodifiableList(schedulingListeners);
    }

    @Override
    public String register(String cronExpression, Runnable runsBody) throws Exception {
        isValidExpression(cronExpression);
        SchedulingRunnable schedulingRunnable = newSchedulingRunnable(runsBody);
        taskRegistrar.scheduleCronTask(new CronTask(schedulingRunnable, cronExpression));
        return schedulingRunnable.getSchedulingInfo().getId();
    }

    @Override
    public void update(String id, String newCronExpression) throws Exception {
        exist(id);
        isValidExpression(newCronExpression);
        remove(id);
        ID.set(id);
        register(newCronExpression, scheduledTaskMap.get(id).getTask().getRunnable());
    }

    @Override
    public void remove(String id) {
        exist(id);
        scheduledTaskMap.get(id).cancel();
    }

    @Override
    public void addCronListener(SchedulingListener cronListener) {
        if (!schedulingListeners.contains(cronListener)) {
            schedulingListeners.add(cronListener);
        }
    }

    @Override
    public void removeCronListener(SchedulingListener cronListener) {
        schedulingListeners.remove(cronListener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CronListenerRepository<SchedulingListener> getCronListenerRepository() {
        return this;
    }

    @Override
    protected ScheduledTaskRegistrar getScheduledTaskRegistrar() {
        return taskRegistrar;
    }

    @Override
    protected SchedulingRunnable newSchedulingRunnable(Runnable runnable) {
        String id = ID.get();
        if (StringUtils.isBlank(id)) {
            id = generateID();
        } else ID.remove();
        return new SchedulingRunnable(id, runnable, getSchedulingListeners());
    }

    @Override
    public TriggerTask newTriggerTask(org.springframework.scheduling.config.TriggerTask triggerTask) {
        if (triggerTask instanceof TriggerTask) {
            return (TriggerTask) triggerTask;
        }
        return new TriggerTask(newSchedulingRunnable(triggerTask.getRunnable()), triggerTask.getTrigger());
    }

    @Override
    public CronTask newCronTask(org.springframework.scheduling.config.CronTask cronTask) {
        if (cronTask instanceof CronTask) {
            return (CronTask) cronTask;
        }
        return new CronTask(newSchedulingRunnable(cronTask.getRunnable()), cronTask.getExpression());
    }

    @Override
    public FixedDelayTask newFixedDelayTask(org.springframework.scheduling.config.FixedDelayTask fixedDelayTask) {
        if (fixedDelayTask instanceof FixedDelayTask) {
            return (FixedDelayTask) fixedDelayTask;
        }
        return new FixedDelayTask(newSchedulingRunnable(fixedDelayTask.getRunnable()),
                fixedDelayTask.getInterval(), fixedDelayTask.getInitialDelay());
    }

    @Override
    public FixedRateTask newFixedRateTask(org.springframework.scheduling.config.FixedRateTask fixedRateTask) {
        if (fixedRateTask instanceof FixedRateTask) {
            return (FixedRateTask) fixedRateTask;
        }
        return new FixedRateTask(newSchedulingRunnable(fixedRateTask.getRunnable()),
                fixedRateTask.getInterval(), fixedRateTask.getInitialDelay());
    }

    //generate ID using UUID
    private String generateID() {
        return UUID.randomUUID().toString();
    }

    //Valid cron Expression
    private void isValidExpression(String srtCronExpression) throws Exception {
        try {
            CronExpression.parse(srtCronExpression);
        } catch (Exception e) {
            throw new CronExpressionInvalidException(srtCronExpression, e);
        }
    }

    //check id exist
    private void exist(String id) {
        if (!scheduledTaskMap.containsKey(id)) {
            throw new CronTaskNoExistException(id);
        }
    }
}
