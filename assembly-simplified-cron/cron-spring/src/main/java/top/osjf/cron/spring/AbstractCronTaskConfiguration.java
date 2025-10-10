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


package top.osjf.cron.spring;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.core.repository.CronTaskRepository;
import top.osjf.cron.core.util.CollectionUtils;
import top.osjf.cron.spring.auth.AuthenticationPredicate;
import top.osjf.cron.spring.auth.WebRequestAuthenticationInterceptor;
import top.osjf.cron.spring.datasource.driven.scheduled.SpringHandlerMappingDatasourceDrivenScheduled;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Abstract {@link Configuration configuration} class for task registration framework.
 * <p>
 * Provide {@link SuperiorProperties} instance conversion for switch annotation properties,
 * as well as configuration for accessing task metadata in restful format and access
 * authentication mechanisms.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.0
 */
@Configuration(proxyBeanMethods = false)
public abstract class AbstractCronTaskConfiguration implements ImportAware {

    /**
     * Store the relevant attributes extracted from {@link AnnotationMetadata} that
     * provide annotation types.
     */
    @Nullable
    private SuperiorProperties superiorProperties;

    @Override
    public void setImportMetadata(@NotNull AnnotationMetadata importMetadata) {
        Class<? extends Annotation> annotationType = enableImportAnnotationType();
        if (annotationType == null) {
            return;
        }
        if (importMetadata.hasMetaAnnotation(annotationType.getName())) {
            superiorProperties = SuperiorProperties.of(importMetadata.getAnnotationAttributes
                    (annotationType.getCanonicalName()));
        }
    }

    /**
     * Return a {@link SuperiorProperties} object compiled from the specified annotation
     * attributes extracted from {@code AnnotationMetadata}.
     * @return The {@code SuperiorProperties} object contains properties extracted from
     * annotations.
     */
    @Nullable
    protected SuperiorProperties getImportAnnotationSuperiorProperties() {
        return superiorProperties;
    }

    /**
     * Returns the annotation type that enables the import.
     * @return the annotation type that enables the import.
     */
    @Nullable
    protected Class<? extends Annotation> enableImportAnnotationType() {
        return null;
    }

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
