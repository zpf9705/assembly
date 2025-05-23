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
//package top.osjf.cron.spring.datasource.driven.scheduled;
//
//import com.baomidou.mybatisplus.extension.service.IService;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//import top.osjf.cron.core.repository.CronTaskRepository;
//import top.osjf.cron.core.util.ReflectUtils;
//import top.osjf.cron.datasource.driven.scheduled.mp.DatabaseTaskElement;
//
//import javax.annotation.Nonnull;
//
///**
// * Extended from {@link SpringMybatisPlusDatasourceDrivenScheduled}, the enhanced controller
// * adds real-time task triggering capability based on HTTP requests, providing a channel for
// * manual execution {@link #run()} to check task updates when the main task automatic scheduling
// * is not executed.
// *
// * <p>This class uses the Spring MVC request mapping mechanism to expose a POST interface
// * ({@value #RUNNING_MAPPING_PATH}), allowing external systems or administrators to immediately
// * trigger task execution when needed, ensuring that task information can be updated in a timely
// * manner. Used for mixed scheduling scenarios that require support for both automatic scheduled
// * and real-time manual triggering.
// *
// * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
// * @since 1.0.4
// */
//public class SpringHandlerMappingMybatisPlusDatasourceDrivenScheduled
//        extends SpringMybatisPlusDatasourceDrivenScheduled implements WebMvcConfigurer {
//
//    /**
//     * External exposed HTTP trigger endpoint path
//     */
//    public static final String RUNNING_MAPPING_PATH = "/datasource/driven/scheduled/run";
//
//    /**
//     * Constructs a new {@code ViewedSpringMybatisPlusDatasourceDrivenScheduled} with {@code CronTaskRepository}
//     * as its task Manager and {@code IService<DatabaseTaskElement>} as its task information storage.
//     *
//     * @param cronTaskRepository the Task management resource explorer.
//     * @param taskElementService the task information storage service.
//     */
//    public SpringHandlerMappingMybatisPlusDatasourceDrivenScheduled(CronTaskRepository cronTaskRepository,
//                                                                    IService<DatabaseTaskElement> taskElementService) {
//        super(cronTaskRepository, taskElementService);
//    }
//
//    @Override
//    public void onApplicationEvent(@Nonnull ContextRefreshedEvent event) {
//        super.onApplicationEvent(event);
//
//        registerMapping(event.getApplicationContext());
//    }
//
//    private void registerMapping(ApplicationContext applicationContext) {
//
//        RequestMappingHandlerMapping handlerMapping
//                = applicationContext.getBean(RequestMappingHandlerMapping.class);
//
//        handlerMapping.registerMapping(RequestMappingInfo.paths(RUNNING_MAPPING_PATH)
//                        .methods(RequestMethod.POST)
//                        .params()
//                        .build(), this,
//                ReflectUtils.getMethod(this.getClass(), "run"));
//    }
//
//    /**
//     * @see top.osjf.cron.spring.auth.WebRequestAuthenticationInterceptor
//     */
//    @Override
//    @ResponseBody
//    public void run() {
//        super.run();
//    }
//}
