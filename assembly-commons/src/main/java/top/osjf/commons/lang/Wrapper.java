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


package top.osjf.commons.lang;

import java.util.Optional;

/**
 * The {@code Wrapper} interface defines a pattern that allows objects to implement wrapping
 * and unwrapping functionalities. It provides generic support through default methods,
 * enabling any object implementing this interface to determine if it can be considered
 * as a wrapper for a specific type and to convert (unwrap) itself into the specified
 * type, supporting multi-level nested wrapper recursive unwrapping.
 *
 * <p>The design of this interface is referenced from JDBC {@link java.sql.Wrapper},
 * based on the Adapter Pattern and type-safe conversion idea, providing flexible type
 * conversion capability for wrapped objects.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public interface Wrapper {

    /**
     * Determines whether the current object or its nested wrapped object
     * can be converted to the specified target type.
     *
     * @param clazz the target class type to check, cannot be {@code null}
     * @param <T>   generic target type
     * @return {@code true} if support unwrap to target type, otherwise {@code false}
     * @throws IllegalArgumentException if clazz is {@code null}
     */
    default <T> boolean isWrapperFor(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Target class must not be null");
        }
        if (clazz.isInstance(this)) {
            return true;
        }
        Object delegate = unwrapIfAvailable();
        return delegate instanceof Wrapper
                ? ((Wrapper) delegate).isWrapperFor(clazz)
                : clazz.isInstance(delegate);
    }

    /**
     * Unwrap the current wrapper object recursively to the specified target type.
     * Supports multi-level nested wrapper unwrapping.
     *
     * @param clazz target class type to unwrap, cannot be {@code null}
     * @param <T>   generic target type
     * @return unwrapped target instance
     * @throws IllegalArgumentException if clazz is {@code null}
     * @throws ClassCastException       if cannot unwrap to target type
     */
    default <T> T unwrap(Class<T> clazz) {
        if (!isWrapperFor(clazz)) {
            throw new ClassCastException(
                    String.format("Cannot unwrap [%s] to target type [%s]",
                            this.getClass().getName(), clazz.getName())
            );
        }
        if (clazz.isInstance(this)) {
            return clazz.cast(this);
        }
        Object delegate = unwrapIfAvailable();
        return delegate instanceof Wrapper
                ? ((Wrapper) delegate).unwrap(clazz)
                : clazz.cast(delegate);
    }

    /**
     * Safe unwrap with {@link Optional}, avoid manual {@link #isWrapperFor} judgment.
     *
     * @param clazz target type class
     * @param <T>   target generic type
     * @return optional target instance
     */
    default <T> Optional<T> optionalUnwrap(Class<T> clazz) {
        return isWrapperFor(clazz) ? Optional.of(unwrap(clazz)) : Optional.empty();
    }

    /**
     * Get the internal delegate wrapped object.
     * <p>
     * Default return {@code this}, override this method when holding a delegate object.
     *
     * @return inner delegate object
     */
    default Object unwrapIfAvailable() {
        return this;
    }
}
