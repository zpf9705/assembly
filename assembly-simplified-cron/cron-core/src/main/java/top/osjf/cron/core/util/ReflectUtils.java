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


package top.osjf.cron.core.util;

import top.osjf.cron.core.lang.Nullable;

import java.lang.reflect.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Simple utility class for working with the reflection API and handling
 * reflection exceptions.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ReflectUtils {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /**
     * Invoke the specified {@link Method} against the supplied target object with no arguments.
     * The target object can be {@code null} when invoking a static {@link Method}.
     *
     * <p>Copy from {@code org.springframework.util.ReflectionUtils}
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @return the invocation result, if any
     */
    @Nullable
    public static Object invokeMethod(Object target, Method method) {
        return invokeMethod(target, method, EMPTY_OBJECT_ARRAY);
    }

    /**
     * Invoke the specified {@link Method} against the supplied target object with the
     * supplied arguments. The target object can be {@code null} when invoking a
     * static {@link Method}.
     *
     * <p>Copy from {@code org.springframework.util.ReflectionUtils}
     *
     * @param method the method to invoke
     * @param target the target object to invoke the method on
     * @param args   the invocation arguments (may be {@code null})
     * @return the invocation result, if any.
     */
    @Nullable
    public static Object invokeMethod(Object target, Method method, Object... args) {
        try {
            makeAccessible(method);
            return method.invoke(target, args);
        } catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException) targetException;
            }
            if (targetException instanceof Error) {
                throw (Error) targetException;
            }
            throw new UndeclaredThrowableException(targetException);
        } catch (IllegalAccessException ex) {
            throw new UndeclaredThrowableException(ex);
        }
    }

    /**
     * Make the given method accessible, explicitly setting it accessible if
     * necessary. The {@code setAccessible(true)} method is only called
     * when actually necessary, to avoid unnecessary conflicts with a JVM
     * SecurityManager (if active).
     *
     * <p>Copy from {@code org.springframework.util.ReflectionUtils}
     *
     * @param method the method to make accessible
     */
    public static void makeAccessible(Method method) {
        if ((!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers())) && !method.isAccessible()) {
            method.setAccessible(true);
        }
    }

    /**
     * Determine whether the given class has a public method with the given signature,
     * and return it if available (else throws an {@code IllegalStateException}).
     * <p>In case of any signature specified, only returns the method if there is a
     * unique candidate, i.e. a single public method with the specified name.
     * <p>Essentially translates {@code NoSuchMethodException} to {@code IllegalStateException}.
     *
     * <p>Copy from {@code org.springframework.util.ClassUtils}
     *
     * @param clazz      the clazz to analyze
     * @param methodName the name of the method
     * @param paramTypes the parameter types of the method
     *                   (may be {@code null} to indicate any signature)
     * @return the method (never {@code null})
     * @throws IllegalStateException if the method has not been found
     */
    public static Method getMethod(Class<?> clazz, String methodName, @Nullable Class<?>... paramTypes) {
        Objects.requireNonNull(clazz, "Class must not be null");
        Objects.requireNonNull(methodName, "Method name must not be null");
        if (paramTypes != null) {
            try {
                return clazz.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException("Expected method not found: " + ex);
            }
        } else {
            Set<Method> candidates = findMethodCandidatesByName(clazz, methodName);
            if (candidates.size() == 1) {
                return candidates.iterator().next();
            } else if (candidates.isEmpty()) {
                throw new IllegalStateException("Expected method not found: " + clazz.getName() + '.' + methodName);
            } else {
                throw new IllegalStateException("No unique method found: " + clazz.getName() + '.' + methodName);
            }
        }
    }

    /**
     * Use the API {@code forName} of {@link Class} to obtain a {@code Class} object with
     * a given name without static initialization.
     *
     * @param className the given class name.
     * @return a {@code Class} object by given name.
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    /**
     * Create a new {@code T} object by given {@code Class<T>} according to {@link Class#newInstance()}.
     *
     * @param clazz given type.
     * @param <T>   given generic type.
     * @return new {@code T} object by given {@code Class<T>}.
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    /**
     * Create a new {@code T} object by given {@code Constructor<T>} according to
     * {@link Constructor#newInstance(Object[])}.
     *
     * @param constructor given {@code Constructor}.
     * @param <T>         given generic type.
     * @return new {@code T} object by given {@code Constructor<T>}.
     */
    public static <T> T newInstance(Constructor<T> constructor, Object... initArgs) {
        try {
            return constructor.newInstance(initArgs);
        } catch (InvocationTargetException ex) {
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw (RuntimeException) targetException;
            }
            if (targetException instanceof Error) {
                throw (Error) targetException;
            }
            throw new UndeclaredThrowableException(targetException);
        } catch (IllegalAccessException | InstantiationException ex) {
            throw new UndeclaredThrowableException(ex);
        }
    }

    /*
     * Non Java doc.
     * Copy from {@code org.springframework.util.ClassUtils}
     */
    private static Set<Method> findMethodCandidatesByName(Class<?> clazz, String methodName) {
        Set<Method> candidates = new HashSet<>(1);
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                candidates.add(method);
            }
        }
        return candidates;
    }
}
