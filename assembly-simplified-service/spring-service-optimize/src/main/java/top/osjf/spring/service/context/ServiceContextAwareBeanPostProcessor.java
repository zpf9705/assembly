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

package top.osjf.spring.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * To implement {@link ServiceContextAware}'s bean setting {@link ServiceContext},
 * the loading time should be after {@link ServiceContext} is loaded.
 *
 * <p>Using this type of configuration caused me to encounter
 * dependency beans losing their AOP weaving function. The reason is that the
 * implementation of AOP function is also dependent on {@link BeanPostProcessor}.
 * The configuration in this class caused the dependency {@link ServiceContext} to
 * be instantiated earlier, resulting in the loss of AOP function implementation.
 * Here, I solved this problem using lazy loading {@link org.springframework.context.annotation.Lazy}.
 * If you also encounter this problem, you can refer to <a href="https://learn.skyofit.com/archives/2020">
 * Spring - BeanPostProcessor Unable to Use AOP - Reason/Solution</a>.
 *
 * @see org.springframework.context.annotation.Lazy
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ServiceContextAwareBeanPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor, Ordered {

    /*** The service context that has already been loaded.*/
    private final ServiceContext serviceContext;

    /**
     * The construction method of carrying service context.
     *
     * @param serviceContext carrying service context
     */
    public ServiceContextAwareBeanPostProcessor(ServiceContext serviceContext) {
        Objects.requireNonNull(serviceContext, "ServiceContext must not be null");
        this.serviceContext = serviceContext;
    }

    @Nullable
    @Override
    public Object postProcessBeforeInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        if (bean instanceof Aware) {
            if (bean instanceof ServiceContextAware) {
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
