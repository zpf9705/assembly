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


package top.osjf.optimize.service_bean.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.lang.NonNull;

/**
 * The {@code ServiceScopeBeanPostProcessor} class is a Spring custom component that
 * implements the {@code BeanDefinitionRegistryPostProcessor} interface and the early
 * stages of the Spring application context refresh process, optimize the beans recorded
 * in {@link ServiceContextBeanNameGenerator}.
 *
 * <p>In this specific implementation, {@code ServiceScopeBeanPostProcessor} primarily
 * performs two tasks:
 * <ul>
 * <li><strong>Modify the scope of a specific bean:</strong>
 * <p>
 * In the {@code postProcessBeanDefinitionRegistry} method, this class traverses a predefined
 * list of Bean names (provided by {@code ServiceContextBeanNameGenerator#RECORD_BEAN_NAME}),
 * and for each Bean name in the list, it will retrieve the corresponding {@link BeanDefinition}
 * from the {@link BeanDefinitionRegistry} and set its scope to {@link ServiceContext#SUPPORT_SCOPE}.
 * </li>
 * <li><strong>Register custom scope:</strong>
 * <p>
 * In the {@code postProcessBeanFactory} method, this class registers a  {@link Scope} scope
 * implementation class with BeanFactory and {@link ServiceScope} comes from the configuration
 * definition, named {@link ServiceScope}, which defines how to create, destroy, and manage the
 * lifecycle of beans under the {@link ServiceContext#SUPPORT_SCOPE} scope.
 * </li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ServiceScopeBeanPostProcessor implements BeanDefinitionRegistryPostProcessor {

    private ServiceScope serviceScope;

    private ServiceContextBeanNameGenerator serviceContextBeanNameGenerator;

    /**
     * Set a new nonNull {@code ServiceScope}.
     *
     * @param serviceScope service {@link Scope} instance.
     */
    public void setServiceScope(ServiceScope serviceScope) {
        this.serviceScope = serviceScope;
    }

    /**
     * Set a new nonNull {@code ServiceContextBeanNameGenerator}.
     *
     * <p>Retrieve the bean name that matches the record and make a scope
     * change at {@link #postProcessBeanDefinitionRegistry}.
     *
     * @param serviceContextBeanNameGenerator an internal {@link ServiceContextBeanNameGenerator} instance.
     */
    public void setServiceContextBeanNameGenerator(ServiceContextBeanNameGenerator serviceContextBeanNameGenerator) {
        this.serviceContextBeanNameGenerator = serviceContextBeanNameGenerator;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
        for (String recordBeanName : serviceContextBeanNameGenerator.getRecordBeanNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(recordBeanName);
            beanDefinition.setScope(ServiceContext.SUPPORT_SCOPE);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope(ServiceContext.SUPPORT_SCOPE, serviceScope);
    }
}
