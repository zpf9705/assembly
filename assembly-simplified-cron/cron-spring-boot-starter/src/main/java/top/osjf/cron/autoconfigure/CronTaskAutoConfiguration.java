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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.core.env.Environment;
import top.osjf.cron.core.annotation.NotNull;
import top.osjf.cron.cron4j.lifestyle.Cron4jCronLifeStyle;
import top.osjf.cron.cron4j.repository.Cron4jCronTaskRepository;
import top.osjf.cron.hutool.lifestyle.HutoolCronLifeStyle;
import top.osjf.cron.hutool.repository.HutoolCronTaskRepository;
import top.osjf.cron.quartz.lifestyle.QuartzCronLifeStyle;
import top.osjf.cron.quartz.repository.QuartzCronTaskRepository;
import top.osjf.cron.spring.CronTaskRegisterPostProcessor;
import top.osjf.cron.spring.cron4j.Cron4jCronTaskRegistrant;
import top.osjf.cron.spring.hutool.HutoolCronTaskRegistrant;
import top.osjf.cron.spring.quartz.QuartzCronTaskRegistrant;
import top.osjf.cron.spring.quartz.QuartzJobFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * The auto configuration class for cron tasks, which automatically configures
 * based on the type of timed task input, defaults to registering hutool for
 * timed configuration.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CronTaskAutoConfiguration {

    /*** Hutool cron auto configuration */
    @ConditionalOnClass({HutoolCronLifeStyle.class, HutoolCronTaskRepository.class})
    @Configuration(proxyBeanMethods = false)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static class HutoolCronTaskAutoConfiguration implements EnvironmentAware {

        private final Map<String, Object> metadata = new LinkedHashMap<>();

        @Override
        public void setEnvironment(@NotNull Environment environment) {
            metadata.put("isMatchSecond", environment.getProperty("hutool.cron.match-second",
                    boolean.class, true));
            metadata.put("isDaemon", environment.getProperty("hutool.cron.daemon",
                    boolean.class, false));
        }

        @Bean(destroyMethod = "stop")
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public HutoolCronLifeStyle hutoolCronLifeStyle() {
            return new HutoolCronLifeStyle();
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public HutoolCronTaskRegistrant cronTaskRegistrant() {
            HutoolCronTaskRepository hutoolCronTaskRepository = new HutoolCronTaskRepository();
            return new HutoolCronTaskRegistrant(hutoolCronTaskRepository);
        }

        @Bean
        public HutoolCronTaskRepository cronTaskRepository(HutoolCronTaskRegistrant cronTaskRegistrant) {
            return cronTaskRegistrant.getCronTaskRepository();
        }

        @Bean
        public CronTaskRegisterPostProcessor cronTaskRegisterPostProcessor(HutoolCronLifeStyle lifeStyle,
                                                                           HutoolCronTaskRegistrant cronTaskRegistrant) {
            CronTaskRegisterPostProcessor postProcessor
                    = new CronTaskRegisterPostProcessor(lifeStyle, cronTaskRegistrant);
            postProcessor.setMetadata(metadata);
            return postProcessor;
        }
    }

    /*** Quartz cron auto configuration */
    @ConditionalOnClass({QuartzCronLifeStyle.class, QuartzCronTaskRepository.class})
    @Configuration(proxyBeanMethods = false)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static class QuartzCronTaskAutoConfiguration implements EnvironmentAware {

        private final Properties properties = new Properties();

        @Override
        public void setEnvironment(@NotNull Environment environment) {
            properties.putAll(environment.getProperty("spring.quartz.properties",
                    HashMap.class, new HashMap<String, String>()));
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public QuartzJobFactory quartzJobFactory() {
            return new QuartzJobFactory();
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public QuartzCronTaskRegistrant cronTaskRegistrant(QuartzJobFactory jobFactory) {
            QuartzCronTaskRepository cronTaskRepository = new QuartzCronTaskRepository(properties, jobFactory);
            return new QuartzCronTaskRegistrant(cronTaskRepository);
        }

        @Bean(destroyMethod = "stop")
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public QuartzCronLifeStyle quartzCronLifeStyle(QuartzCronTaskRegistrant cronTaskRegistrant) {
            return new QuartzCronLifeStyle(cronTaskRegistrant
                    .<QuartzCronTaskRepository>getCronTaskRepository().getScheduler());
        }

        @Bean
        public QuartzCronTaskRepository quartzCronTaskRepository(QuartzCronTaskRegistrant cronTaskRegistrant) {
            return cronTaskRegistrant.getCronTaskRepository();
        }

        @Bean
        public CronTaskRegisterPostProcessor cronTaskRegisterPostProcessor(QuartzCronLifeStyle lifeStyle,
                                                                           QuartzCronTaskRegistrant cronTaskRegistrant) {
            return new CronTaskRegisterPostProcessor(lifeStyle, cronTaskRegistrant);
        }
    }

    /*** Cron4j cron auto configuration */
    @ConditionalOnClass({Cron4jCronLifeStyle.class, Cron4jCronTaskRegistrant.class})
    @Configuration(proxyBeanMethods = false)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public static class Cron4jCronTaskAutoConfiguration implements EnvironmentAware {

        private final Map<String, Object> metadata = new LinkedHashMap<>();

        @Override
        public void setEnvironment(@NotNull Environment environment) {
            metadata.put("daemon", environment.getProperty("spring.cron4j.cron.daemon",
                    boolean.class, true));
            metadata.put("timezone", environment.getProperty("spring.cron4j.cron.zone",
                    String.class, "GMT+8"));
        }

        @Bean(destroyMethod = "stop")
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public Cron4jCronLifeStyle cron4jCronLifeStyle() {
            return new Cron4jCronLifeStyle();
        }

        @Bean
        @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
        public Cron4jCronTaskRegistrant cron4jCronTaskRegistrant(Cron4jCronLifeStyle cron4jCronLifeStyle) {
            Cron4jCronTaskRepository cron4jCronTaskRepository =
                    new Cron4jCronTaskRepository(cron4jCronLifeStyle.getScheduler());
            return new Cron4jCronTaskRegistrant(cron4jCronTaskRepository);
        }

        @Bean
        public Cron4jCronTaskRepository cron4jCronTaskRepository(Cron4jCronTaskRegistrant cronTaskRegistrant) {
            return cronTaskRegistrant.getCronTaskRepository();
        }

        @Bean
        public CronTaskRegisterPostProcessor cronTaskRegisterPostProcessor(Cron4jCronLifeStyle lifeStyle,
                                                                           Cron4jCronTaskRegistrant cronTaskRegistrant) {
            CronTaskRegisterPostProcessor postProcessor
                    = new CronTaskRegisterPostProcessor(lifeStyle, cronTaskRegistrant);
            postProcessor.setMetadata(metadata);
            return postProcessor;
        }
    }
}
