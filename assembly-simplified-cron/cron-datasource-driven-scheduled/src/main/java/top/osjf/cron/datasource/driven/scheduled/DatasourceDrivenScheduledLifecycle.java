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


package top.osjf.cron.datasource.driven.scheduled;

import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * Interface for lifecycle management of datasource-driven scheduled tasks, defining basic lifecycle
 * operations for scheduled task management classes. This interface is the core management interface
 * for scheduled task systems.
 *
 * <p>Main features include:
 * <ul>
 *   <li>{@link #init()}: Initialize the scheduled task management environment</li>
 *   <li>{@link #start()}: Start scheduled task execution</li>
 *   <li>{@link #stop()}: Stop scheduled task execution</li>
 * </ul>
 *
 * <p>Classes implementing this interface are responsible for the full lifecycle management of scheduled
 * tasks,including task registration, scheduling, execution, and cleanup. They typically work with components
 * such as {@link TaskElement} and {@link CronTaskRepository} to implement the scheduled task system.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public interface DatasourceDrivenScheduledLifecycle {

    /**
     * Initialize the scheduled task management environment.
     * This method should be called before starting the scheduled task system to prepare resources required
     * for task execution,including but not limited to: initializing the task repository, loading task
     * configurations, and establishing connection pools.
     *
     * <p>Typical implementations may include:
     * <ul>
     *   <li>Initializing the task repository {@link CronTaskRepository}</li>
     *   <li>Loading and parsing task configurations</li>
     *   <li>Preparing task execution thread pools</li>
     * </ul>
     *
     * @throws IllegalStateException if initialization fails or the environment does not meet execution conditions
     */
    void init();

    /**
     * Start scheduled task execution.
     * This method should be called after successful initialization to start all registered scheduled tasks.
     * Implementation classes must ensure that tasks execute correctly according to their configured cron
     * expressions.
     *
     * <p>Typical implementations may include:
     * <ul>
     *   <li>Loading all active tasks from the repository {@link TaskElement#getStatus()}</li>
     *   <li>Creating scheduling threads for each task</li>
     *   <li>Starting task monitoring mechanisms</li>
     * </ul>
     *
     * @throws IllegalStateException if startup fails or the system is not properly initialized
     */
    void start();

    /**
     * Stop scheduled task execution.
     * This method is used to safely stop all running scheduled tasks and release system resources.
     * Implementation classes must ensure that tasks stop gracefully to avoid data inconsistency or resource leaks.
     *
     * <p>Typical implementations may include:
     * <ul>
     *   <li>Notifying all task execution threads to stop</li>
     *   <li>Waiting for currently executing tasks to complete</li>
     *   <li>Releasing connection pool and thread pool resources</li>
     * </ul>
     *
     * <p>Note: After stopping, tasks can be restarted using the {@link #start()} method.
     *
     * @throws IllegalStateException if stopping fails or the system state is incorrect
     */
    void stop();
}
