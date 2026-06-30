/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.commons.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Common utility methods for annotation reflection parsing.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class AnnotationUtils {

    /**
     * Find all annotations of the specified {@code targetType} declared on the given {@link Method}.
     *
     * <p>This method covers two scenarios:
     * <ul>
     * <li>Annotations directly present on the target method</li>
     * <li>Annotations used as meta-annotations on other annotations that are present on the target
     * method</li>
     * </ul>
     *
     * <p>The returned set preserves the order in which annotations are discovered and automatically
     * eliminates duplicates. Circular meta-annotation references and JDK built-in annotations are
     * safely skipped to avoid infinite recursion and unnecessary reflection overhead.
     *
     * @param method     the target method to inspect for annotations
     * @param targetType the annotation type to look for
     * @param <T>        the concrete type of the target annotation
     * @return an ordered {@link Set} containing all matched annotations; never {@code null}
     */
    public static <T extends Annotation> Set<T> findMethodMergedAnnotations(Method method, Class<T> targetType) {
        Set<T> result = new LinkedHashSet<>();

        Set<Class<? extends Annotation>> visited = new LinkedHashSet<>();

        T directAnnotation = method.getAnnotation(targetType);
        if (directAnnotation != null) {
            result.add(directAnnotation);
        }

        for (Annotation annotation : method.getAnnotations()) {
            collectMetaAnnotations(annotation.annotationType(), visited, targetType, result);
        }

        return result;
    }

    private static <T extends Annotation>void collectMetaAnnotations(Class<? extends Annotation> annotationType,
                                                                     Set<Class<? extends Annotation>> visited,
                                                                     Class<T> targetType, Set<T> result) {
        if (visited.contains(annotationType)
                || annotationType.getName().startsWith("java.lang.annotation")
                || annotationType == targetType) {
            return;
        }
        visited.add(annotationType);

        T metaAnnotation = annotationType.getAnnotation(targetType);
        if (metaAnnotation != null) {
            result.add(metaAnnotation);
        }

        Annotation[] metaAnnotations = annotationType.getAnnotations();
        for (Annotation metaAnn : metaAnnotations) {
            collectMetaAnnotations(metaAnn.annotationType(), visited, targetType, result);
        }
    }
}
