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


package top.osjf.spring.autoconfigure.filewatch.dynamics.yml.config;

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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Dynamic YAML configuration loader that processes {@link Value} annotations
 * and triggers re-injection when config properties change.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
public class DynamicsYamlConfigLoadingBeanPostProcessor
        implements InstantiationAwareBeanPostProcessor, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<String, Set<String>> beanValueFieldsInjectPropertyMapping = new ConcurrentHashMap<>(64);

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
            Set<String> regPropertyNames
                    = beanValueFieldsInjectPropertyMapping.computeIfAbsent(beanName, key -> new LinkedHashSet<>(4));
            regPropertyNames.add(field.getAnnotation(Value.class).value());
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
        AutowiredAnnotationBeanPostProcessor postProcessor
                = applicationContext.getBean(AutowiredAnnotationBeanPostProcessor.class);
        for (Map.Entry<String, Set<String>> entry : beanValueFieldsInjectPropertyMapping.entrySet()) {
            String beanName = entry.getKey();
            if (entry.getValue().stream()
                    .anyMatch(regPropertyName -> updatePropertyNames.stream().anyMatch(regPropertyName::contains))) {
                Object bean = applicationContext.getBean(beanName);
                postProcessor.processInjection(bean);
            }
        }
    }
}
