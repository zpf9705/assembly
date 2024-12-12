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

import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.support.NotNull;

/**
 * The request method function interface, {@link #request()}, is the
 * entry point for the request.
 *
 * <p>This method does not have design parameters. In order to facilitate
 * the construction of static schemes, the parameters were initially encapsulated
 * in the thread class to better manage the current parameters.
 *
 * <p>The current parameters can be obtained at any time in various parts
 * of the cycle, which facilitates subsequent parameter extension operations.
 *
 * <p>In version 1.0.2, the {@code RequestBinder} binding interface is inherited
 * to make the {@code Client} binding of {@code Request} optional.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface RequestCore<R extends Response> extends RequestBinder<R> {

    /**
     * Return the response body {@code Response} of this {@code Client} request,
     * and use the parameter {@link top.osjf.sdk.core.process.Request} in the
     * binding settings and should call {@link #bindRequest} to bind parameters
     * before calling this method, or parameter exceptions will occur.
     *
     * @return response body {@code Response} of this request.
     */
    @NotNull
    R request();
}
