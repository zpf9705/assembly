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


package top.osjf.spring.autoconfigure.cron;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.repository.SimpleCronTaskRepository;
import top.osjf.cron.spring.AbstractCronTaskConfiguration;
import top.osjf.cron.spring.CronTaskConfiguration;
import top.osjf.cron.spring.annotation.CronRepositoryBean;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link SimpleCronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(CronTaskRepository.class)
@Import({CronTaskConfiguration.class, SimpleCronTaskAutoConfiguration.SimpleCronTaskConfiguration.class})
@Conditional(CronCondition.class)
public class SimpleCronTaskAutoConfiguration {

    @CronRepositoryBean
    public SimpleCronTaskRepository simpleCronTaskRepository(CronProperties cronProperties,
                                                             ScheduledExecutorService scheduledExecutorService) {
        CronProperties.Simple simple = cronProperties.getSimple();
        SimpleCronTaskRepository repository
                = new SimpleCronTaskRepository(scheduledExecutorService, simple.getCronType());
        repository.setAwaitTermination(simple.isAwaitTermination());
        repository.setAwaitTerminationTimeout(simple.getAwaitTerminationTimeout());
        repository.setAwaitTerminationTimeoutUnit(simple.getAwaitTerminationTimeoutUnit());
        return repository;
    }

    @Bean
    @ConditionalOnMissingBean
    public ScheduledExecutorService scheduledExecutorService(CronProperties cronProperties) {
        CronProperties.Simple simple = cronProperties.getSimple();
        return Executors.newScheduledThreadPool(simple.getPoolCoreSize());
    }

    @Configuration(proxyBeanMethods = false)
    public static class SimpleCronTaskConfiguration extends AbstractCronTaskConfiguration {

    }
}
