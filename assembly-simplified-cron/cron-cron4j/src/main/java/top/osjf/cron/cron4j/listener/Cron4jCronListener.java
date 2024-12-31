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

import it.sauronsoftware.cron4j.SchedulerListener;
import it.sauronsoftware.cron4j.TaskExecutor;
import top.osjf.cron.core.listener.CronListener;

/**
 * The Cron4j cron task listener {@link CronListener}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface Cron4jCronListener extends SchedulerListener, CronListener {

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Note:</strong>
     * <p>If this method is rewritten, the {@link #start} and {@link #startWithId}
     * methods will become invalid and need to be handled by oneself.
     *
     * @param executor {@inheritDoc}
     */
    @Override
    default void taskLaunching(TaskExecutor executor) {
        start(newCron4jListenerContent(executor));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Note:</strong>
     * <p>If this method is rewritten, the {@link #success} and {@link #successWithId}
     * methods will become invalid and need to be handled by oneself.
     *
     * @param executor {@inheritDoc}
     */
    @Override
    default void taskSucceeded(TaskExecutor executor) {
        success(newCron4jListenerContent(executor));
    }

    /**
     * {@inheritDoc}
     * <p>
     * <strong>Note:</strong>
     * <p>If this method is rewritten, the {@link #failed} and {@link #failedWithId}
     * methods will become invalid and need to be handled by oneself.
     *
     * @param executor {@inheritDoc}
     */
    @Override
    default void taskFailed(TaskExecutor executor, Throwable exception) {
        failed(newCron4jListenerContent(executor), exception);
    }

    /**
     * Creates a new {@code Cron4jListenerContent} by given {@code TaskExecutor}.
     *
     * @param executor cron4j task executor {@code TaskExecutor}.
     * @return a new {@code Cron4jListenerContent}.
     */
    static Cron4jListenerContent newCron4jListenerContent(TaskExecutor executor) {
        return new Cron4jListenerContent(String.valueOf(executor.getTask().getId()), executor);
    }

    @Override
    default void startWithId(String id) {
    }

    @Override
    default void successWithId(String id) {
    }

    @Override
    default void failedWithId(String id, Throwable exception) {
    }
}
