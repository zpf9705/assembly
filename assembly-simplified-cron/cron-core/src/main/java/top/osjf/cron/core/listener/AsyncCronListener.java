/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.core.listener;

import top.osjf.commons.lang.NotNull;
import top.osjf.cron.core.repository.CronExecutorServiceSupplier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Cron scheduled task listener supporting asynchronous execution callbacks.
 *
 * <p>Inherit from {@link CronListener} to obtain the ability to receive callbacks for
 * scheduled task lifecycle events, At the same time, implement the external provision
 * of a custom thread pool by {@link CronExecutorServiceSupplier}, the framework will
 * use this thread pool to asynchronously execute all task callback logic within the
 * current listener, avoiding blocking the main thread for scheduled task dispatch.
 *
 * <p>Usage scenarios: When the listening callback logic of a scheduled task takes a long
 * time, involves IO operations, or requires alarm notifications, it is recommended to
 * implement this interface by isolating callback execution through a custom thread pool
 * to prevent the accumulation and delayed execution of scheduled tasks due to callback
 * blocking.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface AsyncCronListener extends CronListener, CronExecutorServiceSupplier, AutoCloseable {
    /**
     * Return a non-null {@link ExecutorService} instance for asynchronous non-blocking
     * execution when executing the callback {@link #start}/{@link #success}/{@link #failed}
     * of {@link CronListener}, to avoid situations such as task execution blocking caused
     * by long waiting times.
     * @return {@inheritDoc}
     */
    @NotNull @Override ExecutorService get();

    /**
     * The asynchronous listener does not support the operation, and the asynchronous Cron listener
     * does not support the propagation strategy for listener exceptions. Any attempt to call or
     * override this method will throw a {@link UnsupportedOperationException}.
     * @return {@code Nulls}
     * @throws UnsupportedOperationException does not support the operation.
     */
    @Override
    default ListenerErrorPropagateStrategy getListenerErrorPropagateStrategy() {
        throw new UnsupportedOperationException
                ("AsyncCronListener does not support custom exception propagation strategy.");
    }

    /**
     * Automatically release the thread pool resources provided by the developer.
     * <p>Default closing strategy: perform graceful shutdown first, wait 5 seconds for running tasks
     * to complete;if timeout occurs, force termination of unfinished tasks to avoid thread leakage.
     *
     * <p><strong>Note:</strong> If the provided {@link ExecutorService} is a global-shared thread pool
     * whose lifecycle is managed externally, you must override this method with an empty implementation
     * to prevent repeated shutdown of the shared thread pool.
     *
     * @throws Exception thrown when an error occurs during thread pool shutdown
     */
    @Override
    default void close() throws Exception {
        ExecutorService executor = get();
        if (executor.isShutdown()) {
            return;
        }
        executor.shutdown();
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }
    }
}
