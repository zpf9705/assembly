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

package top.osjf.sdk.proxy;

import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.ResponseData;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.lang.Nullable;

import java.lang.reflect.Method;

/**
 * The {@code HandlerPostProcessor} interface provides extension points for customizing the handling
 * of requests and responses during the SDK proxy request processing flow, allowing custom logic to
 * be inserted at different stages of request execution.
 * <p>
 * <strong>When processing SDK proxy requests, the method-level processing flow is as follows:
 * </strong>
 * <p> After creating the {@link Request} object, the method {@link #postProcessRequestBeforeHandle}
 * of this interface is called.
 * <ul>
 * <li>This method allows custom modifications or enhancements to the {@link Request} object before
 * the target method is invoked.</li>
 * <li>The default implementation returns the original request object without any modifications.</li>
 * <li>The return value must not be null and typically returns the modified request object or the
 * original request object.</li>
 * </ul>
 * <p>
 * <strong>When processing SDK proxy responses, the method-level processing flow is as follows:</strong>
 * <p>After obtaining the response result (i.e., the data returned by the target method, which may be
 * of type {@link Response} or its subclasses like {@link ResponseData}), the method
 * {@link #postProcessResultAfterHandle} of this interface is called.
 * <ul>
 * <li>This method allows custom modifications or enhancements to the response result after the
 * target method is invoked and returns a result.</li>
 * <li>The default implementation returns the original response result without any modifications.</li>
 * <li>The return value can be the modified result object or the original result object.</li>
 * </ul>
 *
 * <p>Custom logic can be implemented based on the target type of the proxy and the metadata of the
 * proxy method (such as the method object and parameters).For example, request parameters or response
 * results can be dynamically adjusted based on the method signature or parameter types.
 * <p>
 * <h2>Notes:</h2>
 * <ul>
 * <li>The methods of this interface are called during request runtime, so they can be used to add
 * additional operations before and after SDK requests,thereby enhancing the functionality of the SDK.</li>
 * <li>Some functionalities may be provided by the SDK itself, but you can adapt and extend them according
 * to your actual needs.</li>
 * </ul>
 * <p>
 * <strong>Typical use cases include:</strong>
 * <ul>
 * <li>Adding authentication information, logging, or modifying request parameters before sending the request
 * .</li>
 * <li>Parsing response data, handling errors, or transforming result formats after receiving the response.
 * </li>
 * </ul>
 * <p>
 * Multiple instances of {@code HandlerPostProcessor} are executed, the order of execution is determined
 * by the developer and is not specified here.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface HandlerPostProcessor {

    /**
     * Post-processes the request object before it is handled.
     * <p>This method allows custom modifications or enhancements to the {@link Request} object
     * before the target method is invoked.For example, additional request headers can be added,
     * request parameters can be modified, or logs can be recorded.
     * <p>The default implementation returns the original request object without any modifications.
     *
     * @param request     the original request object.
     * @param proxyMethod the method being proxied.
     * @param args        the array of arguments passed when invoking the target method.
     * @param variable    proxy information other than proxy method objects
     *                    and parameters (provided by the underlying framework).
     * @return The processed request object, which should typically be the modified
     * request object or the original request object.
     */
    @NotNull
    default Request<?> postProcessRequestBeforeHandle(@NotNull Request<?> request, @NotNull Method proxyMethod,
                                                      @Nullable Object[] args,
                                                      @NotNull DelegationCallback.PeculiarProxyVariable variable) {
        return request;
    }

    /**
     * Post-processes the response result after it is handled.
     * <p>This method allows custom modifications or enhancements to the response result after
     * the target method is invoked and returns a result.For example, response data can be parsed,
     * exceptions can be handled, or result formats can be transformed.
     * <p>The default implementation returns the original response result without any modifications.
     *
     * @param result      the result object returned by the target method
     *                    (Not necessarily {@code Response}, it is necessary to
     *                    understand the existence and function of its derived interfaces
     *                    {@code ResponseData} and {@code InspectionResponseData}).
     * @param request     the original request object.
     * @param proxyMethod the method being proxied.
     * @param args        the array of arguments passed when invoking the
     *                    target method.
     * @param variable    proxy information other than proxy method objects
     *                    and parameters (provided by the underlying framework).
     * @return The processed result object, which should typically be the modified
     * result object or the original result object
     */
    @Nullable
    default Object postProcessResultAfterHandle(@Nullable Object result, @NotNull Request<?> request,
                                                @NotNull Method proxyMethod,
                                                @Nullable Object[] args,
                                                @NotNull DelegationCallback.PeculiarProxyVariable variable) {
        return result;
    }
}
