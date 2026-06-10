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


package top.osjf.cron.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.InitializeProperties;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Abstract {@link Configuration configuration} class for task registration framework.
 * <p>
 * Provide {@link InitializeProperties} instance conversion for switch annotation properties,
 * as well as configuration for accessing task metadata in restful format and access
 * authentication mechanisms.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.0
 */
@Configuration(proxyBeanMethods = false)
public abstract class AbstractCronTaskConfiguration implements ImportAware {

    /**
     * Store the relevant attributes extracted from {@link AnnotationMetadata} that
     * provide annotation types.
     */
    @Nullable
    private InitializeProperties initializeProperties;

    @Override
    public void setImportMetadata(@NotNull AnnotationMetadata importMetadata) {
        Class<? extends Annotation> annotationType = enableImportAnnotationType();
        if (annotationType == null) {
            return;
        }
        if (importMetadata.hasMetaAnnotation(annotationType.getName())) {
            Map<String, Object> annotationAttributes
                    = importMetadata.getAnnotationAttributes(annotationType.getCanonicalName());
            if (annotationAttributes != null) {
                initializeProperties = InitializeProperties.copyOfStringKeys(annotationAttributes);
            }
        }
    }

    /**
     * Return a {@link InitializeProperties} object compiled from the specified annotation
     * attributes extracted from {@code AnnotationMetadata}.
     * @return The {@code InitializeProperties} object contains properties extracted from
     * annotations.
     */
    @Nullable
    protected InitializeProperties getImportAnnotationInitializeProperties() {
        return initializeProperties;
    }

    /**
     * Returns the annotation type that enables the import.
     * @return the annotation type that enables the import.
     */
    @Nullable
    protected Class<? extends Annotation> enableImportAnnotationType() {
        return null;
    }
}
