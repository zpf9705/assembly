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


package top.osjf.cron.core.lang;

/**
 * The Wrapper interface defines a pattern that allows objects to implement wrapping
 * and unwrapping functionalities.It provides generic support through default methods,
 * enabling any object implementing this interface to determine if it can be considered
 * as a wrapper for a specific type and to convert (unwrap) itself into the specified
 * type.
 *
 * <p>The design of this interface is based on the Adapter Pattern and the idea of
 * type-safe conversion,providing a flexible way to handle different types of wrapped
 * objects without the need for explicit type checking and conversion at runtime.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface Wrapper {

    /**
     * Determines if the current object is a wrapper for the given type.
     *
     * <p>This method checks if the current object is an instance of the type
     * represented by the {@code clazz} parameter.
     *
     * @param clazz the class type to check against.
     * @param <T>   the generic type, representing the wrapped type to check.
     * @return {@code true} if the current object is an instance of the {@code clazz} type;
     * otherwise, {@code false}.
     */
    default <T> boolean isWrapperFor(Class<T> clazz) {
        return clazz.isInstance(this);
    }

    /**
     * Converts the current object into the specified type {@code T}.
     *
     * <p>This method attempts to cast the current object to the type represented by
     * the {@code clazz} parameter.
     *
     * <p>Before using this method, a type check should be performed using the
     * {@code isWrapperFor} method to ensure type safety.
     *
     * @param clazz the class type to convert into.
     * @param <T>   the generic type, representing the type to convert into.
     * @return The converted object.
     * @throws ClassCastException If the current object cannot be converted to the
     *                            {@code clazz} type, this exception is thrown.
     */
    default <T> T unwrap(Class<T> clazz) {
        if (!isWrapperFor(clazz)) {
            throw new ClassCastException(getClass().getName() + " cannot be cast to " + clazz.getName());
        }
        return clazz.cast(this);
    }
}
