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

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.ListenerContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * A simple implementation of cron task repository that manages scheduled tasks using cron expressions.
 *
 * <p>This repository provides functionality to register, update, remove, and query cron tasks.
 * It uses a {@link ScheduledExecutorService} to schedule tasks based on cron expressions parsed
 * by the cron-utils library.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Supports various cron types (default is QUARTZ)</li>
 *   <li>Provides task lifecycle management (register, update, remove)</li>
 *   <li>Supports task listeners for lifecycle events</li>
 *   <li>Thread-safe implementation</li>
 *   <li>Graceful shutdown support</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class SimpleCronTaskRepository extends AbstractCronTaskRepository {

    private final ScheduledExecutorService scheduledExecutorService;

    /**
     * The generator instance of the task ID.
     */
    private final AtomicLong idGenerator = new AtomicLong(0);

    /**
     * Map mapping between task ID and task running thread instance.
     */
    private final Map<String, SimpleRunnabledScheduledFuture> futureCache = new ConcurrentHashMap<>();

    /**
     * Format instance of cron expression from {@code com.cronutils}.
     */
    private final CronParser cronParser;

    /**
     * When closing {@link #scheduledExecutorService}, do you wait in the pool for the task to complete.
     */
    private boolean awaitTermination = true;

    /**
     * The delay time waiting for the completion of tasks in the pool.
     */
    private long awaitTerminationTimeout = 10;

    /**
     * The delay time unit for waiting for the completion of tasks in the pool.
     */
    private TimeUnit awaitTerminationTimeoutUnit = TimeUnit.SECONDS;

    /**
     * Creates a new {@code SimpleCronTaskRepository} without args and
     * default {@link CronType#QUARTZ}.
     */
    public SimpleCronTaskRepository() {
        this(CronType.QUARTZ);
    }

    /**
     * Creates a new {@code SimpleCronTaskRepository} with given {@code CronType}.
     *
     * @param cronType An enumeration class representing the cron expression parsing style of the framework.
     */
    public SimpleCronTaskRepository(CronType cronType) {
        this(Executors.newScheduledThreadPool(1), cronType);
    }

    /**
     * Creates a new {@code SimpleCronTaskRepository} with the
     * given core pool size and given {@code CronType}.
     *
     * @param cronType     An enumeration class representing the cron expression parsing style of the framework.
     * @param corePoolSize the number of threads to keep in the pool,
     *                     even if they are idle.
     */
    public SimpleCronTaskRepository(int corePoolSize, CronType cronType) {
        this(Executors.newScheduledThreadPool(corePoolSize), cronType);
    }

    /**
     * Creates a new {@code SimpleCronTaskRepository} with the
     * given {@link ScheduledExecutorService} instance given {@code CronType}.
     *
     * @param cronType                 An enumeration class representing the cron expression parsing
     *                                 style of the framework.
     * @param scheduledExecutorService An {@link ScheduledExecutorService} instance that can schedule
     *                                 commands to run after a given delay, or to execute periodically.
     */
    public SimpleCronTaskRepository(ScheduledExecutorService scheduledExecutorService, CronType cronType) {
        this.scheduledExecutorService = scheduledExecutorService;
        this.cronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(cronType));
    }

    /**
     * @param awaitTermination {@link #awaitTermination}
     */
    public void setAwaitTermination(boolean awaitTermination) {
        this.awaitTermination = awaitTermination;
    }

    /**
     * @param awaitTerminationTimeout {@link #awaitTerminationTimeout}
     */
    public void setAwaitTerminationTimeout(long awaitTerminationTimeout) {
        this.awaitTerminationTimeout = awaitTerminationTimeout;
    }

    /**
     * @param awaitTerminationTimeoutUnit {@link #awaitTerminationTimeoutUnit}
     */
    public void setAwaitTerminationTimeoutUnit(TimeUnit awaitTerminationTimeoutUnit) {
        this.awaitTerminationTimeoutUnit = awaitTerminationTimeoutUnit;
    }

    /**
     * @return The next gradually increasing value serves as the ID for task registration.
     */
    private String getNextId() {
        return String.valueOf(idGenerator.incrementAndGet());
    }

    @Override
    public void stop() {
        super.stop();
        if (awaitTermination) {
            if (logger.isDebugEnabled()) {
                logger.debug("Closed Pool");
            }
            scheduledExecutorService.shutdown();
            try {
                if (!scheduledExecutorService.awaitTermination(awaitTerminationTimeout, awaitTerminationTimeoutUnit)) {
                    // If the timeout is not completed, force termination.
                    scheduledExecutorService.shutdownNow();
                }
            } catch (InterruptedException ex) {
                // Restore interrupted state.
                Thread.currentThread().interrupt();
            } finally {
                // Ensure that the service has been completely terminated.
                if (!scheduledExecutorService.isTerminated()) {
                    scheduledExecutorService.shutdownNow();
                }
            }
        } else {
            // Directly force termination.
            scheduledExecutorService.shutdownNow();
        }
    }

    /**
     * A simple implementation class for the {@link ListenerContext} interface.
     */
    private static class SimpleListenerContext implements ListenerContext {

        private final String id;
        private final ScheduledFuture<?> future;

        public SimpleListenerContext(String id, ScheduledFuture<?> future) {
            this.id = id;
            this.future = future;
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public Object getSourceContext() {
            return future;
        }
    }

    /**
     * A simple implementation class for the {@link ScheduledFuture} interface to calculate the
     * next execution time and store mutable {@link ScheduledFuture}.
     */
    private class SimpleRunnabledScheduledFuture implements ScheduledFuture<Object>, Runnable {

        /**
         * The running function executed by the original target.
         */
        private final Runnable rawRunnable;

        private final SimpleListenerContext listenerContext;

        /**
         * The cron format instance analyzed this time.
         */
        private final Cron cron;

        /**
         * {@link #scheduledFuture} Real instances, {@link Lock} lock instances that ensure
         * thread safety through changes.
         */
        private final Lock scheduleLock = new ReentrantLock();

        private ScheduledFuture<?> scheduledFuture;

        /**
         * Creates a new {@link SimpleRunnabledScheduledFuture} with ron expression
         * and source {@link Runnable}.
         *
         * @param expression  the given cron expression.
         * @param rawRunnable the given source {@link Runnable}.
         */
        public SimpleRunnabledScheduledFuture(String expression, Runnable rawRunnable) {
            this.rawRunnable = rawRunnable;
            this.listenerContext = new SimpleListenerContext(getNextId(), this);
            this.cron = parseToCron(expression);
            schedule();
        }

        // Parse cron express to {@link Cron} instance.
        private Cron parseToCron(String expression) throws CronInternalException {
            try {
                return cronParser.parse(expression);
            } catch (IllegalArgumentException ex) {
                throw new CronInternalException(ex.getMessage(), ex);
            }
        }

        /**
         * Execute a task that parses the next run time based on the cron expression and is
         * delayed from the current time interval.
         */
        private void schedule() {
            scheduleLock.lock();
            try {
                this.scheduledFuture
                        = scheduledExecutorService.schedule(this, getNextDelaySeconds(), TimeUnit.SECONDS);
            } finally {
                scheduleLock.unlock();
            }
        }

        private ScheduledFuture<?> getFuture() {
            scheduleLock.lock();
            try {
                return scheduledFuture;
            } finally {
                scheduleLock.unlock();
            }
        }

        /**
         * @return Resolve the second interval between the next run time and the current time based on
         * the cron expression.
         */
        private long getNextDelaySeconds() {
            // Get the current time.
            ZonedDateTime now = ZonedDateTime.now();
            // Calculate the next execution time.
            ExecutionTime executionTime = ExecutionTime.forCron(cron);
            ZonedDateTime nextExecution = executionTime.nextExecution(now).orElse(null);
            return nextExecution != null ? nextExecution.toEpochSecond() - now.toEpochSecond() : -1;
        }

        /**
         * @return A new {@link CronTaskInfo} by this.
         */
        public CronTaskInfo toCronTaskInfo() {
            return customizeCronTaskInfo(new CronTaskInfo(listenerContext.id, cron.asString(), rawRunnable));
        }

        @Override
        public void run() {
            List<CronListener> cronListeners = getCronListenerCollector().getCronListeners();
            try {
                // Notify all cron listeners that the task is about to start
                cronListeners.forEach(c -> c.start(listenerContext));
                // Execute the main logic of the runnable
                rawRunnable.run();
                // Notify all cron listeners that the task has completed successfully
                cronListeners.forEach(c -> c.success(listenerContext));
            } catch (Throwable e) {
                // If an error occurs during task execution, notify all cron listeners
                // of the failure, passing the exception context for further handling
                cronListeners.forEach(c -> c.failed(listenerContext, e));
            }
            schedule();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public long getDelay(@NotNull TimeUnit unit) {
            return getFuture().getDelay(unit);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compareTo(@NotNull Delayed o) {
            return getFuture().compareTo(o);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return getFuture().cancel(mayInterruptIfRunning);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isCancelled() {
            return getFuture().isCancelled();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isDone() {
            return getFuture().isDone();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object get() throws InterruptedException, ExecutionException {
            return getFuture().get();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object get(long timeout, @NotNull TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return getFuture().get(timeout, unit);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull Runnable runnable) throws CronInternalException {
        return new SimpleRunnabledScheduledFuture(expression, runnable).listenerContext.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull CronMethodRunnable runnable) throws CronInternalException {
        return register(expression, (Runnable) runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull RunnableTaskBody body) throws CronInternalException {
        return register(expression, body.getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull String expression, @NotNull TaskBody body) throws CronInternalException {
        if (body.isWrapperFor(RunnableTaskBody.class)) {
            return register(expression, body.unwrap(RunnableTaskBody.class));
        }
        throw new UnsupportedTaskBodyException(body.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String register(@NotNull CronTask task) throws CronInternalException {
        return register(task.getExpression(), task.getRunnable());
    }

    @Override
    public boolean hasCronTaskInfo(@Nonnull String id) {
        return futureCache.containsKey(id);
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public CronTaskInfo getCronTaskInfo(@NotNull String id) {
        return Optional.ofNullable(futureCache.get(id)).map(SimpleRunnabledScheduledFuture::toCronTaskInfo)
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CronTaskInfo> getAllCronTaskInfo() {
        return futureCache.values()
                .stream().map(SimpleRunnabledScheduledFuture::toCronTaskInfo)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(@NotNull String id, @NotNull String newExpression) throws CronInternalException {
        SimpleRunnabledScheduledFuture future = futureCache.get(id);
        if (future == null) {
            throw new CronInternalException("Missing task information according to id " + id);
        }
        remove(id);
        register(newExpression, future.rawRunnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(@NotNull String id) throws CronInternalException {
        SimpleRunnabledScheduledFuture future = futureCache.remove(id);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
    }
}
