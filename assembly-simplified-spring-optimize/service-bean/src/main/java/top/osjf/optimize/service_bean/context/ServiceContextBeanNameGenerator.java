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
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import top.osjf.optimize.service_bean.ServiceContextUtils;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
 * the scope definition of {@link #supportScopes} during the naming
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
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ServiceContextBeanNameGenerator extends AnnotationBeanNameGenerator {
    /**
     * Spring context id.
     */
    private final String applicationId;

    /**
     * Record the main name collection of beans named by this custom naming
     * {@code BeanNameGenerator}.
     */
    static final List<String> RECORD_BEAN_NAMES = new CopyOnWriteArrayList<>();

    /**
     * The scope name required for beans that accept custom naming.
     */
    private final List<String> supportScopes = Stream.of(BeanDefinition.SCOPE_SINGLETON,
            BeanDefinition.SCOPE_PROTOTYPE, AbstractBeanDefinition.SCOPE_DEFAULT).collect(Collectors.toList());

    /**
     * Create a new {@code ServiceContextBeanNameGenerator} with a spring application id.
     *
     * @param applicationId spring context id.
     */
    public ServiceContextBeanNameGenerator(String applicationId) {
        this.applicationId = applicationId;
    }

    @Override
    @NonNull
    public String generateBeanName(@NonNull BeanDefinition definition, @NonNull BeanDefinitionRegistry registry) {

        String beanName;

        //Service collection only accepts default singletons
        String scope = definition.getScope();
        if (!supportScopes.contains(scope)) {
            beanName = super.generateBeanName(definition, registry);

        } else {

            //search for the class object based on the class name.
            String beanClassName = definition.getBeanClassName();
            Class<?> clazz = ServiceContextUtils.getClass(beanClassName);

            //Unknown class object, not processed.
            if (clazz == null) {
                beanName = super.generateBeanName(definition, registry);

            } else {

                //Single instance beans perform service collection operations.
                List<Class<?>> filterServices = ServiceContextUtils.getFilterServices(clazz);

                //If no collection flag is found, the default bean name definition rule will be used.
                if (CollectionUtils.isEmpty(filterServices)) {
                    beanName = super.generateBeanName(definition, registry);

                } else {

                    //Service collection name definition rules.
                    String value;
                    List<String> classAlisa;
                    if (definition instanceof AnnotatedBeanDefinition) {
                        //Get the name of the Spring build annotation.
                        value = determineBeanNameFromAnnotation((AnnotatedBeanDefinition) definition);
                        if (StringUtils.isBlank(value)) {
                            value = clazz.getName();
                            classAlisa = ServiceContextUtils.analyzeClassAlias(clazz, true);
                        } else {
                            classAlisa = ServiceContextUtils.analyzeClassAlias(clazz, false);
                        }
                    } else {
                        value = clazz.getName();
                        classAlisa = ServiceContextUtils.analyzeClassAlias(clazz, true);
                    }

                    Class<?> ms = filterServices.get(0);

                    //Format the main bean name according to the rules first.
                    beanName = ServiceContextUtils.formatId(ms, value, applicationId);

                    //Cache the name of the main bean.
                    RECORD_BEAN_NAMES.add(beanName);

                    //The alias constructed by the first superior class object.
                    classAlisa.forEach(alisa -> registry.registerAlias(beanName,
                            ServiceContextUtils.formatAlisa(ms, alisa, applicationId)));

                    //Remove the first level class that has been built.
                    filterServices.remove(0);


                    if (!CollectionUtils.isEmpty(filterServices)) {

                        //Build an alias for the parent class.
                        for (Class<?> filterService : filterServices) {
                            registry.registerAlias(beanName, ServiceContextUtils.formatAlisa(filterService,
                                    value, applicationId));
                            classAlisa.forEach(alias0 -> registry.registerAlias(beanName,
                                    ServiceContextUtils.formatAlisa(filterService, alias0, applicationId)));
                        }
                    }
                }
            }
        }
        return beanName;
    }
}
