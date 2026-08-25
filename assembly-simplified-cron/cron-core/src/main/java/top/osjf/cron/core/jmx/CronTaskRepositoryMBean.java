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


package top.osjf.cron.core.jmx;

/**
 * Standard JMX MBean interface for cron task repository runtime statistics.
 *
 * <p>This interface follows the JMX standard bean specification and provides
 * readable attribute metrics for cron task repository runtime status.
 * All exposed attributes can be viewed and monitored through JConsole or
 * other JMX monitoring tools.
 *
 * <p>The exposed metrics are divided into two categories:
 * <ul>
 *     <li><b>Cumulative statistics</b>: Record the total number of historical
 *     operations such as task registration, modification, deletion, termination
 *     and listener changes.</li>
 *     <li><b>Real‑time statistics</b>: Reflect the current running snapshot
 *     data of the repository, including the number of registered tasks,
 *     running tasks and registered listeners.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface CronTaskRepositoryMBean {

    /**
     * Gets total count of task registration invocations.
     *
     * @return total registration count
     */
    long getRegisterTaskTotal();

    /**
     * Gets total count of tasks registered with running‑timeout configuration.
     *
     * @return total timeout‑register count
     */
    long getRegisterTimeoutTotal();

    /**
     * Gets total count of task execution timeout events.
     *
     * @return total execution timeout count
     */
    long getTaskTimeoutTotal();

    /**
     * Gets total count of registrations for limited‑run‑times tasks.
     *
     * @return total limited‑run‑times task registration count
     */
    long getRegisterRuntimesTotal();

    /**
     * Gets total count of registrations for tasks with both limited run‑times and timeout.
     *
     * @return total limited‑run‑times with timeout registration count
     */
    long getRegisterTimeoutRuntimesTotal();

    /**
     * Gets total count of task update invocations.
     *
     * @return total update count
     */
    long getUpdateTaskTotal();

    /**
     * Gets total count of task remove invocations.
     *
     * @return total remove count
     */
    long getRemoveTaskTotal();

    /**
     * Gets total count of task terminate invocations.
     *
     * @return total terminate count
     */
    long getTerminateTaskTotal();

    /**
     * Gets total count of adding cron listeners.
     *
     * @return total add‑listener count
     */
    long getAddListenerTotal();

    /**
     * Gets total count of removing cron listeners.
     *
     * @return total remove‑listener count
     */
    long getRemoveListenerTotal();

    /**
     * Gets current number of registered cron tasks.
     *
     * @return current registered task amount
     */
    long getRegisteredTaskCurrent();

    /**
     * Gets current number of registered limited‑run‑times tasks.
     *
     * @return current limited‑run‑times task amount
     */
    long getRegisteredRunTimesTaskCurrent();

    /**
     * Gets current number of running task instances.
     *
     * @return current running task amount
     */
    long getRunningTaskCurrent();

    /**
     * Gets current number of registered cron listeners.
     *
     * @return current registered listener amount
     */
    long getRegisteredTaskListenerCurrent();
}
