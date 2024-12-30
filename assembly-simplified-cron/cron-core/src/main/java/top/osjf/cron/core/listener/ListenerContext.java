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


package top.osjf.cron.core.listener;

import top.osjf.cron.core.lang.Wrapper;

/**
 * The information interface for callback during the scheduled task execution phase
 * defines the key information required for callback.
 *
 * <p>This interface aims to provide standardized contextual information for callback
 * mechanisms during scheduled task execution, including the unique identifier of the
 * task and the original contextual object.
 *
 * <p>By implementing this interface, the framework can pass necessary task information
 * and execution environment to the listener, allowing developers to execute specific
 * logic at different stages of the task.
 *
 * <p>The {@link #getID()} method in the interface is used to obtain the unique identifier
 * of the scheduled task. This identifier remains unchanged throughout the lifecycle of
 * the task and can be used to uniquely identify the task instance in callbacks. It is
 * crucial for scenarios such as tracking task status, logging, or associating related
 * resources.
 *
 * <p>The {@link #getSourceContext()} method  returns the original context object used
 * when executing the scheduled task.</code>. This object represents the environment in
 * which tasks are triggered and executed, and may contain key information such as task
 * scheduling information, business data, user identity, etc. By accessing this context
 * object, developers can utilize this information in callbacks to execute more complex
 * logic.
 *
 * <p>When designing and implementing a timed task system, this interface provides a critical
 * information transmission mechanism, ensuring loose coupling between the task execution
 * phase and callback logic. Developers can create custom context classes by implementing
 * this interface and trigger callbacks at different stages of task execution (such as start,
 * in progress, completion, or failure), thereby achieving flexible and scalable listening
 * and processing mechanisms without affecting task execution logic. It is worth noting that
 * although this interface defines a standard method for obtaining context information, the
 * specific context content and structure depend on the class that implements this interface.
 * Therefore, when implementing and using it, developers need to carefully consider the type
 * and structure of contextual information to ensure that they can meet the requirements of
 * specific task scenarios.
 *
 * <p>This interface implements {@link Wrapper} and can always perform supervisor
 * conversion. In subsequent usage scenarios, conversion operations can be performed
 * based on the current usage type, making it more convenient and accurate to handle.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface ListenerContext extends Wrapper {

    /**
     * Get the unique identifier of the scheduled task.
     *
     * <p>This method returns the unique identifier of the timed task associated with the
     * current context, which remains unchanged throughout the lifecycle of the task.
     *
     * <p>The callback uniquely identifies the task instance and is typically used to track
     * task status, log, or associate related resources.
     *
     * @return The unique identifier of a scheduled task, usually a string.
     */
    String getID();

    /**
     * Retrieve the original context object used for executing scheduled tasks, referring
     * to the one provided by the framework.
     *
     * <p>This method returns the original context object associated with the current context,
     * which represents the environment in which the task was triggered and executed.
     *
     * <p>It may contain key information such as task scheduling information, business data,
     * user identity, etc., which are crucial for executing specific logic in callbacks.
     *
     * <p>Developers can customize the type and structure of context objects as needed, and use
     * this information to execute complex logic at different stages of task execution.
     *
     * @return the original context object provided by the framework used for executing scheduled
     * tasks.
     */
    Object getSourceContext();
}
