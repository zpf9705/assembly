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
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.TaskManagementConfigUtils;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.spring.AbstractCronTaskConfiguration;
import top.osjf.cron.spring.scheduler.SpringSchedulerTaskRepository;
import top.osjf.cron.spring.scheduler.config.EnableScheduling;

/**
 * {@link Configuration Configuration} for {@link SpringSchedulerTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SpringSchedulerTaskRepository.class})
@AutoConfigureBefore(TaskSchedulingAutoConfiguration.class)
@EnableConfigurationProperties(TaskSchedulingProperties.class)
@ConditionalOnMissingBean(CronTaskRepository.class)
@Conditional(CronCondition.class)
class SpringSchedulerConfiguration {


    ////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Essential Configuration //////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    private static final String TASK_SCHEDULER_INTERNAL_BEAN_NAME
            = "org.springframework.scheduling.concurrent.internalThreadPoolTaskScheduler";

    @Bean
    @ConditionalOnMissingBean
    public TaskSchedulerBuilder taskSchedulerBuilder(TaskSchedulingProperties properties,
                                                     ObjectProvider<TaskSchedulerCustomizer> taskSchedulerCustomizers) {
        TaskSchedulerBuilder builder = new TaskSchedulerBuilder();
        builder = builder.poolSize(properties.getPool().getSize());
        TaskSchedulingProperties.Shutdown shutdown = properties.getShutdown();
        builder = builder.awaitTermination(shutdown.isAwaitTermination());
        builder = builder.awaitTerminationPeriod(shutdown.getAwaitTerminationPeriod());
        builder = builder.threadNamePrefix(properties.getThreadNamePrefix());
        builder = builder.customizers(taskSchedulerCustomizers);
        return builder;
    }

    @Bean(TASK_SCHEDULER_INTERNAL_BEAN_NAME)
    public ThreadPoolTaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
        return builder.build();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //////////////// Configuration based on different situations ///////////////////
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Automatically configure the {@link EnableScheduling} of the OSJF framework without
     * using Spring's {@link org.springframework.scheduling.annotation.EnableScheduling}.
     *
     * <p>Annotations {@link top.osjf.cron.spring.annotation.Cron @Cron} and {@link Scheduled @Scheduled}
     * are both supported.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingBean(value = ScheduledAnnotationBeanPostProcessor.class,
            name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @EnableScheduling
    public static class SpringSchedulerConfirmNoLoadingEnableSchedulingUseOSJFConfiguration {
    }

    /**
     * When using Spring's {@link org.springframework.scheduling.annotation.EnableScheduling},
     * automatically configuring a listenable {@link SpringSchedulerTaskRepository TaskScheduler}
     * annotation {@link top.osjf.cron.spring.annotation.Cron @Cron} is no longer supported.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnBean(value = ScheduledAnnotationBeanPostProcessor.class,
            name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    public static class SpringSchedulerConfirmLoadingSpringEnableSchedulingConfiguration
            extends AbstractCronTaskConfiguration {

        @Bean(ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME)
        @ConditionalOnMissingBean(SpringSchedulerTaskRepository.class)
        public SpringSchedulerTaskRepository springSchedulerTaskRepository(
                @Qualifier(TASK_SCHEDULER_INTERNAL_BEAN_NAME) TaskScheduler taskScheduler) {
            return new SpringSchedulerTaskRepository(taskScheduler);
        }
    }
}
