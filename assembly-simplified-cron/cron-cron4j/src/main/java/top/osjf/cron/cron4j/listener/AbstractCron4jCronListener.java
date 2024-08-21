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

package top.osjf.cron.cron4j.listener;

import it.sauronsoftware.cron4j.Task;
import it.sauronsoftware.cron4j.TaskExecutor;
import top.osjf.cron.core.listener.IdCronListener;

/**
 * The abstract service class of the listener for {@code Cron4j} includes the
 * stage method passing that specifies the task ID.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public abstract class AbstractCron4jCronListener implements Cron4jCronListener, IdCronListener<String> {

    @Override
    public void onStart(TaskExecutor value) {
        onStartWithId(getTaskId(value));
    }

    @Override
    public void onSucceeded(TaskExecutor value) {
        onSucceededWithId(getTaskId(value));
    }

    @Override
    public void onFailed(TaskExecutor value, Throwable exception) {
        onFailedWithId(getTaskId(value), exception);
    }

    @Override
    public void onStartWithId(String value) {
    }

    @Override
    public void onSucceededWithId(String value) {
    }

    @Override
    public void onFailedWithId(String value, Throwable exception) {
    }

    /**
     * Return Cron4j task id.
     *
     * @param value Cron4j task executor.
     */
    private String getTaskId(TaskExecutor value) {
        Task task = value.getTask();
        try {
            return Task.class.getDeclaredField("id").get(task).toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
