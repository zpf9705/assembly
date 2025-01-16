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
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.hutool.listener.HutoolCronListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

/**
 * The {@link CronTaskRepository} implementation class of hutool.
 *
 * <p>This implementation class includes the construction and lifecycle management
 * of the hutool build scheduler, as well as operations related to tasks and listeners.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronTaskRepository implements CronTaskRepository {

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

    private ExecutorService executorService;

    private boolean isMatchSecond = DEFAULT_VALUE_OF_MATCH_SECOND;

    private boolean daemon;

    private TimeZone timeZone = TimeZone.getDefault();

    private Scheduler scheduler;

    private boolean setMatchSecond;
    private boolean setDaemon;
    private boolean setTimeZone;

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
        if (superiorProperties != null) {
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
    }

    @Override
    @NotNull
    public String register(@NotNull String cronExpression, @NotNull TaskBody body) {
        return RepositoryUtils.doRegister(() ->
                scheduler.schedule(cronExpression, body.unwrap(RunnableTaskBody.class)
                        .getRunnable()), CronException.class);
    }

    @Override
    @NotNull
    public String register(@NotNull CronTask task) {
        return register(task.getExpression(), new RunnableTaskBody(task.getRunnable()));
    }

    @Nullable
    @Override
    public String getExpression(String id) {
        CronPattern pattern = scheduler.getPattern(id);
        return pattern != null ? pattern.toString() : null;
    }

    @Override
    public void update(@NotNull String taskId, @NotNull String newExpression) {
        RepositoryUtils.doVoidInvoke(() ->
                scheduler.updatePattern(taskId, new CronPattern(newExpression)), CronException.class);
    }

    @Override
    public void remove(@NotNull String taskId) {
        RepositoryUtils.doVoidInvoke(() -> scheduler.descheduleWithStatus(taskId), null);

    }

    @Override
    public void addListener(@NotNull CronListener listener) {
        scheduler.addListener(listener.unwrap(HutoolCronListener.class));
    }

    @Override
    public void removeListener(@NotNull CronListener listener) {
        scheduler.removeListener(listener.unwrap(HutoolCronListener.class));
    }

    @Override
    public void start() {
        if (isStarted()) {
            throw new IllegalStateException("Scheduler has been started, please stop it first!");
        }
        scheduler.start();
    }

    @Override
    @PreDestroy
    public void stop() {
        if (!isStarted()) {
            throw new IllegalStateException("Scheduler not started !");
        }
        scheduler.stop();
    }

    @Override
    public boolean isStarted() {
        return scheduler.isStarted();
    }
}
