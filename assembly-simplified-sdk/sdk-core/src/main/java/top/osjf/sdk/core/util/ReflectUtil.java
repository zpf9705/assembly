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

package top.osjf.sdk.core.util;

import io.reactivex.rxjava3.functions.Supplier;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Reflection utility class, used to instantiate objects through
 * reflection mechanism.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SuppressWarnings("unchecked")
public abstract class ReflectUtil {

    /**
     * Instantiate an object of the specified type through reflection
     * without passing arguments.
     *
     * @param <T>  The type of the instance to instantiate.
     * @param type The {@code Class<T>} object representing the type of
     *             the instance to instantiate.
     * @return An instance of the specified type.
     * @throws NullPointerException If the input type is {@literal null}.
     */
    public static <T> T instantiates(@NotNull Class<T> type) {
        return instantiates(type, ArrayUtils.EMPTY_ARRAY);
    }

    /**
     * This method instantiates an object based on the provided
     * class name and class loader.
     *
     * @param className   The string representation of the class name.
     * @param classLoader A class loader used to load classes.
     * @param <T>         Represents the type of instantiated object.
     * @return The instantiated object cast to {@code T}.
     * @throws NullPointerException If the input className is {@literal null}.
     */
    public static <T> T instantiates(@NotNull String className, @Nullable ClassLoader classLoader) {
        return (T) instantiates(getClass(className, classLoader));
    }

    /**
     * Retrieve the {@code Class} object of the class based on the given
     * class name and class loader, and initialize the {@code Class}.
     *
     * @param className   The fully qualified name of the class to be loaded
     *                    (including package name and class name).
     * @param classLoader The class loader used to load classes, if it is
     *                    {@literal null}, will automatically select an
     *                    available {@code ClassLoader}.
     * @return The {@code Class} object of the loaded and initialized class.
     * @throws NullPointerException     If the input className is {@literal null}.
     * @throws IllegalArgumentException If the specified class cannot be found,
     *                                  throw this exception in its {@code #cause}.
     */
    public static Class<?> getClass(@NotNull String className, @Nullable ClassLoader classLoader) {
        classLoader = getAvailableClassLoader(classLoader);
        try {
            return Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(" Not found class " + className, e);
        }
    }

    /**
     * Retrieve the {@code Class} object of a class based on the given class
     * name and class loader, but it may not necessarily initialize the {@code Class}.
     *
     * @param className   The fully qualified name of the class to be loaded
     *                    (including package name and class name).
     * @param classLoader The class loader used to load classes, if it is
     *                    {@literal null}, will automatically select an
     *                    available {@code ClassLoader}.
     * @return The {@code Class} object of the loaded and initialized class.
     * @throws NullPointerException     If the input className is {@literal null}.
     * @throws IllegalArgumentException If the specified class cannot be found,
     *                                  throw this exception in its {@code #cause}.
     */
    public static Class<?> loadClass(@NotNull String className, @Nullable ClassLoader classLoader) {
        classLoader = getAvailableClassLoader(classLoader);
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(" Not found class " + className, e);
        }
    }

    /**
     * This method retrieves a valid class loader.
     *
     * @param classLoader The class loader provided by the user.
     * @return An effective class loader.
     * @throws IllegalArgumentException If both the provided class loader
     *                                  and thread context class loader are empty,
     *                                  and the system class loader cannot be used,
     *                                  this exception will be thrown.
     */
    public static ClassLoader getAvailableClassLoader(@Nullable ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
                if (classLoader == null) {
                    throw new IllegalArgumentException("No available classLoader");
                }
            }
        }
        return classLoader;
    }

    /**
     * Instantiate an object of the specified type through reflection
     * and pass arguments.
     *
     * @param <T>  The type of the instance to instantiate.
     * @param type The {@code Class<T>} object representing the type of the instance
     *             to instantiate.
     * @param args The argument array to pass to the constructor.
     * @return An instance of the specified type.
     * @throws NullPointerException     If the input type is {@literal null}.
     * @throws IllegalArgumentException If there is an error in object instantiation,
     *                                  please refer to {@link IllegalArgumentException#getCause()}
     *                                  for details.
     */
    public static <T> T instantiates(@NotNull Class<T> type, Object... args) {
        List<Class<?>> parameterTypes = new LinkedList<>();
        if (ArrayUtils.isNotEmpty(args)) {
            for (Object arg : args) {
                parameterTypes.add(arg.getClass());
            }
        }
        Supplier<T> instanceSupplier;
        if (parameterTypes.isEmpty()) {
            instanceSupplier = type::newInstance;
        } else {
            instanceSupplier =
                    () -> getConstructor(type, parameterTypes.toArray(new Class[]{})).newInstance(args);
        }
        try {
            return instanceSupplier.get();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Method not found : " + e.getMessage(), e);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e1) {
            throw new IllegalArgumentException("Construction method instantiation execution failed : "
                    + e1.getMessage(), e1);
        } catch (Throwable e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Get the constructor that matches the specified parameter types.
     *
     * @param <T>                 The type of the class to which the constructor belongs.
     * @param type                The class object for which to get the constructor.
     * @param inputParameterTypes The parameter types of the constructor to get.
     * @return The constructor that matches the specified parameter types.
     * @throws NullPointerException If the input type is {@literal null}.
     * @throws Exception            If an error occurs while searching for the constructor.
     */
    public static <T> Constructor<T> getConstructor(@NotNull Class<T> type, Class<?>... inputParameterTypes)
            throws Exception {
        Constructor<T> conformingConstructor = null;
        Exception directFindConstructorException = null;
        try {
            conformingConstructor = type.getConstructor(inputParameterTypes);
        } catch (Exception e) {
            if (ArrayUtils.isEmpty(inputParameterTypes)) throw e;
            directFindConstructorException = e;
        }
        if (conformingConstructor != null) return conformingConstructor;
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            Class<?>[] hasParameterTypes = constructor.getParameterTypes();
            if (hasParameterTypes.length == inputParameterTypes.length) {
                boolean compareResult = true;
                for (int i = 0; i < hasParameterTypes.length; i++) {
                    Class<?> hasParameterType = hasParameterTypes[i];
                    Class<?> inputParameterType = inputParameterTypes[i];
                    if (hasParameterType != inputParameterType &&
                            !hasParameterType.isAssignableFrom(inputParameterType)) {
                        compareResult = false;
                        break;
                    }
                }
                if (!compareResult) continue;
                conformingConstructor = (Constructor<T>) constructor;
                break;
            }
        }
        if (conformingConstructor == null) throw directFindConstructorException;
        return conformingConstructor;
    }

    /**
     * Retrieve the type at the specified index in the parent generic
     * {@code Class} of the object class.
     *
     * @param <R>         Generic return type, representing the type at the
     *                    specified index in the parent generic type.
     * @param targetClass target object class, used to obtain generic type information
     *                    of its parent class.
     * @param index       The value represents the position of the generic type
     *                    to be obtained in the parent class generic type list (starting from 0).
     * @return Returns generic type information at the specified index,
     * encapsulated as a Class object.
     * @throws NullPointerException If the input target class is {@literal null}.
     * @throws ClassCastException   if the obtained {@code Type} is not of type {@code Class}.
     */
    public static <R> Class<R> getSuperGenericClass(@NotNull Class<?> targetClass, int index) {
        return typeCastClass(getSuperGenericType(targetClass, index));
    }

    /**
     * Retrieve the type at the specified index in the parent generic
     * {@code Type} of the object class.
     *
     * @param targetClass target object class, used to obtain generic type information
     *                    of its parent class.
     * @param index       The value represents the position of the generic type
     *                    to be obtained in the parent class generic type list (starting from 0).
     * @return Returns generic type information at the specified index,
     * encapsulated as a Class object.
     * @throws NullPointerException      If the input target class is {@literal null}.
     * @throws IndexOutOfBoundsException The selected index exceeds the length
     *                                   of the parent class generic array.
     */
    public static Type getSuperGenericType(@NotNull Class<?> targetClass, int index) {
        return getSuperAllGenericType(targetClass)[index];
    }

    /**
     * Retrieve all generic type information of the parent class of the object class.
     *
     * @param targetClass target object class, used to obtain generic type information
     *                    of its parent class.
     * @return Return an array of generic type information for the parent class.
     * @throws NullPointerException If the input target class is {@literal null}.
     */
    public static Type[] getSuperAllGenericType(@NotNull Class<?> targetClass) {
        Type genericSuperclass = targetClass.getGenericSuperclass();
        return getActualGenericTypes(genericSuperclass);
    }

    /**
     * The returned type is a single generic {@code Class}  obtained by taking the {@code index}
     * specified index from all generic arrays that implement the {@code interfaceIndex}
     * position interface specified by the target object class.
     *
     * @param <R>            Generic return type, representing the type at the
     *                       specified index in the interface generic type.
     * @param targetClass    is the target object class used to obtain the interface information
     *                       of its implementation.
     * @param interfaceIndex specifies the index of the interface to obtain generic type
     *                       parameters in the interface array implemented by the target object.
     * @param index          This parameter is the index position of the parameter to be taken
     *                       from the array of all generic parameters of the specified implementation
     *                       location interface obtained by parameter {@code interfaceIndex}.
     * @return an array containing generic type parameters for a specified interface.
     * @throws NullPointerException If the input target class is {@literal null}.
     * @throws ClassCastException   If the length of the reflection parameter for obtaining
     *                              the specified implementation index position interface
     *                              is less than or equal to the length of the input third
     *                              method parameter {@code index}.
     */
    public static <R> Class<R> getIndexedInterfaceGenericClass(@NotNull Class<?> targetClass, int interfaceIndex,
                                                               int index) {
        return typeCastClass(getIndexedInterfaceGenericType(targetClass, interfaceIndex, index));
    }

    /**
     * Cast a {@code Type} to {@code Class},if <pre>{@code type instanceof Class}</pre>
     *
     * @param type input {@code Type}.
     * @param <R>  Generic return type.
     * @return cast result be a {@code Class}.
     * @throws ClassCastException if the obtained {@code Type} is not of type {@code Class}.
     */
    private static <R> Class<R> typeCastClass(Type type) {
        if (type instanceof Class) {
            return (Class<R>) type;
        }
        throw new ClassCastException(type.getClass().getName() + " cannot be cast to " + Class.class.getName());
    }

    /**
     * The returned type is a single generic {@code Type} obtained by taking the {@code index}
     * specified index from all generic arrays that implement the {@code interfaceIndex}
     * position interface specified by the target object class.
     *
     * @param targetClass    is the target object class used to obtain the interface information
     *                       of its implementation.
     * @param interfaceIndex specifies the index of the interface to obtain generic type
     *                       parameters in the interface array implemented by the target object class.
     * @param index          This parameter is the index position of the parameter to be taken
     *                       from the array of all generic parameters of the specified implementation
     *                       location interface obtained by parameter {@code interfaceIndex}.
     * @return an array containing generic type parameters for a specified interface.
     * @throws NullPointerException      If the input target class is {@literal null}.
     * @throws IndexOutOfBoundsException If the length of the reflection parameter for obtaining
     *                                   the specified implementation index position interface
     *                                   is less than or equal to the length of the input third
     *                                   method parameter {@code index}.
     */
    public static Type getIndexedInterfaceGenericType(@NotNull Class<?> targetClass, int interfaceIndex, int index) {
        return getIndexedInterfaceAllGenericType(targetClass, interfaceIndex)[index];
    }

    /**
     * Retrieve all generic type parameters of the specified implementation location
     * index interface implemented by the target object class.
     *
     * @param targetClass    is the target object class used to obtain the interface information
     *                       of its implementation.
     * @param interfaceIndex specifies the index of the interface to obtain generic type
     *                       parameters in the interface array implemented by the target object class.
     * @return an array containing generic type parameters for a specified interface.
     * @throws NullPointerException      If the input target class is {@literal null}.
     * @throws IllegalArgumentException  If the input target class does not implement any interface.
     * @throws IndexOutOfBoundsException If the input index is greater than or equal to the length
     *                                   of the generic interface array obtained.
     */
    public static Type[] getIndexedInterfaceAllGenericType(@NotNull Class<?> targetClass, int interfaceIndex) {
        Type[] genericInterfaces = targetClass.getGenericInterfaces();
        if (ArrayUtils.isEmpty(genericInterfaces))
            throw new IllegalArgumentException(targetClass + " no implements interfaces");
        return getActualGenericTypes(genericInterfaces[interfaceIndex]);
    }

    /**
     * Return the generic actual {@code Type} parameters of the given {@code Type}.
     *
     * @param type input getting {@code Type}.
     * @return Return an array of generic type information for the input type.
     * @throws NullPointerException     If the input type is {@literal null}.
     * @throws IllegalArgumentException If the generic type information of the
     *                                  {@code type} is empty or the index is out of range.
     * @throws IllegalStateException    If the {@code type} is not a {@code java.lang.reflect
     *                                  .ParameterizedType} type (i.e. there is no generic information).
     */
    public static Type[] getActualGenericTypes(@NotNull Type type) {
        if (type instanceof ParameterizedType) {
            Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            if (ArrayUtils.isEmpty(actualTypeArguments)) {
                throw new IllegalArgumentException(type + " generic type is empty");
            }
            return actualTypeArguments;
        }
        throw new IllegalStateException(type + " is not java.lang.reflect.ParameterizedType");
    }

    /**
     * Get all declared fields (including those from parent classes) for the given
     * target class.
     *
     * @param targetClass the target object class.
     * @return all declared fields (including those from parent classes).
     */
    public static List<Field> getAllDeclaredFields(Class<?> targetClass) {
        List<Field> allDeclaredFields = new ArrayList<>();
        Class<?> searchType = targetClass;
        while (searchType != null) {
            Field[] declaredFields = searchType.getDeclaredFields();
            if (ArrayUtils.isNotEmpty(declaredFields)) {
                allDeclaredFields.addAll(Arrays.asList(declaredFields));
            }
            searchType = Object.class.equals(searchType.getSuperclass()) ? null : searchType.getSuperclass();
        }
        return allDeclaredFields;
    }

    /**
     * Get all declared methods (including those from parent classes) for the
     * given target class.
     *
     * @param targetClass the target object class.
     * @return all declared methods (including those from parent classes).
     */
    public static List<Method> getAllDeclaredMethods(Class<?> targetClass) {
        List<Method> allDeclaredMethods = new ArrayList<>();
        Class<?> searchType = targetClass;
        while (searchType != null) {
            Method[] declaredMethods = searchType.getDeclaredMethods();
            if (ArrayUtils.isNotEmpty(declaredMethods)) {
                allDeclaredMethods.addAll(Arrays.asList(declaredMethods));
            }
            searchType = Object.class.equals(searchType.getSuperclass()) ? null : searchType.getSuperclass();
        }
        return allDeclaredMethods;
    }

    /**
     * Use reflection mechanism to perform the assignment operation of the target field.
     *
     * @param target    operate the target object.
     * @param fieldName operate the target field name.
     * @param arg       the assigned value.
     * @throws IllegalStateException    if invoke method failed to find cause.
     * @throws IllegalArgumentException by field set throw.
     */
    public static void setFieldValue(Object target, String fieldName, Object arg) {
        try {
            Field field = target.getClass().getField(fieldName);
            field.setAccessible(true);
            field.set(target, arg);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("set field failed ", e);
        }
    }

    /**
     * Use reflection mechanism to perform the assignment operation of the target field.
     *
     * @param target operate the target object.
     * @param field  operate the target field.
     * @param arg    the assigned value.
     * @throws IllegalStateException    if invoke method failed to find cause.
     * @throws IllegalArgumentException by field set throw.
     */
    public static void setFieldValue(Object target, Field field, Object arg) {
        try {
            field.setAccessible(true);
            field.set(target, arg);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("set field failed ", e);
        }
    }

    /**
     * Returns the value of executing the target method using reflection mechanism.
     *
     * @param target operate the target object.
     * @param method operate the target method.
     * @param args   the execution parameters of the target operation method.
     * @return The return value after executing the target method.
     * @throws IllegalStateException    if invoke method failed to find cause.
     * @throws IllegalArgumentException by method invoke throw.
     */
    @Nullable
    public static Object invokeMethod(Object target, Method method, Object... args) {
        try {
            method.setAccessible(true);
            return method.invoke(target, args);
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException("invoke method failed ", e);
        }
    }
}
