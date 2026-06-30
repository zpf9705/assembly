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


package top.osjf.cron.core.micrometer;

import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface RepositoryTagConstants {

    /**
     * Metric tag key used to identify implementation class of {@link CronTaskRepository}.
     */
    String MODULE_TAG_KEY = "module";

    /**
     * Metric tag key for marking method signature of monitored method.
     */
    String METHOD_SIGNATURE_TAG_KEY = "method.signature";

    /**
     * Metric tag key used to identify cron task registration operation.
     */
    String REGISTER_TAG_KEY = "cron.task.register";

    /**
     * Metric tag key for identifying the timeout duration of cron task registration operations.
     */
    String REGISTER_TIMEOUT_TAG_KEY = "cron.task.timeout.register";

    /**
     * Metric tag key used to identify cron tasks executed with a specified number of run times.
     */
    String REGISTER_RUNTIMES_TAG_KEY = "cron.task.runtimes.register";

    /**
     * Metric tag key for identifying cron tasks configured with both execution run limit and timeout
     * rule during registration.
     */
    String REGISTER_TIMEOUT_RUNTIMES_TAG_KEY = "cron.task.timeout.runtimes.register";

    /**
     * Metric tag key for cron task update operation.
     */
    String UPDATE_TAG_KEY = "cron.task.update";

    /**
     * Metric tag key for cron task remove operation.
     */
    String REMOVE_TAG_KEY = "cron.task.remove";

    /**
     * Metric tag key for cron task terminate operation.
     */
    String TERMINATE_TAG_KEY = "cron.task.terminate";

    /**
     * Metric tag key for cron listener add operation.
     */
    String ADD_LISTENER_TAG_KEY = "cron.task.listener.add";

    /**
     * Metric tag key for cron listener remove operation.
     */
    String REMOVE_LISTENER_TAG_KEY = "cron.task.listener.remove";


    /* ====================================== current ========================================== */

    /**
     * Metric gauge key for the real-time number of currently valid registered cron tasks.
     */
    String REGISTERED_TASK_CURRENT_GAUGE_KEY = "cron.task.registered.current";

    /**
     * Metric gauge key for real-time count of currently running cron tasks.
     */
    String RUNNING_TASK_COUNT_GAUGE_KEY = "cron.task.running";

    /**
     * Metric gauge key for the real-time number of currently valid registered cron listeners.
     */
    String REGISTERED_TASK_LISTENER_CURRENT_GAUGE_KEY = "cron.task.listener.current";
}
