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

import java.util.function.ToDoubleFunction;

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
    private final ExpressionResolver expressionResolver;
    private boolean registerFlag;

    /**
     * Constructs an indicator registrant using Micrometer global default {@link MeterRegistry}
     * and the system built-in configuration placeholder resolver.
     *
     * @param cronTaskRepository the target cron task repository instance for gauge metrics collection
     */
    public RepositoryGaugeIndicatorRegistrant(CronTaskRepository cronTaskRepository) {
        this(cronTaskRepository, new SystemPropertyExpressionResolver());
    }

    /**
     * Constructs an indicator registrant using Micrometer global default {@link MeterRegistry}
     * with a custom placeholder expression resolver.
     *
     * @param cronTaskRepository the target cron task repository instance for gauge metrics collection
     * @param expressionResolver the custom resolver used to parse placeholder expressions in metric tags
     */
    public RepositoryGaugeIndicatorRegistrant(CronTaskRepository cronTaskRepository, ExpressionResolver expressionResolver) {
        this(Metrics.globalRegistry, cronTaskRepository, expressionResolver);
    }

    /**
     * Fully customized constructor, specifies a dedicated {@link MeterRegistry}, cron task repository
     * and placeholder resolver for gauge metric registration.
     *
     * @param meterRegistry the custom metric registry for registering cron task runtime gauge indicators
     * @param cronTaskRepository the target cron task repository instance for gauge metrics collection
     * @param expressionResolver the resolver responsible for parsing placeholder expressions within metric tags
     */
    public RepositoryGaugeIndicatorRegistrant(MeterRegistry meterRegistry, CronTaskRepository cronTaskRepository,
                                              ExpressionResolver expressionResolver) {
        this.meterRegistry = meterRegistry;
        this.cronTaskRepository = cronTaskRepository;
        this.expressionResolver = expressionResolver;
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

        // Register gauge metric for the total number of all registered cron tasks in current repository...
        doRegisterInternal(RepositoryTagConstants.REGISTERED_TOTAL_TASK_COUNT_GAUGE_KEY,
                repository -> repository.getAllRegisteredTaskIds().size(),
                "List<String> getAllRegisteredTaskIds()",
                "Total quantity of all registered cron tasks under current task repository");

        // Register gauge metric for the count of currently executing cron tasks...
        doRegisterInternal(RepositoryTagConstants.RUNNING_TASK_COUNT_GAUGE_KEY,
                repository -> repository.getAllRunningTaskIds().size(),
                "List<String> getAllRunningTaskIds()",
                "Total Real-time quantity of currently executing cron scheduled tasks");

        registerFlag = true;
    }

    private void doRegisterInternal(String name, ToDoubleFunction<CronTaskRepository> f, String methodSignature,
                                    String description) {
        Gauge.builder(name, cronTaskRepository, f)
                .tags(RepositoryTagConstants.MODULE_TAG_KEY, cronTaskRepository.getName(),
                        RepositoryTagConstants.METHOD_SIGNATURE_TAG_KEY, methodSignature,
                        "os.name", expressionResolver.resolveExpression("${os.name}"),
                        "java.version", expressionResolver.resolveExpression("${java.version}"),
                        "hostname", expressionResolver.resolveExpression("${HOSTNAME:local}"))
                .description(description)
                .register(meterRegistry);
    }
}
