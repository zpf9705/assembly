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
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.lang.Wrapper;
import top.osjf.cron.core.exception.CannotCancelConcurrentException;
import top.osjf.cron.core.exception.CronExpressionInvalidException;
import top.osjf.cron.core.exception.CronInternalException;
import top.osjf.cron.core.exception.NotSupportConcurrentExecutionException;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A composite top-level interface that aggregates all core capabilities for scheduled cron task
 * management.It inherits multiple functional sub-interfaces to build a complete, extensible runtime
 * scheduling governance framework.
 *
 * <p>This interface provides full lifecycle governance for scheduled tasks, covering dynamic task
 * registration,runtime execution monitoring, lifecycle start-stop control, limited execution scheduling,
 * execution timeout interception, task event listening, task metadata querying, and decorator wrapping
 * capability.Typical application scenarios include dynamically managing scheduled tasks, monitoring task
 * execution status, customizing task scheduling rules, and uniformly handling task runtime exceptions.
 *
 * <p>The responsibilities of each inherited sub-interface are as follows:
 * <ul>
 *   <li>{@link Repository}: Marker interface, identifying the current type as a task resource repository
 *   .</li>
 *   <li>{@link RunTimesRegistrarRepository}: Supports limited scheduled task registration, controls the
 *   maximum number of task executions, and automatically clears tasks after reaching the execution limit.
 *   </li>
 *   <li>{@link RunTimeoutRegistrarRepository}: Provides single-task execution timeout governance, interrupts
 *   blocked long-running tasks to avoid thread pool exhaustion.</li>
 *   <li>{@link ListableRepository}: Exposes task metadata query capability, supporting task existence check,
 *   task detail query, running status judgment, and next trigger time batch query.</li>
 *   <li>{@link CronListenerRepository}: Supports registration of task lifecycle event listeners to implement
 *   event-driven governance such as task start, completion, and failure callbacks.</li>
 *   <li>{@link LifecycleRepository}: Defines the component lifecycle, supporting repository start, stop,
 *   and restart operations.</li>
 *   <li>{@link Wrapper}: Implements the decorator pattern, enabling cross-cutting capability extension such
 *   as logging, monitoring, and retry for task execution bodies.</li>
 *   <li>{@link Nameable}: Standardizes the naming capability of repository instances for multi-repository
 *   scenario identification.</li>
 * </ul>
 *
 * <p>All modular capabilities above were merged and standardized in version 3.0.1, providing developers with
 * modular, optional scheduling capabilities based on cron expressions, and clarifying the collaborative
 * relationship between each functional module.
 *
 * <p><strong>New capabilities introduced in version 3.0.2:</strong>
 * <ol>
 * <li>Cron expression compatibility check: Provides {@link #isSupportedExpression(String)} and
 * {@link #checkSupportedExpression(String)} to adapt the expression parsing rules of different underlying
 * scheduling frameworks and avoid invalid task registration.</li>
 * <li>Limited execution statistics: Supports querying the remaining execution times of a single task and
 * counting the total number of tasks with execution limit constraints.</li>
 * <li>Task timeout configuration query: Obtains the bound single-task execution timeout rule from the
 * repository cache.</li>
 * <li>Task metadata normalization: Standardizes and supplements default fields for {@link CronTaskInfo}
 * to ensure unified specification of task metadata in multi-framework adaptation scenarios.</li>
 * <li>Wrapped task unwrapping: Parses the original task execution body from wrapped {@link Runnable} to
 * obtain the real business task instance.</li>
 * <li>Concurrency governance capability: Detects the native concurrency support of the underlying scheduler,
 * dynamically binds or revokes task concurrency prohibition constraints, and provides a programmatic
 * alternative to the {@code @DisallowConcurrentExecution} annotation.</li>
 * <li>Custom task ID generation: Supports configuring a global custom task unique ID generator to replace
 * the framework's default ID generation rule.</li>
 * <li>Micrometer observability monitoring: Integrates {@code LongTaskTimer} to implement long-running task
 * runtime metrics monitoring, which can track task concurrent count, execution blocking duration, thread
 * blocking, deadlock and other online risks.</li>
 * <li>Fluent task registration builder: Added the {@link #newBuilder()} method and built-in {@link Builder}
 * interface to support chained parameter configuration for scheduled tasks, which automatically adapts various
 * task types and governance rules to simplify the coding complexity of task registration.</li>
 * <li>By using method {@link #getSupportTaskBodyClasses()}, you can further understand the types of
 * {@link TaskBody} supported by the underlying timing architecture, making it easier to use methods that
 * support {@link TaskBody} registration for scalable task registration..</li>
 * <li>In order to adapt to tasks with more information maintenance and compatibility with attributes such
 * as {@link Description} and {@link Name}, open {@link #getExtendInfo(String)} for developers to manage
 * {@link CronTaskExtendInfo}, and developers can perform relevant operations as needed.</li>
 * </ol>
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
     * @param cronTaskInfo original scheduled task metadata to be customized.
     * @return completed and standardized {@link CronTaskInfo} task metadata instance
     * @since 3.0.2
     */
    CronTaskInfo customizeCronTaskInfo(CronTaskInfo cronTaskInfo);

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
     * @see AnnotationMethodRegistrar
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

    /**
     * Creates a builder instance for fluent registration of cron scheduled tasks.
     *
     * <p>Supports chained configuration of cron expression, task body, maximum execution times and
     * task timeout rules, and automatically matches the corresponding overloaded registration method
     * when executing {@link Builder#build()}.
     *
     * @return the fluent builder for cron task registration.
     * @since 3.0.2
     */
    Builder newBuilder();

    /**
     * Fluent builder interface for registering cron scheduled tasks with governance capabilities.
     * <p>
     * Bind to the current {@link CronTaskRepository} instance, used to assemble task registration
     * parameters in a chained manner, and complete task registration after calling {@link #build()}.
     *
     * @see CronTaskBuilder
     */
    interface Builder {

        /**
         * Sets the cron trigger expression for scheduled task.
         * @param expression valid cron expression string
         * @return current builder instance for chained calls
         */
        Builder withExpression(String expression);

        /**
         * Sets the business display name of the scheduled task.
         * @param name task custom name
         * @return current builder instance for chained calls
         */
        Builder withName(@Nullable String name);

        /**
         * Sets task execution body using native {@link Runnable}.
         * @param runnable task execution logic
         * @return current builder instance for chained calls
         */
        Builder withTask(Runnable runnable);

        /**
         * Sets http callback url for task execution, using default timeout values.
         * @param url target http request url
         * @return current builder instance for chained calls
         */
        Builder withTask(String url);

        /**
         * Sets http callback url and timeout config for task execution.
         * @param url target http request url
         * @param connectTimeout connect timeout in milliseconds
         * @param readTimeout read timeout in milliseconds
         * @return current builder instance for chained calls
         */
        Builder withTask(String url, int connectTimeout, int readTimeout);

        /**
         * Sets task execution body using {@link CronMethodRunnable}
         * wrapped method task.
         * @param methodRunnable method encapsulated task runnable
         * @return current builder instance for chained calls
         */
        Builder withTask(CronMethodRunnable methodRunnable);

        /**
         * Sets task execution body using {@link RunnableTaskBody}
         * wrapped runnable task.
         * @param runnableTaskBody runnable type task wrapper
         * @return current builder instance for chained calls
         */
        Builder withTask(RunnableTaskBody runnableTaskBody);

        /**
         * Sets task execution body using generic {@link TaskBody}.
         * @param taskBody universal task body wrapper
         * @return current builder instance for chained calls
         */
        Builder withTask(TaskBody taskBody);

        /**
         * Sets the complete {@link CronTask} object as task
         * registration metadata.
         * @param cronTask integrated cron task information
         * @return current builder instance for chained calls
         */
        Builder withTask(CronTask cronTask);

        /**
         * Enables limited execution times governance for the scheduled task.
         * The task will be automatically unregistered after reaching the configured
         * maximum execution count.
         * @param maxTimes maximum allowed execution times.
         * @return current builder instance for chained calls
         */
        Builder limitRunTimes(int maxTimes);

        /**
         * Enables timeout governance for the scheduled task.
         * The running task will be interrupted if execution duration exceeds the
         * configured timeout threshold.
         * @param timeout task timeout configuration rule.
         * @return current builder instance for chained calls.
         */
        Builder timeout(@Nullable RunningTimeout timeout);

        /**
         * Prohibits concurrent execution of this scheduled task.
         * The next trigger will be skipped if the previous task instance is still running.
         * @return current builder instance for chained calls
         */
        Builder disallowConcurrentExecution();

        /**
         * Sets the detailed business description of the scheduled task.
         * @param description task remark information
         * @return current builder instance for chained calls
         */
        Builder withDescription(@Nullable String description);

        /**
         * Validates assembled parameters, matches the corresponding overloaded
         * registration method, completes cron task registration and returns the
         * globally unique task ID.
         * @return globally unique registered task identifier
         * @throws IllegalArgumentException required parameter missing or unsupported
         * task type.
         * @throws CronInternalException If task registration occurs error.
         */
        String build();
    }

    /**
     * Returns all {@link TaskBody} implementation classes supported by the current task
     * repository.
     * @return array of supported {@link TaskBody} implementation types
     * @since 3.0.2
     */
    Class<? extends TaskBody>[] getSupportTaskBodyClasses();

    /**
     * Retrieve the extended attribute container of the specified scheduled task by task ID.
     *
     * <p>If the target task exists but no extended attributes have been set yet, a brand-new
     * empty {@link CronTaskExtendInfo} instance will be returned by default.Developers are
     * allowed to freely perform add, delete, modify and query operations on the returned
     * extended attribute container.
     *
     * @param id the unique identifier of the target scheduled task
     * @return non-null extended attribute container of the task
     * @throws CronInternalException if no task can be found for the given task id
     * @since 3.0.2
     */
    @NotNull
    CronTaskExtendInfo getExtendInfo(String id);
}
