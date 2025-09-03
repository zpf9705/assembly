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


package top.osjf.cron.datasource.driven.scheduled.yaml;

import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;
import top.osjf.cron.datasource.driven.scheduled.external.file.ExternalFileDatasourceTaskElement;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * {@code YamlTaskElement} class implements the TaskElement interface, mapping data from
 * YAML configuration files to task elements.
 *
 * <p>The names of YAML configuration properties for tasks written by developers need to
 * be based on constants written in this category, such as case configuration yaml file
 * {@code top.osjf.cron.datasource.driven.scheduled.yaml.Example.yml}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class YamlTaskElement extends ExternalFileDatasourceTaskElement {

    private static final long serialVersionUID = -4605682572958271258L;

    /**
     * @see TaskElement#getId()
     */
    private String id;

    /**
     * @see TaskElement#getTaskId()
     */
    private String taskId;

    /**
     * @see TaskElement#getTaskName()
     */
    private String taskName;

    /**
     * @see TaskElement#getProfiles()
     */
    private String profiles;

    /**
     * @see TaskElement#getTaskDescription()
     */
    private String taskDescription;

    /**
     * @see TaskElement#getStatus()
     */
    private String status;

    /**
     * @see TaskElement#getStatusDescription()
     */
    private String statusDescription;

    /**
     * @see TaskElement#getExpression()
     */
    private String expression;

    /**
     * @see TaskElement#getUpdateSign()
     */
    private Integer updateSign;

    /**
     * Constructs an empty {@code YamlTaskElement}.
     */
    public YamlTaskElement() {
    }

    /**
     * Constructs a {@code YamlTaskElement} with this {@link #getId()} corresponds to
     * source yaml config map.
     *
     * @param sourceConfig the Source yaml config map.
     */
    public YamlTaskElement(Map<Object, Object> sourceConfig) {
        Objects.requireNonNull(sourceConfig, "sourceConfig");
        this.id = getStringConfig(sourceConfig, ID_KEY_NAME, false);
        this.taskId = getStringConfig(sourceConfig, TASK_ID_KEY_NAME, false);
        this.taskName = getStringConfig(sourceConfig, TASK_NAME_KEY_NAME, false);
        this.profiles = getStringConfig(sourceConfig, PROFILES_KEY_NAME, true);
        this.taskDescription = getStringConfig(sourceConfig, TASK_DESCRIPTION_KEY_NAME, false);
        this.status = getStringConfig(sourceConfig, STATUS_KEY_NAME, false);
        this.statusDescription = getStringConfig(sourceConfig, STATUS_DESCRIPTION_KEY_NAME, true);
        this.expression = getStringConfig(sourceConfig, EXPRESSION_KEY_NANE, false);
        this.updateSign = notNullAs(sourceConfig, UPDATE_SIGN_KEY_NAME, false, Integer.class,
                o -> Integer.parseInt(o.toString()));
    }

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

    /**
     * Gets a {@code String} config by the specify key name.
     * @param sourceConfig the Source yaml config map.
     * @param keyName the specify key name.
     * @param nullable is null able.
     * @return the {@code String} config by specify key name.
     * @since 3.0.1
     */
    private String getStringConfig(Map<Object, Object> sourceConfig, String keyName, boolean nullable) {
        return notNullAs(sourceConfig, keyName, nullable, String.class, Object::toString);
    }

    /*
     * NON - JAVADOC
     * @param keyName
     * @param nullable
     * @param clazz
     * @param notInstanceConvertFun
     * @return
     * @param <T>
     * @since 3.0.1
     */
    @Nullable
    private <T>T notNullAs(Map<Object, Object> sourceConfig, String keyName, boolean nullable,
                           Class<T> clazz, @Nullable Function<Object, T> notInstanceConvertFun) {
        Object obj = sourceConfig.get(keyName);
        if (obj == null) {
            if (nullable) {
                return null;
            }
            throw new IllegalArgumentException("<" + keyName + ">" + "can not be null!");
        }
        if (clazz.isInstance(obj)) {
            return clazz.cast(obj);
        }
        if (notInstanceConvertFun == null) {
            return null;
        }
        try {
            return notInstanceConvertFun.apply(obj);
        }
        catch (IllegalArgumentException ex) {
            throw ex;
        }
        catch (Throwable ex) {
            throw new IllegalArgumentException("notInstanceConvertFun apply error", ex);
        }
    }
}
