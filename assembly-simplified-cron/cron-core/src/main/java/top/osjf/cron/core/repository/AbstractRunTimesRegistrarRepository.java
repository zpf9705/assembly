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
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.SimpleCronListener;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * The abstract implementation class of {@link RunTimesRegistrarRepository} implements
 * monitoring of the number of runs.
 *
 * <p>This abstract class uses atomic Boolean notation {@link #isRunTimesCheckListenerRegistered}
 * to specify whether a {@link #checkedCronListener} task runtime listener has been placed when
 * registering related API tasks based on the number of runs, ensuring that it always processes
 * the last check bit to ensure post-processing. Use a thread safe map {@link #taskRunTimesMap} to
 * incrementally decrease the specified number of executions to implement the relevant API logic.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class AbstractRunTimesRegistrarRepository
        extends AbstractCronListenerRepository implements RunTimesRegistrarRepository {

    /**
     * Number of runs, task scheduling listener.
     */
    private final RunTimesCheckedCronListener checkedCronListener = new RunTimesCheckedCronListener();

    /**
     * Atomic {@code Boolean} flag is used to indicate whether a {@link #checkedCronListener} listener
     * is registered.
     */
    private final AtomicBoolean isRunTimesCheckListenerRegistered = new AtomicBoolean(false);

    /**
     * Used to record tasks with specified running times.
     */
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
     * Register a public method for running a specified number of tasks.
     *
     * @param idSupplier the task registration function.
     * @param times      the registration run times.
     * @since 3.0.1
     */
    private void registerRunTimes(Supplier<String> idSupplier, int times) {

        // The specified number of runs cannot be less than or equal to 0.
        if (times <= 0) {
            throw new IllegalArgumentException("Specify run times must be greater than 0");
        }

        // Check if the listener for task frequency management has been registered.
        if (isRunTimesCheckListenerRegistered.compareAndSet(false, true)) {
            super.addLastListener(checkedCronListener);
        }

        // Register the task and obtain the ID.
        String id = idSupplier.get();

        // Record the association mapping between task ID and execution frequency.
        taskRunTimesMap.putIfAbsent(id, new AtomicInteger(times));
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
            addLastListener(checkedCronListener);
        }
    }

    /**
     * @return Return an immutable run count record map.
     */
    protected Map<String, AtomicInteger> getTaskRunTimesMap() {
        return Collections.unmodifiableMap(taskRunTimesMap);
    }

    /**
     * Check the listener class for tasks that limit the number of runs.
     */
    private class RunTimesCheckedCronListener extends SimpleCronListener {
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
                    if (logger.isDebugEnabled()) {
                        logger.debug("Task ID <{}> terminated: maximum run count reached.", id);
                    }
                    return null;
                }
                return count;
            });
        }
    }
}
