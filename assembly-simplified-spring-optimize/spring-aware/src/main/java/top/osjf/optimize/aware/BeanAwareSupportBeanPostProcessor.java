/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.optimize.aware;

import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Spring container generic-aware post-processor implementing {@link MergedBeanDefinitionPostProcessor},
 * designed for automatic dependency injection resolution of {@link BeanAware} generic types.
 *
 * <p>This processor analyzes {@link BeanAware} implementers and their generic parameters during
 * merged bean definition phase, constructing corresponding {@link RuntimeBeanReference} for DI.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class BeanAwareSupportBeanPostProcessor implements MergedBeanDefinitionPostProcessor {

    /**
     * Processes merged bean definition to inject bean reference for {@link BeanAware} generic types.
     *
     * <p>Phase: After bean definition merging, before instantiation
     * <p>Logic Flow:
     * 1. Check if bean class implements {@link BeanAware} interface
     * 2. Resolve generic parameters to determine dependency type
     * 3. Register {@link RuntimeBeanReference} to property values
     *
     * @param beanDefinition {@inheritDoc}
     * @param beanType       {@inheritDoc}
     * @param beanName       {@inheritDoc}
     */
    @Override
    public void postProcessMergedBeanDefinition(@NonNull RootBeanDefinition beanDefinition,
                                                @NonNull Class<?> beanType, @NonNull String beanName) {
        if (BeanAware.class.isAssignableFrom(beanType)) {
            Class<?> beanAwareClass = getBeanAwareClass(beanType);
            beanDefinition.getPropertyValues()
                    .addPropertyValue("bean", new RuntimeBeanReference(beanAwareClass));
        }
    }

    /**
     * Recursively resolves generic parameter type of {@link BeanAware} interface.
     *
     * <p>Algorithm features:
     * 1. Depth-first search through type hierarchy
     * 2. Multi-level generic parameter inference
     * 3. Strict type safety checks
     *
     * @param beanType bean type to analyze
     * @return Resolved generic parameter type
     * @throws IllegalArgumentException thrown when cannot determine generic type.
     */
    @SuppressWarnings("rawtypes")
    private Class<?> getBeanAwareClass(Class<?> beanType) {

        List<Type> genericTypes = new ArrayList<>(Arrays.asList(beanType.getGenericInterfaces()));
        genericTypes.add(beanType.getGenericSuperclass());

        for (Type genericType : genericTypes) {

            if (genericType instanceof Class) {
                Class c = (Class) genericType;
                if (BeanAware.class.isAssignableFrom(c)) {
                    return getBeanAwareClass(c);
                }
            } else if (genericType instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) genericType).getActualTypeArguments();
                Assert.isTrue(actualTypeArguments.length == 1,
                        "Multiple or zero generics were found, unable to determine which type the callback requires.");
                return (Class<?>) actualTypeArguments[0];
            }
        }

        throw new IllegalArgumentException("Unknown callback type.");
    }
}
