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
import top.osjf.cron.core.repository.CronTaskInfo;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.core.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
 *   <dt>{@link #purgeDatasourceTaskElements()}</dt>
 *   <dd>Clean data source task data before registration</dd>
 *
 *   <dt>{@link #getManagerDatasourceTaskElement()}</dt>
 *   <dd>Provide main management task configuration</dd>
 *
 *   <dt>{@link #getDatasourceTaskElements()}</dt>
 *   <dd>Fetch business tasks from data source</dd>
 *
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
public abstract class AbstractDatasourceDrivenScheduled implements DatasourceDrivenScheduledLifecycle, Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CronTaskRepository cronTaskRepository;

    private String mangerTaskId = Constants.MANAGER_TASK_ID;

    /**
     * Constructs a new {@code AbstractDatasourceDrivenScheduled} with {@code CronTaskRepository}
     * as its task Manager.
     *
     * @param cronTaskRepository the Task management resource explorer.
     */
    public AbstractDatasourceDrivenScheduled(CronTaskRepository cronTaskRepository) {
        this.cronTaskRepository = cronTaskRepository;
    }

    @Override
    public void init() {
        purgeDatasourceTaskElements();
    }

    @Override
    public void start() {
        TaskElement taskElement = registerManagerTask();
        List<TaskElement> taskElements = getDatasourceTaskElements();
        if (CollectionUtils.isEmpty(taskElements)) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("No registrable data source task object was obtained.");
                return;
            }
        }
        taskElements.forEach(this::registerTask);
        if (taskElement != null) {
            taskElements.add(taskElement);
        }
        afterStart(taskElements);
    }

    @Override
    public void run() {
        List<TaskElement> runtimeCheckedDatasourceTaskElements = getRuntimeNeedCheckDatasourceTaskElements();
        if (CollectionUtils.isEmpty(runtimeCheckedDatasourceTaskElements)) {
            return;
        }
        for (TaskElement element : runtimeCheckedDatasourceTaskElements) {
            if (element.isAfterUpdate()) {
                if (element.willBePaused()) {
                    if (isManagerTask(element)) {
                        if (getLogger().isWarnEnabled()) {
                            getLogger().warn("[Runtime-checked] The main management task will be stopped" +
                                    " and the management ability will be lost.");
                        }
                    }
                    String taskId = element.getTaskId();
                    cronTaskRepository.remove(taskId);
                    element.pausedClear();
                    getLogger().info("[Runtime-checked-Task-{}] [{}] execution has been stopped.",
                            element.getTaskId(), element.getTaskDescription());
                } else if (element.willBeActive()) {
                    registerTask(element);
                } else {
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
            } else if (element.isAfterInsert()) {
                registerTask(element);
            }
        }
        afterRun(runtimeCheckedDatasourceTaskElements);
    }

    @Override
    public void stop() {
        cronTaskRepository.remove(mangerTaskId);
        for (TaskElement element : getDatasourceTaskElements()) {
            String taskId = element.getTaskId();
            if (!StringUtils.isBlank(taskId)) {
                cronTaskRepository.remove(taskId);
            }
        }
        purgeDatasourceTaskElements();
    }

    /**
     * Register the main management check task to the task management resource.
     *
     * @return the main management.
     */
    @Nullable
    private TaskElement registerManagerTask() {
        TaskElement managerTaskElement = getManagerDatasourceTaskElement();
        if (managerTaskElement == null) {
            mangerTaskId = cronTaskRepository.register(getManagerTaskCheckFrequencyCronExpress(), this);
        } else {
            mangerTaskId = managerTaskElement.getId();
            if (!registerTask(managerTaskElement)) {
                throw new IllegalStateException(String.format("[Manager-Task-%s] Failed to register : %s",
                        managerTaskElement.getId(), managerTaskElement.getStatusDescription()));
            }
        }
        return managerTaskElement;
    }

    /**
     * Register tasks that require dynamic management to the task registration manager.
     *
     * @param taskElement the Task element information.
     * @return {@code true} indicates successful registration, otherwise registration
     * fails and the information is recorded in {@link TaskElement#getStatusDescription()}.
     */
    private boolean registerTask(@NotNull TaskElement taskElement) {
        if (taskElement.noActive()) {
            if (taskElement.noActiveDescriptionExist()) {
                taskElement.setStatusDescription(false, "Status not activated");
            }
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("[Task-{}] Failed to register : Status not activated", taskElement.getTaskId());
            }
            return false;
        }
        if (!profilesMatch(taskElement.getProfiles())) {
            taskElement.setStatusDescription(false, "Environment mismatch");
            if (getLogger().isDebugEnabled()) {
                getLogger().debug("[Task-{}] Failed to register : Environment mismatch", taskElement.getTaskId());
            }
            return false;
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
        return true;
    }

    /**
     * Check if it is the main management task.
     *
     * @param taskElement the Task element information.
     * @return {@code true} represents the information of the main management task,
     * otherwise it is not.
     */
    private boolean isManagerTask(TaskElement taskElement) {
        return Objects.equals(taskElement.getTaskId(), mangerTaskId);
    }

    /**
     * Return the cron expression for the execution frequency of the main management task.
     *
     * @return the cron expression for the execution frequency of the main management task.
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
     * Clean the data source task information data to prevent dirty data issues during the
     * registration process, which is defined by the developer themselves.
     */
    protected abstract void purgeDatasourceTaskElements();

    /**
     * Return the {@link TaskElement} instance of the main management task, which can be
     * {@code null}. When {@code null}, use direct registration without recording the
     * relevant returned task ID.
     *
     * @return the {@link TaskElement} instance of the main management task.
     */
    @Nullable
    protected abstract TaskElement getManagerDatasourceTaskElement();

    /**
     * Return the collection of {@link TaskElement} instances except for the main management
     * task, and return the relevant task IDs after registration is completed.
     *
     * @return the {@link TaskElement} instances of except the main management task.
     */
    protected abstract List<TaskElement> getDatasourceTaskElements();

    /**
     * Judging whether the registration environment matches is determined by the subclass.
     *
     * @param profiles Recorded environmental information.
     * @return {@code true} indicates that the environment matches, otherwise it does not match.
     */
    protected abstract boolean profilesMatch(String profiles);

    /**
     * Analyze the running function of the sub-task through task information parsing.
     *
     * @param element the task element information.
     * @return the Task execution function.
     */
    @NotNull
    protected abstract Runnable resolveTaskRunnable(TaskElement element);

    /**
     * The callback method after the completion of the start cycle execution.
     *
     * <p>The callback provides the relevant registration information set
     * (including the main task information if provided) after filling in the
     * registration information, and developers can perform data persistence
     * update operations.
     *
     * @param fulledDatasourceTaskElement The set of data source task information after
     *                                    filling in the registration information.
     */
    protected void afterStart(List<TaskElement> fulledDatasourceTaskElement) {

    }

    /**
     * Return the collection of data source task information that needs to be updated
     * and checked during runtime.
     *
     * @return the collection of data source task information.
     */
    protected List<TaskElement> getRuntimeNeedCheckDatasourceTaskElements() {
        List<TaskElement> datasourceTaskElements = getDatasourceTaskElements();
        TaskElement managerDatasourceTaskElement = getManagerDatasourceTaskElement();
        if (managerDatasourceTaskElement != null) {
            datasourceTaskElements.add(managerDatasourceTaskElement);
        }
        return datasourceTaskElements.stream().filter(t -> Objects.equals(t.getUpdateSign(), 1)
                || (Objects.equals(t.getUpdateSign(), 1) && t.getTaskId() == null)).collect(Collectors.toList());
    }

    /**
     * The callback after executing the {@link #run()} method of the main management task at runtime.
     *
     * <p>Mainly perform dynamic update detection on the data obtained from
     * {@link #getRuntimeNeedCheckDatasourceTaskElements()}, and there may be updated task fields.
     * Developers can call back here to update the data.
     *
     * @param runtimeCheckedDatasourceTaskElement The collection of relevant task information after the
     *                                            runtime detection process for updating data (if any).
     */
    protected void afterRun(List<TaskElement> runtimeCheckedDatasourceTaskElement) {

    }
}
