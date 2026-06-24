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

import top.osjf.commons.ability.Nameable;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.lang.Wrapper;
import top.osjf.cron.core.exception.CronExpressionInvalidException;

import javax.annotation.concurrent.ThreadSafe;

/**
 * <p>{@code CronTaskRepository} is a composite interface that defines the core management
 * capabilities for cron-triggered tasks within the system. By extending multiple well-defined
 * sub-interfaces,it forms a comprehensive and extensible framework for task persistence and
 * runtime control.
 *
 * <p>This interface is primarily used to support platform-level functionalities such as
 * dynamic task registration,execution monitoring, lifecycle management, run-time tracking,
 * timeout handling, and event listening.Typical use cases include: dynamically adding or
 * removing scheduled tasks, monitoring execution frequency and duration,reacting to task
 * state changes, and controlling task startup/shutdown behavior.
 *
 * <p>The responsibilities of the extended interfaces are as follows:
 * <ul>
 *   <li>{@link Repository}: Used to mark it as a resource operation interface.</li>
 *   <li>{@link RunTimesRegistrarRepository}: Manages the registration and persistence of
 *   task execution counts,used for statistics and scheduling decisions.</li>
 *   <li>{@link RunTimeoutRegistrarRepository}: Handles maximum allowed execution time (timeout
 *   thresholds) for tasks,enabling timeout detection and interruption mechanisms.</li>
 *   <li>{@link ListableRepository}: Provide task scheduling list access capability to dynamically
 *   obtain relevant information.</li>
 *   <li>{@link CronListenerRepository}: Allows registration and management of event listeners
 *   related to cron tasks (e.g., on-start, on-completion, on-failure), facilitating an event-driven
 *   architecture.</li>
 *   <li>{@link LifecycleRepository}: Defines lifecycle control methods such as start, stop, and
 *   restart.</li>
 *   <li>{@link top.osjf.commons.lang.Wrapper}: Enables decorator pattern support, allowing task instances
 *   to be wrapped with cross-cutting concerns like logging, monitoring, retry logic, etc.</li>
 * </ul>
 *
 * <p>The differentiation and combination of the above modules were gathered in version 3.0.1,
 * aiming to provide developers with different feature choices for task scheduling based on
 * cron expressions. This solution fully covers all inherited functions of the interface and
 * helps developers quickly understand the collaborative relationships of multiple blocks
 * through structured display.
 *
 * <p> In version 3.0.2, this interface has also extended multiple unique business capabilities:
 * framework-adaptive Cron expression validity verification, querying the remaining executable
 * times of a specified task, obtaining task runtime timeout configuration, standardized customization
 * of task metadata, and monitoring overdue task executable instances for unpacking operations,
 * to achieve adaptive scheduling for various different scheduled task frameworks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 * @see Repository
 * @see RunTimesRegistrarRepository
 * @see RunTimeoutRegistrarRepository
 * @see ListableRepository
 * @see CronListenerRepository
 * @see LifecycleRepository
 * @see top.osjf.commons.lang.Wrapper
 */
@ThreadSafe
public interface CronTaskRepository extends Repository, RunTimesRegistrarRepository, RunTimeoutRegistrarRepository,
        ListableRepository, CronListenerRepository, LifecycleRepository, Wrapper, Nameable {

    /**
     * Check whether the current scheduling framework repository supports parsing the given cron
     * expression.
     * Different scheduling frameworks have different expression format rules, so the verification
     * logic is implemented independently by each repository implementation class.
     *
     * @param expression the cron or periodic expression to be verified
     * @return {@code true} if the current repository can parse the expression normally,
     *         {@code false} if the expression format does not conform to the framework specification
     * @since 3.0.2
     */
    boolean isSupportedExpression(String expression);

    /**
     * Strongly verify whether the given cron expression complies with the parsing rules of the
     * current repository.
     * If the verification fails, {@link CronExpressionInvalidException} will be thrown directly
     * to interrupt the subsequent task registration or update process to prevent invalid scheduled
     * tasks from being registered.
     *
     * @param expression the cron or periodic expression to be verified
     * @throws CronExpressionInvalidException thrown when the expression is not supported by current
     *                                         repository.
     * @since 3.0.2
     */
    void checkSupportedExpression(String expression) throws CronExpressionInvalidException;

    /**
     * Get the remaining executable times of the task corresponding to the specified task unique ID.
     *
     * <ul>
     * <li>Return {@code -1}: the task supports unlimited repeated execution;</li>
     * <li>Return {@code 0}: no valid task corresponding to the given taskId exists;</li>
     * <li>Return positive number: the remaining available execution times of the task.</li>
     * </ul>
     *
     * @param taskId unique identifier of target scheduled task
     * @return remaining executable count of specified task
     * @since 3.0.2
     */
    long getTaskRemainingNumberOfRuns(String taskId);

    /**
     * Query the single execution timeout configuration bound to the specified task from the
     * local cache.
     *
     * @param taskId unique identifier of target scheduled task
     * @return {@link RunningTimeout} timeout configuration instance of the task,
     *         returns {@code null} if no timeout configuration is bound for the task
     * @since 3.0.2
     */
    @Nullable
    RunningTimeout getTimeoutConfig(String taskId);


    /**
     * Customize or supplement the default configuration information for the incoming scheduled
     * task metadata.
     * Implementations will fill missing default fields, verify parameter legality, or rewrite
     * custom attributes based on the current repository's scheduling rules.
     *
     * @param cronTaskInfo original scheduled task metadata to be customized, can be {@code null}
     * @return completed and standardized {@link CronTaskInfo} task metadata instance
     * @since 3.0.2
     */
    @Nullable
    CronTaskInfo customizeCronTaskInfo(@Nullable CronTaskInfo cronTaskInfo);

    /**
     * Unwrap the original real task from the wrapped {@link Runnable} instance.
     * If the incoming runnable is wrapped by {@link TimeoutMonitoringRunnable}, extract and return
     * the internally wrapped original task; otherwise return the incoming runnable directly.
     *
     * @param given the wrapped or original {@link Runnable} task instance to unwrap
     * @return unwrapped real underlying task instance
     * @since 3.0.2
     */
    Runnable unwaperRunnable(Runnable given);
}
