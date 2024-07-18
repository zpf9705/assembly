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

import top.osjf.cron.core.annotation.NotNull;
import top.osjf.cron.core.listener.CronListener;

import java.util.Arrays;
import java.util.List;

/**
 * The resource management interface of the scheduled task lifecycle
 * listener, which can be used to add or delete listener resources.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public interface CronListenerRepository<T extends CronListener> {

    /**
     * Add a scheduled task listener.
     *
     * @param cronListener a scheduled task listener.
     * @throws Exception The specified exception of the
     *                   scheduled task component.
     */
    void addCronListener(@NotNull T cronListener) throws Exception;

    /**
     * Remove a scheduled task listener.
     *
     * @param cronListener a scheduled task listener.
     * @throws Exception The specified exception of the
     *                   scheduled task component.
     */
    void removeCronListener(@NotNull T cronListener) throws Exception;

    /**
     * Add scheduled task listeners.
     *
     * @param cronListeners scheduled task listeners.
     * @throws Exception The specified exception of the
     *                   scheduled task component.
     */
    default void addCronListeners(List<T> cronListeners) throws Exception {
        for (T cronListener : cronListeners) {
            addCronListener(cronListener);
        }
    }

    /**
     * Add scheduled task listeners.
     *
     * @param cronListeners scheduled task listeners.
     * @throws Exception The specified exception of the
     *                   scheduled task component.
     */
    default void addCronListeners(T... cronListeners) throws Exception {
        addCronListeners(Arrays.asList(cronListeners));
    }

    /**
     * Remove scheduled task listeners.
     *
     * @param cronListeners scheduled task listeners.
     * @throws Exception The specified exception of the
     *                   scheduled task component.
     */
    default void removeCronListeners(List<T> cronListeners) throws Exception {
        for (T cronListener : cronListeners) {
            removeCronListener(cronListener);
        }
    }

    /**
     * Remove scheduled task listeners.
     *
     * @param cronListeners scheduled task listeners.
     * @throws Exception The specified exception of the
     *                   scheduled task component.
     */
    default void removeCronListeners(T... cronListeners) throws Exception {
        removeCronListeners(Arrays.asList(cronListeners));
    }
}
