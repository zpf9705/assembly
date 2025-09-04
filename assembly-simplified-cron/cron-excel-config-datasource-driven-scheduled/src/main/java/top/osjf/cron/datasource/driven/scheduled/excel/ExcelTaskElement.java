/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.cron.datasource.driven.scheduled.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.cron.datasource.driven.scheduled.external.file.ExternalFileDatasourceTaskElement;

/**
 * {@code ExcelTaskElement} class implements the TaskElement interface, mapping data from
 * Excel files to task elements.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class ExcelTaskElement extends ExternalFileDatasourceTaskElement {
    private static final long serialVersionUID = 5701272616640669147L;

    /**
     * @see TaskElement#getId()
     */
    @ExcelProperty(value = ID_KEY_NAME, index = 0)
    @ColumnWidth(value = 25)
    private String id;

    /**
     * @see TaskElement#getTaskId()
     */
    @ExcelProperty(value = TASK_ID_KEY_NAME, index = 1)
    @ColumnWidth(value = 25)
    private String taskId;

    /**
     * @see TaskElement#getTaskName()
     */
    @ExcelProperty(value = TASK_NAME_KEY_NAME, index = 2)
    @ColumnWidth(value = 25)
    private String taskName;

    /**
     * @see TaskElement#getProfiles()
     */
    @ExcelProperty(value = PROFILES_KEY_NAME, index = 3)
    @ColumnWidth(value = 15)
    private String profiles;

    /**
     * @see TaskElement#getTaskDescription()
     */
    @ExcelProperty(value = TASK_DESCRIPTION_KEY_NAME, index = 4)
    @ColumnWidth(value = 55)
    private String taskDescription;

    /**
     * @see TaskElement#getStatus()
     */
    @ExcelProperty(value = STATUS_KEY_NAME, index = 5)
    @ColumnWidth(value = 15)
    private String status;

    /**
     * @see TaskElement#getStatusDescription()
     */
    @ExcelProperty(value = STATUS_DESCRIPTION_KEY_NAME, index = 6)
    @ColumnWidth(value = 55)
    private String statusDescription;

    /**
     * @see TaskElement#getExpression()
     */
    @ExcelProperty(value = EXPRESSION_KEY_NANE, index = 7)
    @ColumnWidth(value = 25)
    private String expression;

    /**
     * @see TaskElement#getUpdateSign()
     */
    @ExcelProperty(value = UPDATE_SIGN_KEY_NAME, index = 8)
    @ColumnWidth(value = 15)
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
