/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.spring.web;

import top.osjf.commons.lang.Nullable;
import top.osjf.cron.core.repository.CronTaskInfo;
import top.osjf.cron.core.repository.RunningTimeout;

import java.io.Serializable;

/**
 * Cron task list responds with parameters and displays a collection of timed tasks
 * after filtering criteria.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 * @see TaskListRequestEntity
 * @see CronTaskRepositoryController#list(TaskListRequestEntity)
 * @see top.osjf.cron.core.repository.CronTaskInfo
 */
public class TaskListResponseEntity implements Serializable {

    private static final long serialVersionUID = 5574935611324566871L;

    /** {@link CronTaskInfo#getId()} */
    private String id;

    /** {@link CronTaskInfo#getName()} */
    @Nullable private String name;

    /** {@link CronTaskInfo#getExpression()} */
    private String expression;

    /** {@link CronTaskInfo#getTargetClassName()} */
    @Nullable private String targetClassName;

    /** {@link CronTaskInfo#getMethodName()} */
    @Nullable private String methodName;

    /** {@link CronTaskInfo#getRemainingNumberOfRuns()} */
    private Long remainingNumberOfRuns;

    /** {@link CronTaskInfo#getTimeoutConfig()} */
    @Nullable private RunningTimeout timeoutConfig;

    /** {@link CronTaskInfo#isRunning()} */
    private Boolean isRunning;

    /** {@link CronTaskInfo#getNextExecuteTimestamp()} */
    @Nullable private Long nextExecuteTimestamp;

    /** {@link CronTaskInfo#isDisallowConcurrentExecution()}*/
    private Boolean disallowConcurrentExecution;

    /** {@link CronTaskInfo#getDescription()} */
    @Nullable private String description;

    public TaskListResponseEntity(CronTaskInfo cronTaskInfo) {
        id = cronTaskInfo.getId();
        name = cronTaskInfo.getName();
        expression = cronTaskInfo.getExpression();
        targetClassName = cronTaskInfo.getTargetClassName();
        methodName = cronTaskInfo.getMethodName();
        remainingNumberOfRuns = cronTaskInfo.getRemainingNumberOfRuns();
        timeoutConfig = cronTaskInfo.getTimeoutConfig();
        isRunning = cronTaskInfo.isRunning();
        nextExecuteTimestamp = cronTaskInfo.getNextExecuteTimestamp();
        disallowConcurrentExecution = cronTaskInfo.isDisallowConcurrentExecution();
        description = cronTaskInfo.getDescription();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(@Nullable String name) {
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Nullable
    public String getTargetClassName() {
        return targetClassName;
    }

    public void setTargetClassName(@Nullable String targetClassName) {
        this.targetClassName = targetClassName;
    }

    @Nullable
    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(@Nullable String methodName) {
        this.methodName = methodName;
    }

    public Long getRemainingNumberOfRuns() {
        return remainingNumberOfRuns;
    }

    public void setRemainingNumberOfRuns(Long remainingNumberOfRuns) {
        this.remainingNumberOfRuns = remainingNumberOfRuns;
    }

    @Nullable
    public RunningTimeout getTimeoutConfig() {
        return timeoutConfig;
    }

    public void setTimeoutConfig(@Nullable RunningTimeout timeoutConfig) {
        this.timeoutConfig = timeoutConfig;
    }

    public Boolean getRunning() {
        return isRunning;
    }

    public void setRunning(Boolean running) {
        isRunning = running;
    }

    @Nullable
    public Long getNextExecuteTimestamp() {
        return nextExecuteTimestamp;
    }

    public void setNextExecuteTimestamp(@Nullable Long nextExecuteTimestamp) {
        this.nextExecuteTimestamp = nextExecuteTimestamp;
    }

    public Boolean getDisallowConcurrentExecution() {
        return disallowConcurrentExecution;
    }

    public void setDisallowConcurrentExecution(Boolean disallowConcurrentExecution) {
        this.disallowConcurrentExecution = disallowConcurrentExecution;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
