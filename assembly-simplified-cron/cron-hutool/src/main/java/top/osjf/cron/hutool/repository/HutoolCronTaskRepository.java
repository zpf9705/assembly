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
import top.osjf.cron.core.lifestyle.StartupProperties;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.hutool.listener.HutoolCronListener;

import javax.annotation.PostConstruct;
import java.util.Properties;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

/**
 * The Hutool cron task repository {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class HutoolCronTaskRepository implements CronTaskRepository {

    private ExecutorService executorService;

    private boolean isMatchSecond = true;

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
     * @param scheduler Custom {@code Scheduler} instance for task scheduling.
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
     * Set the parameter {@link StartupProperties} object for building the hutool task
     * scheduler, compatible with the Cron framework startup parameter series.
     *
     * <p>The configuration file cannot override the active settings of the set method.
     *
     * @param hutoolProperties {@link StartupProperties} object for building the hutool
     *                         task scheduler.
     * @since 1.0.3
     */
    public void setHutoolProperties(StartupProperties hutoolProperties) {
        if (hutoolProperties != null) {
            Properties properties = hutoolProperties.asProperties();
            if (!setDaemon)
                setDaemon(getProperty(properties, "daemon", false));
            if (!setMatchSecond)
                setMatchSecond(getProperty(properties, "isMatchSecond", true));
            if (!setTimeZone)
                setTimeZone(getProperty(properties, "timeZone", TimeZone.getDefault()));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getProperty(Properties properties, String propertyName, T def) {
        T propertyValue = (T) properties.get(propertyName);
        return propertyValue != null ? propertyValue : def;
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
    }

    /**
     * Set time zone.
     *
     * @param timeZone time Zone.
     * @since 1.0.3
     */
    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
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

    /**
     * Return a hutool {@code Scheduler} after initialize.
     *
     * @return a hutool {@code Scheduler} after initialize.
     */
    public Scheduler getScheduler() {
        return scheduler;
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
}
