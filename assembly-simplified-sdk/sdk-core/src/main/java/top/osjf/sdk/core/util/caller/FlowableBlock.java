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

package top.osjf.sdk.core.util.caller;

import top.osjf.sdk.core.process.Response;

/**
 * The {@code FlowableBlock} interface defines a generic blocking transmission subscription
 * response result retrieval mechanism.
 *
 * <p>This interface is used to return a response result (R), where R is an instance of the
 * Response class or its subclass.
 * The class implementing this interface should provide a method 'get()' that blocks the current
 * thread until the subscriber completes sending and retrieves the response body object.
 *
 * @param <R> The type of response result must be the Response class or its subclass.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface FlowableBlock<R extends Response> {

    /**
     * Return the response result of subscription blocking transmission.
     *
     * @return a {@link Response}.
     */
    R get();

    /**
     * Return the response result of subscription blocking transmission
     * and convert it into the required type.
     *
     * @param requiredType the required type.
     * @param <T>          The type of required response type.
     * @return a required {@link Response}.
     * @throws ClassCastException Pay attention to the conversion of result
     *                            types, as this error will be thrown when the
     *                            provided types are inconsistent.
     */
    <T> T get(Class<T> requiredType);
}
