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

package top.osjf.spring.service.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import top.osjf.spring.service.ServiceContextUtils;
import top.osjf.spring.service.context.AbstractServiceContext;
import top.osjf.spring.service.context.RecordServiceContext;
import top.osjf.spring.service.context.ServiceContext;
import top.osjf.spring.service.context.ServiceContextAwareBeanPostProcessor;

/**
 * The import configuration of {@link EnableServiceCollection} annotations, based on the storage name of
 * {@link AbstractServiceContext.ServiceContextBeanNameGenerator},
 * manages the storage of beans after refreshing the context of the spring container.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ServiceContextRecordConfiguration {

    @Bean(ServiceContextUtils.RECORD_BEAN_NAME)
    public RecordServiceContext recordServiceContext() {
        return new RecordServiceContext();
    }

    //—————————————————————————— mv form SimplifiedAutoConfiguration
    @Bean(ServiceContextUtils.SC_AWARE_BPP_NANE)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ServiceContextAwareBeanPostProcessor serviceContextAwareBeanPostProcessor(
            @Lazy //Here, lazy loading is used to prevent dependent beans from losing the function of AOP weaving.
            ServiceContext serviceContext) {
        return new ServiceContextAwareBeanPostProcessor(serviceContext);
    }

    /**
     * Register a {@link RecordServiceContext.ChangeScopePostProcessor} to support scope modification.
     */
    public static class ServiceContextRecordImportConfiguration implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                            @NonNull BeanDefinitionRegistry registry,
                                            @NonNull BeanNameGenerator importBeanNameGenerator) {
            BeanDefinition beanDefinition =
                    BeanDefinitionBuilder.genericBeanDefinition(RecordServiceContext.ChangeScopePostProcessor.class)
                            .getBeanDefinition();
            registry.registerBeanDefinition(importBeanNameGenerator.generateBeanName(beanDefinition, registry),
                    beanDefinition);
        }
    }
}
