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

package top.osjf.cron.cron4j.repository;

import it.sauronsoftware.cron4j.InvalidPatternException;
import it.sauronsoftware.cron4j.Scheduler;
import it.sauronsoftware.cron4j.SchedulingPattern;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.cron4j.listener.Cron4jSchedulerListener;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.TimeZone;

/**
 * The {@link CronTaskRepository} implementation class of cron4j.
 *
 * <p>This implementation class includes the construction and lifecycle management
 * of the cron4j build scheduler, as well as operations related to tasks and listeners.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class Cron4jCronTaskRepository implements CronTaskRepository {

    /**
     * The {@link #daemon} property name of cron4j.
     */
    public static final String PROPERTY_NAME_OF_DAEMON = "isDaemon";
    private static final boolean DEFAULT_VALUE_OF_DAEMON = false;
    /**
     * The {@link #timezone} property name of cron4j.
     */
    public static final String PROPERTY_NAME_OF_TIMEZONE = "timezone";
    private static final TimeZone DEFAULT_VALUE_OF_TIMEZONE = TimeZone.getDefault();
    /**
     * The daemon flag. If true the scheduler and its spawned threads acts like
     * daemons.
     */
    private boolean daemon;

    /**
     * The time zone applied by the scheduler.
     */
    private TimeZone timezone = DEFAULT_VALUE_OF_TIMEZONE;

    private Scheduler scheduler;

    private boolean setDaemon;
    private boolean setTimeZone;

    /**
     * @since 1.0.3
     */
    private final Cron4jSchedulerListener schedulerListener = new Cron4jSchedulerListener();

    /**
     * @since 1.0.3
     */
    public Cron4jCronTaskRepository() {
    }

    /**
     * Creates a {@code Cron4jCronTaskRepository} by given {@code Scheduler} instance.
     *
     * @param scheduler the given {@code Scheduler} instance after initialize.
     * @since 1.0.3
     */
    public Cron4jCronTaskRepository(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    /**
     * Marks this scheduler daemon flag. When a scheduler is marked as a daemon
     * scheduler it spawns only daemon threads. The Java Virtual Machine exits
     * when the only threads running are all daemon threads.
     * <p>
     * This method must be called before the scheduler is started.
     *
     * @param daemon If true, the scheduler will spawn only daemon threads.
     * @throws IllegalStateException If the scheduler is started.
     */
    public void setDaemon(boolean daemon) {
        this.daemon = daemon;
        setDaemon = true;
    }

    /**
     * <p>
     * Sets the time zone applied by the scheduler.
     * </p>
     * <p>
     * Current system time is adapted to the supplied time zone before comparing
     * it with registered scheduling patterns. The result is that any supplied
     * scheduling pattern is treated according to the specified time zone. In
     * example, suppose:
     * </p>
     * <ul>
     * <li>System time: 10:00</li>
     * <li>System time zone: GMT+1</li>
     * <li>Scheduler time zone: GMT+3</li>
     * </ul>
     * <p>
     * The scheduler, before comparing system time with patterns, translates
     * 10:00 from GMT+1 to GMT+3. It means that 10:00 becomes 12:00. The
     * resulted time is then used by the scheduler to activate tasks. So, in the
     * given configuration at the given moment, any task scheduled as
     * <em>0 12 * * *</em> will be executed, while any <em>0 10 * * *</em> will
     * not.
     * </p>
     *
     * @param timezone The time zone applied by the scheduler.
     */
    public void setTimeZone(TimeZone timezone) {
        this.timezone = timezone;
        setTimeZone = true;
    }

    /**
     * Set the parameter {@link SuperiorProperties} object for building the cron4j task
     * scheduler, compatible with the Cron framework startup parameter series.
     *
     * <p>The configuration file cannot overwrite the value set by the external active
     * call to the set method.
     *
     * @param superiorProperties {@link SuperiorProperties} object for building the cron4j
     *                           task scheduler.
     * @since 1.0.3
     */
    public void setProperties(SuperiorProperties superiorProperties) {
        if (superiorProperties != null) {
            if (!setDaemon)
                setDaemon(superiorProperties.getProperty(PROPERTY_NAME_OF_DAEMON, DEFAULT_VALUE_OF_DAEMON));
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

    @PostConstruct
    public void initialize() {
        if (scheduler == null) {
            scheduler = new Scheduler();
            scheduler.setDaemon(daemon);
            scheduler.setTimeZone(timezone);
        }
        scheduler.addSchedulerListener(schedulerListener);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cron4j itself does not support cron expressions precise to seconds.
     * The cron expression of cron4j allows a maximum of 5 parts, each
     * separated by a space, representing "minute", "hour", "day", "month",
     * "week" from left to right, and does not include the second part.
     *
     * @param expression {@inheritDoc}
     * @param body       {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public String register(@NotNull String expression, @NotNull TaskBody body) {
        return RepositoryUtils.doRegister(() ->
                        scheduler.schedule(expression, body.unwrap(RunnableTaskBody.class).getRunnable()),
                InvalidPatternException.class);
    }

    @Override
    @NotNull
    public String register(@NotNull CronTask task) {
        return register(task.getExpression(), new RunnableTaskBody(task.getRunnable()));
    }

    @Nullable
    @Override
    public String getExpression(String id) {
        SchedulingPattern schedulingPattern = scheduler.getSchedulingPattern(id);
        return schedulingPattern != null ? schedulingPattern.toString() : null;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cron4j itself does not support cron expressions precise to seconds.
     * The cron expression of cron4j allows a maximum of 5 parts, each
     * separated by a space, representing "minute", "hour", "day", "month",
     * "week" from left to right, and does not include the second part.
     *
     * @param taskId        {@inheritDoc}
     * @param newExpression {@inheritDoc}
     */
    @Override
    public void update(@NotNull String taskId, @NotNull String newExpression) {
        RepositoryUtils.doVoidInvoke(() ->
                scheduler.reschedule(taskId, newExpression), InvalidPatternException.class);
    }

    @Override
    public void remove(@NotNull String taskId) {
        RepositoryUtils.doVoidInvoke(() ->
                scheduler.deschedule(taskId), null);
    }

    @Override
    public void addListener(@NotNull CronListener listener) {
        schedulerListener.addCronListener(listener);
    }

    @Override
    public void removeListener(@NotNull CronListener listener) {
        schedulerListener.removeCronListener(listener);
    }

    @Override
    public void start() {
        scheduler.start();
    }

    @Override
    @PreDestroy
    public void stop() {
        scheduler.stop();
    }

    @Override
    public boolean isStarted() {
        return scheduler.isStarted();
    }
}
