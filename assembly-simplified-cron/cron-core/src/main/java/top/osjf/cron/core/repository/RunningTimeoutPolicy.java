/*
 * Copyright 2025-? the original author or authors.
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

/**
 * Execution strategy after task scheduling execution timeout.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public enum RunningTimeoutPolicy {

    /**
     * Interrupts the running task when timeout occurs.
     * The thread executing the task will be interrupted via {@code Thread.interrupt()}.
     * This policy should be used when immediate termination is required to free up
     * resources.
     */
    INTERRUPT,

    /**
     * Throws a timeout exception to the caller, but does not interrupt or cancel
     * the actual running task.Use this policy when you want to notify the caller
     * of timeout without affecting backend execution.
     */
    THROW,

    /**
     * Throws a timeout exception to the caller and attempts to cancel the running
     * task. Combines the behaviors of throwing an exception and cancelling the task
     * (with interruption).Recommended when you want to both notify the caller and
     * clean up backend resources.
     */
    CANCEL_THROW,

    /**
     * Ignores the timeout and allows the task to continue running in the background.
     * No exception is thrown, and the result may still be used if completed later.
     * Suitable for fire-and-forget or asynchronous data processing tasks.
     */
    IGNORE
}
