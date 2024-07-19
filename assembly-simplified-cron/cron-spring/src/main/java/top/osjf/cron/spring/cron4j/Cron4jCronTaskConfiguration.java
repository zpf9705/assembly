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

package top.osjf.cron.spring.cron4j;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.cron.cron4j.lifestyle.Cron4jCronLifeStyle;
import top.osjf.cron.cron4j.repository.Cron4jCronTaskRepository;

/**
 * Regarding the configuration classes related to scheduled task
 * registration for Cron4j.
 *
 * @see EnableCron4jCronTaskRegister
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class Cron4jCronTaskConfiguration {

    @Bean(destroyMethod = "stop")
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Cron4jCronLifeStyle cron4jCronLifeStyle() {
        return new Cron4jCronLifeStyle();
    }

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public Cron4jCronTaskRegistrant cron4jCronTaskRegistrant(Cron4jCronLifeStyle cron4jCronLifeStyle) {
        Cron4jCronTaskRepository cron4jCronTaskRepository
                = new Cron4jCronTaskRepository(cron4jCronLifeStyle.getScheduler());
        return new Cron4jCronTaskRegistrant(cron4jCronTaskRepository);
    }

    @Bean
    public Cron4jCronTaskRepository cron4jCronTaskRepository(Cron4jCronTaskRegistrant cronTaskRegistrant) {
        return cronTaskRegistrant.getCronTaskRepository();
    }
}
