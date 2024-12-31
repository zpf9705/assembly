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

package top.osjf.cron.spring.quartz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import top.osjf.cron.core.lifestyle.StartupProperties;
import top.osjf.cron.quartz.lifestyle.QuartzCronLifeStyle;
import top.osjf.cron.quartz.repository.QuartzCronTaskRepository;
import top.osjf.cron.spring.BeanSortUtils;
import top.osjf.cron.spring.CronAnnotationPostProcessor;
import top.osjf.cron.spring.annotation.Cron;

import java.util.List;

/**
 * {@code @Configuration} class that registers a {@link CronAnnotationPostProcessor}
 * bean capable of processing Spring's @{@link Cron} annotation.
 *
 * <p>This configuration class is automatically imported when using the
 * {@link EnableQuartzCronTaskRegister @EnableQuartzCronTaskRegister} annotation. See
 * {@code @EnableQuartzCronTaskRegister}'s javadoc for complete usage details.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 * @see EnableQuartzCronTaskRegister
 */
@Configuration(proxyBeanMethods = false)
public class QuartzCronTaskConfiguration {

    @Bean
    @Order
    public QuartzJobFactory quartzJobFactory() {
        return new QuartzJobFactory();
    }

    @Bean
    @Order
    public QuartzCronTaskRepository quartzCronTaskRepository(List<StartupProperties> startupProperties,
                                                             List<QuartzJobFactory> quartzJobFactories) {
        return new QuartzCronTaskRepository(
                BeanSortUtils.getPriorityBean(startupProperties).asProperties(),
                BeanSortUtils.getPriorityBean(quartzJobFactories));
    }

    @Bean(destroyMethod = "stop")
    public QuartzCronLifeStyle quartzCronLifeStyle(List<QuartzCronTaskRepository> quartzCronTaskRepositories) {
        return new QuartzCronLifeStyle(BeanSortUtils.getPriorityBean(quartzCronTaskRepositories).getScheduler());
    }
}
