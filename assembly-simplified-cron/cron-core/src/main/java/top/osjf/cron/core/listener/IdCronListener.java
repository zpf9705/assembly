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
 * The IdCronListener interface provides the ability to listen for execution events
 * of scheduled tasks with specific identifiers (IDs).
 *
 * <p>It defines three methods for handling the start, successful completion, and failure
 * of scheduled tasks,allowing access to the relevant task ID through the parameters
 * of these methods.
 *
 * @param <ID> The type of the unique identifier for scheduled tasks.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.1
 */
public interface IdCronListener<ID> {

    /**
     * This method is called when a scheduled task with a specific ID starts
     * execution.
     *
     * @param id The unique identifier of the scheduled task, used to identify
     *           and track the task.
     */
    void onStartWithId(ID id);

    /**
     * This method is called when a scheduled task with a specific ID successfully
     * completes.
     *
     * @param id The unique identifier of the scheduled task, indicating the task
     *           that has been successfully completed.
     */
    void onSucceededWithId(ID id);

    /**
     * This method is called when a scheduled task with a specific ID fails to execute.
     *
     * @param id        The unique identifier of the scheduled task, identifying the
     *                  task that failed to execute.
     * @param exception The captured exception, indicating the reason for the failure
     *                  of the scheduled task to execute.
     */
    void onFailedWithId(ID id, Throwable exception);
}
