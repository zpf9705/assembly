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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

import java.util.List;

/**
 * The purpose of this class is to solve the problem of duplicate names
 * for beans of the same type. After setting the naming rules for this
 * class, the same bean name can be set for beans of different parent
 * classes or interfaces (in fact, the naming rules have been re encoded
 * twice).
 *
 * <p>This class is a custom {@link BeanNameGenerator} that inherits
 * {@link AnnotationBeanNameGenerator} and is compatible with its
 * functionality when needed.
 *
 * <p>This class will search for {@link BeanDefinition} that matches
 * the scope definition of {@link BeanDefinition#SCOPE_SINGLETON} or
 * {@link AbstractBeanDefinition#SCOPE_SINGLETON} during the naming
 * process, and obtain its {@link BeanDefinition#getBeanClassName()}
 * type. It will find {@link ServiceCollection} related tag annotations
 * from its parent class and interface, and use the relevant information
 * of its annotated parent class and interface to re encode the name.
 *
 * <p>In the naming process, if there is a discrepancy, the naming rules
 * of {@link AnnotationBeanNameGenerator} are uniformly called, which also
 * ensures the standard naming of beans related to the parent class and
 * interface without annotation {@link ServiceCollection}.
 *
 * <p>
 * <strong>Note:</strong>
 * <p>Service collection is limited to beans that participate in this bean name
 * generator {@link ServiceContextBeanNameGenerator}, which means it only applies
 * to internal beans.Beans imported using {@link Bean} annotation in the configuration
 * class cannot participate in this {@link ServiceContextBeanNameGenerator}, so they
 * cannot be collected in {@link ServiceTypeRegistry} and cannot participate in
 * renaming encoding.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ServiceContextBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    @NonNull
    public String generateBeanName(@NonNull BeanDefinition definition, @NonNull BeanDefinitionRegistry registry) {

        String beanName;

        //Service collection only accepts default or singletons
        String scope = definition.getScope();
        if (!BeanDefinition.SCOPE_SINGLETON.equals(scope) && !AbstractBeanDefinition.SCOPE_DEFAULT.equals(scope)) {
            beanName = super.generateBeanName(definition, registry);

        } else {

            //search for the class object based on the class name.
            String beanClassName = definition.getBeanClassName();
            Class<?> clazz = ServiceDefinitionUtils.getSafeClass(beanClassName);

            //Unknown class object, not processed.
            if (clazz == null) {
                beanName = super.generateBeanName(definition, registry);

            } else {

                //single instance beans perform service collection operations.
                List<Class<?>> targetServiceTypes = ServiceDefinitionUtils.getTargetServiceTypes(clazz);

                //if no collection flag is found, the default bean name definition rule will be used.
                if (CollectionUtils.isEmpty(targetServiceTypes)) {
                    beanName = super.generateBeanName(definition, registry);

                } else {

                    String definitionBeanName;
                    if (definition instanceof AnnotatedBeanDefinition) {
                        //get the name of the Spring build annotation.
                        definitionBeanName = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
                        if (StringUtils.isBlank(definitionBeanName)) {
                            definitionBeanName = buildDefaultBeanName(definition);
                        }
                    } else {
                        definitionBeanName = buildDefaultBeanName(definition);
                    }

                    //enhancement name for self clazz
                    beanName = ServiceDefinitionUtils.enhancementBeanName(clazz, definitionBeanName);
                    getServiceTypeRegistry(registry).registerServiceType(beanName, clazz);

                    //enhancement alisa name for target clazz to this bean.
                    for (Class<?> targetServiceType : targetServiceTypes) {
                        String alisaName = ServiceDefinitionUtils.enhancementAlisaName(targetServiceType, definitionBeanName);
                        registry.registerAlias(beanName, alisaName);
                    }
                }
            }
        }
        return beanName;
    }

    private ServiceTypeRegistry getServiceTypeRegistry(BeanDefinitionRegistry registry) {
        if (registry instanceof SingletonBeanRegistry) {
            SingletonBeanRegistry singletonBeanRegistry = (SingletonBeanRegistry) registry;
            if (!singletonBeanRegistry
                    .containsSingleton(ServiceDefinitionUtils.INTERNAL_SERVICE_TYPE_REGISTER_BEAN_NAME)) {
                singletonBeanRegistry
                        .registerSingleton(ServiceDefinitionUtils.INTERNAL_SERVICE_TYPE_REGISTER_BEAN_NAME,
                                new ServiceTypeRegistry());

            }
            return (ServiceTypeRegistry) singletonBeanRegistry
                    .getSingleton(ServiceDefinitionUtils.INTERNAL_SERVICE_TYPE_REGISTER_BEAN_NAME);
        } else if (registry instanceof BeanFactory) {
            BeanFactory beanFactory = (BeanFactory) registry;
            if (!beanFactory.containsBean(ServiceDefinitionUtils.INTERNAL_SERVICE_TYPE_REGISTER_BEAN_NAME)) {
                registry.registerBeanDefinition(ServiceDefinitionUtils.INTERNAL_SERVICE_TYPE_REGISTER_BEAN_NAME,
                        BeanDefinitionBuilder.genericBeanDefinition(ServiceTypeRegistry.class).getBeanDefinition());
            }
            return beanFactory
                    .getBean(ServiceDefinitionUtils.INTERNAL_SERVICE_TYPE_REGISTER_BEAN_NAME, ServiceTypeRegistry.class);
        }
        throw new IllegalStateException(registry.getClass() + " not a SingletonBeanRegistry or BeanFactory");
    }
}
