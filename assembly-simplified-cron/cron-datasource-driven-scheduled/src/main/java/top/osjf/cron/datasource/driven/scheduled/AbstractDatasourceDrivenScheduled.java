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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.commons.lang.NotNull;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.*;
import top.osjf.cron.core.repository.*;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    private final CronTaskRepository cronTaskRepository;

    private final DatasourceTaskElementsOperation datasourceTaskElementsOperation;

    /**
     * Based on the {@link Runnable} post processor set parsed from {@link TaskElement}.
     * @since 3.0.2
     */
    @Nullable
    private List<ResolvedRunnablePostProcessor> resolvedRunnablePostProcessors;

    /**
     * @since 3.0.2
     */
    @Nullable private DataSourceConfigLoader configLoader;

    /** Flag that indicates whether this driven scheduler is currently init. */
    private boolean inited = false;

    /** Flag that indicates whether this driven scheduler is currently start. */
    private boolean started = false;

    private final Lock lock = new ReentrantLock();

    /** Property name that determines the task execution environment can be configured in the system
     * variable {@link System#setProperty}. */
    public static final String PROFILES_SYSTEM_PROPERTY_NAME = "cron.datasource.driven.scheduled.profiles";
    @Nullable private static List<String> SYSTEM_PROFILES;

    static {  loadRegisterProfiles(); }

    /**
     * Load the system level configuration task loading environment.
     */
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

    @Override
    public void init() {

        lifecycleStepExecute(this::initInternal, false, "init");
    }

    @Override
    public void start() {

        lifecycleStepExecute(this::startInternal, false, "start");
    }

    @Override
    public void stop() {

        lifecycleStepExecute(this::stopInternal, false, "stop");
    }

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
     * Synchronize the execution of various stages of the lifecycle.
     * @param r {@link DatasourceDrivenScheduledLifecycle} action.
     * @param loggerCatch     the boolean flag of catch do error logger.
     * @param lifecycleName   the specify lifecycle name.
     */
    private void lifecycleStepExecute(ThrowableRunnable r, boolean loggerCatch, String lifecycleName) {
        lock.lock();
        try {
            r.run();
        }
        catch (Throwable ex) {
            if (loggerCatch) {
                getLogger().error("Failed to execute lifecycle step [{}] ", lifecycleName, ex);
            }
            else throw new DataSourceDrivenException(lifecycleName, ex);
        }
        finally {
            lock.unlock();
        }
    }

    /**
     * The internal method of {@link #init()}.
     */
    private void initInternal() {

        // Purge data.
        datasourceTaskElementsOperation.purgeDatasourceTaskElements();

        // The marking has been init.
        inited = true;

        debug("Drive scheduler service has been successfully inited !");
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
                    Thread.sleep(getTaskMonitorCheckInternal());
                }
                catch (InterruptedException ex) {
                    // Respond to interrupt signal, restore interrupt status and exit loop safely
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        /**
         * @return Dynamic config if exists, otherwise return default fixed config
         */
        private Long getTaskMonitorCheckInternal() {
            Long taskMonitorCheckInternal = AbstractDatasourceDrivenScheduled.this.getTaskMonitorCheckInternal();
            if (configLoader == null) {
                return taskMonitorCheckInternal;
            }
            try {
                Long loadConfigValue
                        = configLoader.getConfig(KEY_OF_TASK_CHECK_INTERNAL, Long.class);
                if (loadConfigValue != null) taskMonitorCheckInternal = loadConfigValue;
            }
            catch (Throwable ex) {
                logger.error("Failed to obtain config [{}]", KEY_OF_TASK_CHECK_INTERNAL, ex);
            }
            return taskMonitorCheckInternal;
        }

    }

    /**
     * The internal method of {@link #start()}.
     * @throws IllegalStateException if Drive scheduler has not been initialized
     *                               or already started.
     */
    private void startInternal() {

        //Check if initialization has been performed before starting.
        if (!inited) {
            throw new IllegalStateException("Drive scheduler has not been initialized !");
        }

        // Check if dynamic task management has been started.
        if (started) {
            throw new IllegalStateException("Driven Scheduler already started !");
        }

        List<TaskElement> taskElements = datasourceTaskElementsOperation.getDatasourceTaskElements();
        if (CollectionUtils.isEmpty(taskElements)) {
            debug("No registrable data source task objects were obtained from the data source.");
            return;
        }

        for (TaskElement taskElement : taskElements) {

            elementCheck(taskElement);

            registerTask(taskElement);
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

        debug("Drive scheduler service has been successfully started !");
    }

    /**
     * @since 3.0.2
     * @param element the task element.
     */
    private void elementCheck(TaskElement element) {
        Assert.hasText(element.getId(), "Bad Element : No unique ID");
        Assert.hasText(element.getTaskName(), "Bad Element : No task name");
        Assert.hasText(element.getExpression(), "Bad Element : No cron expression");
        Assert.notNull(element.getUpdateSign(), "Bad Element : No update sign");
        Assert.isTrue(element.getUpdateSign() == 0 ||
                element.getUpdateSign() == 1, "Bad Element : update sign can only be 0 or 1");
    }

    /**
     * The internal method of {@link #inspect()}.
     */
    private void inspectInternal() {

        assertStarted();

        debug("[Time-{}] => Drive scheduler service checks on scheduled information.",
                getActiveTime());

        List<TaskElement> runtimeCheckedDatasourceTaskElements =
                datasourceTaskElementsOperation.getRuntimeNeedCheckDatasourceTaskElements();

        if (CollectionUtils.isEmpty(runtimeCheckedDatasourceTaskElements)) {
            debug("[Time-{}] => Drive scheduler service check of timing information has " +
                            "ended : No processable data provided.", getActiveTime());
            return;
        }

        for (TaskElement element : runtimeCheckedDatasourceTaskElements) {

            elementCheck(element);

            // Pre-check for dynamic changes in markers.
            if (element.isAfterUpdate()) {

                // Here it is judged to be terminated.
                if (element.willBePaused()) {
                    String taskId = element.getTaskId();
                    cronTaskRepository.remove(taskId);
                    element.pausedClear();
                    debug("[Runtime-checked-Task-{}] [{}] execution has been stopped.",
                            element.getId(), element.getTaskDescription());
                }

                // Determine the pending startup here.
                else if (element.willBeActive()) {
                    registerTask(element);
                }

                else {
                    // Check for changes in expressions.
                    String taskId = element.getTaskId();
                    if (StringUtils.isNotBlank(taskId)) {
                        CronTaskInfo cronTaskInfo = cronTaskRepository.getCronTaskInfo(taskId);
                        String oldExpression = cronTaskInfo != null ? cronTaskInfo.getExpression() : null;
                        if (element.expressionNoSame(oldExpression)) {
                            cronTaskRepository.update(element.getTaskId(), element.getExpression());
                            debug("[Runtime-checked-Task-{}] Task name [{}] description [{}] change " +
                                            "expression old [{}] to new [{}].", element.getId(), element.getTaskName(),
                                    element.getTaskDescription(), oldExpression, element.getExpression());
                        }
                    }
                }

                // Reset update tag.
                element.resetUpdateStatus();
            }

            // Check the status of dynamically added tasks.
            else if (element.isAfterInsert()) {
                registerTask(element);
            }
        }

        datasourceTaskElementsOperation.afterInspect(runtimeCheckedDatasourceTaskElements);

        debug("[Time-{}] => Drive scheduler service check of timing information has ended.",
                getActiveTime());
    }

    private static String getActiveTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * The internal method of {@link #stop()}.
     */
    private void stopInternal() throws Exception {

        assertStarted();

        for (TaskElement element : datasourceTaskElementsOperation.getDatasourceTaskElements()) {
            String taskId = element.getTaskId();
            if (StringUtils.isNotBlank(taskId)) {
                cronTaskRepository.remove(taskId);
            }
        }
        datasourceTaskElementsOperation.purgeDatasourceTaskElements();

        // Close datasourceTaskElementsOperation.
        datasourceTaskElementsOperation.close();

        // The marking has been stopped.
        started = false;

        debug("Drive scheduler service has stopped running. To reactivate" +
                " the service, trigger the startup operation via the dynamic lifecycle management interface.");
    }

    /**
     * Check if dynamic task management has been started, and if it has not been started,
     * throw a status exception error.
     * @throws IllegalStateException if Driven Scheduler not started.
     */
    private void assertStarted() {
        if (!started) {
            throw new IllegalStateException("Driven Scheduler not started !");
        }
    }

    /**
     * Register tasks that require dynamic management to the task registration manager.
     *
     * @param taskElement the Task element information.
     */
    private void registerTask(@NotNull TaskElement taskElement) {
        if (taskElement.noActive()) {
            if (taskElement.noActiveDescriptionExist()) {
                taskElement.setStatusDescription(false, "Status not activated");
            }
            debug("[Task-{}] Failed to register : Status not activated", taskElement.getId());
            return;
        }
        if (!profilesMatch(taskElement.getProfiles())) {
            taskElement.setStatusDescription(false, "Environment mismatch");
            debug("[Task-{}] Failed to register : Environment mismatch", taskElement.getId());
            return;
        }

        Runnable taskRunnable = resolveTaskRunnable(taskElement);

        // Apply post processing to the resolved task Runnable if any post processors exist.
        if (resolvedRunnablePostProcessors != null) {
            for (ResolvedRunnablePostProcessor postProcessor : resolvedRunnablePostProcessors) {
                // Invoke the post processor to handle the resolved task Runnable.
                taskRunnable = postProcessor.postProcessResolvedRunnable(taskRunnable, taskElement);
            }
        }

        // When returning [top.osjf.cron.core.repository.CronMethodRunnable] instances that know the
        // target object and method, dynamic registration support based on the maximum number of method
        // runs and timeout mechanism will be supported.
        String taskId = taskRunnable instanceof CronMethodRunnable ?
                new AnnotationMethodRegistrar(new CronTask(taskElement.getExpression(), (CronMethodRunnable) taskRunnable))
                        .registerFor(cronTaskRepository) :
                cronTaskRepository.register(taskElement.getExpression(), taskRunnable);

        taskElement.setTaskId(taskId);
        taskElement.setStatusDescription(true, "Running");
        debug("[Task-{}] Successfully to register : name [{}] ||  description [{}] || expression [{}]",
                taskElement.getId(), taskElement.getTaskName(), taskElement.getTaskDescription(),
                taskElement.getExpression());
    }

    /**
     * Return the log object, which can be provided by subclasses.
     *
     * @return the log object.
     */
    protected Logger getLogger() {
        return logger;
    }

    /**
     * Judging whether the registration environment matches is determined by the subclass.
     *
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
     * Analyze the running function of the sub-task through task information parsing.
     *
     * <p>The fully qualified name interval "@" of the default class plus the name of
     * the method is used as a candidate resolution for {@link TaskElement#getTaskName()}.
     *
     * @param element the task element information.
     * @return Return the {@link CronMethodRunnable} type, which supports the parsing of annotations
     * {@link RunTimes} and {@link RunTimeout} by the source method, and supports a registration
     * mechanism with timeout control and limit on the number of times. Otherwise, execute {@link Runnable}
     * normally. For detailed implementation, please refer to method {@link #registerTask(TaskElement)}.
     * @throws DataSourceDrivenException If the parsing rules are not met or the task fails to run.
     */
    @NotNull
    protected Runnable resolveTaskRunnable(TaskElement element) {
        String taskName = element.getTaskName();
        String[] nameArray = taskName.split("@"); /*class.name()@method.name()*/
        if (nameArray.length != 2) {
            debug("{} does not comply with parsing rules [class's qualified name @ method name]", taskName);
            throw new DataSourceDrivenException(taskName + " does not comply with parsing rules " +
                    "[class's qualified name @ method name].");
        }
        Object target;
        Method targetMethod;
        try {
            Class<?> clazz = ClassUtils.forName(nameArray[0], getClass().getClassLoader());
            target = BeanUtils.instantiateClass(clazz);
            targetMethod = ClassUtils.getMethod(clazz, nameArray[1]);
        }
        catch (Exception ex) {
            debug("Failed to resolve task [" + element.getId() + "] to runnable.", ex);
            throw new DataSourceDrivenException("Failed to resolve task runnable " + element.getId(), ex);
        }

        return new CronMethodRunnable(target, targetMethod);
    }

    /**
     * Gets the polling interval of the task monitor thread.
     * @return the polling interval for task monitor checking, unit: millisecond
     * @since 3.0.2
     */
    protected long getTaskMonitorCheckInternal() {
        return Constants.MONITOR_CHECK_INTERNAL;
    }

    /**
     * @return {@code boolean} flag that the logger instance enabled for the DEBUG level.
     */
    private boolean isLoggerDebug() {
        return getLogger().isDebugEnabled();
    }

    /**
     * Log a message at the DEBUG level according to the specified format
     * and arguments.
     * @param format    the format string
     * @param arguments a list of 3 or more arguments
     */
    private void debug(String format, Object... arguments) {
        if (isLoggerDebug()) {
            getLogger().debug(format, arguments);
        }
    }

    /**
     * Log an exception (throwable) at the DEBUG level with an
     * accompanying message.
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    private void debug(String msg, Throwable t) {
        if (isLoggerDebug()) {
            getLogger().debug(msg, t);
        }
    }
}
