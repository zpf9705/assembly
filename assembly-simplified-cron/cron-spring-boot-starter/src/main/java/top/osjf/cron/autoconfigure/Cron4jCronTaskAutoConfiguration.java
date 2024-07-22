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
import top.osjf.cron.cron4j.lifestyle.Cron4jCronLifeStyle;
import top.osjf.cron.cron4j.repository.Cron4jCronTaskRepository;
import top.osjf.cron.spring.cron4j.Cron4jCronTaskRealRegistrant;
import top.osjf.cron.spring.cron4j.Cron4jRegistrantCollector;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The automatic assembly of timed task components Cron4j.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Cron4jCronLifeStyle.class, Cron4jCronTaskRepository.class})
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class Cron4jCronTaskAutoConfiguration extends AbstractImplsCommonConfiguration implements InitializingBean {

    private final Map<String, Object> metadata = new LinkedHashMap<>();

    @Override
    public void afterPropertiesSet() {
        Environment environment = getEnvironment();
        metadata.put("daemon", environment.getProperty("spring.cron4j.cron.daemon",
                boolean.class, true));
        metadata.put("timezone", environment.getProperty("spring.cron4j.cron.zone",
                String.class, "GMT+8"));
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Bean
    public Cron4jRegistrantCollector cron4jRegistrantCollector(){
        return new Cron4jRegistrantCollector();
    }

    @Bean
    public Cron4jCronTaskRepository cron4jCronTaskRepository() {
        return new Cron4jCronTaskRepository();
    }

    @Bean(destroyMethod = "stop")
    public Cron4jCronLifeStyle cron4jCronLifeStyle(Cron4jCronTaskRepository cronTaskRepository) {
        return new Cron4jCronLifeStyle(cronTaskRepository.getScheduler());
    }

    @Bean
    public Cron4jCronTaskRealRegistrant cron4jCronTaskRealRegistrant(Cron4jCronTaskRepository cron4jCronTaskRepository) {
        return new Cron4jCronTaskRealRegistrant(cron4jCronTaskRepository);
    }
}
