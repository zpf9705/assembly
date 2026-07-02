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

import io.micrometer.core.annotation.Counted;
import top.osjf.commons.lang.NotNull;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.SimpleCronListener;
import top.osjf.cron.core.micrometer.SystemPropertiesTags;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static top.osjf.cron.core.micrometer.RepositoryMicrometerConstants.*;

/**
 * Abstract base implementation of {@link RunTimesRegistrarRepository},
 * which provides thread-safe limited execution scheduling capability for cron tasks.
 *
 * <p>Core implementation mechanism:
 * <ol>
 * <li>Registers a global built-in {@link CronListener} to the tail of the listener execution chain,
 * which automatically decrements the remaining execution count after each task completes successfully
 * or fails.Once the remaining count drops to 0, the corresponding task will be automatically unregistered.
 * </li>
 * <li>Uses {@link AtomicBoolean} to ensure the built-in run-time check listener is registered only once
 * throughout the repository lifecycle.</li>
 * <li>Overrides listener registration methods to guarantee the built-in count-check listener always
 * stays at the last position of the listener chain, ensuring all business listeners execute first.</li>
 * <li>Prohibits external deletion of the built-in run-time check listener to avoid invalidation of
 * limited execution scheduling rules.</li>
 * <li>Maintains task remaining execution counts via thread-safe {@link ConcurrentMap} with
 * {@link AtomicInteger}.</li>
 * </ol>
 *
 * <p>All limited-run task registration methods integrate Micrometer metrics to count registration
 * invocations for runtime observability.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see AbstractCronListenerRepository
 * @see RunTimesRegistrarRepository
 */
public abstract class AbstractRunTimesRegistrarRepository
        extends AbstractCronListenerRepository implements RunTimesRegistrarRepository {

    /**
     * @see RunTimesCheckedCronListener
     */
    private final RunTimesCheckedCronListener checkedCronListener = new RunTimesCheckedCronListener();

    /**
     * Atomic {@code Boolean} flag is used to indicate whether a {@link #checkedCronListener} listener
     * is registered.
     */
    private final AtomicBoolean isRunTimesCheckListenerRegistered = new AtomicBoolean(false);

    /**
     * Thread-safe map used to record the remaining executable times of each limited-run task,
     */
    private final ConcurrentMap<String, AtomicInteger> taskRunTimesMap = new ConcurrentHashMap<>(16);

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(String expression, Runnable runnable, int times)"},
            description = "Counts invocation times of cron task registration with limited run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(String expression, Runnable runnable, int times)
            throws CronInternalException {
        return registerRunTimes(() -> register(expression, runnable), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(String expression, CronMethodRunnable runnable, int times)"},
            description = "Counts invocation times of cron task registration with limited run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(String expression, CronMethodRunnable runnable, int times)
            throws CronInternalException {
        return registerRunTimes(() -> register(expression, runnable), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(String expression, RunnableTaskBody body, int times)"},
            description = "Counts invocation times of cron task registration with limited run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(String expression, RunnableTaskBody body, int times)
            throws CronInternalException {
        return registerRunTimes(() -> register(expression, body), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(String expression, TaskBody body, int times)"},
            description = "Counts invocation times of cron task registration with limited run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(String expression, TaskBody body, int times) throws CronInternalException {
        return registerRunTimes(() -> register(expression, body), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REGISTER_RUNTIMES_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "registerRunTimes(CronTask task, int times)"},
            description = "Counts invocation times of cron task registration with limited run times configuration")
    @SystemPropertiesTags
    public String registerRunTimes(CronTask task, int times) throws CronInternalException {
        return registerRunTimes(() -> register(task), times);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = ADD_LISTENER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "addListener(CronListener listener)"},
            description = "Counts invocation times of adding a cron listener")
    @SystemPropertiesTags
    public void addListener(CronListener listener) {
        ensureCheckedListenerIsLastIfRuntime(() -> super.addListener(listener));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = ADD_LISTENER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "addLastListener(CronListener listener)"},
            description = "Counts invocation times of adding a cron listener")
    @SystemPropertiesTags
    public void addLastListener(CronListener listener) {
        ensureCheckedListenerIsLastIfRuntime(() -> super.addLastListener(listener));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REMOVE_LISTENER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "removeListener(CronListener listener)"},
            description = "Counts invocation times of removing a cron listener")
    @SystemPropertiesTags
    public boolean removeListener(CronListener listener) {
        if (Objects.equals(listener, checkedCronListener)) {
            throw new IllegalStateException("Unsupported listener objects for deletion " + listener);
        }
        return super.removeListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Counted(value = REMOVE_LISTENER_COUNTER_KEY,
            extraTags = {METHOD_SIGNATURE_TAG_KEY,
                    "removeListener(String listenerName)"},
            description = "Counts invocation times of removing a cron listener")
    @SystemPropertiesTags
    public boolean removeListener(String listenerName) {
        if (Objects.equals(listenerName, checkedCronListener.getName())) {
            throw new IllegalStateException("Unsupported listener objects for deletion " + listenerName);
        }
        return super.removeListener(listenerName);
    }

    /**
     * Executes the task registration logic and binds the specified maximum execution count
     * to the task.
     *
     * Registers the global run-times check listener only once, then records the task's remaining
     * execution times.
     *
     * @param idSupplier the task registration function.
     * @param times      the registration run times.
     * @return unique registered task ID
     * @since 3.0.1
     */
    private String registerRunTimes(Supplier<String> idSupplier, int times) {

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

        return id;
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
     * Returns an immutable view of the task remaining execution count map to prevent external
     * modification of internal runtime data.
     *
     * @return immutable map containing task ID and corresponding remaining execution counter.
     */
    protected Map<String, AtomicInteger> getTaskRunTimesMap() {
        return Collections.unmodifiableMap(taskRunTimesMap);
    }

    /**
     * Built-in listener used to decrement task remaining execution times after each task execution,
     * and automatically unregister the task once the maximum execution limit is reached.
     */
    private class RunTimesCheckedCronListener extends SimpleCronListener {
        @Override
        public String getName() {
            return "Run frequency check listener";
        }

        @Override
        public void successWithId(@NotNull String id) {
            checkRunTimes(id);
        }

        @Override
        public void failedWithId(@NotNull String id, @NotNull Throwable exception) {
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
                        logger.debug("Task with ID [{}] has been terminated because the maximum allowed " +
                                "run count was reached.", id);
                    }
                    return null;
                }
                return count;
            });
        }
    }
}
