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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.cron.core.lang.Nullable;

import java.util.UUID;
import java.util.concurrent.*;

/**
 * Internally using {@link FutureTask} timeout calculation API
 * {@link FutureTask#get(long, TimeUnit)} to provide timeout
 * monitoring classes for each scheduled task {@link Runnable}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class TimeoutMonitoringRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeoutMonitoringRunnable.class);

    /** the real {@link Runnable}.*/
    private final Runnable real;

    /** the configure instance {@link RunningTimeout}.*/
    private final RunningTimeout timeout;

    /** the monitoring {@link ExecutorService}..*/
    @Nullable private ExecutorService monitoringExecutor;

    /**
     * Construct a {@code TimeoutMonitoringRunnable} with given real {@link Runnable}
     * and the configure instance {@link RunningTimeout}.
     *
     * @param real                the real {@link Runnable}.
     * @param timeout             configure instance for timeout control during task execution.
     */
    public TimeoutMonitoringRunnable(Runnable real, RunningTimeout timeout) {
        this(real, timeout, null);
    }

    /**
     * Construct a {@code TimeoutMonitoringRunnable} with given real {@link Runnable}
     * and the configure instance {@link RunningTimeout}.
     *
     * @param real                the real {@link Runnable}.
     * @param timeout             configure instance for timeout control during task execution.
     * @param monitoringExecutor  the monitoring {@link ExecutorService}.
     */
    public TimeoutMonitoringRunnable(Runnable real, RunningTimeout timeout, @Nullable ExecutorService monitoringExecutor) {
        this.real =  real;
        this.timeout =  timeout;
        this.monitoringExecutor =  monitoringExecutor;
    }

    /**
     * Set a monitoring {@link ExecutorService} if {@link #monitoringExecutor} is {@literal null}.
     * @param monitoringExecutor the monitoring {@link ExecutorService}.
     * @return this.
     */
    public Runnable setMonitoringExecutorIfAbsent(ExecutorService monitoringExecutor) {
        if (this.monitoringExecutor == null) {
            this.monitoringExecutor = monitoringExecutor;
        }
        return this;
    }

    /**
     * @throws RunningException if running fails occurs.
     */
    @Override
    public void run() throws RunningException {

        Future<?> future;
        if (monitoringExecutor != null) {
            future = monitoringExecutor.submit(real);
        }
        else {
            FutureTask<Void> futureTask = new FutureTask<>(real, null);
            future = futureTask;
            newMonitoringThread(futureTask).start();
        }

        get(future);
    }

    /**
     * Create a new timeout monitoring thread.
     * @param futureTask the input {@link FutureTask}.
     * @return a new {@link Thread}.
     */
    static Thread newMonitoringThread(FutureTask<Void> futureTask) {
        Thread thread = new Thread(futureTask);
        thread.setName("Monitoring-Thread-" + UUID.randomUUID());
        return thread;
    }

    /**
     * Monitor task execution time and handle related exceptions.
     * @param future the input {@link Future}.
     */
    public void get(Future<?> future) {
        try {
            future.get(timeout.getTimeout(), timeout.getTimeUnit());
        }
        catch (TimeoutException ex) {
            handlerTimeoutPolicy(future);
        }
        catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RunningException(ex);
        }
    }

    /**
     * Process {@link Future} according to the processing strategy.
     * @param future the input {@link Future}.
     */
    void handlerTimeoutPolicy(Future<?> future) {
        RunningTimeoutPolicy policy = timeout.getPolicy();
        switch (policy) {
            case INTERRUPT: cancel(future); break;
            case THROW: throw new RunningTimeoutException();
            case CANCEL_THROW: {
                cancel(future);
                throw new RunningTimeoutException();
            }
            case IGNORE: break;
        }
    }

    /**
     * Cancel the input {@link Future}.
     * @param future the input {@link Future}.
     */
    void cancel(Future<?> future) {
        if (!future.cancel(true)) {
            if (future.isDone()) {
                LOGGER.warn("Cannot cancel task because it has already completed.");
            } else if (future.isCancelled()) {
                LOGGER.debug("Task was already cancelled by another thread.");
            } else {
                LOGGER.error("Failed to cancel task that is still running and non-interruption. " +
                        "Check if the task properly handles InterruptedException.");
            }
        }
    }
}
