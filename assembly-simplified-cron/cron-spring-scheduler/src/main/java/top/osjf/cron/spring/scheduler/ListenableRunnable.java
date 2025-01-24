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

import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import top.osjf.cron.core.listener.CronListener;

import java.util.List;

/**
 * {@code ListenableRunnable} interface extended from {@link Runnable} for defining tasks
 * that can be listened to and executed.
 *
 * <p>In addition to the standard run method, this interface also provides functions such
 * as obtaining task IDs, obtaining internal Runnable objects, obtaining trigger conditions,
 * and registering timed listeners.This allows tasks to have more control and listening
 * capabilities when scheduled and executed.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface ListenableRunnable extends Runnable {

    /**
     * Get the unique identifier of this {@code ListenableRunnable}.
     *
     * @return is a string representing the unique ID of this task.
     * This ID should be unique throughout the entire application,
     * used to identify and distinguish different tasks.
     */
    String getId();

    /**
     * Get the {@code Runnable} object that is actually executed within this
     * {@code ListenableRunnable}.
     *
     * @return a {@code Runnable} object that contains the actual task logic
     * to be executed.
     */
    Runnable getRunnable();

    /**
     * Get the triggering {@link Trigger} conditions for this {@code ListenableRunnable}.
     *
     * @return A {@link Trigger} object that may return null if there are no
     * specific trigger conditions for this task.The Trigger object defines
     * when and how a task should be triggered.
     */
    @Nullable
    Trigger getTrigger();

    /**
     * Get all scheduled {@code CronListener} listeners registered on this
     * {@code ListenableRunnable}.
     *
     * @return a list of {@code CronListener} that will be notified at specific
     * times or events.If this task has not registered any scheduled listeners,
     * it may return null or an empty list.{@code CronListener} is commonly used
     * to set callbacks on timed tasks based on cron expressions.
     */
    @Nullable
    List<CronListener> getCronListeners();
}
