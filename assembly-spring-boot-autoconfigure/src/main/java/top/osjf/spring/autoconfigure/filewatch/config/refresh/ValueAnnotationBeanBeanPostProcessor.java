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


package top.osjf.spring.autoconfigure.filewatch.config.refresh;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This post processor is used in the early stage of bean initialization to find the fields
 * that use annotation {@link Value} for configuration injection, and record them for targeted
 * preparation for subsequent dynamic configuration refreshes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class ValueAnnotationBeanBeanPostProcessor
        implements InstantiationAwareBeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<String, Set<Field>> beanValueFieldsInjectPropertyMapping = new ConcurrentHashMap<>(64);

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Nullable
    @Override
    public Object postProcessBeforeInstantiation(@NonNull Class<?> beanClass, @NonNull String beanName)
            throws BeansException {

        // Scan all fields with @Value annotation using Spring's ReflectionUtils.

        ReflectionUtils.doWithFields(beanClass, field -> {
            Set<Field> valueFields
                    = beanValueFieldsInjectPropertyMapping.computeIfAbsent(beanName, key -> new LinkedHashSet<>());
            valueFields.add(field);
        }, field -> field.isAnnotationPresent(Value.class));

        return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInstantiation(beanClass, beanName);
    }

    /**
     * Processes dependency injection for beans whose config properties were updated.
     * @param updatePropertyNames list of changed property names.
     * @throws BeanCreationException if autowiring failed
     * @see AutowiredAnnotationBeanPostProcessor#processInjection
     */
    public void processInjection(List<String> updatePropertyNames) {
        if (CollectionUtils.isEmpty(updatePropertyNames)) {
            return;
        }
        Map<Object, Set<Field>> beanReloadingConfigFieldMap = new ConcurrentHashMap<>();
        AutowiredAnnotationBeanPostProcessor postProcessor
                = applicationContext.getBean(AutowiredAnnotationBeanPostProcessor.class);
        for (Map.Entry<String, Set<Field>> entry : beanValueFieldsInjectPropertyMapping.entrySet()) {
            String beanName = entry.getKey();

            Set<Field> reloadingField = new LinkedHashSet<>();
            for (Field field : entry.getValue()) {

                // The similarity of expressions includes.
                // Give an example : ${spring.application.name} contain spring.application.name.
                String value = field.getAnnotation(Value.class).value();
                if (updatePropertyNames.stream().anyMatch(value::contains)) {
                    reloadingField.add(field);
                }
            }

            // Process injection if it has config reloading field.
            if (!reloadingField.isEmpty()) {
                Object bean = applicationContext.getBean(beanName);
                postProcessor.processInjection(bean);
                beanReloadingConfigFieldMap.putIfAbsent(bean, reloadingField);
            }
        }

        // Publish config reloading event.
        applicationContext.publishEvent(new ConfigRefreshedEvent(this, beanReloadingConfigFieldMap));
    }
}
