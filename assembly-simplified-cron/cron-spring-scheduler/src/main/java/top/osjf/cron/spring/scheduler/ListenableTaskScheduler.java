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
import top.osjf.cron.core.lang.NotNull;

import java.util.Date;
import java.util.Map;
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
 * <p>All registered task information will be retained in {@link #listenableScheduledFutures}
 * and can be managed later. For example, in {@link SchedulingRepository}, the retained
 * task information can be updated and stopped through a unique ID.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ListenableTaskScheduler implements TaskScheduler {

    /**
     * The underlying Spring TaskScheduler that is being wrapped.
     */
    private final TaskScheduler taskScheduler;

    /**
     * A map to store the listenable scheduled futures, keyed by a unique identifier for each task.
     */
    private final Map<String, ListenableScheduledFuture> listenableScheduledFutures = new ConcurrentHashMap<>(16);

    /**
     * Constructs a new {@code ListenableTaskScheduler} with the given Spring {@code TaskScheduler}.
     *
     * @param taskScheduler the Spring {@code TaskScheduler} to wrap.
     */
    public ListenableTaskScheduler(@NotNull TaskScheduler taskScheduler) {
        this.taskScheduler = taskScheduler;
    }

    /**
     * Gets the map of listenable scheduled futures.
     *
     * @return the map of listenable scheduled futures.
     */
    public Map<String, ListenableScheduledFuture> getListenableScheduledFutures() {
        return listenableScheduledFutures;
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
     * into a {@link ListenableScheduledFuture} object with an ID tag cached in {@link #listenableScheduledFutures}
     * for later management.
     *
     * @param func     get the function object of the {@link ScheduledFuture} instance.
     * @param runnable the Runnable to execute whenever the trigger fires
     * @param trigger  an implementation of the {@link Trigger} interface,
     * @return An instance of {@link ListenableScheduledFuture} with added listening function.
     */
    @NotNull
    private ListenableScheduledFuture execute(Function<Runnable, ScheduledFuture<?>> func, Runnable runnable, Trigger trigger) {
        ListenableRunnable listenableRunnable = wrapperRunnableToListenable(runnable, trigger);
        ScheduledFuture<?> scheduledFuture = func.apply(listenableRunnable);
        ListenableScheduledFuture listenableScheduledFuture = new ListenableScheduledFuture(listenableRunnable, scheduledFuture);
        listenableScheduledFutures.putIfAbsent(listenableRunnable.getId(), listenableScheduledFuture);
        return listenableScheduledFuture;
    }
}
