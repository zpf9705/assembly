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


package top.osjf.cron.datasource.driven.scheduled;

import com.cronutils.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.*;
import top.osjf.cron.core.repository.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract base class for datasource-driven scheduled task management systems.
 *
 * <p>This class provides a complete implementation framework for managing scheduled tasks
 * that are dynamically driven by external data sources. It handles the full lifecycle
 * of task management including registration, runtime updates, environment validation,
 * and resource cleanup.</p>
 *
 * <h2>Core Responsibilities:</h2>
 * <ul>
 *   <li>Task Lifecycle Management: Implements {@link DatasourceDrivenScheduledLifecycle}
 *       with proper initialization, startup, and shutdown procedures</li>
 *   <li>Dynamic Task Registration: Supports both manual and datasource-driven task registration</li>
 *   <li>Runtime Task Updates: Monitors and applies configuration changes to running tasks</li>
 *   <li>Environment Validation: Ensures tasks only execute in matching environments</li>
 *   <li>Concurrency Control: Safe management of concurrent task executions</li>
 * </ul>
 *
 * <h2>Key Components:</h2>
 * <ol>
 *   <li>{@link #init()}: Initializes task management infrastructure</li>
 *   <li>{@link #start()}: Activates all registered tasks and begins monitoring</li>
 *   <li>{@link #inspect()}: Core execution method for periodic task validation</li>
 *   <li>{@link #stop()}: Safely deactivates all tasks and releases resources</li>
 * </ol>
 *
 * <h2>Extension Points (Abstract Methods):</h2>
 * <dl>
 *   <dt>{@link #profilesMatch(String)}</dt>
 *   <dd>Environment validation for task activation</dd>
 *
 *   <dt>{@link #resolveTaskRunnable(TaskElement)}</dt>
 *   <dd>Convert task metadata to executable Runnable</dd>
 * </dl>
 *
 * <h2>Runtime Behavior:</h2>
 * <ul>
 *   <li>On startup: Registration management tasks and data source tasks, if
 *   {@code {@link TaskScheduleMonitorStartAction#useThreadPolling()} == true} starts a
 *   monitoring thread {@link CronTaskScheduleMonitorThread}, otherwise customize
 *   the monitoring logic {@link TaskScheduleMonitorStartAction#elseMonitorStartAction()}.</li>
 *   <li>During execution: Monitoring tasks call method {@link #inspect()} based on specific logic
 *   to perform inspection tasks and adapt to timely changes in tasks.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public abstract class AbstractDatasourceDrivenScheduled
        implements DatasourceDrivenScheduledLifecycle, ScheduledSurveillanceInspector {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /** Extend key: mark whether task loaded from datasource */
    public static final String EXTEND_INFO_OF_DATASOURCE_DRIVEN_TASK = "datasource.driven.task";

    /** Extend key: primary id of task record in datasource */
    public static final String EXTEND_INFO_OF_DATASOURCE_DRIVEN_ID = "datasource.driven.id";

    private final CronTaskRepository cronTaskRepository;

    private final DatasourceTaskElementsOperation datasourceTaskElementsOperation;

    @Nullable private List<ResolvedRunnablePostProcessor> resolvedRunnablePostProcessors;

    @Nullable private DataSourceConfigLoader configLoader;

    /** Flag that indicates whether this driven scheduler is currently init. */
    private boolean inited = false;

    /** Flag that indicates whether this driven scheduler is currently start. */
    private boolean started = false;

    /** Lock for protecting lifecycle method execution to prevent concurrent conflicts.*/
    private final Lock lock = new ReentrantLock();

    /** System property key for task execution environment. */
    public static final String PROFILES_SYSTEM_PROPERTY_NAME = "cron.datasource.driven.scheduled.profiles";

    /** Cached parsed environment profiles from system property. */
    @Nullable private static List<String> SYSTEM_PROFILES;

    static {  loadRegisterProfiles(); }

    /** Load and cache environment profiles from system property. */
    static void loadRegisterProfiles() {
        String property = System.getProperty(PROFILES_SYSTEM_PROPERTY_NAME);
        SYSTEM_PROFILES = StringUtils.isBlank(property)
                ? Collections.emptyList() : Arrays.asList(property.split(","));
    }

    /**
     * Constructs a new {@code AbstractDatasourceDrivenScheduled} with {@code CronTaskRepository}
     * as its task Manager and {@code DatasourceTaskElementsOperation} as its task information access.
     *
     * @param cronTaskRepository              the Task management resource explorer.
     * @param datasourceTaskElementsOperation the Task data source information retrieval operation interface.
     */
    public AbstractDatasourceDrivenScheduled(CronTaskRepository cronTaskRepository,
                                             DatasourceTaskElementsOperation datasourceTaskElementsOperation) {

        Assert.notNull(cronTaskRepository, "cronTaskRepository must not be null");
        Assert.notNull(datasourceTaskElementsOperation, "datasourceTaskElementsOperation must not be null");

        this.cronTaskRepository = cronTaskRepository;
        this.datasourceTaskElementsOperation = datasourceTaskElementsOperation;
        this.datasourceTaskElementsOperation.setAbstractDatasourceDrivenScheduled(this);
    }

    /**
     * Set the post processors for {@code Runnable} that have been resolved.
     * <p>These processors will be executed in the registration order, and the resolved
     * {@code Runnable} can be wrapped, enhanced, or replaced.
     * @param resolvedRunnablePostProcessors the post processors for resolved Runnable
     * @since 3.0.2
     */
    public void setResolvedRunnablePostProcessors(@Nullable
                                                  List<ResolvedRunnablePostProcessor> resolvedRunnablePostProcessors)
    {
        this.resolvedRunnablePostProcessors = resolvedRunnablePostProcessors;
    }

    /**
     * Set up a data-driven dynamic configuration interface instance for obtaining task driven
     * management related dynamic configurations, with priority over fixed configurations.
     * @param configLoader the {@link DataSourceConfigLoader} to set.
     * @since 3.0.2
     */
    public void setConfigLoader(@Nullable DataSourceConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    /**
     * @return A data-driven dynamic configuration interface instance.
     * @since 3.0.2
     */
    @Nullable
    public DataSourceConfigLoader getConfigLoader() {
        return configLoader;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {

        lifecycleStepExecute(this::initInternal, false, "init");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {

        lifecycleStepExecute(this::startInternal, false, "start");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {

        lifecycleStepExecute(this::stopInternal, false, "stop");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inspect() {

        lifecycleStepExecute(this::inspectInternal, true, "inspect");
    }

    /**
     * @since 3.0.2
     */
    interface ThrowableRunnable {
        void run() throws Throwable;
    }

    /**
     * Execute lifecycle steps with thread lock protection, support optional exception logging or
     * rethrow wrapped error.
     *
     * @param r Runnable logic of target lifecycle stage
     * @param loggerCatch If true, print error log only when exception occurs; If false, wrap and
     *                   throw exception
     * @param lifecycleName Identity name of current lifecycle step for log recognition
     */
    private void lifecycleStepExecute(ThrowableRunnable r, boolean loggerCatch, String lifecycleName) {
        lock.lock();
        try {
            r.run();
        } catch (Throwable ex) {
            if (loggerCatch) {
                getLogger().error("Lifecycle step [{}] execution failed", lifecycleName, ex);
            } else {
                throw new DataSourceDrivenException(lifecycleName, ex);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Internal initialization logic, invoked by {@link #init()}.
     */
    private void initInternal() {

        // Purge data.
        datasourceTaskElementsOperation.purgeDatasourceTaskElements();

        // Mark scheduler initialization state as completed
        inited = true;

        getLogger().info("Drive scheduler service has been successfully inited !");
    }

    /**
     * Internal implementation for scheduler startup logic, invoked by {@link #start()}.
     *
     * @throws IllegalStateException Thrown if the driven scheduler is uninitialized or already running
     */
    private void startInternal() {

        Assert.state(inited, "Drive scheduler has not been initialized !");

        Assert.state(!started, "Driven Scheduler already started !");

        List<TaskElement> taskElements = datasourceTaskElementsOperation.getDatasourceTaskElements();

        if (CollectionUtils.isEmpty(taskElements)) {
            getLogger().info(("No registrable data source task objects were obtained from the data source."));
            return;
        }

        // Register all loaded task elements to cron repository
        for (TaskElement taskElement : taskElements) {
            registerTaskElement(taskElement);
        }

        datasourceTaskElementsOperation.afterStart(taskElements);

        // Determine startup mode based on monitor strategy: use internal thread polling or
        // execute custom monitor logic

        if (datasourceTaskElementsOperation.useThreadPolling()) {
            // Use built-in thread polling mode: start the background thread for task schedule monitoring

            new CronTaskScheduleMonitorThread().start();
        }
        // Do not use internal polling: execute custom monitor startup logic
        else { datasourceTaskElementsOperation.elseMonitorStartAction(); }

        // The marking has been start.
        started = true;

        getLogger().info(("Drive scheduler service has been successfully started !"));
    }

    /**
     * Extended interface for task execution monitoring and startup logic.
     * @since 3.0.2
     */
    public interface TaskScheduleMonitorStartAction {

        /**
         * Whether to use built-in thread polling for monitoring
         * {@code true}: use internal {@link CronTaskScheduleMonitorThread} thread
         * {@code false}: disable internal polling, use custom monitor strategy
         *
         * @return {@code true} if enable internal thread polling
         */
        boolean useThreadPolling();

        /**
         * Provide custom monitor startup logic when internal polling is disabled
         * Only takes effect when {@link #useThreadPolling()} returns {@code false}.
         */
        void elseMonitorStartAction();
    }

    /**
     * Cron Task Monitor Background Thread
     * Periodically polls task configuration changes to implement dynamic task management
     * including start, stop, update and interrupt operations.
     * Runs as a daemon thread and is automatically managed with the application lifecycle.
     * @since 3.0.2
     */
    private class CronTaskScheduleMonitorThread extends Thread {

        static private final String KEY_OF_TASK_CHECK_INTERNAL = "TASK_CHECK_INTERNAL";

        public CronTaskScheduleMonitorThread() {
            setName("Cron-Task-Monitor-Thread");
            setDaemon(true);
        }

        @Override
        @SuppressWarnings("BusyWait")
        public void run() {

            // Main loop: keep polling while in started state
            while (started) {
                try {
                    // Execute core task checking and scheduling logic
                    inspect();
                }
                catch (Throwable ex) {
                    // simply skip all
                }

                try {
                    // Sleep for the configured polling interval to release CPU resources
                    Thread.sleep(Utils.getConfigSafe(configLoader, KEY_OF_TASK_CHECK_INTERNAL, Long.class,
                            AbstractDatasourceDrivenScheduled.this.getTaskMonitorCheckInternal()));
                }
                catch (InterruptedException ex) {
                    // Respond to interrupt signal, restore interrupt status and exit loop safely
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * Internal execution logic for dynamic scheduled task inspection.
     * <p>Responsible for batch processing tasks that need add/update/remove operations at runtime:
     * <ul>
     * <li>Check scheduler startup state before inspection execution</li>
     * <li>Query eligible task metadata list from data source</li>
     * <li>Process each task by update sign: stop registration / register new task / update cron expression</li>
     * <li>Reset update flag after single task processed</li>
     * <li>Trigger post-inspect callback for batch data synchronization</li>
     * </ul>
     * @throws IllegalStateException if drive scheduler not started.
     */
    private void inspectInternal() {

        Assert.state(started, "Driven scheduler not started !");

        getLogger().info("Driven scheduler starts scheduled task inspection");

        List<TaskElement> checkDatasourceTaskElements =
                datasourceTaskElementsOperation.getRuntimeNeedCheckDatasourceTaskElements();

        if (CollectionUtils.isEmpty(checkDatasourceTaskElements)) {
            getLogger().info("No tasks requiring dynamic processing found, inspection finished");
            return;
        }

        // Process each task element that needs dynamic operation
        for (TaskElement e : checkDatasourceTaskElements) {
            int currentUpdateSign = e.getUpdateSign();
            boolean needProcess = UpdateSign.NEED_DYNAMIC_PROCESS.getCode() == currentUpdateSign;

            if (needProcess) {
                String taskId = e.getTaskId();
                String rawStatus = e.getStatus();
                boolean hasTaskId = StringUtils.hasText(taskId);

                // Branch 1: Task exists & paused state , remove cron registration
                if (hasTaskId
                        && cronTaskRepository.hasCronTaskInfo(taskId) && Status.PAUSED.name().equals(rawStatus)) {
                    cronTaskRepository.remove(taskId);
                    e.setTaskId(""); // Clear registered task id binding
                    recordState(e, Status.PAUSED, "Stop Running"); // Record the current state.
                    getLogger().info("[Inspect-Task-{}] Task execution stopped", e.getId());
                }

                // Branch 2: No taskId & active status, perform new task registration
                else if (!hasTaskId && Status.ACTIVE.name().equals(rawStatus)) {
                    registerTaskElement(e);
                }

                else {

                    // Branch 3: Existing task, compare and update cron expression if changed
                    CronTaskInfo cronTaskInfo = cronTaskRepository.getCronTaskInfo(taskId);
                    String oldExpression = cronTaskInfo != null ? cronTaskInfo.getExpression() : null;
                    String newExpression = e.getExpression();
                    if (!Objects.equals(oldExpression, newExpression)) {
                        cronTaskRepository.update(taskId, newExpression);
                        getLogger().info(
                                "[Inspect-Task-{}] cron expression updated, old=[{}] new=[{}]",
                                e.getId(),
                                oldExpression,
                                newExpression
                        );
                    }
                }

                // Clear update mark after processing
                e.setUpdateSign(UpdateSign.NO_UPDATE.getCode());
            }

            // Handle newly added dynamic task without taskId
            else if (StringUtils.isBlank(e.getTaskId())
                    && UpdateSign.NO_UPDATE.getCode() == currentUpdateSign
                    && Status.isActive(e.getStatus())) {
                registerTaskElement(e);
            }
        }

        // Execute post-processing callback after batch inspection finished
        datasourceTaskElementsOperation.afterInspect(checkDatasourceTaskElements);
        getLogger().info("Driven scheduler scheduled task inspection completed");
    }

    /**
     * Internal implementation for scheduler stop logic, invoked by {@link #stop()}.
     *
     * @throws IllegalStateException Thrown when the driven scheduler has not been started
     * @throws Exception Propagated exceptions from data source operation closing
     */
    private void stopInternal() throws Exception {

        Assert.state(started, "Driven scheduler not started !");

        // Remove all registered cron tasks from repository
        for (TaskElement element : datasourceTaskElementsOperation.getDatasourceTaskElements()) {
            String taskId = element.getTaskId();
            if (StringUtils.isNotBlank(taskId)) {
                cronTaskRepository.remove(taskId);
            }
        }

        // Clean data source information...
        datasourceTaskElementsOperation.purgeDatasourceTaskElements();

        // Close datasourceTaskElementsOperation...
        datasourceTaskElementsOperation.close();

        // The marking has been stopped...
        started = false;

        getLogger().info("Drive scheduler service has stopped running. To reactivate" +
                " the service, trigger the startup operation via the dynamic lifecycle management interface.");
    }

    /**
     * Complete registration logic for task element.
     * <p>Execution flow:
     * <ol>
     * <li>Execute basic non-blank verification of task core fields, interrupt registration if any field empty</li>
     * <li>Check enum validity of task status & update sign, and match runtime environment profile</li>
     * <li>Resolve target executable Runnable instance by parsing task name rule</li>
     * <li>Process resolved Runnable through custom post-processors if exists</li>
     * <li>Register cron task to repository, get unique task id and bind to task element</li>
     * <li>Mark task status as {@link Status#ACTIVE} and print registration success log</li>
     * </ol>
     * <p>All failed validation will mark task state to {@link Status#PAUSED} and terminate the
     * registration flow directly.
     *
     * @param taskElement Metadata carrier object of target scheduled task
     * @since 3.0.2
     */
    private void registerTaskElement(TaskElement taskElement) {
        // ====================== 1. Non empty verification of basic fields======================
        if (!resolveRegistrationState(e -> StringUtils.isNotBlank(e.getId()), taskElement,
                "Task unique ID cannot be blank")) {
            return;
        }

        if (!resolveRegistrationState(e -> StringUtils.isNotBlank(e.getTaskName()), taskElement,
                "Task name cannot be blank")) {
            return;
        }

        if (!resolveRegistrationState(e -> StringUtils.isNotBlank(e.getExpression()), taskElement,
                "Cron expression cannot be blank")) {
            return;
        }

        // ====================== 2. Enumeration legality+business status verification======================
        // Verify task status: enumeration is valid and active
        if (!resolveRegistrationState(e -> Status.isStatus(e.getStatus()) && Status.isActive(e.getStatus()), taskElement,
                "Illegal task status (only values defined in " +
                        "top.osjf.cron.datasource.driven.scheduled.Status enum are allowed) " +
                        "or task status is not activated")) {
            return;
        }

        // Verify the updated identifier enumeration value
        if (!resolveRegistrationState(e -> UpdateSign.isUpdateSign(e.getUpdateSign()), taskElement,
                "Illegal update sign, only values defined in " +
                        "top.osjf.cron.datasource.driven.scheduled.UpdateSign enum are allowed")) {
            return;
        }

        // Verify that the running environment configuration matches
        if (!resolveRegistrationState(e -> profilesMatch(e.getProfiles()), taskElement,
                "Task environment profile does not match the current runtime environment")) {
            return;
        }

        // ====================== 3. Analyze task execution Runnable instance======================
        Runnable taskRunnable = resolveTaskRunnable(taskElement);

        // Skip register when runnable unresolved
        if (taskRunnable == null) {
            if (getLogger().isWarnEnabled())
                getLogger().warn("[Task-{}] Task runnable cannot be resolved, skip registration", taskElement.getId());
            return;
        }

       // ====================== 4. Execute Runnable Post Processor======================
        if (resolvedRunnablePostProcessors != null) {
            for (ResolvedRunnablePostProcessor postProcessor : resolvedRunnablePostProcessors) {
                // Invoke the post processor to handle the resolved task Runnable.
                taskRunnable = postProcessor.postProcessResolvedRunnable(taskRunnable, taskElement);
            }
        }

        // ====================== 5. Register tasks with the scheduled task repository======================
        String taskId = taskRunnable instanceof CronMethodRunnable ?
                new AnnotationMethodRegistrar(new CronTask(taskElement.getExpression(),
                        (CronMethodRunnable) taskRunnable)).registerFor(cronTaskRepository) :
                cronTaskRepository.register(taskElement.getExpression(), taskRunnable);

        // Unique ID of the task generated by back filling
        taskElement.setTaskId(taskId);

        // Mark task registration successful, status set to running
        recordState(taskElement, Status.ACTIVE,"Running");

        // Fill extend info for datasource-driven task
        putExtendInfo(taskId, taskElement);

        // Print registration success log
        getLogger().info("[Task-{}] Successfully to register : name [{}] ||  expression [{}] || description [{}]",
                taskElement.getId(),
                taskElement.getTaskName(),
                taskElement.getExpression(),
                taskElement.getTaskDescription());
    }

    /**
     * Fill extended properties for datasource-driven task.
     * <ol>
     * <li>Force write datasource mark and task primary id, overwrite existing value.</li>
     * <li>Task name and description will only be set if absent to preserve custom extend info.</li>
     * </ol>
     * @param taskId Unique task identifier in scheduler memory
     * @param taskElement Raw task entity loaded from datasource
     * @since 3.0.2
     */
    private void putExtendInfo(String taskId, TaskElement taskElement) {
        CronTaskExtendInfo extendInfo = cronTaskRepository.getExtendInfo(taskId);
        // Mark task loaded from datasource
        extendInfo.put(EXTEND_INFO_OF_DATASOURCE_DRIVEN_TASK, true);
        // Store primary id of task record in datasource
        extendInfo.put(EXTEND_INFO_OF_DATASOURCE_DRIVEN_ID, taskElement.getId());
        // Set task name only when absent, keep existed extend data first
        extendInfo.putIfAbsent(AbstractCronTaskRepository.EXTEND_INFO_OF_NAME, taskElement.getTaskName());
        // Set task description only when absent, keep existed extend data first
        extendInfo.putIfAbsent(AbstractCronTaskRepository.EXTEND_INFO_OF_DESCRIPTION, taskElement.getTaskDescription());
    }

    /**
     * Resolve task registration state based on custom check logic.
     * <p>Execute check function with current task element:
     * <ul>
     * <li>Return {@code true}: verification passed, no state change;</li>
     * <li>Return {@code false}: mark task as {@link Status#PAUSED} and record failure message.</li>
     * </ul>
     *
     * @param checkFunction Custom verification logic receiving TaskElement and returning boolean result
     * @param taskElement Target task metadata instance to operate status
     * @param message Failure prompt text when verification fails
     * @return Verification result: true=pass, false=fail
     * @since 3.0.2
     */
    protected boolean resolveRegistrationState(Function<TaskElement, Boolean> checkFunction, TaskElement taskElement,
                                              String message) {
        boolean result = checkFunction.apply(taskElement);
        if (!result) {
            recordState(taskElement, Status.PAUSED, message);
        }
        return result;
    }

    /**
     * Uniformly update task status and status description remark.
     *
     * @param taskElement Target task element to update state info
     * @param status Target status enum to set
     * @param stateMessage Business detail text for this status
     * @since 3.0.2
     */
    protected void recordState(TaskElement taskElement, Status status, String stateMessage) {
        taskElement.setStatus(status.name());
        String statusDescription = String.format("%s => %s", status.name(), stateMessage);
        taskElement.setStatusDescription(statusDescription);
    }

    /**
     * Get the logger instance for current scheduled task component, allow subclass override
     * to customize logger.
     * @return {@link Logger} instance, never {@literal null}.
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * Judging whether the registration environment matches is determined by the subclass.
     * @param profiles Recorded environmental information.
     * @return {@code true} indicates that the environment matches, otherwise it does not match.
     */
    protected boolean profilesMatch(String profiles) {
        if (StringUtils.isBlank(profiles) || SYSTEM_PROFILES == null) {
            return true;
        }
        return SYSTEM_PROFILES.contains(profiles);
    }

    /**
     * Resolve the executable Runnable instance corresponding to the task element.
     * <p>Parsing rule of taskName: full qualified class name @ target method name, split by "@".
     * <p>Processing logic:
     * <ul>
     * <li>Split task name with symbol "@", return null and mark task PAUSED if split result length is not
     * equal to 2;</li>
     * <li>Load target class, instantiate bean object and fetch target execution method by reflection;</li>
     * <li>Return null and record failure description if any reflection exception occurs during parsing;</li>
     * <li>Return wrapped CronMethodRunnable when parsing completes successfully.</li>
     * </ul>
     *
     * @param taskElement Target task metadata element to resolve runnable
     * @return Parsed {@code CronMethodRunnable}; return {@code null} if parsing rule mismatch or reflection
     * load fails.
     */
    @Nullable
    protected Runnable resolveTaskRunnable(TaskElement taskElement) {
        String taskName = taskElement.getTaskName();

        String[] nameArray = taskName.split("@"); /* class.name()@method.name() */

        if (!resolveRegistrationState(e -> nameArray.length != 2, taskElement,
                taskName + " does not comply with parsing rules [class's qualified name @ method name]")) {
            return null;
        }

        Object target;
        Method targetMethod;
        try {
            Class<?> clazz = ClassUtils.forName(nameArray[0], getClass().getClassLoader());
            target = BeanUtils.instantiateClass(clazz);
            targetMethod = ClassUtils.getMethod(clazz, nameArray[1]);
        }
        catch (Exception ex) {
            recordState(taskElement, Status.PAUSED, taskName + " parsing failed: " + ex.getMessage());
            return null;
        }

        return new CronMethodRunnable(target, targetMethod);
    }

    /**
     * Get the polling interval of the task status monitor thread.
     * @return Task monitor polling interval in milliseconds
     * @since 3.0.2
     * @see CronTaskScheduleMonitorThread
     */
    protected long getTaskMonitorCheckInternal() {
        return Constants.MONITOR_CHECK_INTERNAL;
    }
}
