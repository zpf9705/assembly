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

package top.osjf.sdk.core.process;

/**
 * Wrapper Interface: Defines the basic behavior of a wrapper, allowing objects to
 * be type-cast and unwrapped at runtime.
 *
 * <p>This interface is primarily used for wrapper classes that need to dynamically
 * determine their specific implementation type.
 * The {@code unwrap} method allows converting the wrapper object to its actual
 * wrapped object type (if the types match).
 *
 * <p>Define the type conversion interface implemented by {@link Request} and
 * {@link Response},such as
 * <pre>
 *     {@code
 *     Request<R> request = new ExampleHttpRequest();
 *     HttpRequest<R> httpRequest = request.unwrap(HttpRequest.class);
 *     }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface Wrapper {

    /**
     * Checks whether the current wrapper object wraps an object of the specified type.
     *
     * <p>The specific implementation of this method should be based on the actual logic
     * within the wrapper.
     * For example, if the wrapper internally holds a reference to a specific type of object,
     * this method should return true; otherwise, false.
     *
     * @param clazz The Class object of the type to be checked
     * @return True if the current wrapper object wraps an object of the specified type;
     * otherwise, false
     */
    boolean isWrapperFor(Class<?> clazz);

    /**
     * Default method: Attempts to unwrap the current wrapper object to the specified type.
     *
     * <p>This method achieves type casting by invoking the cast method of the Class object.
     * If the current object cannot be successfully converted to the specified type, a
     * {@code ClassCastException} will be thrown.
     *
     * @param <T>   The target type.
     * @param clazz The Class object of the target type.
     * @return The unwrapped object if the types match; otherwise, throws {@code ClassCastException}.
     * @throws ClassCastException If the current object cannot be converted to the
     *                            specified type.
     */
    default <T> T unwrap(Class<T> clazz) throws ClassCastException {
        return clazz.cast(this);
    }
}
