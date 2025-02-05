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

import org.springframework.context.annotation.ImportAware;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.SuperiorProperties;

import java.lang.annotation.Annotation;

/**
 * {@code ImportAnnotationMetadataExtractor} extends the self interface {@link ImportAware}
 * to extract relevant properties from {@link AnnotationMetadata} and build them into
 * {@link SuperiorProperties} objects based on the annotation types provided by subclasses.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ImportAnnotationMetadataExtractor implements ImportAware {
    /**
     * Store the relevant attributes extracted from {@link AnnotationMetadata} that
     * provide annotation types.
     */
    @Nullable
    private SuperiorProperties superiorProperties;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Class<? extends Annotation> annotationType = enableImportAnnotationType();
        if (importMetadata.hasMetaAnnotation(annotationType.getName())) {
            superiorProperties = SuperiorProperties.of(importMetadata.getAnnotationAttributes
                    (annotationType.getCanonicalName()));
        }
    }

    /**
     * Return a {@link SuperiorProperties} object compiled from the specified annotation
     * attributes extracted from {@code AnnotationMetadata}.
     *
     * @return The {@code SuperiorProperties} object contains properties extracted from
     * annotations.
     */
    @Nullable
    protected SuperiorProperties getImportAnnotationSuperiorProperties() {
        return superiorProperties;
    }

    /**
     * Returns the annotation type that enables the import.
     * Subclasses need to implement this method to return the specific annotation type.
     *
     * @return the annotation type that enables the import.
     */
    @NotNull
    protected abstract Class<? extends Annotation> enableImportAnnotationType();
}
