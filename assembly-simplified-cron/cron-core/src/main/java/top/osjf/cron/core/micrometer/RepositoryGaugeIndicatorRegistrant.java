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

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import top.osjf.commons.util.Assert;
import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * The unified registry for timed task storage dashboard indicators is responsible for batch
 * registering all Gauge type instantaneous monitoring indicators under the current timed
 * task storage instance at once, ensuring that each storage instance is only allowed to
 * perform one registration operation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class RepositoryGaugeIndicatorRegistrant {

    private final MeterRegistry meterRegistry;
    private final CronTaskRepository cronTaskRepository;
    private boolean registerFlag;

    /**
     * Constructor: Initialize the registrant instance with Micrometer's global default meter registry.
     * @param cronTaskRepository the cron task repository business instance.
     */
    public RepositoryGaugeIndicatorRegistrant(CronTaskRepository cronTaskRepository) {
        this(Metrics.globalRegistry, cronTaskRepository);
    }

    /**
     * Constructor: Specify the custom meter registry and cron task repository instance to complete
     * registrant initialization.
     *
     * @param meterRegistry      the custom monitoring meter registry manager
     * @param cronTaskRepository the cron task repository business instance
     */
    public RepositoryGaugeIndicatorRegistrant(MeterRegistry meterRegistry, CronTaskRepository cronTaskRepository) {
        this.meterRegistry = meterRegistry;
        this.cronTaskRepository = cronTaskRepository;
    }

    /**
     * Batch register all Gauge type monitoring metrics. This method can only be called once for the
     * current instance. Repeated calls will throw an illegal argument exception. Each cron task
     * repository only needs to call this method once during initialization.
     * @throws IllegalArgumentException Thrown when metric registration is executed repeatedly.
     */
    public void doRegister() {

        Assert.isTrue(!registerFlag,
                String.format("Gauge metric for repository [%s] has already been registered, repeated registration " +
                                "is not allowed", cronTaskRepository.getName()));

        // Register gauge metric for the count of currently executing cron tasks
        Gauge.builder(RepositoryTagConstants.RUNNING_TASK_COUNT_GAUGE_KEY, cronTaskRepository,
                        repository -> repository.getAllRunningTaskIds().size())
                .tag(RepositoryTagConstants.MODULE_TAG_KEY, cronTaskRepository.getName())
                .tag(RepositoryTagConstants.METHOD_SIGNATURE_TAG_KEY, "List<String> getAllRunningTaskIds()")
                .description("Total Real-time quantity of currently executing cron scheduled tasks")
                .register(meterRegistry);

        // Register gauge metric for the total number of all registered cron tasks in current repository
        Gauge.builder(RepositoryTagConstants.REGISTERED_TOTAL_TASK_COUNT_GAUGE_KEY, cronTaskRepository,
                        repository -> repository.getAllRegisteredTaskIds().size())
                .tag(RepositoryTagConstants.MODULE_TAG_KEY, cronTaskRepository.getName())
                .tag(RepositoryTagConstants.METHOD_SIGNATURE_TAG_KEY, "List<String> getAllRegisteredTaskIds()")
                .description("Total quantity of all registered cron tasks under current task repository")
                .register(meterRegistry);

        registerFlag = true;
    }
}
