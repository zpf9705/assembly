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

package top.osjf.cron.core.listener;

/**
 * The CronListener interface defines the event listening methods
 * during the execution of scheduled tasks.
 *
 * <p>Classes implementing this interface can listen to the start, successful
 * completion, and failure events of scheduled tasks,and perform corresponding
 * processing accordingly.
 *
 * @param <T> The generic T represents the data type passed to the listener methods,
 *            allowing different tasks to pass different data.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface CronListener<T> {

    /**
     * This method is triggered when a scheduled task starts.
     *
     * @param value The parameters passed when starting the scheduled task.
     */
    void onStart(T value);

    /**
     * This method is triggered when the scheduled task successfully ends.
     *
     * @param value The parameters passed when the scheduled task successfully
     *              completes.
     */
    void onSucceeded(T value);

    /**
     * This method is triggered when the scheduled task fails to start.
     *
     * @param value     The parameters passed when attempting to start the
     *                  scheduled task.
     * @param exception The specific exception captured by the listener, indicating
     *                  the reason for the failure of the scheduled task to start.
     */
    void onFailed(T value, Throwable exception);
}
