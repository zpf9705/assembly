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
import top.osjf.cron.core.repository.CronTaskInfo;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.core.util.ReflectUtils;
import top.osjf.cron.core.util.StringUtils;

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
    private final Lock runLock = new ReentrantLock();

    private String[] mangerTaskUniqueIds;

    public static final String PROFILES_SYSTEM_PROPERTY_NAME = "cron.datasource.driven.scheduled.profiles";
    private static List<String> SYSTEM_PROFILES;

    static {
        loadRegisterProfiles();
    }

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
    }

    @Override
    public void init() {
        datasourceTaskElementsOperation.purgeDatasourceTaskElements();
    }

    @Override
    public void start() {
        startInternal();
    }

    @Override
    public void run() {
        runLock.lock();
        try {
            runInternal();
        }
        finally {
            runLock.unlock();
        }
    }

    @Override
    public void stop() {
        stopInternal();
    }

    /**
     * The internal method of {@link #start()}.
     */
    private void startInternal() {

        List<TaskElement> taskElements = datasourceTaskElementsOperation.getDatasourceTaskElements();
        if (CollectionUtils.isEmpty(taskElements)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("No registrable data source task objects were obtained from the data source.");
                return;
            }
        }

        this.mangerTaskUniqueIds = getManagerTaskUniqueIdentifiers();
        boolean managerTaskRegisterFlag = false;

        for (TaskElement taskElement : taskElements) {
            registerTask(taskElement);
            if (!managerTaskRegisterFlag && isManagerTask(taskElement)) {
                managerTaskRegisterFlag = true;
            }
        }

        if (!managerTaskRegisterFlag) {
            this.mangerTaskUniqueIds
                    = new String[]{cronTaskRepository.register(getManagerTaskCheckFrequencyCronExpress(), this)};
        }

        datasourceTaskElementsOperation.afterStart(taskElements);
    }

    /**
     * The internal method of {@link #run()}.
     */
    private void runInternal() {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("[Time-{}] => Perform dynamic information checks on scheduled information.",
                    getActiveTime());
        }

        List<TaskElement> runtimeCheckedDatasourceTaskElements =
                datasourceTaskElementsOperation.getRuntimeNeedCheckDatasourceTaskElements();
        if (CollectionUtils.isEmpty(runtimeCheckedDatasourceTaskElements)) {
            return;
        }

        for (TaskElement element : runtimeCheckedDatasourceTaskElements) {
            if (element.isAfterUpdate()) {
                if (element.willBePaused()) {
                    if (isManagerTask(element)) {
                        if (getLogger().isWarnEnabled()) {
                            getLogger().warn("[Runtime-checked] The main management check task [{}] will be " +
                                    "automatically stopped, which will result in the loss of the scheduled check" +
                                    " capability with a frequency of [{}]. If multiple main tasks are configured," +
                                    " please ignore this reminder.", element.getTaskId(), element.getExpression());
                        }
                    }
                    String taskId = element.getTaskId();
                    cronTaskRepository.remove(taskId);
                    element.pausedClear();
                    getLogger().info("[Runtime-checked-Task-{}] [{}] execution has been stopped.",
                            element.getTaskId(), element.getTaskDescription());
                }
                else if (element.willBeActive()) {
                    registerTask(element);
                }
                else {
                    String taskId = element.getTaskId();
                    if (!StringUtils.isBlank(taskId)) {
                        CronTaskInfo cronTaskInfo = cronTaskRepository.getCronTaskInfo(taskId);
                        String oldExpression = cronTaskInfo != null ? cronTaskInfo.getExpression() : null;
                        if (element.expressionNoSame(oldExpression)) {
                            cronTaskRepository.update(element.getTaskId(), element.getExpression());
                            if (getLogger().isDebugEnabled()) {
                                getLogger().debug("[Runtime-checked-Task-{}] Task name [{}] description [{}] change " +
                                                "expression old [{}] to new [{}].", element.getId(), element.getTaskName(),
                                        element.getTaskDescription(), oldExpression, element.getExpression());
                            }
                        }
                    }
                }
                element.resetUpdateStatus();
            }
            else if (element.isAfterInsert()) {
                registerTask(element);
            }
        }

        datasourceTaskElementsOperation.afterRun(runtimeCheckedDatasourceTaskElements);

        if (getLogger().isDebugEnabled()) {
            getLogger().debug("[Time-{}] => The dynamic information check of timing information has ended.",
                    getActiveTime());
        }
    }

    private static String getActiveTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * The internal method of {@link #stop()}.
     */
    private void stopInternal() {

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
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("[Task-{}] Failed to register : Status not activated", taskElement.getTaskId());
            }
            return;
        }
        if (!profilesMatch(taskElement.getProfiles())) {
            taskElement.setStatusDescription(false, "Environment mismatch");
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("[Task-{}] Failed to register : Environment mismatch", taskElement.getTaskId());
            }
            return;
        }
        Runnable taskRunnable = isManagerTask(taskElement) ? this : resolveTaskRunnable(taskElement);
        String taskId = cronTaskRepository.register(taskElement.getExpression(), taskRunnable);
        taskElement.setTaskId(taskId);
        taskElement.setStatusDescription(true, "Running");
        if (getLogger().isDebugEnabled()) {
            getLogger().debug("[Task-{}] Successfully to register : name [{}] ||  description [{}] || expression [{}]",
                    taskElement.getTaskId(), taskElement.getTaskName(), taskElement.getTaskDescription(),
                    taskElement.getExpression());
        }
    }

    /**
     * Check if it is the main management task.
     *
     * @param taskElement the Task element information.
     * @return {@code true} represents the information of the main management task,
     * otherwise it is not.
     */
    protected boolean isManagerTask(TaskElement taskElement) {
        return Arrays.binarySearch(mangerTaskUniqueIds, taskElement.getId()) >= 0;
    }

    /**
     * Return the cron expression when the data source is not provided by the main task,
     * i.e. {@link #getManagerTaskUniqueIdentifiers()} is {@code null}. This framework
     * independently registers the cron expression used for the main management task, and
     * developers can also define this value themselves.
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
        return SYSTEM_PROFILES.contains(profiles);
    }

    /**
     * Analyze the running function of the sub-task through task information parsing.
     *
     * <p>The fully qualified name interval "@" of the default class plus the name of
     * the method is used as a candidate resolution for {@link TaskElement#getTaskName()}.
     *
     * @param element the task element information.
     * @return the Task execution function.
     * @throws DataSourceDrivenException If the parsing rules are not met or the task fails to run.
     */
    @NotNull
    protected Runnable resolveTaskRunnable(TaskElement element) {
        String taskName = element.getTaskName();
        String[] sp = taskName.split("@"); /*class.name()@method.name()*/
        if (sp.length != 2) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("{} does not comply with parsing rules " +
                        "[class's qualified name @ method name]", taskName);
            }
            throw new DataSourceDrivenException(taskName + " does not comply with parsing rules " +
                    "[class's qualified name @ method name].");
        }
        Object target;
        Method targetMethod;
        try {
            Class<?> clazz = ReflectUtils.forName(sp[0]);
            target = ReflectUtils.newInstance(clazz);
            targetMethod = ReflectUtils.getMethod(clazz, sp[1]);
        }
        catch (Exception ex) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("Failed to resolve task [{}] runnable cause [{}]", element.getId(), ex.getMessage());
            }
            throw new DataSourceDrivenException("Failed to resolve task runnable " + element.getId(), ex);
        }
        return () -> {
            try {
                ReflectUtils.invokeMethod(target, targetMethod);
            }
            catch (Exception ex) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug("Failed to invoke task [{}] cause [{}]", element.getId(), ex.getMessage());
                }
                throw new DataSourceDrivenException("Failed to invoke task " + element.getId(), ex);
            }
        };
    }
}
