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
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * The {@code ServiceContextAwareBeanPostProcessor} class is a custom Spring
 * Bean post processor that is primarily responsible for checking whether the
 * Bean implements the custom {@link ServiceContextAware} interface before
 * initializing the Bean in the Spring container. If so, it injects the current
 * {@link ServiceContext} instance.
 *
 * <p>The instance of {@code ServiceContext} is not created or injected through
 * initialization of this class, but is initialized when the implementation of
 * {@code ServiceContextAware} bean is recognized.
 *
 * <p>In addition, it also implements the {@code BeanFactoryPostProcessor}
 * interface to tell the Spring container to ignore the dependency injection
 * of the {@code ServiceContextAware} interface, as we will manually inject
 * it through programming.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ServiceContextAwareBeanPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor,
        ApplicationContextAware, Ordered {

    private ServiceContext serviceContext;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof Aware) {
            if (bean instanceof ServiceContextAware) {
                if (serviceContext == null) {
                    serviceContext = applicationContext.getBean(ServiceContext.class);
                }
                ((ServiceContextAware) bean).setServiceContext(serviceContext);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.ignoreDependencyInterface(ServiceContextAware.class);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 18;
    }
}
