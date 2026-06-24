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


package top.osjf.cron.core.repository;

import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.micrometer.CronTaskMicrometer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This abstract class encapsulates the unified public logic of all cron task repository
 * implementations: parameter pre-verification, unified callback execution after task
 * registration, default implementation of general capability methods defined in
 * {@link CronTaskRepository}, including expression validity judgment, remaining execution
 * times query, task timeout configuration acquisition, task metadata supplementation and
 * wrapping Runnable unwrapping.
 *
 * <p>
 * It splits the task operation logic into two layers:
 * <ol>
 * <li>External public methods: complete unified parameter check, general cross-cutting logic
 * such as callback notification;</li>
 * <li>Protected abstract {@code xxxInternal} methods: only responsible for the underlying task
 * storage, modification, deletion and query operations, which need to be implemented by specific
 * scheduling framework repository subclasses.</li>
 * </ol>
 *
 * <p>
 * Implements the {@link top.osjf.commons.ability.Nameable} capability by default, using the fully
 * qualified class name of the subclass as the unique repository name for log tracking, exception
 * location and monitoring statistics.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class AbstractCronTaskRepository
        extends AbstractRunTimeoutRegistrarRepository implements CronTaskRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, Runnable runnable) throws CronInternalException {
        checkSupportedExpression(expression);
        String id;
        try {
            id = registerInternal(expression, runnable);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
        call(expression, runnable, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, CronMethodRunnable runnable) throws CronInternalException {
        checkSupportedExpression(expression);
        String id;
        try {
            id = registerInternal(expression, runnable);
            recordRegister(null);
        }
        catch (Exception ex) {
            recordRegister(ex);
            throw new CronInternalException(ex);
        }
        call(expression, runnable, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, RunnableTaskBody body) throws CronInternalException {
        checkSupportedExpression(expression);
        String id;
        try {
            id = registerInternal(expression, body);
            recordRegister(null);
        }
        catch (Exception ex) {
            recordRegister(ex);
            throw new CronInternalException(ex);
        }
        call(expression, body, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, TaskBody body) throws CronInternalException {
        checkSupportedExpression(expression);
        String id;
        try {
            id = registerInternal(expression, body);
            recordRegister(null);
        }
        catch (UnsupportedTaskBodyException ex) {
            recordRegister(ex);
            throw ex;
        }
        catch (Exception ex) {
            recordRegister(ex);
            throw new CronInternalException(ex);
        }
        call(expression, body, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(CronTask task) throws CronInternalException {
        checkSupportedExpression(task.getExpression());
        String id;
        try {
            id = registerInternal(task);
            recordRegister(null);
        }
        catch (Exception ex) {
            recordRegister(ex);
            throw new CronInternalException(ex);
        }
        call(task, id);
        return id;
    }

    private void recordRegister(@Nullable Throwable ex) {
        if (ex == null) {
            CronTaskMicrometer.recordRegister(this, true, null);
        }
        else {
            CronTaskMicrometer.recordRegister(this, false, ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(String id, String newExpression) throws CronInternalException {
        checkSupportedExpression(newExpression);
        try {
            updateInternal(id, newExpression);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String id) throws CronInternalException {
        try {
            removeInternal(id);
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
    }

    @Override
    public void removeAll() throws CronInternalException {
        try {
            removeAllInternal();
        }
        catch (Exception ex) {
            throw new CronInternalException(ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCronTaskInfo(String id) {
        return hasCronTaskInfoInternal(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskInfo getCronTaskInfo(String id) {
        return getCronTaskInfoInternal(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return getClass().getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSupportedExpression(String expression) {
        try {
            checkSupportedExpression(expression);
            return true;
        }
        catch (CronExpressionInvalidException ex) {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTaskRemainingNumberOfRuns(String taskId) {
        AtomicInteger count = getTaskRunTimesMap().getOrDefault(taskId, null);
        return count == null ? hasCronTaskInfo(taskId) ? -1 : 0 : count.get();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public RunningTimeout getTimeoutConfig(String taskId) {
        return getTaskRunTimeoutMap().getOrDefault(taskId, null);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public CronTaskInfo customizeCronTaskInfo(@Nullable CronTaskInfo cronTaskInfo) {
        if (cronTaskInfo == null) {
            return null;
        }
        // Setting remaining number of runs.
        cronTaskInfo.setRemainingNumberOfRuns(getTaskRemainingNumberOfRuns(cronTaskInfo.getId()));
        // Setting running timeout config.
        cronTaskInfo.setTimeoutConfig(getTimeoutConfig(cronTaskInfo.getId()));
        return cronTaskInfo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Runnable unwrapRunnable(Runnable given) {
        if (given instanceof TimeoutMonitoringRunnable) {
            return ((TimeoutMonitoringRunnable) given).getReal();
        }
        return given;
    }

    /**
     * Underlying internal registration method for {@link Runnable} scheduled task.
     * <p>
     * All input parameters have passed legality verification and framework expression rule verification
     * in the outer public method.
     * Subclasses implement the task persistence and scheduling registration logic for the current framework.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param expression Valid cron or periodic expression that passed framework verification
     * @param runnable Original executable task body
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     */
    protected abstract String registerInternal(String expression, Runnable runnable) throws Exception;

    /**
     * Underlying internal registration method for {@link CronMethodRunnable} scheduled task.
     * <p>
     * All input parameters have passed legality verification and framework expression rule verification
     * in the outer public method.
     * Subclasses implement the task persistence and scheduling registration logic for the current framework.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param expression Valid cron or periodic expression that passed framework verification
     * @param runnable Method reflection wrapped task executable body
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     */
    protected abstract String registerInternal(String expression, CronMethodRunnable runnable) throws Exception;

    /**
     * Underlying internal registration method for {@link RunnableTaskBody} scheduled task.
     * <p>
     * All input parameters have passed legality verification and framework expression rule verification
     * in the outer public method.
     * Subclasses implement the task persistence and scheduling registration logic for the current framework.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param expression Valid cron or periodic expression that passed framework verification
     * @param body Runnable encapsulated task metadata and execution body
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     */
    protected abstract String registerInternal(String expression, RunnableTaskBody body) throws Exception;

    /**
     * Underlying internal registration method for generic {@link TaskBody} custom task body.
     * <p>
     * All input parameters have passed legality verification and framework expression rule verification
     * in the outer public method. Subclasses implement the task persistence and scheduling registration
     * logic for the current framework.Any runtime or checked exception thrown during implementation will
     * be uniformly wrapped into {@link CronInternalException}.
     *
     * @param expression Valid cron or periodic expression that passed framework verification
     * @param body Generic custom encapsulated task body
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     * @throws UnsupportedTaskBodyException Thrown when the current scheduling framework does not support
     * the incoming {@link TaskBody} type
     */
    protected abstract String registerInternal(String expression, TaskBody body) throws Exception;

    /**
     * Underlying internal registration method for complete {@link CronTask} metadata object.
     * <p>
     * The complete task metadata has completed parameter verification in the outer public method.
     * Subclasses store the full task metadata and complete framework scheduling registration.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param task Complete encapsulated cron task metadata
     * @return Globally unique task ID generated after successful registration
     * @throws Exception Any exception thrown by the underlying scheduling framework during registration
     */
    protected abstract String registerInternal(CronTask task) throws Exception;

    /**
     * Underlying internal update method for modifying the cron expression of a specified task.
     * <p>
     * The task ID and new expression have passed parameter and framework rule verification in the
     * outer public method.
     * Subclasses implement the logic of updating the scheduled expression bound to the task.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param id Unique identifier of the target task to be updated
     * @param newExpression New valid cron expression that passed framework verification
     * @throws Exception Any exception thrown by the underlying scheduling framework during update
     */
    protected abstract void updateInternal(String id, String newExpression) throws Exception;

    /**
     * Underlying internal deletion method for releasing a scheduled task and its runtime resources.
     * <p>
     * The task ID has passed non-null parameter verification in the outer public method.
     * Subclasses implement task metadata deletion and framework resource release logic.
     * Any runtime or checked exception thrown during implementation will be uniformly wrapped into
     * {@link CronInternalException}.
     *
     * @param id Unique identifier of the target task to be removed
     * @throws Exception Any exception thrown by the underlying scheduling framework during deletion
     */
    protected abstract void removeInternal(String id) throws Exception;

    /**
     * Underlying internal batch deletion method to clear all registered scheduled tasks and release
     * all runtime resources held by the current repository.
     * <p>
     * This method is invoked after the outer public {@link #removeAll()} method, no parameter verification
     * is required in subclasses.Any runtime or checked exception thrown during implementation will be
     * uniformly caught and wrapped into {@link CronInternalException}.
     *
     * @throws Exception Any exception thrown by the underlying scheduling framework during batch task
     * removal and resource recycling
     */
    protected abstract void removeAllInternal() throws Exception;

    /**
     * Underlying internal existence judgment method for specified task ID.
     *
     * @param id Unique identifier of the task to be queried
     * @return {@code true} if the task exists in the current repository, otherwise {@code false}
     */
    protected abstract boolean hasCronTaskInfoInternal(String id);

    /**
     * Underlying internal query method for obtaining complete task metadata.
     *
     * @param id Unique identifier of the target task
     * @return Complete {@link CronTaskInfo} metadata of the task; return {@code null} if no
     * matching task exists
     */
    @Nullable protected abstract CronTaskInfo getCronTaskInfoInternal(String id);
}
