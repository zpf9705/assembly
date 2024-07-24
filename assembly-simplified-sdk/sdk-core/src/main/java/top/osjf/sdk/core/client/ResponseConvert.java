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

package top.osjf.sdk.core.client;

import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;

/**
 * A custom processing scheme for response string conversion {@link Response}.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface ResponseConvert<R extends Response> {

    /**
     * Convert the response type {@link Request} of the request
     * class record to the specified {@link Response} using the
     * following custom method.
     *
     * <p>You can customize the conversion method based on the
     * predefined return values of the API.
     *
     * @param request     {@link Request} class model parameters of API.
     * @param responseStr String data for API response.
     * @return The converted response model data is implemented in
     * {@link Response} and cannot be {@literal null}.
     */
    R convertToResponse(Request<R> request, String responseStr);
}
