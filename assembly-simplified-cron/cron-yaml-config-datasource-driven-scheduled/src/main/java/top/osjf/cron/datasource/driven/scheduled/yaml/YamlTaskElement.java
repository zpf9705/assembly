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
import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import java.util.Map;
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
public class YamlTaskElement implements TaskElement {

    private static final long serialVersionUID = -2374827664799945441L;

    /**
     * Source yaml config file key name of {@link TaskElement#getId()}.
     */
    public static final String ID_KEY_NAME = "id";

    /**
     * Source yaml config file key name of {@link TaskElement#getTaskId()}.
     */
    public static final String TASK_ID_KEY_NAME = "taskId";

    /**
     * Source yaml config file key name of {@link TaskElement#getTaskName()}.
     */
    public static final String TASK_NAME_KEY_NAME = "taskName";

    /**
     * Source yaml config file key name of {@link TaskElement#getProfiles()}.
     */
    public static final String PROFILES_KEY_NAME = "profiles";

    /**
     * Source yaml config file key name of {@link TaskElement#getTaskDescription()}.
     */
    public static final String TASK_DESCRIPTION_KEY_NAME = "taskDescription";

    /**
     * Source yaml config file key name of {@link TaskElement#getStatus()}.
     */
    public static final String STATUS_KEY_NAME = "status";

    /**
     * Source yaml config file key name of {@link TaskElement#getStatusDescription()}.
     */
    public static final String STATUS_DESCRIPTION_KEY_NAME = "statusDescription";

    /**
     * Source yaml config file key name of {@link TaskElement#getExpression()}.
     */
    public static final String EXPRESSION_KEY_NANE = "expression";

    /**
     * Source yaml config file key name of {@link TaskElement#getUpdateSign()}.
     */
    public static final String UPDATE_SIGN_KEY_NAME = "updateSign";

    /**
     * Source yaml config map corresponds to {@link #getId()}.
     */
    public final Map<Object, Object> sourceYamlConfig;

    /**
     * Constructs a {@code YamlTaskElement} with this {@link #getId()} corresponds to
     * source yaml config map.
     *
     * @param sourceConfig the Source yaml config map.
     */
    public YamlTaskElement(Map<Object, Object> sourceConfig) {
        this.sourceYamlConfig = sourceConfig;
    }

    @Override
    public String getId() {
        return getStringConfig(ID_KEY_NAME, false);
    }

    @Override
    public String getTaskId() {
        return getStringConfig(TASK_ID_KEY_NAME, false);
    }

    @Override
    public void setTaskId(String taskId) {
        sourceYamlConfig.put(TASK_ID_KEY_NAME, taskId);
    }

    @Override
    public String getTaskName() {
        return getStringConfig(TASK_NAME_KEY_NAME, false);
    }

    @Override
    public String getProfiles() {
        return getStringConfig(PROFILES_KEY_NAME, true);
    }

    @Override
    public String getTaskDescription() {
        return getStringConfig(TASK_DESCRIPTION_KEY_NAME, false);
    }

    @Override
    public String getStatus() {
        return getStringConfig(STATUS_KEY_NAME, false);
    }

    @Override
    public void setStatus(String status) {
        sourceYamlConfig.put(STATUS_KEY_NAME, status);
    }

    @Override
    public String getStatusDescription() {
        return getStringConfig(STATUS_DESCRIPTION_KEY_NAME, true);
    }

    @Override
    public void setStatusDescription(String statusDescription) {
        sourceYamlConfig.put(STATUS_DESCRIPTION_KEY_NAME, statusDescription);
    }

    @Override
    public String getExpression() {
        return getStringConfig(EXPRESSION_KEY_NANE, false);
    }

    @Override
    public Integer getUpdateSign() {
        return notNullAs(UPDATE_SIGN_KEY_NAME, false, Integer.class, o -> Integer.parseInt(o.toString()));
    }

    @Override
    public void setUpdateSign(Integer updateSign) {
        sourceYamlConfig.put(UPDATE_SIGN_KEY_NAME, updateSign.toString());
    }

    /**
     * @return Configure the relevant information corresponding to the unique ID of the task {@link #getId()}.
     */
    public Map<Object, Object> getSourceYamlConfig() {
        return sourceYamlConfig;
    }

    /**
     * Purge potential task IDs, update tags, and task status descriptions from the data.
     * @return if {@code true} has been purge out，{@code false} otherwise.
     */
    protected boolean purge() {
        if (!StringUtils.isBlank(getTaskId())) {
            sourceYamlConfig.put(TASK_ID_KEY_NAME, "");
            sourceYamlConfig.put(UPDATE_SIGN_KEY_NAME, "0");
            sourceYamlConfig.put(STATUS_DESCRIPTION_KEY_NAME, "");
            return true;
        }
        return false;
    }

    /**
     * Gets a config by specify key name.
     * @param keyName the specify key name.
     * @return the config by specify key name.
     * @since 3.0.1
     */
    public Object getSourceYamlConfig(String keyName) {
        return sourceYamlConfig.get(keyName);
    }

    /**
     * Gets a {@code String} config by the specify key name.
     * @param keyName the specify key name.
     * @param nullable is null able.
     * @return the {@code String} config by specify key name.
     * @since 3.0.1
     */
    private String getStringConfig(String keyName, boolean nullable) {
        return notNullAs(keyName, nullable, String.class, Object::toString);
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
    private <T>T notNullAs(String keyName, boolean nullable,
                           Class<T> clazz, @Nullable Function<Object, T> notInstanceConvertFun) {
        Object obj = sourceYamlConfig.get(keyName);
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
