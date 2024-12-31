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

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import top.osjf.cron.cron4j.lifestyle.Cron4jCronLifeStyle;
import top.osjf.cron.cron4j.repository.Cron4jCronTaskRepository;
import top.osjf.cron.spring.CronAnnotationPostProcessor;
import top.osjf.cron.spring.annotation.Cron;

/**
 * {@code @Configuration} class that registers a {@link CronAnnotationPostProcessor}
 * bean capable of processing Spring's @{@link Cron} annotation.
 *
 * <p>This configuration class is automatically imported when using the
 * {@link EnableCron4jCronTaskRegister @EnableCron4jCronTaskRegister} annotation. See
 * {@code @EnableCron4jCronTaskRegister}'s javadoc for complete usage details.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 * @see EnableCron4jCronTaskRegister
 */
@Configuration(proxyBeanMethods = false)
public class Cron4jCronTaskConfiguration {

    @Bean
    @Order
    public Cron4jCronTaskRepository cron4jCronTaskRepository() {
        return new Cron4jCronTaskRepository();
    }

    @Bean
    public Cron4jCronLifeStyle cron4jCronLifeStyle(Cron4jCronTaskRepository cronTaskRepository) {
        return new Cron4jCronLifeStyle(cronTaskRepository.getScheduler());
    }
}
