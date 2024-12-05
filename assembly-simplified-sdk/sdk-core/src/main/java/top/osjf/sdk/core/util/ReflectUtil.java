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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

    /*** Empty static Object array.*/
    private static final Object[] EMPTY = new Object[]{};

    /**
     * Instantiate an object of the specified type through reflection
     * without passing arguments.
     *
     * @param <T>  The type of the instance to instantiate.
     * @param type The {@code Class<T>} object representing the type of
     *             the instance to instantiate.
     * @return An instance of the specified type.
     */
    public static <T> T instantiates(@NotNull Class<T> type) {
        return instantiates(type, EMPTY);
    }

    /**
     * This method instantiates an object based on the provided
     * class name and class loader.
     *
     * @param className   The string representation of the class name.
     * @param classLoader A class loader used to load classes.
     * @param <T>         Represents the type of instantiated object.
     * @return The instantiated object cast to {@code T}.
     */
    public static <T> T instantiates(@NotNull String className, @Nullable ClassLoader classLoader) {
        classLoader = getAvailableClassLoader(classLoader);
        Class<?> clazz;
        try {
            clazz = classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(" Not found class " + className, e);
        }
        return (T) instantiates(clazz);
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
     * @throws Exception If an error occurs while searching for the constructor.
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
}
