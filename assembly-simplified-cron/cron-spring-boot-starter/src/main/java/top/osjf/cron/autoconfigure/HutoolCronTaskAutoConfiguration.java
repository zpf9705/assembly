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

package top.osjf.cron.autoconfigure;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;
import top.osjf.cron.hutool.lifestyle.HutoolCronLifeStyle;
import top.osjf.cron.hutool.repository.HutoolCronTaskRepository;
import top.osjf.cron.spring.hutool.HutoolCronTaskRealRegistrant;
import top.osjf.cron.spring.hutool.HutoolRegistrantCollector;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The automatic assembly of timed task components Hutool.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@ConditionalOnClass({HutoolCronLifeStyle.class, HutoolCronTaskRepository.class})
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class HutoolCronTaskAutoConfiguration extends AbstractImplsCommonConfiguration implements InitializingBean {

    private final Map<String, Object> metadata = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() {
        Environment environment = getEnvironment();
        metadata.put("isMatchSecond", environment.getProperty("spring.hutool.cron.match-second",
                boolean.class, true));
        metadata.put("isDaemon", environment.getProperty("spring.hutool.cron.daemon",
                boolean.class, false));
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Bean
    public HutoolRegistrantCollector hutoolRegistrantCollector(){
        return new HutoolRegistrantCollector();
    }

    @Bean(destroyMethod = "stop")
    public HutoolCronLifeStyle hutoolCronLifeStyle() {
        return new HutoolCronLifeStyle();
    }

    @Bean
    public HutoolCronTaskRepository hutoolCronTaskRepository() {
        return new HutoolCronTaskRepository();
    }

    @Bean
    public HutoolCronTaskRealRegistrant hutoolCronTaskRealRegistrant(HutoolCronTaskRepository hutoolCronTaskRepository) {
        return new HutoolCronTaskRealRegistrant(hutoolCronTaskRepository);
    }
}
