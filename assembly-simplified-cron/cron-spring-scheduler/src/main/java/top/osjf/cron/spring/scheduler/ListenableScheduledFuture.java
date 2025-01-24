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


package top.osjf.cron.spring.scheduler;

import top.osjf.cron.core.lang.NotNull;

import java.util.concurrent.*;

/**
 * A class that implements the {@code ScheduledFuture<Object>} interface, encapsulating
 * a listenable task ({@code ListenableRunnable}) and an underlying scheduled task
 * ({@code ScheduledFuture<?>}).
 *
 * <p>This class allows you to combine a listenable task with the scheduling capabilities
 * of Java's concurrency utilities.Through this class, you can obtain the delay of the
 * task, compare the execution order of tasks, cancel tasks,check if a task is cancelled
 * or completed, and retrieve the result of the task.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ListenableScheduledFuture implements ScheduledFuture<Object> {

    private final ListenableRunnable listenableRunnable;

    private final ScheduledFuture<?> scheduledFuture;

    /**
     * Constructs a new instance of {@link ListenableScheduledFuture} with given
     * {@code ListenableRunnable} and {@code ScheduledFuture}.
     *
     * @param listenableRunnable the listenable task associated with this scheduled task.
     * @param scheduledFuture    the underlying scheduled task object that manages the scheduling and execution
     *                           of the task.
     */
    public ListenableScheduledFuture(ListenableRunnable listenableRunnable, ScheduledFuture<?> scheduledFuture) {
        this.listenableRunnable = listenableRunnable;
        this.scheduledFuture = scheduledFuture;
    }

    /**
     * Return the listenable task associated with this scheduled task.
     *
     * @return the listenable task associated with this scheduled task.
     */
    public ListenableRunnable getListenableRunnable() {
        return listenableRunnable;
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
        return scheduledFuture.getDelay(unit);
    }

    @Override
    public int compareTo(@NotNull Delayed o) {
        return scheduledFuture.compareTo(o);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return scheduledFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return scheduledFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return scheduledFuture.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        return scheduledFuture.get();
    }

    @Override
    public Object get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException,
            TimeoutException {
        return scheduledFuture.get(timeout, unit);
    }
}
