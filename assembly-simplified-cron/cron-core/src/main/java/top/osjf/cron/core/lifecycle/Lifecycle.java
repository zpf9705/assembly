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


package top.osjf.cron.core.lifecycle;

/**
 * {@code Lifecycle} interface: defines the lifecycle management specification
 * for timed task frameworks.
 *
 * <p>This interface aims to provide a universal interface for managing the start,
 * stop, and status checks of timed task frameworks. By implementing this interface,
 * timed task frameworks can follow a unified lifecycle management standard, making
 * integration and monitoring easier.
 *
 * <p>In practical applications, timed task frameworks may involve complex task scheduling,
 * resource management, and error handling. By implementing the {@code Lifecycle} interface,
 * developers can ensure that the framework can correctly load configuration and initialize
 * resources at startup, safely release resources and save state at shutdown, and provide
 * status query functionality for monitoring and management.
 * <p>
 * <strong>Note:</strong>
 * <p>This interface does not contain specific task scheduling logic and only defines
 * lifecycle management methods.The specific task scheduling implementation should be
 * extended by the implementation class according to the requirements.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface Lifecycle {

    /**
     * Start the scheduled task framework.
     *
     * <p>Prior to invoking this method, it is imperative to confirm that the scheduler
     * for scheduled tasks has been properly initialized and configured. This method serves
     * as the pivotal point for igniting the process of scheduling tasks in accordance with
     * predefined time intervals and sequences.
     *
     * <p>The method leverages the underlying timing mechanism to orchestrate the execution
     * of tasks at specified intervals, ensuring that they are performed in a timely and orderly
     * manner. This, in turn, facilitates the seamless execution of periodic and recurring tasks
     * within the application domain.
     *
     * <p>It is worth noting that if the scheduled task framework has already been initiated,
     * an {@link IllegalStateException} will be thrown to prevent potential conflicts and ensure
     * the integrity of the system's operational state. This exception serves as a safeguard against
     * accidental or inappropriate re-initiation of the framework.
     *
     * @throws IllegalStateException If the timing framework has already been started.
     */
    void start();

    /**
     * Stop the scheduled task framework.
     *
     *
     * <p>Upon invoking this method, the framework should terminate all ongoing tasks,
     * relinquish occupied resources (such as thread pools, database connections, etc.),
     * and persist necessary state information for restoration upon subsequent startups.
     * If the framework has not yet been initiated, it is up to the specific implementation
     * to decide whether to throw an exception.
     *
     * <p>When implementing this method, developers must ensure that all tasks can be safely
     * halted and resources can be correctly released. This includes, but is not limited to,
     * gracefully interrupting executing tasks, shutting down database connection pools, and
     * freeing threads in thread pools. The halt operation should strive to guarantee data
     * integrity and consistency, preventing data loss or inconsistent states during task
     * execution.
     *
     * <p>It is noteworthy that if the scheduled task framework has already been halted or is
     * in an uninitiated state, invoking this method again should not throw an exception, but
     * may instead notify developers of the current state through logging or other means.
     *
     * @throws IllegalStateException if the scheduled task framework has not been initiated,
     *                               this exception is thrown to indicate that the halt operation
     *                               is not permitted.
     */
    void stop();

    /**
     * Verifies whether the scheduled task framework has been launched.
     *
     * <p>This method serves to inquire about the current status of the scheduled task framework.
     * If the framework has already been initiated, it returns {@code true}; otherwise, it returns
     * {@code false}.
     *
     * <p>When implementing this method, developers must ensure the atomicity and consistency of
     * state inquiry operations,preventing inconsistent states in concurrent environments. This
     * typically involves using synchronization mechanisms or thread-safe variables to store and
     * access the initiation status of the framework.
     *
     * <p>The status information returned should accurately reflect the current state of the framework,
     * enabling callers to make corresponding processing decisions based on the status information.
     * For example, if the framework has already been initiated, re-initiation may not be permitted;
     * if the framework has not been initiated, initialization operations may be required.
     *
     * @return Returns {@code true} if the scheduled task framework has been initiated; otherwise,
     * returns {@code false}.
     */
    boolean isStarted();
}
