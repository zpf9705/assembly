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

import org.springframework.lang.NonNull;
import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.ResponseData;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;

import java.lang.reflect.Method;

/**
 * When processing SDK proxy requests, at the method level, after creating {@link Request},
 * this interface method {@link #postProcessRequestBeforeHandle} can be used to customize
 * the above {@link Request} parameters and obtain the final request parameter result, which
 * cannot be empty by default and returns the original created request parameter.
 *
 * <p>When processing SDK proxy response data, at the method level, after obtaining the
 * response result (specified data type for {@link Response} or {@link ResponseData}),
 * this interface method {@link #postProcessResultAfterHandle} can be used to customize
 * and modify the above response result parameters, and obtain the final request response
 * result. The default cannot be empty, and return the original processing to obtain the
 * request response output parameter.
 *
 * <p>Special customization can be applied to the result set based on the target type of
 * the proxy in the provided method and the method object of the proxy.
 *
 * <p>As mentioned above, there are some functions that are specified by the SDK itself,
 * but you can also adapt according to the situation, because this interface occurs during
 * the request runtime. You can use the relevant methods of this interface to add related
 * actions before and after the SDK request to enhance its functionality.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface HandlerPostProcessor {

    /**
     * Post-process the request before it is handled.
     *
     * <p>This method allows custom modifications or enhancements to the request
     * object before the target method is invoked.
     *
     * <p>The default implementation directly returns the original request object
     * without any modifications.
     *
     * @param request     the original request object.
     * @param proxyMethod the method being proxied.
     * @param args        the array of arguments passed when invoking the target method.
     * @param variable    proxy information other than proxy method objects
     *                    and parameters (provided by the underlying framework).
     * @return The processed request object, which should typically be the modified
     * request object or the original request object.
     */
    @NonNull
    default Request<?> postProcessRequestBeforeHandle(@NotNull Request<?> request, @NotNull Method proxyMethod,
                                                      @Nullable Object[] args,
                                                      @NotNull DelegationCallback.PeculiarProxyVariable variable) {
        return request;
    }

    /**
     * Post-process the result after the request is handled.
     *
     * <p>This method allows custom modifications or enhancements to the result
     * object after the target method is invoked and returns a result.
     *
     * <p>The default implementation directly returns the original result object
     * without any modifications.
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
