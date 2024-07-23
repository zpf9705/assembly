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
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronTaskNoExistException;
import top.osjf.cron.core.repository.CronListenerRepository;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.cron4j.listener.Cron4jCronListener;

/**
 * The Cron4j cron task {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class Cron4jCronTaskRepository implements CronTaskRepository<String, Runnable>,
        CronListenerRepository<Cron4jCronListener> {

    /*** scheduler management*/
    private final Scheduler scheduler;

    /**
     * Construct for create {@link Scheduler}.
     */
    public Cron4jCronTaskRepository() {
        this.scheduler = new Scheduler();
    }

    /**
     * Return the scheduled task management class of Cron4j.
     *
     * @return {@link Scheduler}.
     */
    public Scheduler getScheduler() {
        return scheduler;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cron4j itself does not support cron expressions precise to seconds.
     * The cron expression of cron4j allows a maximum of 5 parts, each
     * separated by a space, representing "minute", "hour", "day", "month",
     * "week" from left to right, and does not include the second part.
     *
     * @param cronExpression {@inheritDoc}
     * @param runnable       {@inheritDoc}
     * @return {@inheritDoc}
     * @throws Exception {@inheritDoc}
     */
    @Override
    public String register(String cronExpression, Runnable runnable) throws Exception {
        parseSchedulingPattern(cronExpression);
        return scheduler.schedule(cronExpression, runnable);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cron4j itself does not support cron expressions precise to seconds.
     * The cron expression of cron4j allows a maximum of 5 parts, each
     * separated by a space, representing "minute", "hour", "day", "month",
     * "week" from left to right, and does not include the second part.
     *
     * @param taskId            {@inheritDoc}
     * @param newCronExpression {@inheritDoc}
     * @throws Exception {@inheritDoc}
     */
    @Override
    public void update(String taskId, String newCronExpression) throws Exception {
        assertExist(taskId);
        scheduler.reschedule(taskId, parseSchedulingPattern(newCronExpression));
    }

    @Override
    public void remove(String taskId) {
        assertExist(taskId);
        scheduler.deschedule(taskId);
    }

    void assertExist(String taskId) {
        if (scheduler.getTask(taskId) == null) {
            throw new CronTaskNoExistException(taskId);
        }
    }

    SchedulingPattern parseSchedulingPattern(String strCronExpression) throws Exception {
        SchedulingPattern schedulingPattern;
        try {
            schedulingPattern = new SchedulingPattern(strCronExpression);
        } catch (InvalidPatternException e) {
            throw new CronExpressionInvalidException(strCronExpression, e);
        }
        return schedulingPattern;
    }

    @Override
    public void addCronListener(Cron4jCronListener cronListener) {
        scheduler.addSchedulerListener(cronListener);
    }

    @Override
    public void removeCronListener(Cron4jCronListener cronListener) {
        scheduler.removeSchedulerListener(cronListener);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CronListenerRepository<Cron4jCronListener> getCronListenerRepository() {
        return this;
    }
}
