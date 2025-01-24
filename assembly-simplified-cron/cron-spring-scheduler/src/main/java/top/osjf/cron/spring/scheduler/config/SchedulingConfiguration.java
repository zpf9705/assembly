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


package top.osjf.cron.spring.scheduler.config;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.config.TaskManagementConfigUtils;
import top.osjf.cron.spring.ObjectProviderUtils;
import top.osjf.cron.spring.scheduler.SpringSchedulerTaskRepository;

/**
 * /**
 * {@code @Configuration} class that registers a {@link ScheduledAnnotationBeanPostProcessor}
 * bean capable of processing Spring's @{@link org.springframework.scheduling.annotation.Scheduled}
 * and cron framework {@link top.osjf.cron.spring.annotation.Cron} annotation.
 *
 * <p>This configuration class is automatically imported when using the
 * {@link EnableScheduling @EnableScheduling} annotation. See
 * {@code @EnableScheduling}'s javadoc for complete usage details.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 * @see EnableScheduling
 * @see ScheduledAnnotationBeanPostProcessor
 * @see SpringSchedulerTaskRepository
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SchedulingConfiguration {

    @Bean(name = TaskManagementConfigUtils.SCHEDULED_ANNOTATION_PROCESSOR_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ScheduledAnnotationBeanPostProcessor scheduledAnnotationProcessor() {
        return new ScheduledAnnotationBeanPostProcessor();
    }

    @Bean(org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
            .DEFAULT_TASK_SCHEDULER_BEAN_NAME)
    public SpringSchedulerTaskRepository springSchedulerTaskRepository(ObjectProvider<TaskScheduler> provider) {
        TaskScheduler taskScheduler = ObjectProviderUtils.getPriority(provider);
        if (taskScheduler != null) {
            return new SpringSchedulerTaskRepository(taskScheduler);
        }
        return new SpringSchedulerTaskRepository();
    }
}
