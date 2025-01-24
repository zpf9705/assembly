///*
// * Copyright 2024-? the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//
//package top.osjf.cron.spring.scheduler;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.scheduling.config.ScheduledTaskRegistrar;
//
///**
// * {@code @Configuration} class that registers a {@link SchedulingRepository}
// * bean and {@link ScheduledTaskRegistrar} capable of register Spring's
// * {@link Scheduled} and {@link top.osjf.cron.spring.annotation.Cron} annotation
// * corresponding execution tasks.
// *
// * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
// * @since 1.0.3
// * @see EnableScheduling
// */
//@Configuration(proxyBeanMethods = false)
//public class SchedulingRepositoryConfiguration {
//
//    @Bean
//    public SchedulingRepository schedulingRepository() {
//        return new SchedulingRepository();
//    }
//
//    @Bean
//    public ScheduledTaskRegistrar scheduledTaskRegistrar(SchedulingRepository schedulingRepository){
//        return schedulingRepository.getScheduledTaskRegistrar();
//    }
//}
