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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.DefaultManagedTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.Assert;
import org.springframework.util.IdGenerator;
import org.springframework.util.SimpleIdGenerator;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.core.util.GsonUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * The {@code SpringSchedulerTaskRepository} class is a scheduled task repository that
 * implements the {@code CronTaskRepository} interface, utilizing Spring's {@link TaskScheduler}
 * to manage and schedule scheduled tasks.
 *
 * <p>This class provides the function of registering, updating, and removing scheduled tasks,
 * and supports defining the execution cycle of tasks through Cron expressions. It also supports
 * adding and removing task listeners to execute specific logic before and after task execution.
 *
 * <p>This class inherits from {@code ListenableTaskScheduler} and extends the related functions of
 * task registration provided by its {@link TaskScheduler}, ensuring that tasks registered using
 * this class can be listened to, and its lifecycle stages can add additional enhanced logic on
 * various methods of {@link CronListener}.
 *
 * <p>The {@link TaskScheduler} used in this class does not provide examples of declaration cycles.
 * Here, {@link AtomicBoolean} is used to control the start and stop states of tasks, ensuring that
 * the modification of task states using the {@link top.osjf.cron.core.lifecycle.Lifecycle} supported
 * methods in a multi-thread environment is thread safe.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 * @see CronTaskRepository
 * @see TaskScheduler
 * @see ListenableTaskScheduler
 * @see CronTrigger
 * @see ListenableRunnable
 * @see CronListener
 */
public class SpringSchedulerTaskRepository
        extends ListenableTaskScheduler
        implements InitializingBean, ApplicationListener<ContextRefreshedEvent>, DisposableBean {

    private final IdGenerator idGenerator = new SimpleIdGenerator();

    /**
     * Creates a new {@code SpringSchedulerTaskRepository} with default {@link DefaultManagedTaskScheduler}.
     */
    public SpringSchedulerTaskRepository() {
        super(new DefaultManagedTaskScheduler());
    }

    /**
     * Creates a new {@code SpringSchedulerTaskRepository} with given {@code TaskScheduler}.
     *
     * @param taskScheduler the given {@code TaskScheduler}.
     */
    public SpringSchedulerTaskRepository(@NotNull TaskScheduler taskScheduler) {
        super(taskScheduler);
    }

    @Override
    public void afterPropertiesSet() {
        super.start();
    }

    /**
     * {@inheritDoc}
     * Set all {@link CronListener} beans in the container to the current bean.
     *
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(@NotNull ContextRefreshedEvent event) {
        event.getApplicationContext().getBeansOfType(CronListener.class)
                .forEach((n, c) -> getCronListenerCollector().addLastCronListener(c));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ListenableRunnable wrapperRunnableToListenable(Runnable runnable, Trigger trigger) {
        String id = idGenerator.generateId().toString();
        return new DefaultListenableRunnable(id, runnable, trigger, getCronListenerCollector().getCronListeners());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull Runnable runnable) throws CronInternalException {
        ensureStarted();
        return RepositoryUtils.doRegister(() ->
                        schedule(runnable, new CronTrigger(expression)).getListenableRunnable().getId(),
                IllegalArgumentException.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull CronMethodRunnable runnable) throws CronInternalException {
        return register(expression, (Runnable) runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull RunnableTaskBody body) throws CronInternalException {
        return register(expression, body.getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull TaskBody body) {
        if (body.isWrapperFor(RunnableTaskBody.class)) {
            return register(expression, body.unwrap(RunnableTaskBody.class));
        }
        throw new UnsupportedTaskBodyException(body.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull top.osjf.cron.core.repository.CronTask task) {
        return register(task.getExpression(), task.getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public CronTaskInfo getCronTaskInfo(@NotNull String id) {
        ensureStarted();
        return buildCronTaskInfo(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CronTaskInfo> getAllCronTaskInfo() {
        ensureStarted();
        return getFutureIds()
                .stream()
                .map(this::buildCronTaskInfo)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Build a new {@code CronTaskInfo} by given id.
     *
     * @param id the given id.
     * @return a new {@code CronTaskInfo}.
     */
    @Nullable
    private CronTaskInfo buildCronTaskInfo(String id) {
        ListenableScheduledFuture future = getFuture(id);
        if (future == null) {
            return null;
        }
        ListenableRunnable listenableRunnable = future.getListenableRunnable();
        Trigger trigger = listenableRunnable.getTrigger();
        String expression = null;
        if (trigger instanceof CronTrigger) {
            expression = ((CronTrigger) trigger).getExpression();
        } else if (trigger instanceof PeriodicTrigger) {
            PeriodicTrigger periodicTrigger = (PeriodicTrigger) trigger;
            expression = toPeriodicTriggerExpression(periodicTrigger);
        }
        Runnable runnable = listenableRunnable.getRunnable();
        Object target = null;
        Method method = null;
        if (runnable instanceof CronMethodRunnable) {
            CronMethodRunnable cronMethodRunnable = (CronMethodRunnable) runnable;
            target = cronMethodRunnable.getTarget();
            method = cronMethodRunnable.getMethod();
        }
        return new CronTaskInfo(id, expression, runnable, target, method);
    }

    /**
     * Concatenate the properties of the {@link PeriodicTrigger} instance into a JSON
     * string as its expression.
     *
     * @param periodicTrigger the {@link PeriodicTrigger} instance.
     * @return the {@link PeriodicTrigger} json string.
     */
    private String toPeriodicTriggerExpression(PeriodicTrigger periodicTrigger) {
        return GsonUtils.toJson(periodicTrigger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(@NotNull String id, @NotNull String newExpression) {
        ensureStarted();
        ListenableScheduledFuture future = getFuture(id);
        Assert.notNull(future, "Missing task information according to id " + id);
        cancelFuture(id);
        register(newExpression, future.getListenableRunnable().getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(@NotNull String id) {
        ensureStarted();
        cancelFuture(id);
    }

    @Override
    public void destroy() {
        super.stop();
    }
}
