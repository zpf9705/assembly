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
import top.osjf.cron.quartz.lifestyle.QuartzCronLifeStyle;
import top.osjf.cron.quartz.repository.QuartzCronTaskRepository;
import top.osjf.cron.spring.quartz.QuartzCronTaskRealRegistrant;
import top.osjf.cron.spring.quartz.QuartzJobFactory;
import top.osjf.cron.spring.quartz.QuartzRegistrantCollector;

import java.util.HashMap;
import java.util.Properties;

/**
 * The automatic assembly of timed task components Quartz.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@ConditionalOnClass({QuartzCronLifeStyle.class, QuartzCronTaskRepository.class})
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class QuartzCronTaskConfiguration extends AbstractImplsCommonConfiguration implements InitializingBean {

    private final Properties properties = new Properties();

    @Override
    public void afterPropertiesSet() {
        properties.putAll(getEnvironment().getProperty("spring.quartz.properties",
                HashMap.class, new HashMap<String, String>()));
    }

    @Bean
    public QuartzRegistrantCollector quartzRegistrantCollector(){
        return new QuartzRegistrantCollector();
    }

    @Bean
    public QuartzJobFactory quartzJobFactory() {
        return new QuartzJobFactory();
    }

    @Bean
    public QuartzCronTaskRepository quartzCronTaskRepository(QuartzJobFactory quartzJobFactory) {
        return new QuartzCronTaskRepository(properties, quartzJobFactory);
    }

    @Bean
    public QuartzCronTaskRealRegistrant quartzCronTaskRealRegistrant(QuartzCronTaskRepository quartzCronTaskRepository) {
        return new QuartzCronTaskRealRegistrant(quartzCronTaskRepository);
    }

    @Bean(destroyMethod = "stop")
    public QuartzCronLifeStyle quartzCronLifeStyle(QuartzCronTaskRepository quartzCronTaskRepository) {
        return new QuartzCronLifeStyle(quartzCronTaskRepository.getScheduler());
    }
}
