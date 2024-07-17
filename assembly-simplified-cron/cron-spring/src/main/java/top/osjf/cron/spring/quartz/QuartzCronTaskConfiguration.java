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

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.cron.core.lifestyle.LifeStyle;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.quartz.lifestyle.QuartzCronLifeStyle;
import top.osjf.cron.quartz.repository.QuartzCronTaskRepository;

/**
 * Regarding the configuration classes related to scheduled task
 * registration for Quartz.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class QuartzCronTaskConfiguration {

    @Bean
    public CronTaskRepository<JobKey, JobDetail> cronTaskRepository() {
        return new QuartzCronTaskRepository(null);
    }

    @Bean
    public LifeStyle lifeStyle(CronTaskRepository<JobKey, JobDetail> cronTaskRepository) {
        return new QuartzCronLifeStyle(((QuartzCronTaskRepository) cronTaskRepository).getScheduler());
    }
}
