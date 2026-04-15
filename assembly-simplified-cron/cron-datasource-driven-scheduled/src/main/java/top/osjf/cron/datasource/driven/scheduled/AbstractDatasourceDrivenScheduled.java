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
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.repository.*;
import top.osjf.cron.core.util.*;

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
 *   <li>{@link #run()}: Core execution method for periodic task validation</li>
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
 *   <li>On startup: Registers management task and data source tasks</li>
 *   <li>During execution: Periodically checks for task updates
 *   (every {@value Constants#MANAGER_TASK_CHECK_FREQUENCY_CRON} if no provider main task information)</li>
 *   <li>On update detection: Applies configuration changes or stops/starts tasks as needed</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public abstract class AbstractDatasourceDrivenScheduled
        implements DatasourceDrivenScheduledLifecycle, ManagerTaskUniqueIdentifiersProvider, Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CronTaskRepository cronTaskRepository;
    private final DatasourceTaskElementsOperation datasourceTaskElementsOperation;

    /**
     * Based on the {@link Runnable} post processor set parsed from {@link TaskElement}.
     * @since 3.0.2
     */
    @Nullable private List<ResolvedRunnablePostProcessor> resolvedRunnablePostProcessors;

    /** Flag that indicates whether this driven scheduler is currently init. */
    private boolean inited = false;
    /** Flag that indicates whether this driven scheduler is currently start. */
    private boolean started = false;

    private final Lock lock = new ReentrantLock();

    private String[] mangerTaskUniqueIds;

    /** Property name that determines the task execution environment can be configured in the system
     * variable {@link System#setProperty}. */
    public static final String PROFILES_SYSTEM_PROPERTY_NAME = "cron.datasource.driven.scheduled.profiles";
    private static List<String> SYSTEM_PROFILES;

    /**
     * Due to compatibility limitations on {@link RunTimes} run times, it is not possible to return
     * the task ID and a custom prefix constant is assigned to the combined task ID.
     * @since 3.0.2
     */
    private static final String PREFIX_SIGN_OF_TIMES_REGISTED = "Frequency-limit-";

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

    @Override
    public void init() {

        lockExecuteLifecycle(this::initInternal, false, "init");
    }

    @Override
    public void start() {

        lockExecuteLifecycle(this::startInternal, false, "start");
    }

    @Override
    public void run() {

        lockExecuteLifecycle(this::runInternal, true, "run");
    }

    @Override
    public void stop() {

        lockExecuteLifecycle(this::stopInternal, false, "stop");
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
    private void lockExecuteLifecycle(ThrowableRunnable r, boolean loggerCatch, String lifecycleName) {
        lock.lock();
        try {
            r.run();
        }
        catch (Throwable ex) {
            if (loggerCatch) {
                getLogger().error("Failed to execute <{}> ", lifecycleName, ex);
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

        this.mangerTaskUniqueIds = getManagerTaskUniqueIdentifiers();
        boolean managerTaskRegisterFlag = false;

        for (TaskElement taskElement : taskElements) {

            elementCheck(taskElement);

            registerTask(taskElement);
            if (!managerTaskRegisterFlag && isManagerTask(taskElement)) {
                managerTaskRegisterFlag = true;
            }
        }

        if (!managerTaskRegisterFlag
                && datasourceTaskElementsOperation.registerDefaultIfMainTaskInfoNotProvided()) {

            // Execute at a self configured fixed frequency without a designated main task management.
            this.mangerTaskUniqueIds
                    = new String[]{cronTaskRepository.register(getManagerTaskCheckFrequencyCronExpress(), this)};

            managerTaskRegisterFlag = true;
        }

        datasourceTaskElementsOperation.afterStart(taskElements);

        // Notify the data source operation class that there is no main check task running.
        if (!managerTaskRegisterFlag) {
            datasourceTaskElementsOperation.notifyMainTaskInfoNotProvidedAndNoDefaultUsed();
        }

        // The marking has been start.
        started = true;

        debug("Drive scheduler service has been successfully started !");
    }

    /**
     * @since 3.0.2
     * @param element the task element.
     */
    private void elementCheck(TaskElement element) {
        AssertUtils.assertNotBlank(element.getId(), "Bad Element : No unique ID");
        AssertUtils.assertNotBlank(element.getTaskName(), "Bad Element : No task name");
        AssertUtils.assertNotBlank(element.getExpression(), "Bad Element : No cron expression");
        AssertUtils.assertNotNull(element.getUpdateSign(), "Bad Element : No update sign");
        AssertUtils.assertTrue(element.getUpdateSign() == 0 ||
                element.getUpdateSign() == 1, "Bad Element : update sign can only be 0 or 1");
    }

    /**
     * The internal method of {@link #run()}.
     */
    private void runInternal() {

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
                    if (isManagerTask(element)) {

                        // The stopping of the main inspection task is quite serious and may lead to
                        // the effectiveness of automatic management.
                        debug("[Runtime-checked] The main management check task [{}] will be " +
                                "automatically stopped, which will result in the loss of the scheduled check" +
                                " capability with a frequency of [{}]. If multiple main tasks are configured," +
                                " please ignore this reminder.", element.getId(), element.getExpression());
                    }
                    String taskId = element.getTaskId();
                    // Tasks with limited registration times do not require manual deletion of tasks.
                    if (isFrequencyLimitTask(element)) {
                        debug("[Runtime-checked-Task-{}] Task name [{}] description [{}]  is a task that " +
                                        "limits the number of registrations and does not require manual " +
                                        "deletion of tasks.",
                                element.getId(), element.getTaskName(), element.getTaskDescription());
                        continue;
                    }
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
                    if (!StringUtils.isBlank(taskId)) {
                        // Last time it was run as a limited number of task execution mechanism,
                        // if you want to continue registering and executing, you need to set
                        // this ID to a null value.
                        if (isFrequencyLimitTask(element)) {
                            debug("[Runtime-checked-Task-{}] Task name [{}] description [{}]  is a limited " +
                                            "registration task. If you need to make any configuration changes, " +
                                            "please change the task ID to a null value.",
                                    element.getId(), element.getTaskName(), element.getTaskDescription());
                            continue;
                        }
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

        datasourceTaskElementsOperation.afterRun(runtimeCheckedDatasourceTaskElements);

        debug("[Time-{}] => Drive scheduler service check of timing information has ended.",
                getActiveTime());
    }

    private static String getActiveTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    private static boolean isFrequencyLimitTask(TaskElement element) {
        return element.getTaskId().startsWith(PREFIX_SIGN_OF_TIMES_REGISTED);
    }

    /**
     * The internal method of {@link #stop()}.
     */
    private void stopInternal() throws Exception {

        assertStarted();

        for (String mangerTaskUniqueId : mangerTaskUniqueIds) {
            cronTaskRepository.remove(mangerTaskUniqueId);
        }

        for (TaskElement element : datasourceTaskElementsOperation.getDatasourceTaskElements()) {
            String taskId = element.getTaskId();
            if (!StringUtils.isBlank(taskId)) {
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
        Runnable taskRunnable = isManagerTask(taskElement) ? this : resolveTaskRunnable(taskElement);

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
                new CronTaskRegistrar(new CronTask(taskElement.getExpression(), (CronMethodRunnable) taskRunnable))
                        .registerFor(cronTaskRepository) :
                cronTaskRepository.register(taskElement.getExpression(), taskRunnable);

        // Registration with a limit on the number of runs will not be able to return the unique ID of the
        // task. Therefore, a prefix+the task ID set by the user will be used as the task registration ID,
        // in order to pause the registration of this task when checking again.
        if (StringUtils.isBlank(taskId)) taskId = PREFIX_SIGN_OF_TIMES_REGISTED + taskElement.getId();

        taskElement.setTaskId(taskId);
        taskElement.setStatusDescription(true, "Running");
        debug("[Task-{}] Successfully to register : name [{}] ||  description [{}] || expression [{}]",
                taskElement.getId(), taskElement.getTaskName(), taskElement.getTaskDescription(),
                taskElement.getExpression());
    }

    /**
     * Check if it is the main management task.
     *
     * @param taskElement the Task element information.
     * @return {@code true} represents the information of the main management task,
     * otherwise it is not.
     */
    protected boolean isManagerTask(TaskElement taskElement) {
        return mangerTaskUniqueIds != null
                && Arrays.binarySearch(mangerTaskUniqueIds, taskElement.getId()) >= 0;
    }

    /**
     * Return the cron expression when the data source is not provided by the main task,
     * i.e. {@link #getManagerTaskUniqueIdentifiers()} is {@code null}. This framework
     * independently registers the cron expression used for the main management task, and
     * developers can also define this value themselves.
     *
     * <p>If the detailed running task of the main inspection task is not provided and
     * {@link DatasourceTaskElementsOperation#registerDefaultIfMainTaskInfoNotProvided()}
     * returns {@code true}, {@link DatasourceTaskElementsOperation} will independently
     * implement the main task inspection, and this class will not provide inspection.
     *
     * @return the default cron expression for the execution frequency of the main management task.
     */
    protected String getManagerTaskCheckFrequencyCronExpress() {
        return Constants.MANAGER_TASK_CHECK_FREQUENCY_CRON;
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
        if (StringUtils.isBlank(profiles)) {
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
        String[] sp = taskName.split("@"); /*class.name()@method.name()*/
        if (sp.length != 2) {
            debug("{} does not comply with parsing rules [class's qualified name @ method name]", taskName);
            throw new DataSourceDrivenException(taskName + " does not comply with parsing rules " +
                    "[class's qualified name @ method name].");
        }
        Object target;
        Method targetMethod;
        try {
            Class<?> clazz = ClassUtils.forName(sp[0]);
            target = ReflectUtils.newInstance(clazz);
            targetMethod = ReflectUtils.getMethod(clazz, sp[1]);
        }
        catch (Exception ex) {
            debug("Failed to resolve task [" + element.getId() + "] to runnable.", ex);
            throw new DataSourceDrivenException("Failed to resolve task runnable " + element.getId(), ex);
        }

        return new CronMethodRunnable(target, targetMethod);
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
