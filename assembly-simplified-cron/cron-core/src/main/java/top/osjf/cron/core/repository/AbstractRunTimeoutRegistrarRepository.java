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
import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.lifecycle.InitializeProperties;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * A thread-safe, immutable-reference map that stores the running timeout configuration for each task.
     * @since 3.0.2
     */
    private final Map<String, RunningTimeout> taskRunTimeoutMap = new ConcurrentHashMap<>(16);

    @Nullable private PropertiesParsedThreadPoolExecutor monitoringExecutor;


    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() throws Exception {
        super.initialize();
        InitializeProperties initializeProperties = getInitializeProperties();
        if (initializeProperties == null) {
            initializeProperties = InitializeProperties.systemProperties();
            setInitializeProperties(initializeProperties);
        }
        monitoringExecutor = new PropertiesParsedThreadPoolExecutor(initializeProperties);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, Runnable runnable, RunningTimeout timeout)
            throws CronInternalException {
        return register(expression, wrapWithTimeoutMonitoring(runnable, timeout));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, CronMethodRunnable runnable, RunningTimeout timeout)
            throws CronInternalException {
        return register(expression, wrapWithTimeoutMonitoring(runnable, timeout));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, RunnableTaskBody body, RunningTimeout timeout)
            throws CronInternalException {
        return register(expression, body.getRunnable(), timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, TaskBody body, RunningTimeout timeout) throws CronInternalException {
        return register(expression, asRunnable(body), timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(CronTask task, RunningTimeout timeout) throws CronInternalException {
        return register(task.getExpression(), task.getRunnable(), timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(String expression, Runnable runnable, int times,
                                 RunningTimeout timeout) throws CronInternalException {
        registerRunTimes(expression, wrapWithTimeoutMonitoring(runnable, timeout), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(String expression, CronMethodRunnable runnable,
                                 int times, RunningTimeout timeout) throws CronInternalException {
        registerRunTimes(expression, wrapWithTimeoutMonitoring(runnable, timeout), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(String expression, RunnableTaskBody body, int times, RunningTimeout timeout)
            throws CronInternalException {
        registerRunTimes(expression, body.getRunnable(), times, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(String expression, TaskBody body, int times, RunningTimeout timeout)
            throws CronInternalException {
        registerRunTimes(expression, asRunnable(body), times, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(CronTask task, int times, RunningTimeout timeout) throws CronInternalException {
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
     * {@inheritDoc}
     */
    @Override
    public void call(String expression, Runnable runnable, String id) {
        if (runnable instanceof TimeoutMonitoringRunnable) {
            ((TimeoutMonitoringRunnable) runnable).setTaskId(id);
            taskRunTimeoutMap.putIfAbsent(id, ((TimeoutMonitoringRunnable) runnable).getTimeout());
        }
    }

    /**
     * Returns an unmodifiable view of the task running timeout map.
     * @return an unmodifiable Map mapping task IDs (String) to their {@code RunningTimeout} objects.
     * @since 3.0.2
     */
    protected Map<String, RunningTimeout> getTaskRunTimeoutMap() {
        return Collections.unmodifiableMap(taskRunTimeoutMap);
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

    /**
     * Wrap the original {@link Runnable} into a timeout-detectable {@link TimeoutMonitoringRunnable}.
     * @param raw       the original {@link Runnable}.
     * @param timeout   the Timeout configuration parameters.
     * @return          Wrapper {@link TimeoutMonitoringRunnable} result.
     */
    protected Runnable wrapWithTimeoutMonitoring(Runnable raw, RunningTimeout timeout) {
        return new TimeoutMonitoringRunnable(raw, timeout, monitoringExecutor);
    }
}
