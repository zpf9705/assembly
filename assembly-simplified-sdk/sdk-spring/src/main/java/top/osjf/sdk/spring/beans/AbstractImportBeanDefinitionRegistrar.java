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

package top.osjf.sdk.spring.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.osjf.sdk.core.util.MapUtils;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Abstract {@link ImportBeanDefinitionRegistrar} implementation class,
 * parsing the triggered annotation content, and providing the package
 * path where the application main class is located.
 *
 * <p>Pass in method {@link #registerBeanDefinitions(AnnotationAttributes, BeanDefinitionRegistry)}
 * and hand it over to subclasses to implement the behavior of registering beans.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {

    /*** Log output, providing its own subclass usage.*/
    protected final Logger log = LoggerFactory.getLogger(getClass());

    /*** Configure metadata information for the class where the annotation is located.*/
    private ConfiguredClassMetadata configClassMetadata;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata, @NonNull BeanDefinitionRegistry registry,
                                        @NonNull BeanNameGenerator beanNameGenerator) {
        this.registerBeanDefinitions(metadata, registry);
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata, @NonNull BeanDefinitionRegistry registry) {
        this.configClassMetadata = ConfiguredClassMetadata.of(metadata);
        AnnotationAttributes importAnnotationAttributes;
        Class<? extends Annotation> importAnnotationType = getImportAnnotationType();
        if (importAnnotationType != null) {
            String annotationName = importAnnotationType.getName();
            Map<String, Object> annotationMap = metadata.getAnnotationAttributes(annotationName);
            if (MapUtils.isNotEmpty(annotationMap)) {
                importAnnotationAttributes = AnnotationAttributes.fromMap(annotationMap);
            } else {
                importAnnotationAttributes = null;
                if (log.isDebugEnabled()) {
                    log.debug("No corresponding map structure was obtained based on the annotation type name {}.",
                            annotationName);
                }
            }
        } else {
            importAnnotationAttributes = null;
            if (log.isDebugEnabled()) {
                log.debug("There is no type provided that can trigger this configuration annotation.");
            }
        }
        registerBeanDefinitions(importAnnotationAttributes, registry);
    }

    /**
     * Return the prototype interface of the configured class extension properties.
     *
     * @return prototype interface of the configured class extension properties.
     */
    public ConfiguredClassMetadata getConfigClassMetadata() {
        return configClassMetadata;
    }

    /**
     * Process the annotation content analyzed by the above method
     * here, present {@link AnnotationAttributes} in the form of an
     * encapsulated map, without providing a default method, and leave
     * it to the subclass to implement the processing logic.
     *
     * @param importAnnotationAttributes Pour {@link AnnotationAttributes} into the
     *                                   configuration annotation.
     * @param registry                   The registration machine for the bean.
     */
    protected abstract void registerBeanDefinitions(@Nullable AnnotationAttributes importAnnotationAttributes,
                                                    @NonNull BeanDefinitionRegistry registry);

    /**
     * Provide {@link org.springframework.context.annotation.Import}
     * annotation to pour into the target annotation of the configuration,
     * and obtain it for analyzing the content for subsequent extension use.
     * <p>Must need Override and provider not be {@literal null}</p>
     *
     * @return Annotation type,must not be {@literal null}.
     */
    @Nullable
    protected abstract Class<? extends Annotation> getImportAnnotationType();
}
