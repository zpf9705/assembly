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
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.generated.mybatisplus.MybatisPlusCodeGenerate;

/**
 * Dynamic management table for scheduled tasks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@TableName("ZT_TASK_SCHEDULER")
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
