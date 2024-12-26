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

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

import java.beans.Introspector;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The core name of this service framework is the static method class,
 * which includes various operations such as secondary encoding and
 * parsing of bean names, aiming to solve the problem of equal naming
 * for different types.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ServiceCore {
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
     * @return enhanced and optimized service name.
     */
    @Nullable
    public static String getEnhancementBeanName(String serviceName, Class<?> requiredType) {
        Objects.requireNonNull(serviceName, "serviceName = null");
        Objects.requireNonNull(requiredType, "requiredType = null");

        if (ServiceContextBeanNameGenerator.getRecordBeanTypes().contains(requiredType)) {
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
     * @return enhanced and optimized service name.
     */
    public static String getEnhancementName(String serviceName, Class<?> requiredType) {
        Objects.requireNonNull(serviceName, "serviceName = null");
        Objects.requireNonNull(requiredType, "requiredType = null");

        if (ServiceContextBeanNameGenerator.getRecordBeanTypes().contains(requiredType)) {
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
    public static List<Class<?>> getTargetServiceTypes(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<Class<?>> serviceTypes = new ArrayList<>();

        //Interface directly implemented by class
        Class<?>[] canSeeInterfaces = clazz.getInterfaces();

        //If the interface directly implemented by the class does not exist, query the inherited class.
        if (ArrayUtils.isEmpty(canSeeInterfaces)) {
            Class<?> superclass = clazz.getSuperclass();
            //It cannot be an Object .
            if ("java.lang.Object".equals(superclass.getName())) {
                //No interface, no parent class, directly returning null.
                return Collections.emptyList();
            } else {
                //must comply with annotation requirements.
                if (TARGET_FILTER.test(superclass)) {
                    serviceTypes.add(superclass);
                }
            }
        } else {
            Class<?> clazz0 = Arrays.stream(canSeeInterfaces).filter(TARGET_FILTER)
                    .findFirst().orElse(null);
            //For interfaces, use any one of the current IDs, and take the first one here.
            if (clazz0 != null) {
                serviceTypes.add(clazz0);
            }
        }

        //get all super classes
        List<Class<?>> supers = ClassUtils.getAllSuperclasses(clazz);
        supers.remove(Object.class);
        supers = supers.stream().filter(TARGET_FILTER).collect(Collectors.toList());

        //get all interfaces
        List<Class<?>> interfaces = ClassUtils.getAllInterfaces(clazz);
        interfaces = interfaces.stream().filter(TARGET_FILTER).collect(Collectors.toList());

        //If the first element is not yet present at this point,
        // take the first one from all filtered parent and interface classes.
        if (serviceTypes.isEmpty()) {
            if (!interfaces.isEmpty()) {
                serviceTypes.add(interfaces.get(0));
            } else {
                if (!supers.isEmpty()) {
                    serviceTypes.add(supers.get(0));
                }
            }
        }

        //If it is still empty, return null directly.
        if (CollectionUtils.isEmpty(serviceTypes)) return null;

        interfaces.addAll(supers);

        interfaces.removeAll(serviceTypes);

        //After removing the duplicate from the first position, add it directly.
        serviceTypes.addAll(interfaces);

        return serviceTypes;
    }

    /**
     * Parse and return a series of aliases based on the given class
     * and whether to ignore the lowercase first letter flag.
     *
     * <p>These aliases include the simple name of the class (excluding
     * package name), the fully qualified name of the class (including
     * package name), and (if not ignored) the lowercase version of the
     * first letter of the simple name of the class.
     *
     * @param clazz               to resolve the class object of the alias.
     *                            If null is passed, an empty list is returned.
     * @param ignoredDecapitalize a Boolean value indicating whether to ignore
     *                            the capitalization of the first letter of a
     *                            class's simple name.
     * @return A list containing the resolved aliases. The list should include
     * at least the simple name and fully qualified name of the class. If
     * {@code ignoredDecapitalize} is false, it will also include the lowercase
     * version of the class's simple name.
     */
    public static List<String> resolveAliasNames(Class<?> clazz, boolean ignoredDecapitalize) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<String> alisaNames = new ArrayList<>();
        String simpleName = clazz.getSimpleName();
        alisaNames.add(simpleName);
        alisaNames.add(clazz.getName());
        if (!ignoredDecapitalize) {
            alisaNames.add(Introspector.decapitalize(simpleName));
        }
        return alisaNames;
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
