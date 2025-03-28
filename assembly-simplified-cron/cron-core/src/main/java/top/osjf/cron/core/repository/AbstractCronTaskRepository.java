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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.CronListenerCollector;
import top.osjf.cron.core.listener.SimpleCronListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract implementation of the {@link CronTaskRepository} interface.
 *
 * <p>This class primarily manages the registration of scheduled tasks and
 * the operation of listeners,providing a unified behavior pattern for subclasses
 * through abstract methods.
 *
 * <p>By obtaining the {@link CronListenerCollector} listener collector defined by
 * subclasses, the general implementation of listener addition and deletion functions
 * makes listener object management more convenient and unified.
 *
 * <p>For newly added APIs (such as {@link #registerRunTimes}) for registering tasks
 * a specified number of times, abstract implementation classes have added support
 * for these APIs. The registration calls of these APIs will be based on the ID as
 * the key and the specified number of times as the value. Each time they call back
 * to {@link RegisterTimesCheckedCronListener}, the number of times will be checked,
 * and when the number of times is exhausted, the task will automatically end, in
 * order to better unify the low-level management of the task.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class AbstractCronTaskRepository implements CronTaskRepository {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final RegisterTimesCheckedCronListener checkedCronListener = new RegisterTimesCheckedCronListener();

    private final AtomicBoolean addRegisterTimesCheckedCronListener = new AtomicBoolean(false);

    /**
     * Used to record tasks with specified running times.
     */
    private final ConcurrentMap<String, AtomicInteger> specifyTimesCountMap = new ConcurrentHashMap<>(16);

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull Runnable runnable, int times)
            throws CronInternalException {
        assertTimes(times);
        addRegisterTimesCheckedCronListener();
        specifyTimesCountMap.putIfAbsent(register(expression, runnable), new AtomicInteger(times));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull CronMethodRunnable runnable, int times)
            throws CronInternalException {
        assertTimes(times);
        addRegisterTimesCheckedCronListener();
        specifyTimesCountMap.putIfAbsent(register(expression, runnable), new AtomicInteger(times));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull RunnableTaskBody body, int times)
            throws CronInternalException {
        assertTimes(times);
        addRegisterTimesCheckedCronListener();
        specifyTimesCountMap.putIfAbsent(register(expression, body), new AtomicInteger(times));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull String expression, @NotNull TaskBody body, int times)
            throws CronInternalException {
        assertTimes(times);
        addRegisterTimesCheckedCronListener();
        specifyTimesCountMap.putIfAbsent(register(expression, body), new AtomicInteger(times));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerRunTimes(@NotNull CronTask task, int times)
            throws CronInternalException {
        assertTimes(times);
        addRegisterTimesCheckedCronListener();
        specifyTimesCountMap.putIfAbsent(register(task), new AtomicInteger(times));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addListener(@NotNull CronListener listener) {
        getCronListenerCollector().addCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addFirstListener(@NotNull CronListener listener) {
        getCronListenerCollector().addFirstCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addLastListener(@NotNull CronListener listener) {
        getCronListenerCollector().addLastCronListener(listener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeListener(@NotNull CronListener listener) {
        getCronListenerCollector().removeCronListener(listener);
    }

    /**
     * @return The listener collector for subclasses.
     */
    protected abstract CronListenerCollector getCronListenerCollector();

    /**
     * Assert specify run times > 0.
     *
     * @param times specify run times.
     */
    private void assertTimes(int times) {
        if (times <= 0) {
            throw new IllegalArgumentException("Specify run times must be greater than 0");
        }
    }

    /**
     * Added {@link #checkedCronListener} to the listener list?
     */
    private void addRegisterTimesCheckedCronListener() {
        if (addRegisterTimesCheckedCronListener.compareAndSet(false, true)) {
            addLastListener(checkedCronListener);
        }
    }

    /**
     * Check the listener class for tasks that limit the number of runs.
     */
    private class RegisterTimesCheckedCronListener extends SimpleCronListener {
        @Override
        public void successWithId(String id) {
            checkRunTimes(id);
        }

        @Override
        public void failedWithId(String id, Throwable exception) {
            checkRunTimes(id);
        }

        private void checkRunTimes(String id) {
            specifyTimesCountMap.compute(id, (key, count) -> {
                if (count == null) {
                    return null;
                }
                if (count.decrementAndGet() <= 0) {
                    remove(id);
                    logger.info("Task {} has reached the specified number of runs and has been stopped!", id);
                    return null;
                }
                return count;
            });
        }
    }
}
