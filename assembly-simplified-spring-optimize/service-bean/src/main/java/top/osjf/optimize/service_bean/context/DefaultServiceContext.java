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
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import top.osjf.optimize.service_bean.ServiceContextUtils;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

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
 * <p>This type utilizes the utility class {@link ServiceContextUtils} to assist
 * in tasks such as service name resolution and alias generation, in order to
 * achieve the goal of heterogeneous naming.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class DefaultServiceContext extends AbstractServiceContext {

    private static final Logger logger = LoggerFactory.getLogger(ServiceContext.class);

    @Override
    public <S> S getService(String serviceName, Class<S> requiredType) throws NoAvailableServiceException {
        Objects.requireNonNull(serviceName, "serviceName = null");
        Objects.requireNonNull(requiredType, "requiredType = null");

        ApplicationContext applicationContext = getApplicationContext();
        S service = null;
        for (String candidateServiceName :
                ServiceContextUtils.getCandidateServiceNames(serviceName, requiredType, applicationContext.getId())) {
            if (applicationContext.containsBean(candidateServiceName)) {
                service = applicationContext.getBean(candidateServiceName, requiredType);
                break;
            }
        }
        if (service == null) {
            throw new NoAvailableServiceException(serviceName, requiredType);
        }
        return service;
    }

    @Override
    public <S> boolean addService(@Nullable String serviceName, Class<S> serviceType) {

        Objects.requireNonNull(serviceType, "ServiceType no be null");

        if (serviceType.isInterface()) {
            throw new IllegalStateException("Specified class is an interface");
        }

        List<Class<?>> filterServices = ServiceContextUtils.getFilterServices(serviceType);
        /*
         * When adding a service entity, it is necessary to ensure that its parent class or
         *  implementation interface has a specific identifier for the service to obtain annotations.
         * You can view the process of redefining names : top.osjf.optimize.service_bean.context
         * .AbstractServiceContext.ServiceContextBeanNameGenerator#generateBeanName
         * */
        if (CollectionUtils.isEmpty(filterServices)) {
            if (logger.isWarnEnabled()) {
                logger.warn("No annotation {} was found on the related parent class or interface of type {}.",
                        serviceType.getName(), ServiceCollection.class.getName());
            }
            return false;
        }

        ApplicationContext applicationContext = getApplicationContext();
        String applicationId = applicationContext.getId();

        //Get the format prefix of the alias encoding.
        List<String> classAlisa = ServiceContextUtils.analyzeClassAlias(serviceType, true);

        //Prioritize formatting based on the provided service name, followed by
        // limiting the name based on the type.
        String encodeSuffix = StringUtils.isNotBlank(serviceName) ? serviceName : serviceType.getName();

        //Collection list of aliases.
        List<String> beanAlisaNames = new ArrayList<>();

        //Directly implemented interfaces or parent classes are preferred for
        // formatting bean main names.
        Class<?> ms = filterServices.get(0);

        String beanName = ServiceContextUtils.formatId(ms, encodeSuffix, applicationId);

        //All parent classes or interfaces carrying specific tags need to be encoded
        // and formatted according to specific alias rules.
        for (String ca : classAlisa) {
            beanAlisaNames.add(ServiceContextUtils.formatAlisa(ms, ca, applicationId));
        }

        filterServices.remove(0);
        if (!CollectionUtils.isEmpty(filterServices)) {
            for (Class<?> filterService : filterServices) {
                beanAlisaNames.add(ServiceContextUtils.formatAlisa(filterService, encodeSuffix, applicationId));
                for (String ca : classAlisa) {
                    beanAlisaNames.add(ServiceContextUtils.formatAlisa(filterService, ca, applicationId));
                }
            }
        }

        //Because beans that can be recognized by the Spring container will already be automatically
        // added to the collection column, dynamic service scope bean creation is required here.
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(serviceType)
                .setScope(ServiceContext.SUPPORT_SCOPE);
        BeanDefinitionReaderUtils.registerBeanDefinition
                (new BeanDefinitionHolder(builder.getBeanDefinition(), beanName,
                        beanAlisaNames.toArray(new String[]{})), (BeanDefinitionRegistry) applicationContext);

        //After registration, activate the bean and initialize it.
        applicationContext.getBean(beanName, serviceType);

        if (logger.isInfoEnabled()) {
            logger.info("Created a dynamic bean for name {} and type {}.", beanName, serviceType.getName());
        }
        return true;
    }

    @Override
    public <S> boolean containsService(String serviceName, Class<S> requiredType) {
        Objects.requireNonNull(serviceName, "serviceName = null");
        Objects.requireNonNull(requiredType, "requiredType = null");

        ApplicationContext applicationContext = getApplicationContext();
        return ServiceContextUtils.getCandidateServiceNames(serviceName, requiredType, applicationContext.getId())
                .stream()
                .anyMatch(applicationContext::containsBean);
    }

    @Override
    public <S> boolean removeService(String serviceName, Class<S> requiredType) {
        Objects.requireNonNull(serviceName, "serviceName = null");
        Objects.requireNonNull(requiredType, "requiredType = null");

        ConfigurableApplicationContext applicationContext =
                (ConfigurableApplicationContext) getApplicationContext();
        boolean removeResult = false;
        for (String name
                : ServiceContextUtils.getCandidateServiceNames(serviceName, requiredType, applicationContext.getId())) {
            if (applicationContext.containsBean(name)) {
                applicationContext.getBeanFactory().destroyScopedBean(name);
                removeResult = true;
                break;
            }
        }
        return removeResult;
    }
}
