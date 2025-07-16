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

import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * Define the standard interface for {@link CronTaskRepository} initialization
 * function.
 *
 * <p>This interface requires the implementation class to provide a {@link #initialize()}
 * method,used to perform necessary operations during object initialization, such as resource
 * loading, configuration parsing, state initialization, etc.
 *
 * <p>Ensure that all implementation classes have unified initialization capabilities through
 * interface contracts, facilitating unified management of initialization processes in the
 * system.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.0
 */

public interface InitializeAble {

    /**
     * Method for performing initialization operations.
     * <p>
     * This method should be called after {@link CronTaskRepository} creation and before
     * {@link Lifecycle} cycle execution to complete initialization preparation. The
     * specific implementation may include:
     * <ul>
     * Loading and validation of resources (such as database connection, file reading)
     * <li>Analysis and verification of configuration parameters</li>
     * <li>Dependency Object Injection and Validation</li>
     * <li>Initialization settings for state variables</li>
     * </ul>
     * <p>
     * Note: After calling this method, ensure that the object is in a usable state, otherwise
     * throw the corresponding exception.
     *
     * @throws Exception is thrown when any unrecoverable error occurs during initialization,
     *                   Including but not limited to configuration errors, resource unavailability,
     *                   missing dependencies, and other issues.
     */
    void initialize() throws Exception;
}
