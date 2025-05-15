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

import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;

/**
 * Interface for scheduled task element information, used to define and manage basic properties and
 * states of scheduled tasks.
 *
 * <p>This interface provides methods to get and set various information of scheduled tasks, including
 * task ID, task name, environment configuration, status, description, execution frequency, etc.
 * The status management of scheduled tasks is supported by the {@link Status} enumeration class.
 *
 * <p>Main features include:
 * <ul>
 *   <li>Task unique identification management: Obtain task identification through {@link #getId()} and
 *   {@link #getTaskId()}</li>
 *   <li>Task status management: Manage task status through {@link #getStatus()} and {@link #setStatus(String)}</li>
 *   <li>Task execution configuration: Obtain cron expression through {@link #getExpression()}, and environment
 *   configuration through {@link #getProfiles()}</li>
 *   <li>Task dynamic management: Support dynamic start/stop of tasks through methods such as {@link #pausedClear()}
 *   and {@link #willBePaused()}</li>
 *   <li>Task update detection: Detect task configuration updates through {@link #getUpdateSign()} and
 *   {@link #isAfterUpdate()}</li>
 * </ul>
 *
 * <p>This interface is the core interface of the scheduled task system, implementing the full lifecycle management
 * of scheduled tasks,including task registration, status management, execution configuration, and dynamic updates.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface TaskElement extends Serializable {

    /**
     * Return the unique ID of this task element, determined by the developer,
     * to differentiate the data source uniquely.
     *
     * @return The unique ID of this task element.
     */
    String getId();

    /**
     * Return a successful registration of this task element, i.e. the registration
     * of {@link CronTaskRepository#register} returns a unique ID generated and returned
     * by the component.
     *
     * @return The unique ID by element registration successful.
     */
    String getTaskId();

    /**
     * Set an ID returned by the task element calling API {@link CronTaskRepository#register}
     * to register successfully.
     *
     * @param taskId the unique ID by element registration successful.
     */
    void setTaskId(String taskId);

    /**
     * Return the unique name of the scheduled task, which is used for runtime resolution to
     * obtain the source of the task's execution. It will be custom analyzed by the implementation
     * subclass in {@link AbstractDatasourceDrivenScheduled#resolveTaskRunnable(TaskElement)}.
     *
     * @return The unique name of the scheduled task.
     */
    String getTaskName();

    /**
     * Return the environment in which the task is registered, which can be defined and analyzed
     * by subclasses in the method processing of {@link AbstractDatasourceDrivenScheduled#profilesMatch},
     * mainly for task registration assertions.
     *
     * @return The environment in which the task is registered.
     */
    String getProfiles();

    /**
     * Returns the registration status of the current task, which indicates the value of the enumeration
     * type {@link Status}. Otherwise, it is considered as not meeting the registration requirements.
     *
     * @return The registration status of the current task.
     */
    String getStatus();

    /**
     * Set a {@link Status#name()} status value after registration processing.
     *
     * @param status The names of {@link Status} values according to {@link Status#name()}.
     */
    void setStatus(String status);

    /**
     * Return the current state description information of this task, which is a visual
     * description statement containing {@link Status#name()}, indicating the call settings
     * of {@code AbstractDatasourceDrivenScheduled#registerTask(TaskElement)}.
     *
     * @return The current state description information of this task
     */
    String getStatusDescription();

    /**
     * Set the task status description information for the current task after the
     * registration process.
     *
     * @param statusDescription The current state description information of this task.
     */
    void setStatusDescription(String statusDescription);

    /**
     * Return the description information of the source of this task, generally introducing
     * the role of this task execution.
     *
     * @return the description information of this task.
     */
    String getTaskDescription();

    /**
     * Return the expression of the execution frequency when registering timed operations for
     * this task. In this cron framework, it is a cron expression that supports minute or second
     * level, but depends on the implementation choice of {@link CronTaskRepository}.
     *
     * <p>Special attention should be paid to the specification of cron expressions, otherwise
     * there will be issues related to format errors {@link IllegalArgumentException} thrown
     * during the registration process.
     *
     * @return The expression for the execution frequency when registering timed operations for this task.
     */
    String getExpression();

    /**
     * Return the reference indicators modified by the current task, divided into the following
     * states for differentiation.
     *
     * <ul>
     * <li><Strong>0</Strong> : There are no relevant updates indicated.</li>
     * <li><Strong>1</Strong> : Indicates that there are updates to relevant fields that require
     * dynamic processing.</li>
     * </ul>
     *
     * <p>Values outside of 0/1 will be considered invalid, please pay special attention.
     *
     * @return The reference indicators modified by the current task
     */
    Integer getUpdateSign();

    /**
     * Set the update status value of the current task after the registration monitoring process.
     * At this time, the update status of the current task is usually changed to 'already updated',
     * that is, changed from 1 to 0.
     *
     * @param updateSign The reference indicators modified by the current task.
     * @see #resetUpdateStatus()
     */
    void setUpdateSign(Integer updateSign);

    /**
     * Return the Boolean basis of inactive state, where {@code true} is inactive
     * and {@code false} is activated.
     *
     * <p>Divided into the following state judgment segments:
     * <ul>
     * <li>{@link #getStatus()} is not blank.</li>
     * <li>{@link #getStatus()} is a valid {@link Status}.</li>
     * <li>{@link #getStatus()} not equals {@link Status#ACTIVE}.</li>
     * </ul>
     *
     * @return {@code true} is inactive and {@code false} is activated.
     */
    default boolean noActive() {
        String status = getStatus();
        return !StringUtils.isBlank(status) && Status.isStatus(status) && Status.valueOf(status) != Status.ACTIVE;
    }

    /**
     * Return the {@code Boolean} flag indicating the existence of state description
     * information in an inactive state.
     *
     * @return {@code true} is unactivated existence status information and {@code false} otherwise.
     */
    default boolean noActiveDescriptionExist() {
        return noActive() && getStatusDescription() == null;
    }

    /**
     * Set status description information based on successful flags and custom status
     * descriptions.
     *
     * @param success           {@code Boolean}success flag, {@code true} indicates success, otherwise
     *                          it indicates failure.
     * @param statusDescription The relevant status description information of the linkage success marker
     *                          defined.
     */
    default void setStatusDescription(boolean success, String statusDescription) {
        Status status = success ? Status.ACTIVE : Status.PAUSED;
        setStatus(status.name());
        setStatusDescription(status.name() + " => " + statusDescription);
    }

    /**
     * Clear the scheduled task registration ID and set the status description to stop running, usually
     * in the case of dynamically stopping the task.
     */
    default void pausedClear() {
        setTaskId("");
        setStatusDescription(Status.PAUSED.name() + " => stop running");
    }

    /**
     * A dynamic management of paused task judgment situation: there is a scheduled task registration
     * ID but the status is stopped.
     *
     * @return {@code true} is Indicating that it is about to stop. and {@code false} otherwise.
     */
    default boolean willBePaused() {
        return !StringUtils.isBlank(getTaskId()) && Status.PAUSED.name().equals(getStatus());
    }

    /**
     * A situation of dynamically managing the suspension of task activation: the registration task
     * ID is empty, but the status is activated.
     *
     * @return {@code true} is indicating that it is about to run. and {@code false} otherwise.
     */
    default boolean willBeActive() {
        return StringUtils.isBlank(getTaskId()) && Status.ACTIVE.name().equals(getStatus());
    }

    /**
     * Determine whether the frequency expressions for dynamic updates are different.
     *
     * @param oldExpression The old expression.
     * @return {@code true} is indicating that they are different. and {@code false} otherwise.
     */
    default boolean expressionNoSame(String oldExpression) {
        return !StringUtils.isBlank(oldExpression) && !Objects.equals(oldExpression, getExpression());
    }

    /**
     * Reset the update status to 'not updated', which means that the update has been
     * processed and restored.
     */
    default void resetUpdateStatus() {
        setUpdateSign(0);
    }

    /**
     * Determine whether there is an update status in the check after startup, that is, the information
     * of registered tasks needs to be dynamically updated.
     *
     * @return {@code true} is indicating that update exists. and {@code false} otherwise.
     */
    default boolean isAfterUpdate() {
        return getUpdateSign() == 1;
    }

    /**
     * Determine whether it is a task type that requires dynamic registration during runtime.
     * The following status conditions need to be met:
     * <ul>
     * <li>{@link #getTaskId()} is null</li>
     * <li>{@link #getUpdateSign()} == 0 Non modified old data</li>
     * <li>{@link #getStatus()} is null or The state that needs to be activated.</li>
     * </ul>
     *
     * @return {@code true} is indicating that meet the dynamic registration task and {@code false} otherwise.
     */
    default boolean isAfterInsert() {
        String status = getStatus();
        return getTaskId() == null && getUpdateSign() == 0 && (status == null || Status.isActive(status));
    }
}
