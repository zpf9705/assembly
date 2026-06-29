/*
 * Copyright 2026-? the original author or authors.
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

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.osjf.commons.util.CollectionUtils;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.spring.CronTaskInfoReadableWebMvcHandlerController;
import top.osjf.cron.spring.CronTaskInfoView;
import top.osjf.cron.spring.auth.AuthenticationPredicate;
import top.osjf.cron.spring.auth.WebRequestAuthenticationInterceptor;
import top.osjf.cron.spring.datasource.driven.scheduled.SpringHandlerMappingDatasourceDrivenScheduled;

import java.util.List;

/**
 * {@link Configuration Configuration} for expose HTTP request interfaces
 * using MVC for cron tasks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({ RequestMappingHandlerMapping.class })
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.ANY)
@ConditionalOnBean({ RequestMappingHandlerMapping.class })
class CronWebMvcConfiguration {

    /**
     * Return the {@link CronTaskInfoView} readable controller, which is the HTTP access interface.
     * @param cronTaskRepository            the configured {@link CronTaskRepository}.
     * @param requestMappingHandlerMapping  the configured {@link RequestMappingHandlerMapping}.
     * @return the configured {@link CronTaskInfoView} readable controller.
     */
    @Bean
    public CronTaskInfoReadableWebMvcHandlerController cronTaskInfoReadableWebMvcHandlerController
    (CronTaskRepository cronTaskRepository,
     RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return new CronTaskInfoReadableWebMvcHandlerController(cronTaskRepository, requestMappingHandlerMapping);
    }

    /**
     * Return the authentication interceptor for accessing task scheduling information.
     * @param provider     the lazy loader of {@link AuthenticationPredicate}.
     * @param environment  the {@link Environment} instance.
     * @param providers    the {@link WebRequestAuthenticationInterceptor.AuthenticationProvider} instances.
     * @return the configured {@link CronTaskInfoView} readable controller.
     */
    @Bean
    public WebRequestAuthenticationInterceptor webRequestAuthenticationInterceptor
    (ObjectProvider<AuthenticationPredicate> provider, Environment environment,
     @Autowired(required = false) List<WebRequestAuthenticationInterceptor.AuthenticationProvider> providers) {
        WebRequestAuthenticationInterceptor authenticationInterceptor
                = new WebRequestAuthenticationInterceptor(provider, environment);

        // The default URL that requires registration and authentication.
        authenticationInterceptor.registerAuthenticationPath
                (CronTaskInfoReadableWebMvcHandlerController.REQUEST_MAPPING_PATH_OF_GET_CRON_TASK_LIST);
        if (environment.getProperty("spring.schedule.cron.scheduled-driven.enable", boolean.class, false)) {
            authenticationInterceptor.registerAuthenticationPath
                    (SpringHandlerMappingDatasourceDrivenScheduled.RUNNING_MAPPING_PATH);
        }

        // The URL provided externally that requires authentication.
        if (CollectionUtils.isNotEmpty(providers)) {
            for (WebRequestAuthenticationInterceptor.AuthenticationProvider authenticationProvider : providers) {
                for (String authenticationPaths : authenticationProvider.get()) {
                    authenticationInterceptor.registerAuthenticationPath(authenticationPaths);
                }
            }
        }

        return authenticationInterceptor;
    }
}
