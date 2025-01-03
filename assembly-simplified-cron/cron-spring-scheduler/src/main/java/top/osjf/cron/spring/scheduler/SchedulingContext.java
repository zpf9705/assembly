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

package top.osjf.cron.spring.scheduler;

import java.util.List;

/**
 * The {@code SchedulingContext} interface defines contextual information related
 * to scheduled task execution. It encapsulates the basic properties of scheduled
 * tasks, including the unique identifier of the task, the executable object (Runnable
 * object) of the task, and the list of listeners that need to be notified during
 * task execution.
 *
 * <p>Through this interface, various necessary information about scheduled task
 * execution can be obtained, thereby achieving precise control and monitoring
 * of the task execution process.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface SchedulingContext {

    /**
     * Retrieve the unique identifier of the current scheduled task.
     *
     * <p>Each scheduled task should have a unique identifier during execution to
     * distinguish and identify during task scheduling and monitoring,this identifier
     * is typically generated when a task is created or registered with the scheduler
     * and remains unchanged throughout the entire lifecycle of task execution.
     *
     * @return The unique identifier of the current scheduled task, usually represented
     * in string form.
     */
    String getId();

    /**
     * Retrieve the original Runnable object of the current scheduled task
     * execution body.
     *
     * @return The original Runnable object of the current scheduled task
     * execution body.
     */
    Runnable getRawRunnable();

    /**
     * Retrieve the list of listeners that need to be notified when the current
     * scheduled task is executed.
     *
     * <p>Through this method, all listener objects of the current scheduled task
     * can be obtained, thereby achieving monitoring and control of the task execution
     * process.
     *
     * @return The list of listeners that need to be notified during the execution
     * of the current scheduled task.
     */
    List<SchedulingListener> getRawListeners();
}
