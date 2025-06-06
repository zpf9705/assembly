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

import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.repository.AbstractCronTaskRepository;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Function;

/**
 * {@code ListenerTaskScheduler} is a custom proxy implementation of an interface
 * {@link TaskScheduler} from the Spring framework. The execution of its core API
 * requires a real {@link TaskScheduler} object, which adds listening functionality
 * to the {@link ScheduledFuture} interface of the method's return result.
 *
 * <p>The execution task instance of the core API will be converted by method
 * {@link #wrapperRunnableToListenable} into a listener executable {@link ListenableRunnable}
 * object, and its return value will be converted into a listener attached
 * {@link ListenableScheduledFuture} instance object.
 *
 * <p>All registered task information will be retained in {@link #futureCache} and can be managed
 * later. For example, in {@link SpringSchedulerTaskRepository}, the retained task information can
 * be updated and stopped through a unique ID.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ListenableTaskScheduler extends AbstractCronTaskRepository implements TaskScheduler {

    /**
     * The underlying Spring TaskScheduler that is being wrapped.
     */
    private final TaskScheduler taskScheduler;

    /**
     * A map to cache listenable scheduled futures, keyed by a unique identifier for each task.
     */
    private final Map<String, ListenableScheduledFuture> futureCache = new ConcurrentHashMap<>(16);

    /**
     * Constructs a new {@code ListenableTaskScheduler} with the given Spring {@code TaskScheduler}.
     *
     * @param taskScheduler the Spring {@code TaskScheduler} to wrap.
     */
    public ListenableTaskScheduler(@NotNull TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * {@inheritDoc}
     *
     * @param task    {@inheritDoc}
     * @param trigger {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if the returned {@code ScheduledFuture} is null.
     */
    @Override
    @NotNull
    public ListenableScheduledFuture schedule(@NotNull Runnable task, @NotNull Trigger trigger) {
        return execute(r -> {
            ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(r, trigger);
            if (scheduledFuture == null) {
                throw new IllegalArgumentException
                        ("In the attempt to schedule a task based on the provided " + trigger.getClass() + " object," +
                                " an error was encountered. Specifically, the nextExecutionTime method of the Trigger" +
                                " object consistently returns null, indicating that the Trigger will never fire to" +
                                " execute the task.");
            }
            return scheduledFuture;
        }, task, trigger);
    }

    @Override
    @NotNull
    public ListenableScheduledFuture schedule(@NotNull Runnable task, @NotNull Date startTime) {
        return execute(r -> taskScheduler.schedule(r, startTime), task, null);
    }

    @Override
    @NotNull
    public ListenableScheduledFuture scheduleAtFixedRate(@NotNull Runnable task, @NotNull Date startTime, long period) {
        return execute(r -> taskScheduler.scheduleAtFixedRate(r, startTime, period), task, null);
    }

    @Override
    @NotNull
    public ListenableScheduledFuture scheduleAtFixedRate(@NotNull Runnable task, long period) {
        return execute(r -> taskScheduler.scheduleAtFixedRate(r, period), task, null);
    }

    @Override
    @NotNull
    public ListenableScheduledFuture scheduleWithFixedDelay(@NotNull Runnable task, @NotNull Date startTime, long delay) {
        return execute(r -> taskScheduler.scheduleWithFixedDelay(r, startTime, delay), task, null);
    }

    @Override
    @NotNull
    public ListenableScheduledFuture scheduleWithFixedDelay(@NotNull Runnable task, long delay) {
        return execute(r -> taskScheduler.scheduleWithFixedDelay(r, delay), task, null);
    }

    /**
     * Return unmodifiable set of unique ID of the registered tasks.
     * @return Unmodifiable set of unique ID of the registered tasks.
     */
    protected Set<String> getFutureIds() {
        return Collections.unmodifiableSet(futureCache.keySet());
    }

    /**
     * Return the cached {@link ListenableScheduledFuture} task found by ID.
     * @param id the Unique ID of the registered task.
     * @return Specify the {@link ListenableScheduledFuture} task with the specified ID.
     */
    @Nullable
    protected ListenableScheduledFuture getFuture(@NotNull String id) {
        return futureCache.get(id);
    }

    /**
     * Cancel the cache {@link ScheduledFuture} task corresponding to the specified ID.
     * @param id the Unique ID of the registered task.
     */
    protected void cancelFuture(@NotNull String id) {
        ListenableScheduledFuture future = futureCache.remove(id);
        if (future != null && !future.isCancelled()) {
            future.cancel(true);
        }
    }

    /**
     * The closing operation of the task scheduler and task cache cleaning should be completed by
     * this class, compatible with the Spring framework's bean cycle processing, and should be
     * completed by the resource class.
     */
    @Override
    public void stop() {
        super.stop();

        // Pause the running task.
        for (Map.Entry<String, ListenableScheduledFuture> entry : futureCache.entrySet()) {
            ListenableScheduledFuture future = entry.getValue();
            if (!future.isCancelled()) {
                future.cancel(true);
            }
        }
        futureCache.clear();

        // Try to close the thread pool.
        if (taskScheduler instanceof CloseableTaskScheduler) {
            try {
                ((CloseableTaskScheduler) taskScheduler).close();
            } catch (IOException ignored) {
            }
        }
        else if (taskScheduler instanceof ThreadPoolTaskScheduler) {
            ((ThreadPoolTaskScheduler) taskScheduler).getScheduledExecutor().shutdownNow();
        }
    }

    /**
     * Encapsulate the given runtime and trigger condition object into a {@link ListenableRunnable}
     * instance object that can be executed and listened to.
     *
     * @param runnable the Runnable to execute whenever the trigger fires
     * @param trigger  an implementation of the {@link Trigger} interface,
     * @return A {@link ListenableRunnable} that can be monitored and executed.
     */
    protected abstract ListenableRunnable wrapperRunnableToListenable(Runnable runnable, Trigger trigger);

    /**
     * The function object that executes the given encapsulation task registration behavior
     * encapsulates the {@link ScheduledFuture} execution result and related task information
     * into a {@link ListenableScheduledFuture} object with an ID tag cached in {@link #futureCache}
     * for later management.
     *
     * @param func     get the function object of the {@link ScheduledFuture} instance.
     * @param runnable the Runnable to execute whenever the trigger fires
     * @param trigger  an implementation of the {@link Trigger} interface,
     * @return An instance of {@link ListenableScheduledFuture} with added listening function.
     */
    @NotNull
    private ListenableScheduledFuture execute(Function<Runnable, ScheduledFuture<?>> func, Runnable runnable,
                                              Trigger trigger) {
        ListenableRunnable listenableRunnable = wrapperRunnableToListenable(runnable, trigger);
        ScheduledFuture<?> scheduledFuture = func.apply(listenableRunnable);
        ListenableScheduledFuture listenableScheduledFuture
                = new ListenableScheduledFuture(listenableRunnable, scheduledFuture);
        futureCache.putIfAbsent(listenableRunnable.getId(), listenableScheduledFuture);
        return listenableScheduledFuture;
    }
}
