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

import io.micrometer.core.annotation.Counted;
import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lifecycle.InitializeProperties;
import top.osjf.cron.core.micrometer.SystemPropertiesTags;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static top.osjf.cron.core.micrometer.RepositoryMicrometerConstants.*;

/**
 * Abstract base implementation of {@link RunTimeoutRegistrarRepository}.
 *
 * <p>This implementation extends {@link AbstractRunTimesRegistrarRepository}, adding
 * task execution timeout governance capability based on the existing limited-run scheduling feature.
 * It wraps the original task {@link Runnable} into {@link TimeoutMonitoringRunnable} to implement
 * interrupt control for long-running blocked tasks.
 *
 * <p>Core capabilities:
 * <ul>
 * <li>Initializes a dedicated thread pool for timeout task monitoring via {@link #initialize()},
 * configured from framework initialization properties or system default configurations.</li>
 * <li>Provides overloaded registration methods to bind {@link RunningTimeout} rules for tasks,
 * supporting both ordinary scheduled tasks and limited-execution scheduled tasks.</li>
 * <li>Automatically records each task's timeout configuration to a thread-safe map during task startup,
 * enabling subsequent query of task timeout rules via repository APIs.</li>
 * <li>Properly releases monitoring thread pool resources when the repository stops to avoid thread
 * leaks.</li>
 * <li>Supports conversion of various {@link TaskBody} types to standard {@link Runnable} for unified
 * timeout wrapping.</li>
 * </ul>
 *
 * <p>All timeout-related task registration methods integrate Micrometer metrics to count invocation times
 * for runtime observability analysis.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see AbstractRunTimesRegistrarRepository
 * @see RunTimeoutRegistrarRepository
 * @see TimeoutMonitoringRunnable
 * @see PropertiesParsedThreadPoolExecutor
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
    @Counted(value = REGISTER_TIMEOUT_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "register(String expression, Runnable runnable, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout configuration")
    @SystemPropertiesTags
    @Override
    public String register(String expression, Runnable runnable, RunningTimeout timeout)
            throws CronInternalException {
        return register(expression, wrapWithTimeoutMonitoring(runnable, timeout));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_TIMEOUT_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "register(String expression, CronMethodRunnable runnable, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout configuration")
    @SystemPropertiesTags
    public String register(String expression, CronMethodRunnable runnable, RunningTimeout timeout)
            throws CronInternalException {
        return register(expression, wrapWithTimeoutMonitoring(runnable, timeout));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_TIMEOUT_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "register(String expression, RunnableTaskBody body, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout configuration")
    @SystemPropertiesTags
    public String register(String expression, RunnableTaskBody body, RunningTimeout timeout)
            throws CronInternalException {
        return register(expression, body.getRunnable(), timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_TIMEOUT_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "register(String expression, TaskBody body, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout configuration")
    @SystemPropertiesTags
    public String register(String expression, TaskBody body, RunningTimeout timeout) throws CronInternalException {
        return register(expression, asRunnable(body), timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_TIMEOUT_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "register(CronTask task, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout configuration")
    @SystemPropertiesTags
    public String register(CronTask task, RunningTimeout timeout) throws CronInternalException {
        return register(task.getExpression(), task.getRunnable(), timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_TIMEOUT_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(String expression, Runnable runnable, int times, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout and limited " +
                    "run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(String expression, Runnable runnable, int times,
                                 RunningTimeout timeout) throws CronInternalException {
        return registerRunTimes(expression, wrapWithTimeoutMonitoring(runnable, timeout), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_TIMEOUT_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(String expression, CronMethodRunnable runnable, int times, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout and limited " +
                    "run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(String expression, CronMethodRunnable runnable,
                                 int times, RunningTimeout timeout) throws CronInternalException {
        return registerRunTimes(expression, wrapWithTimeoutMonitoring(runnable, timeout), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_TIMEOUT_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(String expression, RunnableTaskBody body, int times, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout and limited " +
                    "run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(String expression, RunnableTaskBody body, int times, RunningTimeout timeout)
            throws CronInternalException {
        return registerRunTimes(expression, body.getRunnable(), times, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_TIMEOUT_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(String expression, TaskBody body, int times, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout and limited " +
                    "run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(String expression, TaskBody body, int times, RunningTimeout timeout)
            throws CronInternalException {
        return registerRunTimes(expression, asRunnable(body), times, timeout);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_TIMEOUT_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(CronTask task, int times, RunningTimeout timeout)"},
            description = "Counts invocation times of cron task registration with execution timeout and limited " +
                    "run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(CronTask task, int times, RunningTimeout timeout) throws CronInternalException {
        return registerRunTimes(task.getExpression(), task.getRunnable(), times, timeout);
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
     *
     * <p>Binds the unique task ID to the wrapped timeout monitoring runnable, and persists the task
     * timeout configuration to the local cache for subsequent metadata query.
     */
    @Override
    public void call(String expression, Runnable runnable, String id) {
        if (runnable instanceof TimeoutMonitoringRunnable) {
            ((TimeoutMonitoringRunnable) runnable).setTaskId(id);
            taskRunTimeoutMap.putIfAbsent(id, ((TimeoutMonitoringRunnable) runnable).getTimeout());
        }
    }

    /**
     * Returns an immutable view of the task timeout configuration map to prevent external tampering
     * with internal runtime configuration data.
     *
     * @return unmodifiable map associating task ID with its bound {@link RunningTimeout} configuration
     * @since 3.0.2
     */
    protected Map<String, RunningTimeout> getTaskRunTimeoutMap() {
        return Collections.unmodifiableMap(taskRunTimeoutMap);
    }

    /**
     * Shuts down the timeout monitoring thread pool if it has been initialized,
     * releasing all occupied thread resources.
     */
    protected void closeMonitoringExecutor() {
        if (monitoringExecutor != null) {
            monitoringExecutor.close();
        }
    }

    /**
     * Unwraps the specified {@link TaskBody} instance and converts it to a standard {@link Runnable}.
     * Supports both native {@link Runnable} and wrapped {@link RunnableTaskBody} types.
     *
     * @param body the generic task body instance to unwrap
     * @return the underlying {@link Runnable} extracted from the task body
     * @throws UnsupportedTaskBodyException if the task body type cannot be converted to Runnable
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
     * Wraps the original raw {@link Runnable} into a timeout-aware {@link TimeoutMonitoringRunnable},
     * binding the specified timeout rule and the dedicated monitoring thread pool.
     *
     * @param raw     original user-defined task runnable
     * @param timeout task execution timeout governance configuration
     * @return wrapped runnable with timeout interrupt monitoring capability
     */
    protected Runnable wrapWithTimeoutMonitoring(Runnable raw, RunningTimeout timeout) {
        return new TimeoutMonitoringRunnable(raw, timeout, monitoringExecutor);
    }
}
