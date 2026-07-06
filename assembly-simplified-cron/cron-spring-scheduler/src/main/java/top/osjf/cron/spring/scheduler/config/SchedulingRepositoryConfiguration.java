/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.cron.spring.scheduler.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import top.osjf.cron.core.lifecycle.InitializeProperties;
import top.osjf.cron.spring.AbstractCronTaskConfiguration;
import top.osjf.cron.spring.Utils;
import top.osjf.cron.spring.scheduler.SpringSchedulerTaskRepository;

/**
 * {@link Configuration Configuration} for {@link SpringSchedulerTaskRepository}.
 *
 * <p>The configuration class provides {@link ScheduledAnnotationBeanPostProcessor}
 * or {@link top.osjf.cron.spring.scheduler.config.ScheduledAnnotationBeanPostProcessor}
 * with a {@link TaskScheduler} implementation named {@literal taskScheduler},
 * essentially {@link top.osjf.cron.core.repository.CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@Configuration(proxyBeanMethods = false)
public class SchedulingRepositoryConfiguration extends AbstractCronTaskConfiguration {

    /**
     * Internal bean name of {@link TaskScheduler}.
     */
    public static final String TASK_SCHEDULER_INTERNAL_BEAN_NAME
            = "org.springframework.scheduling.concurrent.internalThreadPoolTaskScheduler";

    @Bean
    public SpringSchedulerTaskRepository springSchedulerTaskRepository(
            @Autowired(required = false) @Qualifier(TASK_SCHEDULER_INTERNAL_BEAN_NAME) TaskScheduler taskScheduler,
            ObjectProvider<InitializeProperties> provider) {
        SpringSchedulerTaskRepository repository = taskScheduler != null ?
                new SpringSchedulerTaskRepository(taskScheduler) : new SpringSchedulerTaskRepository();
        InitializeProperties properties = Utils.getHighestPriorityMatchingInstance(provider);
        if (properties != null) {
            repository.setInitializeProperties(properties);
        }
        return repository;
    }

    /**
     * @since 3.0.1
     * @param repository {@link SpringSchedulerTaskRepository}
     * @return a {@link SchedulingConfigurer} to set a {@link SpringSchedulerTaskRepository}
     * for {@link ScheduledTaskRegistrar}.
     */
    @Bean
    public SchedulingConfigurer repositorySchedulingConfigurer(SpringSchedulerTaskRepository repository) {
        return taskRegistrar -> taskRegistrar.setTaskScheduler(repository);
    }
}
