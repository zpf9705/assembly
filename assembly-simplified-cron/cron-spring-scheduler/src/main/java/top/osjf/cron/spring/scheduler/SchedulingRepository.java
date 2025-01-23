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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.util.IdGenerator;
import org.springframework.util.SimpleIdGenerator;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.repository.RunnableTaskBody;
import top.osjf.cron.core.repository.TaskBody;
import top.osjf.cron.spring.scheduler.task.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code SchedulingRepository} is a class that extends {@code ManageableTaskSupport}
 * and implements {@code CronTaskRepository} and {@code EnhanceTaskConvertFactory}
 * interfaces , it provides management functionality for scheduled tasks, including
 * registering, updating, deleting scheduled tasks, and monitoring task changes.
 *
 * <p>This class stores scheduled tasks by using an internal cache {@link #scheduledTaskCache}
 * and registers these tasks using {@link ScheduledTaskRegistrar}. In addition, it also
 * supports defining the execution time of tasks through Cron expressions.
 *
 * <p> This class maintains a {@link ScheduledTask} to store registered scheduled tasks.
 * It also maintains a list of Cron Listeners {@link #cronListenerCollector}, used to
 * monitor changes in scheduled tasks. The {@code ScheduledTaskRegistrar} instance is used
 * to interact with Spring's scheduled task scheduler, the {@code IdGenerator} instance is
 * used to generate unique task identifiers.
 *
 * <p>Scheduling Repository provides multiple methods for managing scheduled tasks:
 * <ul>
 *     <li>register(String expression, TaskBody body): Register a new scheduled task based on the given
 *     Cron expression and task body, and return the unique identifier of the task</li>
 *     <li>register(CronTask task): Register a new scheduled task directly using the {@code CronTask}
 *     instance</li>
 *     <li>update(String id, String newExpression): Update the Cron expression of the scheduled task
 *     with the specified identifier</li>
 *     <li>remove(String id):  Delete the specified scheduled task based on the identifier</li>
 *     <li>addListener(CronListener listener): Add a listener to monitor changes in scheduled tasks</li>
 *     <li>removeListener(CronListener listener): Remove a listener</li>
 * </ul>
 *
 * <p>In addition, this class also provides conversion methods for Spring scheduled task configuration
 * objects (such as TriggerTask, CronTask, FixedDelayTask, FixedRateTask), allowing the original convert
 * the Spring scheduled task configuration object to an enhanced or manageable version.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class SchedulingRepository extends ManageableTaskSupport implements CronTaskRepository, EnhanceTaskConvertFactory,
        ApplicationListener<ContextRefreshedEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SchedulingRepository.class);

    private final Map<String, ScheduledTask> scheduledTaskCache = new ConcurrentHashMap<>();

    private final CronListenerCollector cronListenerCollector = new CronListenerCollectorImpl();

    private final ScheduledTaskRegistrar scheduledTaskRegistrar = new EnhanceScheduledTaskRegistrar(this);

    private final IdGenerator idGenerator = new SimpleIdGenerator();

    /**
     * Set all {@link CronListener} beans in the container to the current bean.
     *
     * <p><strong>Note:</strong></p>
     * If a bean depends on this bean {@code SchedulingRepository} and implements
     * {@code CronListener}, it is also a {@code CronListener} bean, which
     * will result in circular dependencies and program errors. It is recommended to
     * separate the logic extraction and processing.
     *
     * @param cronListeners all {@link CronListener} beans in the container.
     */
    @Autowired(required = false)
    public void setSchedulingListeners(List<CronListener> cronListeners) {
        for (CronListener cronListener : cronListeners) {
            cronListenerCollector.addCronListener(cronListener);
        }
    }

    /**
     * Registers a scheduled task in the cache, without overwriting an existing
     * task with the same ID.
     *
     * @param id            the unique identifier for the scheduled task.
     * @param scheduledTask the {@code ScheduledTask} object to be registered.
     */
    protected void registerScheduledTask(String id, ScheduledTask scheduledTask) {
        scheduledTaskCache.putIfAbsent(id, scheduledTask);
    }

    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        // Registration quantity result log input.
        logger.info("The total number of timed tasks successfully registered by the post processor " +
                "{} during this startup is <{}>.", getClass().getName(), scheduledTaskCache.size());
    }

    @Override
    @NotNull
    public String register(@NotNull String expression, @NotNull TaskBody body) {
        return registerOrUpdateSchedulingTask(expression, body, null);
    }

    @Override
    @NotNull
    public String register(@NotNull top.osjf.cron.core.repository.CronTask task) {
        return register(task.getExpression(), new RunnableTaskBody(task.getRunnable()));
    }

    @Nullable
    @Override
    public String getExpression(String id) {
        ScheduledTask scheduledTask = scheduledTaskCache.get(id);
        if (scheduledTask == null || !(scheduledTask.getTask()
                instanceof org.springframework.scheduling.config.CronTask)) {
            return null;
        }
        return ((org.springframework.scheduling.config.CronTask) scheduledTask.getTask()).getExpression();
    }

    @Override
    public void update(@NotNull String id, @NotNull String newExpression) {
        Runnable raw = scheduledTaskCache.get(id).getTask().getRunnable();
        remove(id);
        registerOrUpdateSchedulingTask(newExpression, new RunnableTaskBody(raw), id);
    }

    /**
     * Register or update for a scheduled task based on the given expression and
     * {@code TaskBody} instance, where the ID is not required to be provided.
     * When it is not provided, call {@link #newSchedulingRunnableInternal} to
     * generate it automatically, which can be used to update the task instance.
     *
     * @param expression the Cron expression defining the scheduling time for the task.
     * @param body       the task body containing the logic to be executed.
     * @param id         an optional unique identifier to identify this scheduled task.
     *                   If null, a unique identifier will be generated automatically.
     * @return The unique identifier returned after successful registration, used to
     * identify this scheduled task.
     */
    private String registerOrUpdateSchedulingTask(String expression, TaskBody body, @Nullable String id) {
        SchedulingRunnable schedulingRunnable
                = newSchedulingRunnableInternal(body.unwrap(RunnableTaskBody.class).getRunnable(), id);
        scheduledTaskRegistrar.scheduleCronTask(new CronTask(schedulingRunnable, expression));
        return schedulingRunnable.get().getId();
    }

    @Override
    public void remove(@NotNull String id) {
        ScheduledTask scheduledTask = scheduledTaskCache.remove(id);
        if (scheduledTask != null) {
            scheduledTask.cancel();
        }
    }

    @Override
    public void addListener(@NotNull CronListener listener) {
        cronListenerCollector.addCronListener(listener);
    }

    @Override
    public void removeListener(@NotNull CronListener listener) {
        cronListenerCollector.removeCronListener(listener);
    }

    @Override
    protected ScheduledTaskRegistrar getScheduledTaskRegistrar() {
        return scheduledTaskRegistrar;
    }

    @Override
    protected SchedulingRunnable newSchedulingRunnable(Runnable runnable) {
        return newSchedulingRunnableInternal(runnable, null);
    }

    /**
     * Creates a new {@code scheduling runnable} instance that encapsulates the
     * given {@code Runnable} object and assigns it a unique identifier if not
     * provided.
     *
     * @param runnable the Runnable object to be scheduled for execution.
     * @param id       an optional unique identifier to identify this scheduling
     *                 runnable instance. If null, a unique identifier will be
     *                 generated automatically.
     * @return a new {@code scheduling runnable} instance that encapsulates the given
     * Runnable, with a unique identifier and scheduling listeners set.
     */
    private SchedulingRunnable newSchedulingRunnableInternal(Runnable runnable, @Nullable String id) {
        if (id == null) {
            id = idGenerator.generateId().toString();
        }
        return new SchedulingRunnable(id, runnable, cronListenerCollector.getCronListeners());
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

    @Override
    public void start() {
        //start by spring...
    }

    @Override
    public void stop() {
        //stop by spring...
    }

    @Override
    public boolean isStarted() {
        //isStarted by spring...
        return false;
    }
}
