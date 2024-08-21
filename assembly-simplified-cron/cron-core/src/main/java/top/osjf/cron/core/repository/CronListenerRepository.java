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

import top.osjf.cron.core.listener.CronListener;

import java.util.Arrays;
import java.util.List;

/**
 * Cron Listener Repository Interface, used for managing Cron task listeners.
 *
 * <p>This interface provides methods for adding and removing single or multiple
 * Cron listeners.
 *
 * <p>It uses a generic type T to specify the type of listener, which must extend
 * from the CronListener interface.
 *
 * @param <T> The type of cron listener.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public interface CronListenerRepository<T extends CronListener> {

    /**
     * Adds a Cron listener to the repository.
     *
     * @param cronListener The Cron listener instance to be added.
     */
    void addCronListener(T cronListener);

    /**
     * Removes a Cron listener from the repository.
     *
     * @param cronListener The Cron listener instance to be removed.
     */
    void removeCronListener(T cronListener);

    /**
     * Adds multiple Cron listeners to the repository.
     *
     * @param cronListeners The list of Cron listener instances to be added.
     */
    default void addCronListeners(List<T> cronListeners) {
        for (T cronListener : cronListeners) {
            addCronListener(cronListener);
        }
    }

    /**
     * Adds multiple Cron listeners to the repository, passed as a varargs list.
     *
     * @param cronListeners The array of Cron listener instances to be added.
     */
    default void addCronListeners(T... cronListeners) {
        addCronListeners(Arrays.asList(cronListeners));
    }

    /**
     * Removes multiple Cron listeners from the repository.
     *
     * @param cronListeners The list of Cron listener instances to be removed.
     */
    default void removeCronListeners(List<T> cronListeners) {
        for (T cronListener : cronListeners) {
            removeCronListener(cronListener);
        }
    }

    /**
     * Removes multiple Cron listeners from the repository, passed as a varargs list.
     *
     * @param cronListeners The array of Cron listener instances to be removed.
     */
    default void removeCronListeners(T... cronListeners) {
        removeCronListeners(Arrays.asList(cronListeners));
    }
}
