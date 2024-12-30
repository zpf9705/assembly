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
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.cron4j.listener.Cron4jCronListener;

/**
 * The Cron4j cron task repository {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class Cron4jCronTaskRepository implements CronTaskRepository {

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
        scheduler.addSchedulerListener(listener.unwrap(Cron4jCronListener.class));
    }

    @Override
    public void removeListener(@NotNull CronListener listener) {
        scheduler.removeSchedulerListener(listener.unwrap(Cron4jCronListener.class));
    }
}
