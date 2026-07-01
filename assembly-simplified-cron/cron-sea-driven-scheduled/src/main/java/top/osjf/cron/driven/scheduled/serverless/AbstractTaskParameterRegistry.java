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


package top.osjf.cron.driven.scheduled.serverless;

import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.Assert;
import top.osjf.cron.datasource.driven.scheduled.TaskElement;

import java.util.concurrent.ConcurrentHashMap;

/**
 * The abstract {@link TaskParameter} registration class provides a registration mapping
 * between {@link TaskParameter} and the ID. At runtime, relevant parameters are obtained
 * by default from {@link #LOCAL_PARAM}, and specific dynamic parameters can be dynamically
 * set to {@link TaskParameter} and prioritized for use.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class AbstractTaskParameterRegistry {

    private static final ThreadLocal<TaskParameter> LOCAL_PARAM = new ThreadLocal<>();

    /** Mapping of task unique custom ID {@link TaskElement#getId()} and function jar starts {@link TaskParameter}. */
    private final ConcurrentHashMap<String, TaskParameter> taskParameterMapping = new ConcurrentHashMap<>();

    /**
     * Register persistent {@link TaskParameter} parameters for a single ID mapping.
     * <p>The parameter registration of this method is used for regular task execution
     * and does not support dynamic replacement.
     * @param taskId        unique task identifier
     * @param taskParameter task parameter object
     */
    public void registerTaskParameter(String taskId, TaskParameter taskParameter) {
        Assert.hasText(taskId, "TaskId cannot be blank");
        Assert.notNull(taskParameter, "TaskParameter cannot be null");
        taskParameterMapping.put(taskId, taskParameter);
    }

    /**
     * Set the local dynamic parameter {@link TaskParameter} for the current execution.
     * <p>When the task is actively triggered, relevant running parameters can be
     * dynamically passed in.
     * @param taskParameter the {@code TaskParameter} for setting.
     */
    public void setLocalTaskParameter(@Nullable TaskParameter taskParameter) {
        if (taskParameter == null) {
            LOCAL_PARAM.remove();
        }
        else {
            LOCAL_PARAM.set(taskParameter);
        }
    }

    /**
     * Get the local dynamic parameter {@link TaskParameter} for the current execution.
     * @return taskParameter the {@code TaskParameter} by {@link #setLocalTaskParameter}.
     */
    @Nullable
    public TaskParameter getLocalTaskParameter() {
        return LOCAL_PARAM.get();
    }

    /**
     * Retrieve the mapping parameters between registered {@link TaskParameter} and input
     * {@link TaskElement#getId()}.
     * @param taskId unique task identifier.
     * @return task {@code TaskParameter} object, or {@code null} if not found
     */
    @Nullable
    protected TaskParameter getTaskParameter(String taskId) {
        return taskParameterMapping.get(taskId);
    }
}
