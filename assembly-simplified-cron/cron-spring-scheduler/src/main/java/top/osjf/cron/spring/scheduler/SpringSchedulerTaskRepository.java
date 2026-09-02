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
import org.springframework.scheduling.support.*;
import org.springframework.util.Assert;
import org.springframework.util.IdGenerator;
import org.springframework.util.SimpleIdGenerator;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.core.util.GsonUtils;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

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
    public void afterPropertiesSet() throws Exception {
        super.initialize();
        super.start(); // self-starting
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
                .forEach((n, c) -> addListener(c));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected ListenableRunnable wrapperRunnableToListenable(Runnable runnable, @Nullable Trigger trigger) {
        String id;
        IDGenerator idGenerator = getIDGenerator();
        if (idGenerator != null) {
            id = idGenerator.generate();
        }
        else {
            id = this.idGenerator.generateId().toString();
        }
        return new DefaultListenableRunnable(id, runnable, trigger, getCronListenerCollector(), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getName() {
        return "SPRING_SCHEDULER@" + super.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getSourceType() {
        return TaskScheduler.class.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getSourceVersion() {
        return "5.3.12";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportedExpression(@NotNull String expression) {
        return CronExpression.isValidExpression(expression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkSupportedExpression(@NotNull String expression) throws CronExpressionInvalidException {
        if (!isSupportedExpression(expression)) {
            throw new CronExpressionInvalidException(expression, getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull Runnable runnable) {
        return schedule(runnable, new CronTrigger(expression)).getListenableRunnable().getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull CronMethodRunnable runnable) {
        return registerInternal(expression, (Runnable) runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull RunnableTaskBody body) {
        return registerInternal(expression, body.getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull String expression, @NotNull TaskBody body)
            throws UnsupportedTaskBodyException{
        if (body.isWrapperFor(RunnableTaskBody.class)) {
            return registerInternal(expression, body.unwrap(RunnableTaskBody.class));
        }
        throw new UnsupportedTaskBodyException(body.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String registerInternal(@NotNull top.osjf.cron.core.repository.CronTask task) {
        return registerInternal(task.getExpression(), task.getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<String> getAllRegisteredTaskIds() {
        return getFutureIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public List<String> getAllRunningTaskIds() {
        return getRunningFutureIds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public Long getNextExecuteTime(@NotNull String id) {
        ListenableScheduledFuture future = getFuture(id);
        if (future == null) {
            return null;
        }
        Trigger trigger = future.getListenableRunnable().getTrigger();
        if (trigger == null) {
            return null;
        }
        Date nextExecutionTime
                = trigger.nextExecutionTime(new SimpleTriggerContext());
        return nextExecutionTime != null ? nextExecutionTime.toInstant().toEpochMilli() : null;
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
    public void updateInternal(@NotNull String id, @NotNull String newExpression) {
        ListenableScheduledFuture future = getFuture(id);
        Assert.notNull(future, "Missing task information according to id " + id);
        cancelFuture(id);
        register(newExpression, future.getListenableRunnable().getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeInternal(@NotNull String id) {
        cancelFuture(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void removeAllInternal() {
        cancelAllFutures();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCronTaskInfo(@NotNull String id) {
        return getFuture(id) != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CronTaskInfo getCronTaskInfoInternal(@NotNull String id) {
        ListenableScheduledFuture future = getFuture(id);
        if (future == null) {
            return null;
        }
        ListenableRunnable listenableRunnable = future.getListenableRunnable();
        Trigger trigger = listenableRunnable.getTrigger();
        String expression = null;
        if (trigger instanceof CronTrigger) {
            expression = ((CronTrigger) trigger).getExpression();
        }
        else if (trigger instanceof PeriodicTrigger) {
            PeriodicTrigger periodicTrigger = (PeriodicTrigger) trigger;
            expression = toPeriodicTriggerExpression(periodicTrigger);
        }
        Runnable runnable = listenableRunnable.getRunnable();
        runnable = unwrapRunnable(runnable);
        Object target = null;
        Method method = null;
        if (runnable instanceof CronMethodRunnable) {
            CronMethodRunnable cronMethodRunnable = (CronMethodRunnable) runnable;
            target = cronMethodRunnable.getTarget();
            method = cronMethodRunnable.getMethod();
        }
        else if (runnable instanceof ScheduledMethodRunnable) {
            ScheduledMethodRunnable scheduledMethodRunnable = (ScheduledMethodRunnable) runnable;
            target = scheduledMethodRunnable.getTarget();
            method = scheduledMethodRunnable.getMethod();
        }
        return new CronTaskInfo(id, expression, runnable, target, method);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void terminateInternal(@NotNull String id) {
        terminateFuture(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void terminateAllInternal() {
        terminateAllFutures();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportConcurrentExecution() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @NotNull
    @Override
    protected String runBodyWrapperClassName() {
        return DefaultListenableRunnable.class.getName();
    }

    @Override
    public void destroy() {
        super.stop();
    }
}
