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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.SimpleCronListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * The abstract implementation of the {@link CronTaskRepository} interface utilizes
 * the listener management and related registration scheduling task functions of
 * {@link AbstractCronListenerRepository} to accomplish the following common functions.
 *
 * <p>For newly added APIs (such as {@link #registerRunTimes}) for registering tasks
 * a specified number of times, abstract implementation classes have added support
 * for these APIs. The registration calls of these APIs will be based on the ID as
 * the key and the specified number of times as the value. Each time they call back
 * to {@link RegisterTimesCheckedCronListener}, the number of times will be checked,
 * and when the number of times is exhausted, the task will automatically end, in
 * order to better unify the low-level management of the task.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class AbstractCronTaskRepository extends AbstractCronListenerRepository implements CronTaskRepository {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    /** Number of runs, task scheduling listener.*/
    private final RegisterTimesCheckedCronListener checkedCronListener = new RegisterTimesCheckedCronListener();

    /** Atomic {@code Boolean} flag is used to indicate whether a {@link #checkedCronListener} listener
     * is registered. */
    private final AtomicBoolean isRunTimesCheckListenerRegistered = new AtomicBoolean(false);

    /** Used to record tasks with specified running times. */
    private final ConcurrentMap<String, AtomicInteger> taskRunTimesMap = new ConcurrentHashMap<>(16);

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull Runnable runnable, int times)
            throws CronInternalException {
        registerRunTimes(() -> register(expression, runnable), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull CronMethodRunnable runnable, int times)
            throws CronInternalException {
        registerRunTimes(() -> register(expression, runnable), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull RunnableTaskBody body, int times)
            throws CronInternalException {
        registerRunTimes(() -> register(expression, body), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull TaskBody body, int times)
            throws CronInternalException, UnsupportedTaskBodyException {
        registerRunTimes(() -> register(expression, body), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull CronTask task, int times) throws CronInternalException {
        registerRunTimes(() -> register(task), times);
    }

    /**
     * Register a public method for running a specified number of tasks.
     * @param idSupplier the task registration function.
     * @param times      the registration run times.
     * @since 3.0.1
     */
    private void registerRunTimes(Supplier<String> idSupplier, int times) {

        // The specified number of runs cannot be less than or equal to 0.
        if (times <= 0) {
            throw new IllegalArgumentException("Specify run times must be greater than 0");
        }

        // Added {@link #checkedCronListener} to the listener list?
        if (isRunTimesCheckListenerRegistered.compareAndSet(false, true)) {
            super.addLastListener(checkedCronListener);
        }

        // Get id by do register.
        String id = idSupplier.get();
        // Record run times.
        taskRunTimesMap.putIfAbsent(id, new AtomicInteger(times));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(@NotNull CronListener listener) {
        ensureCheckedListenerIsLastIfRuntime(() -> super.addListener(listener));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLastListener(@NotNull CronListener listener) {
        ensureCheckedListenerIsLastIfRuntime(() -> super.addLastListener(listener));
    }

    /**
     * Return remaining number of runs of the specify id task.
     * @param taskId the specify task id.
     * @return Remaining number of runs of the specify id task,
     * the unlimited number of times is {@code -1}, and there
     * are no tasks with {@code 0}. Otherwise, it is the remaining
     * number of runs.
     * @since 3.0.1
     */
    public long getTaskRemainingNumberOfRuns(String taskId) {
        AtomicInteger count = taskRunTimesMap.getOrDefault(taskId, null);
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

    /**
     * To ensure that {@link #checkedCronListener} is at the end of the queue and can be removed
     * after completing the registration task, and can go through all previous listeners, the
     * interception check method for the tail methods {@link #addLastListener} and {@link #addListener}
     * is used.
     *
     * @param next The next step is to add a real operation listener.
     */
    private void ensureCheckedListenerIsLastIfRuntime(Runnable next) {
        boolean shouldAddCheckedLast = isRunTimesCheckListenerRegistered.get();

        if (shouldAddCheckedLast && hasListener(checkedCronListener)) {
            // Remove checkedCronListener if it exists
            removeListener(checkedCronListener);
        }

        // Execute the next consumer
        next.run();

        if (shouldAddCheckedLast) {
            // Ensure checkedCronListener is the last
            super.addLastListener(checkedCronListener);
        }
    }

    /**
     * Check the listener class for tasks that limit the number of runs.
     */
    private class RegisterTimesCheckedCronListener extends SimpleCronListener {
        @Override
        public void successWithId(String id) {
            checkRunTimes(id);
        }

        @Override
        public void failedWithId(String id, Throwable exception) {
            checkRunTimes(id);
        }

        private void checkRunTimes(String id) {
            taskRunTimesMap.compute(id, (key, count) -> {
                if (count == null) {
                    return null;
                }
                if (count.decrementAndGet() <= 0) {
                    remove(id);
                    logger.info("Task {} has reached the specified number of runs and has been stopped!", id);
                    return null;
                }
                return count;
            });
        }
    }
}
