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

import javax.management.MBeanAttributeInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

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

    /**
     * Custom {@link StandardMBean} implementation for {@link CronTaskRepositoryMBean}
     * that provides human-readable English descriptions for all exposed attributes.
     *
     * <p>This class enhances the JMX management interface by overriding the
     * {@link #getDescription(MBeanAttributeInfo)} method to return descriptive
     * English labels for each metric, making it easier for operators to understand
     * the meaning of each attribute when monitoring through JConsole or similar tools.
     */
    class CronTaskRepositoryStandardMBean extends StandardMBean {

        /**
         * Constructs a new {@code CronTaskRepositoryStandardMBean} with the given
         * MBean implementation.
         *
         * @param mBean the MBean implementation instance
         * @throws NotCompliantMBeanException if the MBean does not comply with
         *         the JMX standard bean specification
         */
        public CronTaskRepositoryStandardMBean(CronTaskRepositoryMBean mBean)
                throws NotCompliantMBeanException {
            super(mBean, CronTaskRepositoryMBean.class);
        }

        /**
         * Returns a human-readable English description for the specified MBean attribute.
         *
         * <p>This method overrides the default behavior to provide descriptive
         * labels for all attributes defined in {@link CronTaskRepositoryMBean}.
         * Unrecognized attribute names fall back to the default description.
         *
         * @param info the attribute metadata
         * @return an English description string for the attribute
         */
        @Override
        protected String getDescription(MBeanAttributeInfo info) {
            String name = info.getName();
            switch (name) {
                // Cumulative statistics
                case "RegisterTaskTotal":
                    return "Total number of task registration operations";
                case "RegisterTimeoutTotal":
                    return "Total number of tasks registered with running-timeout configuration";
                case "TaskTimeoutTotal":
                    return "Total number of task execution timeout events";
                case "RegisterRuntimesTotal":
                    return "Total number of limited-run-times task registrations";
                case "RegisterTimeoutRuntimesTotal":
                    return "Total number of limited-run-times with timeout task registrations";
                case "UpdateTaskTotal":
                    return "Total number of task update operations";
                case "RemoveTaskTotal":
                    return "Total number of task remove operations";
                case "TerminateTaskTotal":
                    return "Total number of task terminate operations";
                case "AddListenerTotal":
                    return "Total number of add-listener operations";
                case "RemoveListenerTotal":
                    return "Total number of remove-listener operations";
                // Real-time snapshot statistics
                case "RegisteredTaskCurrent":
                    return "Current number of registered tasks";
                case "RegisteredRunTimesTaskCurrent":
                    return "Current number of limited-run-times tasks";
                case "RunningTaskCurrent":
                    return "Current number of running tasks";
                case "RegisteredTaskListenerCurrent":
                    return "Current number of registered listeners";
                // Default super call
                default:
                    return super.getDescription(info);
            }
        }
    }
}
