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
import top.osjf.commons.lang.Nullable;

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
     * Retrieve the corresponding {@link RunningTimeout} configuration from the expired
     * configuration instance cache based on the unique ID.
     * @param taskId the specify task id.
     * @return The timeout configuration instance for a single run of this task.
     * @since 3.0.2
     */
    @Nullable
    protected RunningTimeout getTimeoutConfig(String taskId) {
        return getTaskRunTimeoutMap().getOrDefault(taskId, null);
    }

    /**
     * Customize a specified {@link CronTaskInfo}.
     * @param cronTaskInfo a specified {@link CronTaskInfo}.
     * @return a specified {@link CronTaskInfo}.
     * @since 3.0.1
     */
    @Nullable
    protected CronTaskInfo customizeCronTaskInfo(@Nullable CronTaskInfo cronTaskInfo) {
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
     * Unwraps the given Runnable instance. If the provided Runnable is an instance of
     * {@link TimeoutMonitoringRunnable}, this method returns the underlying wrapped task
     * via {@code getReal()}. Otherwise, it returns the original Runnable directly.
     * @param given the {@code Runnable} to unwrap.
     * @return the underlying real Runnable if wrapped; otherwise, the same instance.
     * @since 3.0.2
     */
    protected Runnable unwaperRunnable(Runnable given) {
        if (given instanceof TimeoutMonitoringRunnable) {
            return ((TimeoutMonitoringRunnable) given).getReal();
        }
        return given;
    }
}
