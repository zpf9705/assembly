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
import top.osjf.cron.core.exception.CannotCancelConcurrentException;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.NotSupportConcurrentExecutionException;

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
     * @param id unique identifier of target scheduled task
     * @return remaining executable count of specified task
     * @since 3.0.2
     */
    long getTaskRemainingNumberOfRuns(String id);

    /**
     * Returns the total number of registered cron tasks with remaining limited execution times.
     * @return the total number of registered cron tasks with remaining limited execution times.
     * @see #getTaskRemainingNumberOfRuns
     * @see RunTimesRegistrarRepository
     * @since 3.0.2
     */
    long getRemainingLimitedRunTimesTaskCount();

    /**
     * Query the single execution timeout configuration bound to the specified task from the
     * local cache.
     *
     * @param id unique identifier of target scheduled task
     * @return {@link RunningTimeout} timeout configuration instance of the task,
     *         returns {@code null} if no timeout configuration is bound for the task
     * @since 3.0.2
     */
    @Nullable
    RunningTimeout getTimeoutConfig(String id);


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
    Runnable unwrapRunnable(Runnable given);

    /**
     * Check whether the current task scheduler natively supports concurrent task execution.
     * <p>This method retrieves the underlying scheduling capability of the task scheduler:
     * <ul>
     * <li>{@code true}: Scheduler allows for immediate execution upon trigger by cron expressions
     * and support concurrent execution of multiple tasks.</li>
     * <li>{@code false}: After the previous task is completed, the scheduler will calculate the
     * interval time for the next task to be executed, and the execution of a single task will
     * appear serial as a whole.</li>
     * </ul>
     *
     * @return {@code true} the scheduler allows for concurrent execution of individual tasks under
     *          expression rules;
     *         {@code false} all tasks are sequential and cannot be executed concurrently.
     * @since 3.0.2
     */
    boolean isSupportConcurrentExecution();

    /**
     * Check whether the specified scheduled task is restricted from concurrent execution.
     *
     * @param id unique identifier of target scheduled task
     * @return {@code true} if the task is prohibited from concurrent execution;
     *         {@code false} if the task allows concurrent scheduling
     */
    boolean hasDisallowConcurrentExecution(String id);

    /**
     * Bind the disallow-concurrent-execution constraint to the specified scheduled task programmatically.
     *
     * <p>This method provides the programmatic alternative to the {@link DisallowConcurrentExecution}
     * annotation. When the task is triggered, if the previous execution has not finished, the new
     * scheduling request will be blocked to prevent duplicate business processing, resource contention
     * and data inconsistency caused by concurrent invocation.
     *
     * <p>Prerequisite: The scheduler must support concurrent scheduling (i.e. {@link #isSupportConcurrentExecution()}
     * returns {@code true}), otherwise {@code NotSupportConcurrentExecutionException} will be thrown
     * immediately as a fail-fast check.
     *
     * @param id unique identifier of target scheduled task
     * @throws NotSupportConcurrentExecutionException if the underlying scheduler does not support concurrent
     * scheduling, thus disallow-concurrent rule cannot be registered.
     * @see DisallowConcurrentExecution
     * @see CronTaskRegistrar
     * @since 3.0.2
     */
    void disallowConcurrentExecution(String id) throws NotSupportConcurrentExecutionException;

    /**
     * Cancel the disallow-concurrent-execution constraint of the specified scheduled task.
     *
     * <p>This method only works for constraints registered programmatically via {@link #disallowConcurrentExecution}.
     * Tasks annotated with {@link DisallowConcurrentExecution} adopt static declarative configuration, whose
     * concurrency restriction cannot be revoked at runtime. Attempting to cancel such constraints will throw
     * an exception.
     *
     * <p>After cancellation, the task will follow the default concurrency scheduling rule of the underlying
     * scheduler.
     *
     * @param id unique identifier of target scheduled task
     * @throws CannotCancelConcurrentException thrown if failing to cancel the disallow-concurrent execution.
     * @see #disallowConcurrentExecution(String)
     * @see DisallowConcurrentExecution
     * @since 3.0.2
     */
    void cancelDisallowConcurrentExecution(String id) throws CannotCancelConcurrentException;

    /**
     * Set the task identity unique ID generator interface.
     *
     * <p>There is a mandatory requirement not to pass non {@code null} data. If it is passed as
     * {@code null}, the default ID generation rule of the underlying scheduling class will be used.
     * @param idGenerator the task identity unique ID generator interface.
     * @since 3.0.2
     */
    void setIDGenerator(@Nullable IDGenerator idGenerator);

    /**
     * Returns the task identity unique ID generator for custom settings. If the {@link #setIDGenerator}
     * setting is not called or the setting is invalid, it returns {@code null} and does not return the
     * default generator of the underlying scheduler.
     * @return Return the custom generator. If it is {@code null}, the underlying generator will be used
     * by default.
     * @since 3.0.2
     */
    @Nullable
    IDGenerator getIDGenerator();

    /**
     * Create a long-task monitoring executor for observability of asynchronous scheduled tasks.
     * <p>
     * Implemented based on Micrometer {@link io.micrometer.core.instrument.LongTaskTimer}, it is used to
     * count the current concurrent number of running tasks and the blocking duration of tasks.It can
     * effectively discover online risks such as duplicate concurrent execution of scheduled tasks, thread
     * deadlock, IO blocking and thread hang, and task backlog.
     * <p>
     * Cooperated with ordinary Timer metrics, it can realize full-link observability including running
     * concurrency monitoring, execution duration statistics and exception tracking.
     *
     * @param tags {@link io.micrometer.core.instrument.Tags the variable-length array of static low-cardinality tags.}
     * @return {@code LongTimedExecutor} Long-task monitor executor. Invoke {@code start()} before business logic
     * execution, and {@code stop()} must be called after task finished (whether succeeded or failed) to
     * terminate timing and report metrics.
     * @since 3.0.2
     * @see LongTimedExecutor
     */
    LongTimedExecutor longTimed(String... tags);

    /**
     * Executor interface for long-task metrics monitoring.
     * <p>
     * Encapsulates start and stop operations for Micrometer {@link io.micrometer.core.instrument.LongTaskTimer},
     * designed for runtime observability of asynchronous scheduled long-running tasks.
     * It can manually control task timing lifecycle via {@link #start()} and {@link #stop()},
     * or use the wrapped {@link #record(Runnable)} method for automatic timing management.
     * <p>
     * Usage Specification:
     * <ul>
     * <li>1. Manual mode: Call {@link #start()} right before the execution of task business logic to start timing
     * and register running task instance; invoke {@link #stop()} in finally block no matter the task succeeds
     * or throws exceptions, to decrease active task counter and stop timing.</li>
     * <li>2. Automatic wrapping mode: Use {@link #record(Runnable)} to execute the task directly,
     * which internally calls start() before task execution and guarantees stop() execution in finally block.</li>
     * <li>3. This interface only monitors runtime concurrency, active task count and task blocking duration.</li>
     * </ul>
     */
    interface LongTimedExecutor {

        /**
         * Start timing for current long-running task, register task instance to metric system and increment
         * active task counter.
         */
        void start();

        /**
         * Terminate task timing and unregister running task, decrement active task counter.
         * This method must be guaranteed to execute, otherwise active task metrics will be permanently accumulated
         * and cause monitoring data distortion.
         */
        void stop();

        /**
         * Automatically wrap and execute target runnable task with full lifecycle metrics timing.
         * <p>
         * Internal execution logic:
         * <ol>
         * <li>Call {@link #start()} to start task timing before executing business runnable</li>
         * <li>Execute the target task business logic</li>
         * <li>Execute {@link #stop()} in finally block to ensure timing is closed normally whether task succeeds
         * or throws any runtime exception</li>
         * </ol>
         * <p>
         * Recommended preferred usage for most scenarios to avoid missing stop() invocation manually.
         *
         * @param runnable target asynchronous/scheduled business task that needs long-task metrics monitoring.
         */
        void record(Runnable runnable);
    }
}
