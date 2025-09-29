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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskManagementConfigUtils;
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.spring.AbstractCronTaskConfiguration;
import top.osjf.cron.spring.ObjectProviderUtils;
import top.osjf.cron.spring.scheduler.SpringSchedulerTaskRepository;
import top.osjf.cron.spring.scheduler.config.EnableScheduling;
import top.osjf.cron.spring.scheduler.config.SchedulingRepositoryConfiguration;

/**
 * {@link Configuration Configuration} for {@link SpringSchedulerTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SpringSchedulerTaskRepository.class})
@AutoConfigureBefore(TaskSchedulingAutoConfiguration.class)
@ConditionalOnMissingBean(CronTaskRepository.class)
@Conditional(CronCondition.class)
class SpringSchedulerConfiguration {

    @Bean(SchedulingRepositoryConfiguration.TASK_SCHEDULER_INTERNAL_BEAN_NAME)
    public ThreadPoolTaskScheduler threadPoolTaskScheduler(TaskSchedulerBuilder builder) {
        return builder.build();
    }

    @Bean
    public SuperiorProperties springSchedulerProperties(CronProperties cronProperties) {
        return cronProperties.getRunTimeoutMonitoring().get();
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(value = ScheduledAnnotationBeanPostProcessor.class,
            name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @EnableScheduling
    static class CompatibilitySpringSchedulerConfiguration {
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(value = ScheduledAnnotationBeanPostProcessor.class,
            name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    static class AdaptedSpringSchedulerConfiguration extends AbstractCronTaskConfiguration {

        @Bean
        public SpringSchedulerTaskRepository springSchedulerTaskRepository(
                @Qualifier(SchedulingRepositoryConfiguration.TASK_SCHEDULER_INTERNAL_BEAN_NAME)
                TaskScheduler taskScheduler, ObjectProvider<SuperiorProperties> provider) {
            SpringSchedulerTaskRepository repository = new SpringSchedulerTaskRepository(taskScheduler);
            SuperiorProperties properties = ObjectProviderUtils.getPriority(provider);
            if (properties != null) {
                repository.setSuperiorProperties(properties);
            }
            return repository;
        }

        @Bean
        public SchedulingConfigurer repositorySchedulingConfigurer(SpringSchedulerTaskRepository repository) {
            return taskRegistrar -> taskRegistrar.setTaskScheduler(repository);
        }
    }
}
