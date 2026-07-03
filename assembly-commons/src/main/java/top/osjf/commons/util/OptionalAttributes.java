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

import top.osjf.commons.lang.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class was copied from {@code org.springframework.core}, with minor modifications
 * and adaptations. I would like to express my sincere gratitude here!
 */
@SuppressWarnings("serial")
public class OptionalAttributes extends LinkedHashMap<String, Object> {

    /**
     * Create a new, empty {@link OptionalAttributes} instance.
     */
    public OptionalAttributes() {
    }

    /**
     * Create a new, empty {@link OptionalAttributes} instance with the
     * given initial capacity to optimize performance.
     * @param initialCapacity initial size of the underlying map
     */
    public OptionalAttributes(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Create a new {@link OptionalAttributes} instance, wrapping the provided
     * map and all its <em>key-value</em> pairs.
     * @param map original source of annotation attribute <em>key-value</em> pairs
     * @see #fromMap(Map)
     */
    public OptionalAttributes(Map<String, Object> map) {
        super(map);
    }

    /**
     * Create a new {@link OptionalAttributes} instance, wrapping the provided
     * map and all its <em>key-value</em> pairs.
     * @param other original source of annotation attribute <em>key-value</em> pairs
     * @see #fromMap(Map)
     */
    public OptionalAttributes(OptionalAttributes other) {
        super(other);
    }

    /**
     * Get the value stored under the specified {@code attributeName} as a string.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the @Nullable value
     */
    @Nullable
    public String getString(String attributeName) {
        return getNullableAttribute(attributeName, String.class);
    }

    /**
     * Get the value stored under the specified {@code attributeName} as an
     * array of strings.
     * <p>If the value stored under the specified {@code attributeName} is
     * a string, it will be wrapped in a single-element array before
     * returning it.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the @Nullable value
     */
    @Nullable
    public String[] getStringArray(String attributeName) {
        return getNullableAttribute(attributeName, String[].class);
    }

    /**
     * Get the value stored under the specified {@code attributeName} as a boolean.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the @Nullable value
     */
    @Nullable
    public boolean getBoolean(String attributeName) {
        return getNullableAttribute(attributeName, Boolean.class);
    }

    /**
     * Get the value stored under the specified {@code attributeName} as a number.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the @Nullable value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <N extends Number> N getNumber(String attributeName) {
        return (N) getNullableAttribute(attributeName, Number.class);
    }

    /**
     * Get the value stored under the specified {@code attributeName} as an enum.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the @Nullable value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <E extends Enum<?>> E getEnum(String attributeName) {
        return (E) getNullableAttribute(attributeName, Enum.class);
    }

    /**
     * Get the value stored under the specified {@code attributeName} as a class.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the @Nullable value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <T> Class<? extends T> getClass(String attributeName) {
        return getNullableAttribute(attributeName, Class.class);
    }

    /**
     * Get the value stored under the specified {@code attributeName} as an
     * array of classes.
     * <p>If the value stored under the specified {@code attributeName} is a class,
     * it will be wrapped in a single-element array before returning it.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the @Nullable value
     */
    @Nullable
    public Class<?>[] getClassArray(String attributeName) {
        return getNullableAttribute(attributeName, Class[].class);
    }

    /**
     * Get the {@link OptionalAttributes} stored under the specified
     * {@code attributeName}.
     * <p>Note: if you expect an actual annotation, invoke
     * {@link #getAnnotation(String, Class)} instead.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the @Nullable {@code OptionalAttributes}
     */
    @Nullable
    public OptionalAttributes getAnnotation(String attributeName) {
        return getNullableAttribute(attributeName, OptionalAttributes.class);
    }

    /**
     * Get the annotation of type {@code annotationType} stored under the
     * specified {@code attributeName}.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @param annotationType the expected annotation type; never {@code null}
     * @return the @Nullable annotation
     */
    @Nullable
    public <A extends Annotation> A getAnnotation(String attributeName, Class<A> annotationType) {
        return getNullableAttribute(attributeName, annotationType);
    }

    /**
     * Get the array of {@link OptionalAttributes} stored under the specified
     * {@code attributeName}.
     * <p>If the value stored under the specified {@code attributeName} is
     * an instance of {@code OptionalAttributes}, it will be wrapped in
     * a single-element array before returning it.
     * <p>Note: if you expect an actual array of annotations, invoke
     * {@link #getAnnotationArray(String, Class)} instead.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @return the @Nullable array of {@code OptionalAttributes}
     */
    @Nullable
    public OptionalAttributes[] getAnnotationArray(String attributeName) {
        return getNullableAttribute(attributeName, OptionalAttributes[].class);
    }

    /**
     * Get the array of type {@code annotationType} stored under the specified
     * {@code attributeName}.
     * <p>If the value stored under the specified {@code attributeName} is
     * an {@code Annotation}, it will be wrapped in a single-element array
     * before returning it.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @param annotationType the expected annotation type; never {@code null}
     * @return the @Nullable array.
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public <A extends Annotation> A[] getAnnotationArray(String attributeName, Class<A> annotationType) {
        Object array = Array.newInstance(annotationType, 0);
        return (A[]) getNullableAttribute(attributeName, array.getClass());
    }

    /**
     * Get the value stored under the specified {@code attributeName},
     * ensuring that the value is of the {@code expectedType}.
     * <p>If the {@code expectedType} is an array and the value stored
     * under the specified {@code attributeName} is a single element of the
     * component type of the expected array type, the single element will be
     * wrapped in a single-element array of the appropriate type before
     * returning it.
     * @param attributeName the name of the attribute to get;
     * never {@code null} or empty
     * @param expectedType the expected type; never {@code null}
     * @return the @Nullable value
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private <T> T getNullableAttribute(String attributeName, Class<T> expectedType) {
        Assert.hasText(attributeName, "'attributeName' must not be null or empty");
        Object value = get(attributeName);
        if (value == null) return null;
        if (!expectedType.isInstance(value) && expectedType.isArray() &&
                expectedType.getComponentType().isInstance(value)) {
            Object array = Array.newInstance(expectedType.getComponentType(), 1);
            Array.set(array, 0, value);
            value = array;
        }
        assertAttributeType(attributeName, value, expectedType);
        return (T) value;
    }

    private void assertAttributeType(String attributeName, Object attributeValue, Class<?> expectedType) {
        if (!expectedType.isInstance(attributeValue)) {
            throw new IllegalArgumentException(String.format(
                    "Attribute '%s' is of type %s, but %s was expected in attribute",
                    attributeName, attributeValue.getClass().getSimpleName(), expectedType.getSimpleName()));
        }
    }

    @Override
    public String toString() {
        Iterator<Map.Entry<String, Object>> entries = entrySet().iterator();
        StringBuilder sb = new StringBuilder("{");
        while (entries.hasNext()) {
            Map.Entry<String, Object> entry = entries.next();
            sb.append(entry.getKey());
            sb.append('=');
            sb.append(valueToString(entry.getValue()));
            if (entries.hasNext()) {
                sb.append(", ");
            }
        }
        sb.append('}');
        return sb.toString();
    }

    private String valueToString(Object value) {
        if (value == this) {
            return "(this Map)";
        }
        if (value instanceof Object[]) {
            return "[" + StringUtils.arrayToDelimitedString((Object[]) value, ", ") + "]";
        }
        return String.valueOf(value);
    }


    /**
     * Return an {@link OptionalAttributes} instance based on the given map.
     * <p>If the map is already an {@code Attributes} instance, it
     * will be cast and returned immediately without creating a new instance.
     * Otherwise a new instance will be created by passing the supplied map
     * to the {@link #OptionalAttributes(Map)} constructor.
     * @param map original source of annotation attribute <em>key-value</em> pairs
     */
    @Nullable
    public static OptionalAttributes fromMap(@Nullable Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        if (map instanceof OptionalAttributes) {
            return (OptionalAttributes) map;
        }
        return new OptionalAttributes(map);
    }
}
