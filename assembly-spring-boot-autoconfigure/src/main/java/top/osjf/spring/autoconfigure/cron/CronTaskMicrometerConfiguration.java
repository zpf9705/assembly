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


package top.osjf.spring.autoconfigure.cron;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.osjf.cron.core.micrometer.ExpressionResolver;
import top.osjf.cron.core.micrometer.MeterRegistryDelegation;
import top.osjf.cron.core.micrometer.RepositoryGaugeIndicatorRegistrant;
import top.osjf.cron.core.micrometer.RepositoryTagsBasedOnJoinPointFunction;
import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * {@link Configuration Configuration} for cron task micrometer monitoring capability.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({MeterRegistry.class, CountedAspect.class,
        TimedAspect.class, ProceedingJoinPoint.class, MeterRegistryCustomizer.class})
@ConditionalOnBean({ MeterRegistry.class })
class CronTaskMicrometerConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ExpressionResolver expressionResolver(Environment environment) {
        return environment::resolvePlaceholders;
    }

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> cronTaskMeterRegistryCustomizer(CronTaskRepository cronTaskRepository,
                                                                                  ExpressionResolver expressionResolver)
    {
        return registry -> {
            MeterRegistryDelegation.initProperties(registry, expressionResolver);
            new RepositoryGaugeIndicatorRegistrant(registry, cronTaskRepository, expressionResolver)
                    .doRegister();
        };
    }

    @Bean
    @ConditionalOnMissingBean
    public RepositoryTagsBasedOnJoinPointFunction repositoryTagsBasedOnJoinPointFunction
            (ExpressionResolver expressionResolver) {
        return new RepositoryTagsBasedOnJoinPointFunction(expressionResolver);
    }

    @Bean
    @ConditionalOnMissingBean
    public CountedAspect countedAspect(MeterRegistry meterRegistry,
                                       RepositoryTagsBasedOnJoinPointFunction repositoryTagsBasedOnJoinPointFunction) {
        return new CountedAspect(meterRegistry, repositoryTagsBasedOnJoinPointFunction);
    }

    @Bean
    @ConditionalOnMissingBean
    public TimedAspect timedAspect(MeterRegistry meterRegistry,
                                   RepositoryTagsBasedOnJoinPointFunction repositoryTagsBasedOnJoinPointFunction) {
        return new TimedAspect(meterRegistry, repositoryTagsBasedOnJoinPointFunction);
    }
}
