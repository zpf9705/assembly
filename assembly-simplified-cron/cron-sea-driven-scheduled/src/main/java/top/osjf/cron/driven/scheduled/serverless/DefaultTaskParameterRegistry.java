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

import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.util.AssertUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Default Thread-safe Registry for Task Parameters.
 *
 * <p>This abstract class is used to <strong>register, store, and manage</strong>
 * the mapping between task IDs and task parameter objects.
 * All parameter entities implementing the {@link TaskParameter} marker interface
 * can be bound and stored by task ID.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class DefaultTaskParameterRegistry {

    /**
     * Task ID to TaskParameter mapping.
     * key: task unique ID
     * value: task parameter object (implements {@code TaskParameter})
     */
    private final ConcurrentHashMap<String, TaskParameter> taskParameterMapping = new ConcurrentHashMap<>();

    /**
     * Registers the parameter object for a given task.
     *
     * <p>Binds the task ID with its parameter object.
     * Overwrites any existing entry for the same taskId.
     * Validates that taskId is not blank and parameter is not null.
     *
     * @param taskId        unique task identifier
     * @param taskParameter task parameter object
     */
    public void registerTaskParameter(String taskId, TaskParameter taskParameter) {
        AssertUtils.assertNotBlank(taskId, "TaskId cannot be blank");
        AssertUtils.assertNotNull(taskParameter, "TaskParameter cannot be null");
        taskParameterMapping.put(taskId, taskParameter);
    }

    /**
     * Retrieves the task parameter object by task ID.
     *
     * <p>Intended for use by subclasses.
     * Returns the registered {@link TaskParameter} for the given taskId.
     * Returns null if no mapping exists for the taskId.
     *
     * @param taskId unique task identifier
     * @return task parameter object, or null if not found
     */
    @Nullable
    protected TaskParameter getTaskParameter(String taskId) {
        return taskParameterMapping.get(taskId);
    }
}
