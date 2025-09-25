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

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Internally using {@link FutureTask} timeout calculation API
 * {@link FutureTask#get(long, TimeUnit)} to provide timeout
 * monitoring classes for each scheduled task {@link Runnable}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
class FutureTaskRunnable implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(FutureTaskRunnable.class);

    /** the real {@link Runnable}.*/
    private final Runnable real;

    /** the configure instance {@link RunningTimeout}.*/
    private final RunningTimeout timeout;

    /**
     * Construct a {@link FutureTaskRunnable} with given real {@link Runnable}
     * and the configure instance {@link RunningTimeout}.
     *
     * @param real       the real {@link Runnable}.
     * @param timeout    configure instance for timeout control during task execution.
     */
    public FutureTaskRunnable(Runnable real, RunningTimeout timeout) {
        this.real =  real;
        this.timeout =  timeout;
    }

    /**
     * @throws RunningException if running fails occurs.
     */
    @Override
    public void run() throws RunningException {

        // Create a new java.util.concurrent.FutureTask each time to
        // calculate the running time and wait.
        FutureTask<Void> futureTask = new FutureTask<>(real, null);

        try {
            futureTask.get(timeout.getTimeout(), timeout.getTimeUnit());
        }
        catch (TimeoutException ex) {
            handlerTimeoutPolicy(futureTask);
        }
        catch (Exception ex) {
            if (ex instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RunningException(ex);
        }
    }

    /**
     * Process {@link FutureTask} according to the processing strategy.
     * @param futureTask the input {@link FutureTask}.
     */
    void handlerTimeoutPolicy(FutureTask<Void> futureTask) {
        RunningTimeoutPolicy policy = timeout.getPolicy();
        switch (policy) {
            case INTERRUPT: cancel(futureTask); break;
            case THROW: throw new RunningTimeoutException();
            case CANCEL_THROW: {
                cancel(futureTask);
                throw new RunningTimeoutException();
            }
            case IGNORE: break;
        }
    }

    /**
     * Cancel the input {@link FutureTask}.
     * @param futureTask the input {@link FutureTask}.
     */
    void cancel(FutureTask<Void> futureTask) {
        if (!futureTask.cancel(true)) {
            if (futureTask.isDone()) {
                LOGGER.warn("Cannot cancel task because it has already completed.");
            } else if (futureTask.isCancelled()) {
                LOGGER.debug("Task was already cancelled by another thread.");
            } else {
                LOGGER.error("Failed to cancel task that is still running and non-interruption. " +
                        "Check if the task properly handles InterruptedException.");
            }
        }
    }
}
