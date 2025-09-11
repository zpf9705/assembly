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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor;
import top.osjf.cron.spring.AbstractCronTaskConfiguration;
import top.osjf.cron.spring.ObjectProviderUtils;
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

    @Bean(ScheduledAnnotationBeanPostProcessor.DEFAULT_TASK_SCHEDULER_BEAN_NAME)
    public SpringSchedulerTaskRepository springSchedulerTaskRepository(ObjectProvider<TaskScheduler> provider) {
        TaskScheduler taskScheduler = ObjectProviderUtils.getPriority(provider);
        if (taskScheduler != null) {
            return new SpringSchedulerTaskRepository(taskScheduler);
        }
        return new SpringSchedulerTaskRepository();
    }
}
