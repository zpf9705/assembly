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
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.UnsupportedTaskBodyException;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.listener.ListenerContext;
import top.osjf.cron.core.listener.ListenerExecuteSupport;
import top.osjf.cron.core.util.ExecutorUtils;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
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
    private final Map<String, SimpleRunnabledScheduledFuture> futureCache = new ConcurrentHashMap<>(16);

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
        if (logger.isDebugEnabled()) {
            logger.debug("Close Pool...");
        }
        ExecutorUtils.shutdownExecutor(scheduledExecutorService, awaitTermination, awaitTerminationTimeout,
                awaitTerminationTimeoutUnit);
    }

    /**
     * A simple implementation class for the {@link ListenerContext} interface.
     */
    private static class SimpleListenerContext implements ListenerContext {

        private final String id;
        private final ScheduledFuture<?> future;
        private final RepositoryContext repositoryContext;

        public SimpleListenerContext(String id, ScheduledFuture<?> future, SimpleCronTaskRepository repository) {
            this.id = id;
            this.future = future;
            this.repositoryContext = new DefaultRepositoryContext(repository);
        }

        @Override
        public String getID() {
            return id;
        }

        @Override
        public RepositoryContext getRepositoryContext() {
            return repositoryContext;
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
    private class SimpleRunnabledScheduledFuture extends ListenerExecuteSupport implements ScheduledFuture<Object>,
            Runnable {

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

        /**
         * The atomic Boolean tag indicates that there should be no more {@link #schedule()}
         * after {@link #cancel(boolean)}, indicating that the current task has been interrupted.
         * @since 3.0.1
         */
        private final AtomicBoolean canceledFlag = new AtomicBoolean(false);

        private volatile ScheduledFuture<?> scheduledFuture;

        /**
         * Creates a new {@link SimpleRunnabledScheduledFuture} with ron expression
         * and source {@link Runnable}.
         *
         * @param expression  the given cron expression.
         * @param rawRunnable the given source {@link Runnable}.
         */
        public SimpleRunnabledScheduledFuture(String expression, Runnable rawRunnable) {
            this.rawRunnable = rawRunnable;
            this.listenerContext = new SimpleListenerContext(getNextId(),
                    this, SimpleCronTaskRepository.this);
            this.cron = parseToCron(expression);
            schedule();
            futureCache.putIfAbsent(listenerContext.id, this);
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
                if (canceledFlag.get()) {
                    return;
                }
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
         * @since 3.0.1
         */
        private void setCancelFutureFlag() {
            canceledFlag.set(true);
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
         * @return Returns the timestamp in milliseconds of the next execution time after the current
         * expression is parsed.
         */
        @Nullable
        public Long getNextExecuteMilliseconds() {
            // Get the current time.
            ZonedDateTime now = ZonedDateTime.now();
            // Calculate the next execution time.
            ExecutionTime executionTime = ExecutionTime.forCron(cron);
            Optional<ZonedDateTime> optional = executionTime.nextExecution(now);
            return optional.isPresent() ? optional.get().toInstant().toEpochMilli() : null;
        }

        /**
         * @return A new {@link CronTaskInfo} by this.
         */
        public CronTaskInfo toCronTaskInfo() {
            Runnable runnable = unwrapRunnable(rawRunnable);
            if (runnable instanceof CronMethodRunnable) {
                CronMethodRunnable cr = (CronMethodRunnable) runnable;
                return new CronTaskInfo(listenerContext.id, cron.asString(), runnable,
                        cr.getTarget(), cr.getMethod());
            }
            return new CronTaskInfo(listenerContext.id, cron.asString(), runnable);
        }

        @Override
        public void run() {
            super.run();
            schedule();
        }

        @Override
        protected Runnable getRaw() {
            return rawRunnable;
        }

        @Override
        protected CronListenerCollector getCronListenerCollector() {
            return SimpleCronTaskRepository.this.getCronListenerCollector();
        }

        @Override
        protected ListenerContext getListenerContext() {
            return listenerContext;
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
            setCancelFutureFlag();
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

        /**
         * Temporarily terminates the currently executing round of the scheduled task.
         * <p>
         * This method only sends a thread interrupt signal to the ongoing task execution
         * cooperatively.
         * It will NOT update the global {@code canceledFlag}, so subsequent scheduled cycles
         * can still run normally after the current execution completes.
         * <p>
         * An exclusive lock is applied to ensure thread safety and mutual exclusion with the
         * scheduling registration logic, avoiding concurrent state exceptions and duplicate
         * task scheduling.
         *
         * <p>
         * <b>Constraints:</b>
         * <ul>
         * <li>Directly returns if the task is not currently running</li>
         * <li>Directly returns if the task has been marked as globally canceled permanently</li>
         * <li>Bypasses the overridden {@code cancel()} method to prevent polluting the global
         * cancel flag</li>
         * </ul>
         */
        public void terminate() {
            scheduleLock.lock();
            try {
                if (!isRunning() || canceledFlag.get()) {
                    return;
                }
                getFuture().cancel(true);
            }
            finally {
                scheduleLock.unlock();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotNull
    public String getName() {
        return "SIMPLE_SCHEDULER@" + super.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkSupportedExpression(String expression) throws CronExpressionInvalidException {
        try {
            cronParser.parse(expression);
        }
        catch (IllegalArgumentException ex) {
            throw new CronExpressionInvalidException(expression, getName(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull String expression, @NotNull Runnable runnable) {
        return new SimpleRunnabledScheduledFuture(expression, runnable).listenerContext.id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull String expression, @NotNull CronMethodRunnable runnable) {
        return registerInternal(expression, (Runnable) runnable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull String expression, @NotNull RunnableTaskBody body) {
        return registerInternal(expression, body.getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull String expression, @NotNull TaskBody body)
            throws UnsupportedTaskBodyException{
        if (body.isWrapperFor(RunnableTaskBody.class)) {
            return registerInternal(expression, body.unwrap(RunnableTaskBody.class));
        }
        throw new UnsupportedTaskBodyException(body.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String registerInternal(@NotNull CronTask task) {
        return registerInternal(task.getExpression(), task.getRunnable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllRegisteredTaskIds() {
        return futureCache.values()
                .stream().map(future -> future.listenerContext.id)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getAllRunningTaskIds() {
        return futureCache.values()
                .stream().filter(SimpleRunnabledScheduledFuture::isRunning)
                .map(future -> future.listenerContext.id)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getNextExecuteTime(String id) {
        SimpleRunnabledScheduledFuture future = futureCache.get(id);
        if (future == null) {
            return null;
        }
        return future.getNextExecuteMilliseconds();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateInternal(@NotNull String id, @NotNull String newExpression)  {
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
    public void removeInternal(@NotNull String id) {
        SimpleRunnabledScheduledFuture future = futureCache.remove(id);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void removeAllInternal() {
        for (SimpleRunnabledScheduledFuture future : futureCache.values()) {
            if (future != null && !future.isCancelled()) {
                future.cancel(true);
            }
        }
        futureCache.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CronTaskInfo getCronTaskInfoInternal(String id) {
        return Optional.ofNullable(futureCache.get(id)).map(SimpleRunnabledScheduledFuture::toCronTaskInfo)
                .orElse(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void terminateInternal(String id) {
        SimpleRunnabledScheduledFuture future = futureCache.get(id);
        if (future != null) {
            future.terminate();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void terminateAllInternal() {
        for (SimpleRunnabledScheduledFuture future : futureCache.values()) {
            future.terminate();
        }
    }
}
