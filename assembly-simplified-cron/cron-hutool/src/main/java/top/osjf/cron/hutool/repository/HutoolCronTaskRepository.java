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

import cn.hutool.cron.CronException;
import cn.hutool.cron.Scheduler;
import cn.hutool.cron.pattern.CronPattern;
import cn.hutool.cron.task.InvokeTask;
import cn.hutool.cron.task.Task;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.hutool.listener.TaskListenerImpl;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * The {@link CronTaskRepository} implementation class of hutool.
 *
 * <p>This implementation class includes the construction and lifecycle management
 * of the hutool build scheduler, as well as operations related to tasks and listeners.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronTaskRepository extends AbstractCronTaskRepository {

    /**
     * The {@link #daemon} property name of hutool.
     */
    public static final String PROPERTY_NAME_OF_DAEMON = "isDaemon";
    private static final boolean DEFAULT_VALUE_OF_DAEMON = false;
    /**
     * The {@link #timeZone} property name of hutool.
     */
    public static final String PROPERTY_NAME_OF_TIMEZONE = "timezone";
    private static final TimeZone DEFAULT_VALUE_OF_TIMEZONE = TimeZone.getDefault();
    /**
     * The {@link #isMatchSecond} property name of hutool.
     */
    public static final String PROPERTY_NAME_OF_MATCH_SECOND = "isMatchSecond";
    private static final boolean DEFAULT_VALUE_OF_MATCH_SECOND = true;
    /**
     * The {@link #isMatchSecond} property name of hutool.
     */
    public static final String PROPERTY_NAME_OF_IF_STOP_CLEAR_TASK = "isIfStopClearTasks";
    private static final boolean DEFAULT_VALUE_OF_IF_STOP_CLEAR_TASK = true;

    private ExecutorService executorService;

    private boolean isMatchSecond = DEFAULT_VALUE_OF_MATCH_SECOND;

    private boolean daemon;

    /**
     * This flag indicates whether to clear the related task list when closing the scheduler.
     * If {@code #daemon == true}, this value does not need to be set, otherwise it needs to
     * be monitored.
     */
    private boolean ifStopClearTasks;

    private TimeZone timeZone = TimeZone.getDefault();

    private Scheduler scheduler;

    private boolean setMatchSecond;
    private boolean setDaemon;
    private boolean setTimeZone;
    private boolean setIfStopClearTasks;

    /**
     * @since 1.0.3
     */
    private final TaskListenerImpl taskListener = new TaskListenerImpl();

    /**
     * @since 1.0.3
     */
    public HutoolCronTaskRepository() {
    }

    /**
     * The constructor with parameter {@code Scheduler} allows developers to pass
     * in a custom Scheduler instance to initialize the task repository.
     *
     * @param scheduler Custom {@code Scheduler} instance for task scheduling after
     *                  initialize.
     * @since 1.0.3
     */
    public HutoolCronTaskRepository(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Set custom thread pool.
     *
     * <p>When customizing a thread pool, it is necessary to consider whether the thread
     * executing the method is a daemon thread {@link #setDaemon}.
     *
     * @param executorService Custom thread pool service instance.
     * @since 1.0.3
     */
    public void setThreadExecutor(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Set the parameter {@link SuperiorProperties} object for building the hutool task
     * scheduler, compatible with the Cron framework startup parameter series.
     *
     * <p>The configuration file cannot overwrite the value set by the external active
     * call to the set method.
     *
     * @param superiorProperties {@link SuperiorProperties} object for building the hutool
     *                           task scheduler.
     * @since 1.0.3
     */
    public void setProperties(SuperiorProperties superiorProperties) {
        if (superiorProperties != null && !superiorProperties.isEmpty()) {
            if (!setDaemon)
                setDaemon(superiorProperties.getProperty(PROPERTY_NAME_OF_DAEMON, DEFAULT_VALUE_OF_DAEMON));
            if (!setMatchSecond)
                setMatchSecond(superiorProperties.getProperty(PROPERTY_NAME_OF_MATCH_SECOND, DEFAULT_VALUE_OF_MATCH_SECOND));
            if (!setTimeZone) {
                Object zone = superiorProperties.getProperty(PROPERTY_NAME_OF_TIMEZONE);
                if (zone instanceof TimeZone) {
                    setTimeZone((TimeZone) zone);
                } else {
                    TimeZone timeZone = DEFAULT_VALUE_OF_TIMEZONE;
                    if (zone != null) {
                        timeZone = TimeZone.getTimeZone(zone.toString());
                    }
                    setTimeZone(timeZone);
                }
            }
            if (!setIfStopClearTasks) {
                setIfStopClearTasks(superiorProperties.getProperty(PROPERTY_NAME_OF_IF_STOP_CLEAR_TASK,
                        DEFAULT_VALUE_OF_IF_STOP_CLEAR_TASK));
            }
        }
    }

    /**
     * Set whether to support second matching.
     *
     * <p>This method is used to define whether to use the second matching mode.
     * If it is {@code true}, the first digit in the timed task expression is seconds,
     * otherwise it is minutes, and the default is minutes.
     *
     * @param matchSecond {@code true} supports, {@code false} does not support.
     * @since 1.0.3
     */
    public void setMatchSecond(boolean matchSecond) {
        isMatchSecond = matchSecond;
        setMatchSecond = true;
    }

    /**
     * Set whether to be a guardian thread.
     *
     * <p>If true, the scheduled task executed immediately after calling the
     * {@link Scheduler#stop()} method ends;otherwise, it waits for execution to
     * complete before ending. Default non daemon thread.
     *
     * <p>If the user calls {@link #setThreadExecutor} custom thread pool, this
     * parameter is invalid.
     *
     * @param daemon {@code true} is a daemon thread, otherwise it is not a daemon thread
     * @since 1.0.3
     */
    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
        setDaemon = true;
    }

    /**
     * Set time zone.
     *
     * @param timeZone time Zone.
     * @since 1.0.3
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
        setTimeZone = true;
    }

    /**
     * Set a boolean flag to control whether to clear tasks when closing the scheduler.
     *
     * @param ifStopClearTasks the boolean flag of when stop clear tasks.
     * @since 1.0.3
     */
    public void setIfStopClearTasks(boolean ifStopClearTasks) {
        this.ifStopClearTasks = ifStopClearTasks;
        setIfStopClearTasks = true;
    }

    /**
     * Initialize the scheduled task manager based on the provided attributes.
     *
     * @since 1.0.3
     */
    @PostConstruct
    public void initialize() {
        if (scheduler == null) {
            scheduler = new Scheduler();
            scheduler.setDaemon(daemon);
            scheduler.setMatchSecond(isMatchSecond);
            scheduler.setTimeZone(timeZone);
            scheduler.setThreadExecutor(executorService);
        }
        scheduler.addListener(taskListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull Runnable runnable) throws CronInternalException {
        return RepositoryUtils.doRegister(() ->
                scheduler.schedule(expression, runnable), CronException.class);
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
     *
     * @param expression {@inheritDoc}
     * @param body       {@link RunnableTaskBody} or {@link DefineIDRunnableTaskBody} or {@link InvokeTaskBody}
     *                   or {@link SettingTaskBody}.
     * @return {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull TaskBody body) {
        if (body.isWrapperFor(DefineIDRunnableTaskBody.class)) {
            DefineIDRunnableTaskBody defineIDRunnableTaskBody = body.unwrap(DefineIDRunnableTaskBody.class);
            String id = defineIDRunnableTaskBody.getId();
            Task task = scheduler.getTask(id);
            if (task != null) {
                throw new CronInternalException("The task corresponding to id " + id + "already exists!");
            }
            return RepositoryUtils.doRegister(() -> {
                scheduler.schedule(id, expression, defineIDRunnableTaskBody.getRunnable());
                return id;
            }, CronException.class);
        } else if (body.isWrapperFor(InvokeTaskBody.class)) {
            InvokeTask invokeTask = body.unwrap(InvokeTaskBody.class).getInvokeTask();
            return RepositoryUtils.doRegister(() -> scheduler.schedule(expression, invokeTask), CronException.class);
        } else if (body.isWrapperFor(RunnableTaskBody.class)) {
            return register(expression, body.unwrap(RunnableTaskBody.class));
        } else if (body.isWrapperFor(SettingTaskBody.class)) {
            return RepositoryUtils.doRegister(() -> {
                SettingTaskBody settingTaskBody = body.unwrap(SettingTaskBody.class);
                scheduler.schedule(settingTaskBody.getSetting());
                /* the IDs in the order of configuration. */
                return scheduler.getTaskTable().getIds().stream()
                        .filter(id -> id.startsWith("id_"))
                        .collect(Collectors.joining(","));
            }, CronException.class);
        }
        throw new UnsupportedTaskBodyException(body.getClass());
    }

    @Override
    public String register(@NotNull CronTask task) {
        return register(task.getExpression(), new RunnableTaskBody(task.getRunnable()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nullable
    public CronTaskInfo getCronTaskInfo(@NotNull String id) {
        return CronTaskInfoBuildUtils.buildCronTaskInfo(id, scheduler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CronTaskInfo> getAllCronTaskInfo() {
        return scheduler.getTaskTable()
                .getIds()
                .stream()
                .map(this::getCronTaskInfo)
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(@NotNull String taskId, @NotNull String newExpression) {
        RepositoryUtils.doVoidInvoke(() ->
                scheduler.updatePattern(taskId, new CronPattern(newExpression)), CronException.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(@NotNull String taskId) {
        RepositoryUtils.doVoidInvoke(() -> scheduler.descheduleWithStatus(taskId), null);

    }

    @Override
    protected CronListenerCollector getCronListenerCollector() {
        return taskListener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        if (isStarted()) {
            throw new IllegalStateException("Scheduler has been started, please stop it first!");
        }
        scheduler.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @PreDestroy
    public void stop() {
        if (!isStarted()) {
            throw new IllegalStateException("Scheduler not started !");
        }
        scheduler.stop(ifStopClearTasks);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStarted() {
        return scheduler.isStarted();
    }

    @Override
    public void reStart() {
        if (isStarted()) {
            stop();
            start();
        }
        else {
            start();
        }
    }
}
