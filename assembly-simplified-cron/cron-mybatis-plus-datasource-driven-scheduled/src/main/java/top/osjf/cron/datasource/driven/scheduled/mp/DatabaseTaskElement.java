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


package top.osjf.cron.datasource.driven.scheduled.mp;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import top.osjf.cron.datasource.driven.scheduled.Constants;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.generated.mybatisplus.MybatisPlusCodeGenerate;

/**
 * Database entity class for scheduled task configuration, mapped to the {@link Constants#TASK_NAME} table.
 *
 * <p>This class implements the {@link TaskElement} interface to represent scheduled task configurations
 * persisted in relational database. It uses MyBatis-Plus annotations for ORM mapping and provides
 * getter/setter methods for all task properties.
 * <hr><blockquote>
 * <h2>Database Mapping:</h2>
 * <table>
 *   <caption>Field Introduction</caption>
 *   <thead>
 *   <tr><th>Field</th><th>Database Column</th><th>Description</th></tr>
 *   </thead>
 *   <tbody>
 *   <tr><td>id</td><td>ID</td><td>Unique technical identifier (PK)</td></tr>
 *   <tr><td>taskId</td><td>TASK_ID</td><td>System-generated task execution ID</td></tr>
 *   <tr><td>taskName</td><td>TASK_NAME</td><td>Unique task name for execution resolution</td></tr>
 *   <tr><td>profiles</td><td>PROFILES</td><td>Environment configuration str</td></tr>
 *   <tr><td>taskDescription</td><td>TASK_DESCRIPTION</td><td>Human-readable task description</td></tr>
 *   <tr><td>status</td><td>STATUS</td><td>Current task registration status</td></tr>
 *   <tr><td>statusDescription</td><td>STATUS_DESCRIPTION</td><td>Detailed status explanation</td></tr>
 *   <tr><td>expression</td><td>EXPRESSION</td><td>Cron execution schedule</td></tr>
 *   <tr><td>updateSign</td><td>UPDATE_SIGN</td><td>Update indicator (0=no change, 1=modified)</td></tr>
 *   </tbody>
 * </table>
 * </blockquote><hr>
 *
 * <h2>Design Notes:</h2>
 * <ul>
 *   <li>Implements Serializable for distributed task management</li>
 *   <li>Uses MyBatis-Plus annotations for ORM mapping</li>
 *   <li>Maintains 1:1 mapping with {@link TaskElement} interface</li>
 *   <li>Includes technical audit fields (ID, UPDATE_SIGN) for data versioning</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@TableName(Constants.TASK_NAME)
@MybatisPlusCodeGenerate(noProviderPackageUseDefault = true, tableChineseName = "定时任务管理表")
public class DatabaseTaskElement implements TaskElement {
    private static final long serialVersionUID = 6220853967182260722L;

    /**
     * @see TaskElement#getId()
     */
    @TableId("ID")
    private String id;

    /**
     * @see TaskElement#getTaskId()
     */
    @TableField("TASK_ID")
    private String taskId;

    /**
     * @see TaskElement#getTaskName()
     */
    @TableField("TASK_NAME")
    private String taskName;

    /**
     * @see TaskElement#getProfiles()
     */
    @TableField("PROFILES")
    private String profiles;

    /**
     * @see TaskElement#getTaskDescription()
     */
    @TableField("TASK_DESCRIPTION")
    private String taskDescription;

    /**
     * @see TaskElement#getStatus()
     */
    @TableField("STATUS")
    private String status;

    /**
     * @see TaskElement#getStatusDescription()
     */
    @TableField("STATUS_DESCRIPTION")
    private String statusDescription;

    /**
     * @see TaskElement#getExpression()
     */
    @TableField("EXPRESSION")
    private String expression;

    /**
     * @see TaskElement#getUpdateSign()
     */
    @TableField("UPDATE_SIGN")
    private Integer updateSign;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTaskId() {
        return taskId;
    }

    @Override
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @Override
    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    @Override
    public String getProfiles() {
        return profiles;
    }

    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    @Override
    public String getTaskDescription() {
        return taskDescription;
    }

    public void setTaskDescription(String taskDescription) {
        this.taskDescription = taskDescription;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getStatusDescription() {
        return statusDescription;
    }

    @Override
    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public Integer getUpdateSign() {
        return updateSign;
    }

    @Override
    public void setUpdateSign(Integer updateSign) {
        this.updateSign = updateSign;
    }
}
