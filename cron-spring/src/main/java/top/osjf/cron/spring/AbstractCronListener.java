/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.cron.spring;

import cn.hutool.cron.TaskExecutor;
import org.springframework.lang.NonNull;

/**
 * For abstract classes implemented by exception methods, the signals
 * for startup and success can be selectively rewritten.
 *
 * @see CronListener
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractCronListener implements CronListener {

    @Override
    public void onStart(TaskExecutor executor) {
        onStartSetSchedulerId(executor.getCronTask().getId());
    }

    @Override
    public void onSucceeded(TaskExecutor executor) {
        onSucceededSetSchedulerId(executor.getCronTask().getId());
    }

    @Override
    public void onFailed(TaskExecutor executor, Throwable exception) {
        onFailedSetSchedulerId(executor.getCronTask().getId(), exception);
    }

    /**
     * Triggered when a scheduled task is initiated, passing a unique scheduled ID.
     * @param schedulerId a unique scheduled ID.
     */
    public void onStartSetSchedulerId(@NonNull String schedulerId) {

    }

    /**
     * Triggered when a timed task is completed, passing a unique timed ID.
     * @param schedulerId a unique scheduled ID.
     */
    public void onSucceededSetSchedulerId(@NonNull String schedulerId) {

    }

    /**
     * Triggered when a scheduled task is abnormal, passing a unique scheduled ID.
     * @param schedulerId a unique scheduled ID.
     * @param exception Abnormal encapsulation.
     */
    public void onFailedSetSchedulerId(@NonNull String schedulerId, Throwable exception) {

    }
}
