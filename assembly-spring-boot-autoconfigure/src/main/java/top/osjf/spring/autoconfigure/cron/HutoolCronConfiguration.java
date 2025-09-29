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

import cn.hutool.cron.Scheduler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.hutool.repository.HutoolCronTaskRepository;
import top.osjf.cron.spring.CronTaskConfiguration;
import top.osjf.cron.spring.hutool.HutoolCronTaskConfiguration;

/**
 * {@link Configuration Configuration} for {@link HutoolCronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({HutoolCronTaskRepository.class, HutoolCronTaskConfiguration.class})
@Import({HutoolCronTaskConfiguration.class, CronTaskConfiguration.class})
@ConditionalOnMissingBean(CronTaskRepository.class)
@Conditional(CronCondition.class)
class HutoolCronConfiguration {

    @Bean
    @ConditionalOnMissingBean(Scheduler.class)
    public SuperiorProperties hutoolProperties(CronProperties cronProperties) {
        return cronProperties.getClientProperties(ClientType.HUTOOL);
    }
}
