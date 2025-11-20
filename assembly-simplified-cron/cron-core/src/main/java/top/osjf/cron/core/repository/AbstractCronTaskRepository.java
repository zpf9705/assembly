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

import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.util.AssertUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The abstract implementation of {@link CronTaskRepository} inherits a series of classes
 * such as {@link AbstractRunTimeoutRegistrarRepository} to give {@link CronTaskRepository}
 * a default implementation.
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
        AssertUtils.assertNotBlank(expression, "expression must not be blank");
        AssertUtils.assertNotNull(runnable, "runnable must not be null");
        return registerInternal(expression, runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, CronMethodRunnable runnable)
            throws CronInternalException {
        AssertUtils.assertNotBlank(expression, "expression must not be blank");
        AssertUtils.assertNotNull(runnable, "runnable must not be null");
        return registerInternal(expression, runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, RunnableTaskBody body)
            throws CronInternalException {
        AssertUtils.assertNotBlank(expression, "expression must not be blank");
        AssertUtils.assertNotNull(body, "body must not be null");
        return registerInternal(expression, body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(String expression, TaskBody body)
            throws CronInternalException, UnsupportedTaskBodyException {
        AssertUtils.assertNotBlank(expression, "expression must not be blank");
        AssertUtils.assertNotNull(body, "body must not be null");
        return registerInternal(expression, body);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(CronTask task) throws CronInternalException {
        AssertUtils.assertNotNull(task, "task must not be blank");
        return registerInternal(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(String id, String newExpression) throws CronInternalException {
        AssertUtils.assertNotBlank(id, "id must not be blank");
        AssertUtils.assertNotBlank(newExpression, "newExpression must not be null");
        updateInternal(id, newExpression);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(String id) throws CronInternalException {
        AssertUtils.assertNotBlank(id, "id must not be blank");
        removeInternal(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasCronTaskInfo(String id) {
        AssertUtils.assertNotBlank(id, "id must not be blank");
        return hasCronTaskInfoInternal(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CronTaskInfo getCronTaskInfo(String id) {
        AssertUtils.assertNotBlank(id, "id must not be blank");
        return getCronTaskInfoInternal(id);
    }

    /*   Internal API implementation method group after parameter validation in version 3.0.2.    */

    /*
     * (NON-JavaDoc)
     * @param expression
     * @param runnable
     * @return
     * @throws CronInternalException
     */
    protected abstract String registerInternal(@NotNull String expression, @NotNull Runnable runnable)
            throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param expression
     * @param runnable
     * @return
     * @throws CronInternalException
     */
    protected abstract String registerInternal(@NotNull String expression, @NotNull CronMethodRunnable runnable)
            throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param expression
     * @param body
     * @return
     * @throws CronInternalException
     */
    protected abstract String registerInternal(@NotNull String expression, @NotNull RunnableTaskBody body)
            throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param expression
     * @param body
     * @return
     * @throws CronInternalException
     * @throws UnsupportedTaskBodyException
     */
    protected abstract String registerInternal(@NotNull String expression, @NotNull TaskBody body)
            throws CronInternalException, UnsupportedTaskBodyException;

    /*
     * (NON-JavaDoc)
     * @param task
     * @return
     * @throws CronInternalException
     */
    protected abstract String registerInternal(@NotNull CronTask task) throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param id
     * @param newExpression
     * @throws CronInternalException
     */
    protected abstract void updateInternal(@NotNull String id, @NotNull String newExpression)
            throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param id
     * @throws CronInternalException
     */
    protected abstract void removeInternal(@NotNull String id) throws CronInternalException;

    /*
     * (NON-JavaDoc)
     * @param id
     * @return
     */
    protected abstract boolean hasCronTaskInfoInternal(@NotNull String id);

    /*
     * (NON-JavaDoc)
     * @param id
     * @return
     */
    @Nullable protected abstract CronTaskInfo getCronTaskInfoInternal(@NotNull String id);

    /**
     * Return remaining number of runs of the specify id task.
     * @param taskId the specify task id.
     * @return Remaining number of runs of the specify id task,
     * the unlimited number of times is {@code -1}, and there
     * are no tasks with {@code 0}. Otherwise, it is the remaining
     * number of runs.
     * @since 3.0.1
     */
    protected long getTaskRemainingNumberOfRuns(String taskId) {
        AtomicInteger count = getTaskRunTimesMap().getOrDefault(taskId, null);
        return count == null ? hasCronTaskInfo(taskId) ? -1 : 0 : count.get();
    }

    /**
     * Customize a specified {@link CronTaskInfo}.
     * @param cronTaskInfo a specified {@link CronTaskInfo}.
     * @return a specified {@link CronTaskInfo}.
     * @since 3.0.1
     */
    @Nullable
    protected CronTaskInfo customizeCronTaskInfo(CronTaskInfo cronTaskInfo) {
        if (cronTaskInfo == null) {
            return null;
        }
        // Setting remaining number of runs.
        cronTaskInfo.setRemainingNumberOfRuns(getTaskRemainingNumberOfRuns(cronTaskInfo.getId()));

        return cronTaskInfo;
    }
}
