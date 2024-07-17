/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.cron.spring.annotation;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.cron.spring.*;

import java.util.List;
import java.util.Objects;

/**
 * Automatic assembly and configuration of beans related to scheduled
 * listeners, dynamic registration, etc.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CronTaskRegisterConfiguration {

    /**
     * Automatically set the listener for scheduled task execution.
     *
     * @param cronListeners Implement {@link CronListener} using configured Spring components.
     */
    @Autowired(required = false)
    public void setCronListeners(List<CronListener> cronListeners) {
        CronTaskManager.addCronListeners(cronListeners);
    }

    /**
     * Automatically set the listener by {@link CronConfigurer} for scheduled task execution.
     *
     * @param cronConfigurers Implement the interface configuration item for {@link CronConfigurer}.
     */
    @Autowired(required = false)
    public void setCronListenersByCronConfigurer(List<CronConfigurer> cronConfigurers) {
        if (CollectionUtils.isNotEmpty(cronConfigurers)) {
            cronConfigurers.stream()
                    .map(CronConfigurer::getWillRegisterCronListeners)
                    .filter(Objects::nonNull)
                    .distinct()
                    .forEach(CronTaskManager::addCronListeners);
        }
    }

    @Bean
    public CronTaskRegistrant cronTaskRegistrant() {
        return new DefaultCronTaskRegistrant();
    }
}
