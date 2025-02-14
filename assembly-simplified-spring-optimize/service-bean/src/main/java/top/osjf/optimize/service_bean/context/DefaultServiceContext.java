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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * {@code DefaultServiceContext} is the default service context implementation.
 *
 * <p>This class is a specific implementation of {@link AbstractServiceContext},
 * which provides functions for service registration, retrieval, existence
 * checking, and removal. It utilizes Spring's {@code ApplicationContext} to
 * manage and operate service beans, supporting dynamic addition and removal
 * of services.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class DefaultServiceContext extends AbstractServiceContext {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * {@inheritDoc}
     *
     * @param name {@inheritDoc}
     * @param <S>  {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NoAvailableServiceException {@inheritDoc}
     * @throws BeansException              if the bean could not be obtained
     * @throws ClassCastException          If the bean obtained by name is not the
     *                                     required generic type.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S> S getService(String name) throws NoAvailableServiceException {
        ApplicationContext applicationContext = unwrapContext().getApplicationContext();
        if (!ServiceDefinitionUtils.isEnhancementServiceName(name)
                || !applicationContext.containsBean(name)) {
            throw new NoAvailableServiceException(name);
        }
        return (S) applicationContext.getBean(name);
    }

    /**
     * {@inheritDoc}
     *
     * @param name         {@inheritDoc}
     * @param requiredType {@inheritDoc}
     * @param <S>          {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NoAvailableServiceException    {@inheritDoc}
     * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
     * @throws BeansException                 if the bean could not be created
     */
    @Override
    public <S> S getService(String name, Class<S> requiredType) throws NoAvailableServiceException {
        ApplicationContext applicationContext = unwrapContext().getApplicationContext();
        S service = null;
        String enhancementName = ServiceDefinitionUtils.getEnhancementName(name, requiredType, isRecordType(requiredType));
        if (applicationContext.containsBean(enhancementName)) {
            service = applicationContext.getBean(enhancementName, requiredType);
        }
        if (service == null) {
            throw new NoAvailableServiceException(name, requiredType);
        }
        return service;
    }

    /**
     * {@inheritDoc}
     *
     * @param name        {@inheritDoc}
     * @param serviceType {@inheritDoc}
     * @param <S>         {@inheritDoc}
     * @return {@inheritDoc}
     * @throws BeanDefinitionStoreException if registration failed.
     */
    @Override
    public <S> boolean addService(@Nullable String name, Class<S> serviceType) {

        Objects.requireNonNull(serviceType, "ServiceType no be null");

        List<Class<?>> targetServiceTypes = ServiceDefinitionUtils.getTargetServiceTypes(serviceType);

        if (CollectionUtils.isEmpty(targetServiceTypes)) {
            if (logger.isWarnEnabled()) {
                logger.warn("No annotation {} was found on the related parent class or interface of type {}.",
                        ServiceCollection.class.getName(), serviceType.getName());
            }
            return false;
        }

        //The service name provides priority adoption, but does not provide the name after
        // the first lowercase of the abbreviation according to JavaBean specifications.
        name = StringUtils.isNotBlank(name) ? name : Introspector.decapitalize(serviceType.getSimpleName());

        String beanName = ServiceDefinitionUtils.enhancementBeanName(serviceType, name);

        List<String> alisaNames = new ArrayList<>();
        for (Class<?> targetServiceType : targetServiceTypes) {
            alisaNames.add(ServiceDefinitionUtils.enhancementAlisaName(targetServiceType, beanName));
        }

        //Because beans that can be recognized by the Spring container will already be automatically
        // added to the collection column, dynamic service scope bean creation is required here.
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(serviceType)
                .setScope(ServiceContext.SUPPORT_SCOPE);
        BeanDefinitionReaderUtils.registerBeanDefinition
                (new BeanDefinitionHolder(builder.getBeanDefinition(), beanName,
                        alisaNames.toArray(new String[]{})), unwrapContext().getBeanDefinitionRegistry());

        //After registration, activate the bean and initialize it.
        unwrapContext().getApplicationContext().getBean(beanName, serviceType);

        if (logger.isInfoEnabled()) {
            logger.info("Created a dynamic bean for name {} and type {}.", beanName, serviceType.getName());
        }
        return true;
    }

    @Override
    public boolean containsService(String name) {
        return ServiceDefinitionUtils.isEnhancementServiceName(name) &&
                unwrapContext().getApplicationContext().containsBean(name);
    }

    @Override
    public <S> boolean containsService(String name, Class<S> requiredType) {
        String enhancementName = ServiceDefinitionUtils.getEnhancementName(name, requiredType, isRecordType(requiredType));
        return unwrapContext().getApplicationContext().containsBean(enhancementName);
    }

    /**
     * {@inheritDoc}
     *
     * @param serviceName {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if beanName does not correspond to an object in a mutable scope.
     * @throws IllegalStateException    if no Scope SPI registered for certain scope name.
     */
    @Override
    public boolean removeService(String serviceName) {
        ConfigurableApplicationContext applicationContext = unwrapContext().getConfigurableApplicationContext();
        if (!ServiceDefinitionUtils.isEnhancementBeanServiceName(serviceName)
                || !applicationContext.containsBean(serviceName)) {
            return false;
        }
        applicationContext
                .getBeanFactory()
                .destroyScopedBean(serviceName);
        getServiceTypeRegistry().removeServiceType(serviceName);
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @param serviceName  {@inheritDoc}
     * @param requiredType {@inheritDoc}
     * @param <S>          {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalArgumentException if beanName does not correspond to an object in a mutable scope.
     * @throws IllegalStateException    if no Scope SPI registered for certain scope name.
     */
    @Override
    public <S> boolean removeService(String serviceName, Class<S> requiredType) {
        String enhancementBeanName = ServiceDefinitionUtils.getEnhancementBeanName(serviceName, requiredType,
                isRecordType(requiredType));
        ConfigurableApplicationContext applicationContext = unwrapContext().getConfigurableApplicationContext();
        if (StringUtils.isBlank(enhancementBeanName) || !applicationContext.containsBean(enhancementBeanName)) {
            return false;
        }
        applicationContext
                .getBeanFactory()
                .destroyScopedBean(enhancementBeanName);
        getServiceTypeRegistry().removeServiceType(enhancementBeanName);
        return true;
    }
}
