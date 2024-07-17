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

import top.osjf.cron.core.annotation.Nullable;

/**
 * An interface for monitoring the execution status of scheduled tasks,
 * including startup, success, and failure.
 *
 * <p>And in each execution stage, specified generic parameters can be carried.
 *
 * @param <T> The specified generic parameter.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface CronListener<T> {

    /**
     * Triggered when a scheduled task starts.
     *
     * @param value The parameters passed.
     */
    void onStart(@Nullable T value);

    /**
     * Triggered when the task successfully ends.
     *
     * @param value The parameters passed.
     */
    void onSucceeded(@Nullable T value);

    /**
     * Triggered when the task fails to start.
     *
     * @param value     The parameters passed.
     * @param exception The specific exception captured by the
     *                  listener when a scheduled task is abnormal.
     */
    void onFailed(@Nullable T value, Throwable exception);
}
