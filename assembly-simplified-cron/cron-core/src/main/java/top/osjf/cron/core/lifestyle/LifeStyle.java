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

package top.osjf.cron.core.lifestyle;

/**
 * {@code LifeStyle} interface: defines the lifecycle management specification
 * for timed task frameworks.
 *
 * <p>This interface aims to provide a universal interface for managing the start,
 * stop, and status checks of timed task frameworks. By implementing this interface,
 * timed task frameworks can follow a unified lifecycle management standard, making
 * integration and monitoring easier.
 *
 * <p>In practical applications, timed task frameworks may involve complex task scheduling,
 * resource management, and error handling. By implementing the {@code LifeStyle} interface,
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
 * @since 1.0.0
 */
public interface LifeStyle {

    /**
     * Start the scheduled task framework.
     *
     * <p>When calling this method, the framework should load and parse the
     * incoming startup parameter {@code StartupProperties}, initialize necessary
     * resources based on parameter configuration (such as thread pool core
     * configuration, etc.), and start executing registered tasks. If the
     * framework has already been launched, it should be decided whether
     * to throw an exception based on the implementation.
     *
     * <p>{@code StartupProperties} is a custom class used to encapsulate various
     * configuration information required to start a timed task framework.
     * These configuration information may include but are not limited to:
     * <ul>
     * <li>ime intervals and triggering conditions for task scheduling</li>
     * <li>Thread pool configuration for task execution (such as number of threads,
     * queue size, etc.)</li>
     * <li>Log configuration (such as log level, log output location, etc.)</li>
     * Other configurations related to task execution (such as task retry strategy,
     * exception handling, etc.)</li>
     * </ul>
     *
     * @param properties the startup parameters include various configuration
     *                   information required to start the timed task framework.
     * @throws IllegalArgumentException If the startup parameters have been validated
     *                                  and there are invalid parameters.
     * @throws IllegalStateException    If the timing framework has already been started.
     */
    void start(StartupProperties properties);

    /**
     * Stop the scheduled task framework.
     *
     * <p>When calling this method, the framework should stop all executing tasks,
     * free up occupied resources (such as thread pools, database connections, etc.),
     * and save necessary state information for recovery at the next startup. If the
     * framework has not been started yet, it should be decided whether to throw an
     * exception based on the implementation.
     *
     * <p>When implementing this method, developers should ensure that all tasks can
     * be safely stopped and resources can be released correctly.
     *
     * @throws IllegalStateException If the timed task framework has not been started,
     *                               throwing this exception to indicate that stopping
     *                               the operation is not allowed.
     */
    void stop();

    /**
     * Check if the scheduled task framework has been launched.
     *
     * <p>This method is used to query the current status of the scheduled task framework.
     * If the framework has already been started, return true; Otherwise, return false.
     *
     * <p>When implementing this method, developers should ensure the atomicity and consistency
     * of state query operations to avoid inconsistent states in concurrent environments.
     *
     * @return If the scheduled task framework has already been launched,
     * return true; Otherwise, return false.
     */
    boolean isStarted();
}
