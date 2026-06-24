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

import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.commons.lang.Nullable;

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
        String id = registerInternal(expression, runnable);
        call(expression, runnable, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, CronMethodRunnable runnable)
            throws CronInternalException {
        String id = registerInternal(expression, runnable);
        call(expression, runnable, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, RunnableTaskBody body)
            throws CronInternalException {
        String id = registerInternal(expression, body);
        call(expression, body, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, TaskBody body)
            throws CronInternalException, UnsupportedTaskBodyException {
        String id = registerInternal(expression, body);
        call(expression, body, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(CronTask task) throws CronInternalException {
        String id = registerInternal(task);
        call(task, id);
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(String id, String newExpression) throws CronInternalException {
        updateInternal(id, newExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String id) throws CronInternalException {
        removeInternal(id);
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

    /*   Internal API implementation method group after parameter validation in version 3.0.2.    */

    /*
     * (NON-JavaDoc)
     * @param expression
     * @param runnable
     * @return
     * @throws CronInternalException
     */
    protected abstract String registerInternal(String expression, Runnable runnable)
            throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param expression
     * @param runnable
     * @return
     * @throws CronInternalException
     */
    protected abstract String registerInternal(String expression, CronMethodRunnable runnable)
            throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param expression
     * @param body
     * @return
     * @throws CronInternalException
     */
    protected abstract String registerInternal(String expression, RunnableTaskBody body)
            throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param expression
     * @param body
     * @return
     * @throws CronInternalException
     * @throws UnsupportedTaskBodyException
     */
    protected abstract String registerInternal(String expression, TaskBody body)
            throws CronInternalException, UnsupportedTaskBodyException;

    /*
     * (NON-JavaDoc)
     * @param task
     * @return
     * @throws CronInternalException
     */
    protected abstract String registerInternal(CronTask task) throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param id
     * @param newExpression
     * @throws CronInternalException
     */
    protected abstract void updateInternal(String id, String newExpression)
            throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param id
     * @throws CronInternalException
     */
    protected abstract void removeInternal(String id) throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param id
     * @return
     */
    protected abstract boolean hasCronTaskInfoInternal(String id);

    /*
     * (NON-JavaDoc)
     * @param id
     * @return
     */
    @Nullable protected abstract CronTaskInfo getCronTaskInfoInternal(String id);
}
