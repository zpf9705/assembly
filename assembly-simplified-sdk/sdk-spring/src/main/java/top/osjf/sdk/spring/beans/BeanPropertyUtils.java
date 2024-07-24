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

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.sdk.core.util.ArrayUtils;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * The relevant general method tool class for {@link BeanProperty}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class BeanPropertyUtils {

    /**
     * @see BeanProperty#autowire()
     */
    private static final String AUTOWIRE = "autowire";

    /**
     * @see BeanProperty#autowireCandidate()
     */
    private static final String AUTOWIRE_CANDIDATE = "autowireCandidate";

    /**
     * @see BeanProperty#scope()
     */
    private static final String SCOPE = "scope";

    /**
     * @see BeanProperty#role()
     */
    private static final String ROLE = "role";

    /**
     * @see BeanProperty#lazyInit()
     */
    private static final String LAZY = "lazyInit";

    /**
     * @see BeanProperty#description()
     */
    private static final String DESCRIPTION = "description";

    /**
     * Following the processing specification of {@link org.springframework.context.annotation.Bean},
     * the first name should be the main name of the bean.
     *
     * @param names Define a collection of names.
     * @return The primary name of the bean.
     */
    public static String getBeanName(String[] names) {
        if (ArrayUtils.isNotEmpty(names)) {
            return names[0];
        }
        return null;
    }

    /**
     * According to the definition specification of {@link org.springframework.context.annotation.Bean},
     * the non empty element array after removing the first element is
     * called an alias element array.
     *
     * @param names Define a collection of names.
     * @return alias name array.
     */
    public static String[] getAlisaNames(String[] names) {
        if (ArrayUtils.isNotEmpty(names)) {
            if (names.length == 1) {
                return null;
            }
            return new LinkedHashSet<>(Arrays.asList(
                    ArrayUtils.remove(names, 0))).toArray(new String[]{});
        }
        return null;
    }

    /**
     * The scope name setting of the bean supports annotations
     * {@link Scope} corresponding to attributes other than
     * {@link BeanProperty#scope()}}, and annotations are
     * used first.
     * <p>Of course, when there is no direct existence of {@link Scope},
     * search for the derived annotations of {@link Scope}.
     * If multiple derived annotations appear, the topmost one
     * will be selected as the scope definition option for this time.
     *
     * @param annotationMetadata Annotate metadata information.
     * @param defaultValue       The default value when no annotations are provided.
     * @return Final logical value.
     */
    public static String getMaybeAnnotationScope(AnnotationMetadata annotationMetadata, String defaultValue) {
        if (annotationMetadata.hasAnnotation(Scope.class.getCanonicalName())) {
            return getMaybeAnnotationValue(annotationMetadata, Scope.class, String.class, defaultValue);
        }
        MergedAnnotation<Annotation> mergedAnnotation = annotationMetadata.getAnnotations()
                .stream()
                .filter(m -> m.getType().isAnnotationPresent(Scope.class))
                .min((o1, o2) -> {
                    int o1_index = o1.getAggregateIndex();
                    int o2_index = o2.getAggregateIndex();
                    if (o1_index == o2_index) return 0;
                    return o1_index > o2_index ? 1 : -1;
                })
                .orElse(null);
        return mergedAnnotation != null ? mergedAnnotation.getType().getAnnotation(Scope.class).value() : defaultValue;
    }

    /**
     * The role setting of the bean supports annotations
     * {@link Role} corresponding to attributes other than
     * {@link BeanProperty#role()}}, and annotations are
     * used first.
     *
     * @param annotationMetadata Annotate metadata information.
     * @param defaultValue       The default value when no annotations are provided.
     * @return Final logical value.
     */
    public static int getMaybeAnnotationRole(AnnotationMetadata annotationMetadata, int defaultValue) {
        return getMaybeAnnotationValue(annotationMetadata, Role.class, int.class, defaultValue);
    }

    /**
     * The lazy load setting of the bean supports annotations
     * {@link Lazy} corresponding to attributes other than
     * {@link BeanProperty#lazyInit()}}, and annotations are
     * used first.
     *
     * @param annotationMetadata Annotate metadata information.
     * @param defaultValue       The default value when no annotations are provided.
     * @return Final logical value.
     */
    public static boolean getMaybeAnnotationLazy(AnnotationMetadata annotationMetadata, boolean defaultValue) {
        return getMaybeAnnotationValue(annotationMetadata, Lazy.class, boolean.class, defaultValue);
    }

    /**
     * The description information setting of the bean supports annotations
     * {@link Description} corresponding to attributes other than
     * {@link BeanProperty#description()}, and annotations are used first.
     *
     * @param annotationMetadata Annotate metadata information.
     * @param defaultValue       The default value when no annotations are provided.
     * @return Final logical value.
     */
    public static String getMaybeAnnotationDescription(AnnotationMetadata annotationMetadata, String defaultValue) {
        return getMaybeAnnotationValue(annotationMetadata, Description.class, String.class, defaultValue);
    }

    /**
     * Add support for special annotations that support certain defined
     * properties of annotation {@link BeanProperty}, with priority greater
     * than the value definition in this annotation. If present, return the
     * defined {@code value}.
     *
     * @param annotationMetadata Annotate metadata information.
     * @param annotationType     Possible priority annotation types.
     * @param valueType          The type of the final parsed value.
     * @param defaultValue       The default value when no annotations are provided.
     * @param <A>                Possible priority annotation types.
     * @param <T>                The type of the final parsed value.
     * @return Final logical value.
     */
    @SuppressWarnings("unchecked")
    private static <A extends Annotation, T> T getMaybeAnnotationValue(AnnotationMetadata annotationMetadata,
                                                                       Class<A> annotationType,
                                                                       Class<T> valueType,
                                                                       T defaultValue) {
        String canonicalName = annotationType.getCanonicalName();
        if (!annotationMetadata.hasAnnotation(canonicalName)) {
            return defaultValue;
        }
        Map<String, Object> annotationAttributes =
                annotationMetadata.getAnnotationAttributes(canonicalName);
        if (MapUtils.isEmpty(annotationAttributes)) {
            return defaultValue;
        }
        Object value = annotationAttributes.get("value");
        if (valueType.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        throw new ClassCastException("Required type " + valueType.getName() + ", actual type " +
                value.getClass().getName() + ".");
    }

    /**
     * Fill {@link BeanProperty} with the relevant properties of {@link BeanDefinition} and
     * call the relevant methods for building the class.
     *
     * @param builder                The construction class of {@link BeanDefinition}.
     * @param annotationMetadata     Annotate metadata information.
     * @param beanPropertyAttributes Encapsulation of {@link BeanProperty}'s properties.
     * @return A BeanDefinition describes a bean instance after full use {@link BeanProperty}.
     */
    public static BeanDefinition fullBeanDefinition(BeanDefinitionBuilder builder,
                                                    AnnotationMetadata annotationMetadata,
                                                    AnnotationAttributes beanPropertyAttributes) {
        Autowire autowire = beanPropertyAttributes.getEnum(AUTOWIRE);
        boolean autowireCandidate = beanPropertyAttributes.getBoolean(AUTOWIRE_CANDIDATE);
        String scope = getMaybeAnnotationScope(annotationMetadata,
                beanPropertyAttributes.getString(SCOPE));
        int role = getMaybeAnnotationRole(annotationMetadata,
                beanPropertyAttributes.<Integer>getNumber(ROLE));
        boolean lazyInit = getMaybeAnnotationLazy(annotationMetadata,
                beanPropertyAttributes.getBoolean(LAZY));
        String description = getMaybeAnnotationDescription(annotationMetadata,
                beanPropertyAttributes.getString(DESCRIPTION));
        //—————————————————————————— General attributes ——————————————————————————
        builder.setScope(scope);
        builder.setAutowireMode(autowire.value());
        builder.setRole(role);
        builder.setLazyInit(lazyInit);
        //—————————————————————————— Pre inspection ——————————————————————————
        BeanDefinition beanDefinition = builder.getBeanDefinition();
        //—————————————————————————— BeanDefinition attributes ——————————————————————————
        beanDefinition.setAutowireCandidate(autowireCandidate);
        if (StringUtils.isNotBlank(description)) beanDefinition.setDescription(description);
        return beanDefinition;
    }
}
