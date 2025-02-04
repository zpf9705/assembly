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

import org.springframework.boot.LazyInitializationExcludeFilter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.osjf.cron.core.repository.CronTaskRepository;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link CronTaskRepository}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(CronProperties.class)
@Import({HutoolCronTaskAutoConfiguration.class,
        QuartzCronTaskAutoConfiguration.class,
        Cron4jCronTaskAutoConfiguration.class,
        SpringSchedulerAutoConfiguration.class})
public class CronTaskAutoConfiguration {

    @Bean
    public LazyInitializationExcludeFilter cronBeanLazyInitializationExcludeFilter() {
        return new CronBeanLazyInitializationExcludeFilter();
    }
}
