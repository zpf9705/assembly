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
import org.springframework.context.annotation.Bean;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
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
 * <p>
 * <strong>Note:</strong>
 * <p>Service collection is limited to beans that participate in this bean name
 * generator {@link ServiceContextBeanNameGenerator}, which means it only applies
 * to internal beans.Beans imported using {@link Bean} annotation in the configuration
 * class cannot participate in this {@link ServiceContextBeanNameGenerator}, so they
 * cannot be collected in {@link #RECORD_BEAN_NAMES} or {@link #RECORD_BEAN_TYPES} and
 * cannot participate in renaming encoding.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ServiceContextBeanNameGenerator extends AnnotationBeanNameGenerator {
    /**
     * Record the main name collection of beans named by this custom naming
     * {@code BeanNameGenerator}.
     */
    private static final Set<String> RECORD_BEAN_NAMES = new CopyOnWriteArraySet<>();
    /**
     * Record the main name collection of beans type by this custom naming
     * {@code BeanNameGenerator}.
     */
    private static final Set<Class<?>> RECORD_BEAN_TYPES = new CopyOnWriteArraySet<>();

    /**
     * The scope name required for beans that accept custom naming.
     */
    private final List<String> supportScopes = Stream.of(BeanDefinition.SCOPE_SINGLETON,
            BeanDefinition.SCOPE_PROTOTYPE, AbstractBeanDefinition.SCOPE_DEFAULT).collect(Collectors.toList());

    /**
     * Return un modifiable set of record bean name.
     *
     * @return un modifiable set of record bean name.
     */
    protected static Set<String> getRecordBeanNames() {
        return Collections.unmodifiableSet(RECORD_BEAN_NAMES);
    }

    /**
     * Return un modifiable set of record bean type.
     *
     * @return un modifiable set of record bean type.
     */
    protected static Set<Class<?>> getRecordBeanTypes() {
        return Collections.unmodifiableSet(RECORD_BEAN_TYPES);
    }

    /**
     * Clear record bean names.
     */
    protected static void clear() {
        RECORD_BEAN_NAMES.clear();
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
            Class<?> clazz = ServiceCore.getSafeClass(beanClassName);

            //Unknown class object, not processed.
            if (clazz == null) {
                beanName = super.generateBeanName(definition, registry);

            } else {

                //single instance beans perform service collection operations.
                List<Class<?>> targetServiceTypes = ServiceCore.getTargetServiceTypes(clazz);

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
                    beanName = ServiceCore.enhancementBeanName(clazz, definitionBeanName);

                    //cache the name of the main bean.
                    RECORD_BEAN_NAMES.add(beanName);
                    //cache the type of the main bean.
                    RECORD_BEAN_TYPES.add(clazz);

                    //enhancement alisa name for target clazz to this bean.
                    for (Class<?> targetServiceType : targetServiceTypes) {
                        String alisaName = ServiceCore.enhancementAlisaName(targetServiceType, definitionBeanName);
                        registry.registerAlias(beanName, alisaName);
                    }
                }
            }
        }
        return beanName;
    }
}
