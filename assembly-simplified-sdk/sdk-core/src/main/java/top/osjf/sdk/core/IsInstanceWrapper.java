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


package top.osjf.sdk.core;

/**
 * The {@code IsInstanceWrapper} interface - Wraps type checking functionality.
 *
 * <p>This is a generic interface that extends the Wrapper interface, providing
 * a mechanism to check if a certain object (typically the object implementing
 * this interface) is an instance of a specific class. It defaults to using the
 * built-in {@link Class#isInstance(Object)} method to achieve this functionality.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface IsInstanceWrapper extends Wrapper {

    /**
     * {@inheritDoc}
     * <p>
     * Default to use {@link Class#isInstance(Object)}.
     *
     * @param clazz {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    default boolean isWrapperFor(Class<?> clazz) {
        return clazz.isInstance(this);
    }
}
