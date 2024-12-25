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
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

import java.beans.Introspector;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * Generate and return a list of candidate names for depolarization based on the
     * given name, required type, and optional application ID.
     *
     * @param name          input the original name to be generated.
     * @param requiredType  the specified required type.
     * @param applicationId optional application ID, which may be used to generate name
     *                      variants specific to the application. If not available, null
     *                      can be passed in.
     * @return a list of candidate names for depolarization based on the given name,
     * required type, and optional application ID.
     * @see #decapitalizeDefinitionBeanName
     * @see #decapitalizeAlisaName
     */
    public static List<String> getDecapitalizeCandidateNames(String name, Class<?> requiredType,
                                                             @Nullable String applicationId) {
        return Stream.of(name,
                        decapitalizeDefinitionBeanName(requiredType, name, applicationId),
                        decapitalizeAlisaName(requiredType, name, applicationId))
                .collect(Collectors.toList());
    }

    public static List<Class<?>> getTargetServiceTypes(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        LinkedList<Class<?>> sortServiceTypes = new LinkedList<>();

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
                    sortServiceTypes.add(superclass);
                }
            }
        } else {
            Class<?> clazz0 = Arrays.stream(canSeeInterfaces).filter(TARGET_FILTER)
                    .findFirst().orElse(null);
            //For interfaces, use any one of the current IDs, and take the first one here.
            if (clazz0 != null) {
                sortServiceTypes.add(clazz0);
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
        if (sortServiceTypes.isEmpty()) {
            if (!interfaces.isEmpty()) {
                sortServiceTypes.add(interfaces.get(0));
            } else {
                if (!supers.isEmpty()) {
                    sortServiceTypes.add(supers.get(0));
                }
            }
        }

        //If it is still empty, return null directly.
        if (CollectionUtils.isEmpty(sortServiceTypes)) return null;

        interfaces.addAll(supers);

        interfaces.removeAll(sortServiceTypes);

        //After removing the duplicate from the first position, add it directly.
        sortServiceTypes.addAll(interfaces);

        return sortServiceTypes;
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
     * given class, defined Bean name, and application ID.
     *
     * <p>This method is mainly used to handle defining the Bean name {@code definitionBeanName},
     * calling another private method named {@link #decapitalize}, and passing in the
     * parent class, defining the Bean name, application ID, and a fixed ending tag
     * {@link #BEAN_NAME_CLOSE_TAG}.
     *
     * @param parent             the Class object of the parent class is used to
     *                           obtain the value (if any) of the {@code ServiceCollection}.
     * @param definitionBeanName the bean name to be processed is usually the identifier of
     *                           a bean.
     * @param applicationId      application ID, if not empty, will be used as a prefix
     *                           for generating strings; If it is empty or contains only
     *                           blank characters, ignore it.
     * @return Return a hexadecimal string that has been processed by the {@code decapitalize}
     * method with a defined Bean name (possibly with an application ID prefix and a fixed ending
     * tag), and then encrypted with MD5.
     */
    public static String decapitalizeDefinitionBeanName(Class<?> parent, String definitionBeanName,
                                                        @Nullable String applicationId) {
        return decapitalize(parent, definitionBeanName, applicationId, BEAN_NAME_CLOSE_TAG);
    }

    /**
     * Generate a specially processed MD5 encrypted hexadecimal string based
     * on the given class, alias, and application ID.
     *
     * <p>This method is mainly used to handle aliases, which call another
     * private method called {@link #decapitalize},and pass in the parent
     * class, alias, application ID, and a fixed ending tag is {@link #ALISA_NAME_CLOSE_TAG}.
     *
     * @param parent        the Class object of the parent class is used to
     *                      obtain the value (if any) of the {@code ServiceCollection}.
     * @param alisaName     the alias to be processed is usually the alias of
     *                      the component.
     * @param applicationId application ID, if not empty, will be used as a prefix
     *                      for generating strings; If it is empty or contains only
     *                      blank characters, ignore it.
     * @return Returns a hexadecimal string encrypted with MD5 after specific processing
     * by an alias (possibly with an application ID prefix and a fixed ending tag).
     */
    public static String decapitalizeAlisaName(Class<?> parent, String alisaName,
                                               @Nullable String applicationId) {
        return decapitalize(parent, alisaName, applicationId, ALISA_NAME_CLOSE_TAG);
    }

    /**
     * Generate an MD5 encrypted hexadecimal string based on the given class,
     * name, application ID, and ending tag.
     *
     * <p>The method first attempts to obtain the {@code ServiceCollection}
     * annotation from the given parent class, and uses the value or class
     * name in the annotation as the prefix for the service collection.
     * Then, the application ID (if not empty), prefix, original name, and
     * ending tag are concatenated to form the final string, which is then
     * MD5 encrypted.
     *
     * @param parent        the Class object of the parent class is used to
     *                      obtain the value (if any) of the {@code ServiceCollection}.
     * @param name          the original name to be processed is usually the name
     *                      of a property or method.
     * @param applicationId application ID, if not empty, will be used as a prefix
     *                      for generating strings; If it is empty or contains only
     *                      blank characters, ignore it.
     * @param closingTag    the end tag will be attached to the end of the processed
     *                      name.
     * @return Return a hexadecimal string encrypted by MD5 using the original name
     * (possibly with a prefix and ending label).
     * @throws NullPointerException if input parent or name or closingTag is null.
     */
    private static String decapitalize(Class<?> parent, String name, @Nullable String applicationId,
                                       String closingTag) {
        Objects.requireNonNull(parent, "parent = null");
        Objects.requireNonNull(name, "name = null");
        Objects.requireNonNull(closingTag, "closingTag =null");
        ServiceCollection serviceCollection = parent.getAnnotation(ServiceCollection.class);
        Assert.notNull(serviceCollection,
                "No annotation top.osjf.optimize.service_bean.annotation.ServiceCollection.");
        String value = serviceCollection.value();
        if (StringUtils.isBlank(value)) value = parent.getName();
        final String decapitalizeName = (StringUtils.isNotBlank(applicationId) ? applicationId : "")
                + value + name + closingTag;
        return DigestUtils.md5DigestAsHex(decapitalizeName.getBytes(StandardCharsets.UTF_8));
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
