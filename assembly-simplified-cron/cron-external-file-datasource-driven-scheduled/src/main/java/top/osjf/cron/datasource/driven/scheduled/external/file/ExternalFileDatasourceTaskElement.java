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


package top.osjf.cron.datasource.driven.scheduled.external.file;

import top.osjf.cron.core.util.StringUtils;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

/**
 * Define abstract classes for task elements in external fixed configuration files.
 *
 * <p>Define the standardized names of external configuration file properties and
 * related functional methods, as follows:
 * <ul>
 * <li>{@link #purge()} Clean up dynamic information related to task scheduling.</li>
 * </ul>
 *
 * <p>Hope to inherit the {@link TaskElement} implementation class of external configuration
 * elements to better support abstract operations between {@link ExternalFileTaskElementLoader}
 * and {@link ExternalFileDatasourceTaskElementsOperation}, achieving the goal of easy development.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public abstract class ExternalFileDatasourceTaskElement implements TaskElement {

    private static final long serialVersionUID = -3079249310272365214L;

    /**
     * Config file key name of {@link TaskElement#getId()}.
     */
    public static final String ID_KEY_NAME = "id";

    /**
     * Config file key name of {@link TaskElement#getTaskId()}.
     */
    public static final String TASK_ID_KEY_NAME = "taskId";

    /**
     * Config file key name of {@link TaskElement#getTaskName()}.
     */
    public static final String TASK_NAME_KEY_NAME = "taskName";

    /**
     * Config file key name of {@link TaskElement#getProfiles()}.
     */
    public static final String PROFILES_KEY_NAME = "profiles";

    /**
     * Config file key name of {@link TaskElement#getTaskDescription()}.
     */
    public static final String TASK_DESCRIPTION_KEY_NAME = "taskDescription";

    /**
     * Config file key name of {@link TaskElement#getStatus()}.
     */
    public static final String STATUS_KEY_NAME = "status";

    /**
     * Config file key name of {@link TaskElement#getStatusDescription()}.
     */
    public static final String STATUS_DESCRIPTION_KEY_NAME = "statusDescription";

    /**
     * Config file key name of {@link TaskElement#getExpression()}.
     */
    public static final String EXPRESSION_KEY_NANE = "expression";

    /**
     * Config file key name of {@link TaskElement#getUpdateSign()}.
     */
    public static final String UPDATE_SIGN_KEY_NAME = "updateSign";

    /**
     * Purge potential task IDs, update tags, and task status descriptions from the data.
     * @return if {@code true} has been purge out，{@code false} otherwise.
     */
    public boolean purge() {
        if (!StringUtils.isBlank(getTaskId())) {
            setTaskId("");
            setUpdateSign(0);
            setStatusDescription("");
            return true;
        }
        return false;
    }
}
