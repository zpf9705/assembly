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
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.repository.NoOpCronTaskRepository;
import top.osjf.cron.spring.annotation.CronRepositoryBean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link NoOpCronTaskRepository}
 * used to disable {@link CronTaskRepository} configuration.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(CronTaskRepository.class)
@Conditional(CronCondition.class)
public class NoOpCornTaskAutoConfiguration {

    @CronRepositoryBean
    public NoOpCronTaskRepository noOpCronTaskRepository() {
        return new NoOpCronTaskRepository();
    }
}
