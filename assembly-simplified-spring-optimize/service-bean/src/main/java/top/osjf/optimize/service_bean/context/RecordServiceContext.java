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
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import top.osjf.optimize.service_bean.ServiceContextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Using the service registration record in
 * {@link AbstractServiceContext.ServiceContextBeanNameGenerator#getRecordBeanNames()},
 * when the current class is loaded, retrieve the service registration record,
 * retrieve the corresponding bean object and alias information from the Spring
 * container, and register it in the container collected by the service.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class RecordServiceContext extends AbstractServiceContext implements InitializingBean {

    /**
     * Bottom line handling of beans that have not been changed scope.
     */
    @Override
    public void afterPropertiesSet() {
        for (String recordBeanName : getRecordBeanNames()) {
            Object bean = getApplicationContext().getBean(recordBeanName);
            getContextMap().putIfAbsent(recordBeanName, bean);
            for (String aliasName : getBeanDefinitionRegistry().getAliases(recordBeanName)) {
                getContextMap().putIfAbsent(aliasName, bean);
            }
        }
    }

    /**
     * Rewrite the service retrieval method to point to the beans saved in the specified
     * scope, whether to use the method of the parent class to retrieve beans that have
     * not changed the scope.
     *
     * @see ServiceScope
     */
    @Override
    public <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException {
        ApplicationContext applicationContext = getApplicationContext();
        for (String name :
                ServiceContextUtils.getCandidateServiceNames(serviceName, requiredType, applicationContext.getId())) {
            try {
                return applicationContext.getBean(name, requiredType);
            } catch (BeansException e) {
                if (!(e instanceof NoSuchBeanDefinitionException)) throw new RuntimeException(e);
            } catch (Throwable e) {
                throw new IllegalStateException("Service acquisition failed", e);
            }
        }
        return super.getService(serviceName, requiredType);
    }

    @Override
    protected <S> BeanDefinitionBuilder getBeanDefinitionBuilder(String beanName, List<String> beanAlisaNames,
                                                                 Class<S> requiredType) {
        return super.getBeanDefinitionBuilder(beanName, beanAlisaNames, requiredType)
                .setScope(ServiceContextUtils.SERVICE_SCOPE);
    }

    @Override
    public <S> boolean containsService(String serviceName, Class<S> requiredType) {
        ApplicationContext applicationContext = getApplicationContext();
        boolean containsResult
                = ServiceContextUtils.getCandidateServiceNames(serviceName, requiredType, applicationContext.getId())
                .stream()
                .anyMatch(applicationContext::containsBean);
        if (!containsResult) {
            containsResult = super.containsService(serviceName, requiredType);
        }
        return containsResult;
    }

    @Override
    public <S> boolean removeService(String serviceName, Class<S> requiredType) {
        ApplicationContext applicationContext = getApplicationContext();
        boolean removeResult = false;
        for (String name
                : ServiceContextUtils.getCandidateServiceNames(serviceName, requiredType, applicationContext.getId())) {
            if (applicationContext.containsBean(name)) {
                ((ConfigurableApplicationContext) applicationContext).getBeanFactory().destroyScopedBean(name);
                removeResult = true;
                break;
            }
        }
        if (!removeResult) {
            removeResult = super.removeService(serviceName, requiredType);
        }
        return removeResult;
    }

    @Override
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        clearRecordBeanNames();
    }

    /**
     * Support modification of scope {@link ServiceContextUtils#SERVICE_SCOPE}.
     */
    public static class ChangeScopePostProcessor implements BeanDefinitionRegistryPostProcessor {
        @Override
        public void postProcessBeanDefinitionRegistry(@NonNull BeanDefinitionRegistry registry) throws BeansException {
            List<String> recordBeanNames = ServiceContextBeanNameGenerator.getRecordBeanNames();
            List<String> updateRecordBeanNames = new ArrayList<>();
            for (String recordBeanName : recordBeanNames) {
                BeanDefinition beanDefinition;
                try {
                    beanDefinition = registry.getBeanDefinition(recordBeanName);
                } catch (NoSuchBeanDefinitionException e) {
                    continue;
                }
                beanDefinition.setScope(ServiceContextUtils.SERVICE_SCOPE);
                updateRecordBeanNames.add(recordBeanName);
            }
            if (!CollectionUtils.isEmpty(updateRecordBeanNames)) {
                recordBeanNames.removeAll(updateRecordBeanNames);
            }
        }

        @Override
        public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory)
                throws BeansException {
            beanFactory.registerScope(ServiceContextUtils.SERVICE_SCOPE, new ServiceScope());
        }
    }
}
