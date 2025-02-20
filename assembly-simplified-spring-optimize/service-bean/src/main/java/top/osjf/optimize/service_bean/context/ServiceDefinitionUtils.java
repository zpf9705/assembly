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

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Method and tool classes related to service definitions,
 * including names and definition conditions for various changes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ServiceDefinitionUtils {

    /**
     * Determine whether there are annotations {@code ServiceCollection} on the class.
     */
    private static final Predicate<Class<?>> TARGET_FILTER = c -> c.isAnnotationPresent(ServiceCollection.class);
    /**
     * Closing tag of bean name.
     */
    private static final String BEAN_NAME_CLOSE_TAG = "@service.bean.name";
    /**
     * Closing tag of bean alisa name.
     */
    private static final String ALISA_NAME_CLOSE_TAG = "@service.alisa.name";

    /**
     * Return a boolean tag indicating whether the input name is an enhanced
     * bean service name.
     *
     * @param serviceName the input service name.
     * @return If {@code true} is returned, it means yes, otherwise it is not.
     */
    public static boolean isEnhancementBeanServiceName(String serviceName) {
        Objects.requireNonNull(serviceName, "serviceName = null");
        return serviceName.endsWith(BEAN_NAME_CLOSE_TAG);
    }

    /**
     * Return a boolean tag indicating whether the input name is an enhanced
     * service name.
     *
     * @param serviceName the input service name.
     * @return If {@code true} is returned, it means yes, otherwise it is not.
     */
    public static boolean isEnhancementServiceName(String serviceName) {
        Objects.requireNonNull(serviceName, "serviceName = null");
        return isEnhancementBeanServiceName(serviceName) || serviceName.endsWith(ALISA_NAME_CLOSE_TAG);
    }

    /**
     * Based on whether the incoming type contains a record type, return the bean name after
     * enhancement. If it is not a record type, return a null value.
     *
     * @param serviceName  the original service  name to be enhancement is usually the
     *                     name.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param isRecordType is it recorded in {@code ServiceContextBeanNameGenerator#recordServiceBeanMap}.
     * @return enhanced and optimized service name.
     */
    @Nullable
    public static String getEnhancementBeanName(String serviceName, Class<?> requiredType, boolean isRecordType) {
        Objects.requireNonNull(serviceName, "serviceName = null");
        Objects.requireNonNull(requiredType, "requiredType = null");

        if (isRecordType) {
            return enhancementBeanName(requiredType, serviceName);
        }
        return null;
    }

    /**
     * Return the enhanced bean name or alias based on whether the incoming type contains
     * a record type.
     *
     * @param serviceName  the original service  name to be enhancement is usually the
     *                     name.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param isRecordType is it recorded in {@code ServiceContextBeanNameGenerator#recordServiceBeanMap}.
     * @return enhanced and optimized service name.
     */
    public static String getEnhancementName(String serviceName, Class<?> requiredType, boolean isRecordType) {
        Objects.requireNonNull(serviceName, "serviceName = null");
        Objects.requireNonNull(requiredType, "requiredType = null");

        if (isRecordType) {
            return enhancementBeanName(requiredType, serviceName);
        }
        return enhancementAlisaName(requiredType, serviceName);
    }

    /**
     * Return the class type of the current input, with its interface or parent class
     * marked with annotation {@link ServiceCollection} as a collection of class types.
     * <p>
     * <strong>Note:</strong>
     * According to the requirement of annotation {@link ServiceCollection}, this method
     * only filters the parent class or interface of tag {@link ServiceCollection}, and
     * discards all others.
     *
     * @param clazz input class.
     * @return Marked with annotation {@link ServiceCollection} as a collection of
     * class types.
     */
    @Nullable
    public static List<Class<?>> getTargetServiceTypes(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        List<Class<?>> serviceTypes = new ArrayList<>();

        for (Class<?> inerfaceClass : ClassUtils.getAllInterfaces(clazz)) {
            if (TARGET_FILTER.test(inerfaceClass)) {
                serviceTypes.add(inerfaceClass);
            }
        }

        for (Class<?> superclass : ClassUtils.getAllSuperclasses(clazz)) {
            if (!Objects.equals(Object.class, superclass) && TARGET_FILTER.test(superclass)) {
                serviceTypes.add(superclass);
            }
        }

        return serviceTypes;
    }

    /**
     * Generate a specially processed MD5 encrypted hexadecimal string based on the
     * given class, defined service name.
     *
     * <p>This method is mainly used to handle defining the service name , calling another
     * private method named {@link #enhancement}, and passing in the appointment class,
     * defining the service name, and a fixed ending tag{@link #BEAN_NAME_CLOSE_TAG}.
     *
     * @param clazz       the Class object is used to obtain the value (if any)
     *                    of the {@code ServiceCollection}.
     * @param serviceName the original service  name to be enhancement is usually the
     *                    name.
     * @return Return a hexadecimal string that has been processed by the {@code decapitalize}
     * method with a defined Bean name (fixed ending tag), and then encrypted with MD5.
     */
    public static String enhancementBeanName(Class<?> clazz, String serviceName) {
        return enhancement(clazz, serviceName, BEAN_NAME_CLOSE_TAG);
    }

    /**
     * Generate a specially processed MD5 encrypted hexadecimal string based
     * on the given class, service name.
     *
     * <p>This method is mainly used to handle alisa of service name, which call another
     * private method called {@link #enhancement},and pass in the appointment
     * class, service name , and a fixed ending tag is {@link #ALISA_NAME_CLOSE_TAG}.
     *
     * @param clazz       the Class object is used to obtain the value (if any)
     *                    of the {@code ServiceCollection}.
     * @param serviceName the original service name to be enhancement is usually the
     *                    name.
     * @return Returns a hexadecimal string encrypted with MD5 after specific processing
     * by an service name (fixed ending tag).
     */
    public static String enhancementAlisaName(Class<?> clazz, String serviceName) {
        return enhancement(clazz, serviceName, ALISA_NAME_CLOSE_TAG);
    }

    /**
     * Generate an MD5 encrypted hexadecimal string based on the given class,
     * service name and ending tag.
     *
     * <p>The method first attempts to obtain the {@code ServiceCollection}
     * annotation from the given class, and uses the value or class name in
     * the annotation as the prefix for the service collection.
     *
     * <p>Then, prefix, original service name , and ending tag are concatenated
     * to form the final string, which is then MD5 encrypted.
     *
     * @param clazz       the Class object is used to obtain the value (if any)
     *                    of the {@code ServiceCollection}.
     * @param serviceName the original service name to be enhancement is usually the
     *                    name.
     * @param closingTag  the end tag will be attached to the end of the processed
     *                    name.
     * @return Return a hexadecimal string encrypted by MD5 using the original name
     * (possibly with a prefix and ending label).
     * @throws NullPointerException if input clazz or name or closingTag is null.
     */
    private static String enhancement(Class<?> clazz, String serviceName, String closingTag) {
        Objects.requireNonNull(clazz, "clazz = null");
        Objects.requireNonNull(serviceName, "serviceName = null");
        Objects.requireNonNull(closingTag, "closingTag =null");
        String annotationValue;
        ServiceCollection serviceCollection = clazz.getAnnotation(ServiceCollection.class);
        if (serviceCollection != null) {
            annotationValue = serviceCollection.value();
            if (StringUtils.isBlank(annotationValue)) annotationValue = clazz.getName();
        } else annotationValue = clazz.getName();
        final String enhancementBeanName = annotationValue + serviceName;
        return DigestUtils
                .md5DigestAsHex(enhancementBeanName.getBytes(StandardCharsets.UTF_8)) + closingTag;
    }

    /**
     * Safely retrieve the corresponding Class object based on the provided
     * class name string.
     *
     * @param className the fully qualified name of the class to be obtained
     *                  (including package name and class name).
     * @return If the class name string is not empty and the class can be found,
     * return the corresponding Class object;If the class name string is empty or
     * the class cannot be found (for example, because the class does not exist
     * in the class path), return null.
     */
    @Nullable
    public static Class<?> getSafeClass(String className) {
        if (StringUtils.isBlank(className)) {
            return null;
        }
        try {
            return ClassUtils.getClass(className, false);
        } catch (Exception e) {
            return null;
        }
    }
}
