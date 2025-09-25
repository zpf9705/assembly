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

package top.osjf.cron.core.repository;

import top.osjf.cron.core.lang.Wrapper;

import javax.annotation.concurrent.ThreadSafe;

/**
 * <p>{@code CronTaskRepository} is a composite interface that defines the core management
 * capabilities for cron-triggered tasks within the system. By extending multiple well-defined
 * sub-interfaces,it forms a comprehensive and extensible framework for task persistence and
 * runtime control.
 *
 * <p>This interface is primarily used to support platform-level functionalities such as
 * dynamic task registration,execution monitoring, lifecycle management, run-time tracking,
 * timeout handling, and event listening.Typical use cases include: dynamically adding or
 * removing scheduled tasks, monitoring execution frequency and duration,reacting to task
 * state changes, and controlling task startup/shutdown behavior.
 *
 * <p>The responsibilities of the extended interfaces are as follows:
 * <ul>
 *   <li>{@link Repository}: Used to mark it as a resource operation interface.</li>
 *   <li>{@link RunTimesRegistrarRepository}: Manages the registration and persistence of
 *   task execution counts,used for statistics and scheduling decisions.</li>
 *   <li>{@link RunTimeoutRegistrarRepository}: Handles maximum allowed execution time (timeout
 *   thresholds) for tasks,enabling timeout detection and interruption mechanisms.</li>
 *   <li>{@link ListableRepository}: Provide task scheduling list access capability to dynamically
 *   obtain relevant information.</li>
 *   <li>{@link CronListenerRepository}: Allows registration and management of event listeners
 *   related to cron tasks (e.g., on-start, on-completion, on-failure), facilitating an event-driven
 *   architecture.</li>
 *   <li>{@link LifecycleRepository}: Defines lifecycle control methods such as start, stop, and
 *   restart.</li>
 *   <li>{@link Wrapper}: Enables decorator pattern support, allowing task instances to be wrapped
 *   with cross-cutting concerns like logging, monitoring, retry logic, etc.</li>
 * </ul>
 *
 * <p>The differentiation and combination of the above modules were gathered in version 3.0.2,
 * aiming to provide developers with different feature choices for task scheduling based on
 * cron expressions. This solution fully covers all inherited functions of the interface and
 * helps developers quickly understand the collaborative relationships of multiple blocks
 * through structured display.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 * @see Repository
 * @see RunTimesRegistrarRepository
 * @see RunTimeoutRegistrarRepository
 * @see ListableRepository
 * @see CronListenerRepository
 * @see LifecycleRepository
 * @see Wrapper
 */
@ThreadSafe
public interface CronTaskRepository extends Repository, RunTimesRegistrarRepository, RunTimeoutRegistrarRepository,
        ListableRepository, CronListenerRepository, LifecycleRepository, Wrapper {
}
