/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.cron.core.repository;

import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lifecycle.SuperiorProperties;

/**
 * An abstract implementation class of {@link RunTimeoutRegistrarRepository} that adds
 * a single timeout run on top of regular registration and run limit registration.
 *
 * <p>Relying on {@link TimeoutMonitoringRunnable} to achieve operational control over
 * {@link Runnable}, regular registration still uses APIs for {@link RunTimesRegistrarRepository}
 * and {@link GeneralRegistrarRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class AbstractRunTimeoutRegistrarRepository
        extends AbstractRunTimesRegistrarRepository implements RunTimeoutRegistrarRepository {

    private SuperiorPropertiesParsedThreadPoolExecutor monitoringExecutor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws Exception {
        super.initialize();
        SuperiorProperties superiorProperties = getSuperiorProperties();
        if (superiorProperties == null) {
            superiorProperties = SuperiorProperties.of(System.getProperties());
            setSuperiorProperties(superiorProperties);
        }
        monitoringExecutor = new SuperiorPropertiesParsedThreadPoolExecutor(superiorProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull Runnable runnable, @NotNull RunningTimeout timeout)
            throws CronInternalException {
        return register(expression, new TimeoutMonitoringRunnable(runnable, timeout, monitoringExecutor));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull CronMethodRunnable runnable,
                           @NotNull RunningTimeout timeout) throws CronInternalException {
        return register(expression, new TimeoutMonitoringRunnable(runnable, timeout, monitoringExecutor));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull RunnableTaskBody body, @NotNull RunningTimeout timeout)
            throws CronInternalException {
        return register(expression, body.getRunnable(), timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull TaskBody body, @NotNull RunningTimeout timeout)
            throws CronInternalException, UnsupportedTaskBodyException {
        return register(expression, new TimeoutMonitoringRunnable(asRunnable(body), timeout, monitoringExecutor));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull CronTask task, @NotNull RunningTimeout timeout) throws CronInternalException {
        return register(task.getExpression(), task.getRunnable(), timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull Runnable runnable, int times,
                                 @NotNull RunningTimeout timeout) throws CronInternalException {
        registerRunTimes(expression, new TimeoutMonitoringRunnable(runnable, timeout, monitoringExecutor), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull CronMethodRunnable runnable,
                                 int times, @NotNull RunningTimeout timeout) throws CronInternalException {
        registerRunTimes(expression, new TimeoutMonitoringRunnable(runnable, timeout, monitoringExecutor), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull RunnableTaskBody body, int times,
                                 @NotNull RunningTimeout timeout) throws CronInternalException {
        registerRunTimes(expression, body.getRunnable(), times, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull TaskBody body, int times,
                                 @NotNull RunningTimeout timeout) throws CronInternalException, UnsupportedTaskBodyException {
        registerRunTimes(expression, asRunnable(body), times, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull CronTask task, int times, @NotNull RunningTimeout timeout) throws CronInternalException {
        registerRunTimes(task.getExpression(), task.getRunnable(), times, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        super.stop();
        closeMonitoringExecutor();
    }

    /**
     * Close {@link #monitoringExecutor}.
     */
    protected void closeMonitoringExecutor() {
        if (monitoringExecutor != null) {
            monitoringExecutor.close();
        }
    }

    /**
     * Convert {@link TaskBody} as {@link Runnable}.
     * @param body the {@link TaskBody}.
     * @return the {@link Runnable} result after convert.
     */
    protected Runnable asRunnable(TaskBody body) throws UnsupportedTaskBodyException {
        if (body.isWrapperFor(Runnable.class)) {
            return body.unwrap(Runnable.class);
        } else if (body.isWrapperFor(RunnableTaskBody.class)) {
            return body.unwrap(RunnableTaskBody.class).getRunnable();
        }
        throw new UnsupportedTaskBodyException(body.getClass());
    }
}
