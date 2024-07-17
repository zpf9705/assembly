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

package top.osjf.sdk.spring.beans;

import top.osjf.sdk.core.util.NotNull;
import top.osjf.sdk.core.util.Nullable;
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.ResponseData;

import java.lang.reflect.Method;

/**
 * When processing SDK proxy requests, at the method level, after creating {@link Request},
 * this interface method {@link #postProcessRequestBeforeHandle(Request, Class, Method,Object[])}
 * can be used to customize the above {@link Request} parameters and obtain the final
 * request parameter result, which cannot be empty by default and returns the original
 * created request parameter.
 *
 * <p>When processing SDK proxy response data, at the method level, after obtaining the
 * response result (specified data type for {@link top.osjf.sdk.core.process.Response}
 * or {@link ResponseData}), this interface method {@link #postProcessResultAfterHandle}
 * can be used to customize and modify the above response result parameters, and obtain the
 * final request response result. The default cannot be empty, and return the original
 * processing to obtain the request response output parameter.
 *
 * <p>Special customization can be applied to the result set based on the target type of
 * the proxy in the provided method and the method object of the proxy.
 *
 * <p>When specifying multiple {@link HandlerPostProcessor}, if you want to specify the
 * execution order, you can use the concept of {@link org.springframework.core.annotation.Order}
 * to specify it. Support for it has been added because the implementation class of this
 * interface requires adding a Spring container to take effect.
 *
 * <p>As mentioned above, there are some functions that are specified by the SDK itself,
 * but you can also adapt according to the situation, because this interface occurs during
 * the request runtime. You can use the relevant methods of this interface to add related
 * actions before and after the SDK request to enhance its functionality.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface HandlerPostProcessor {

    /**
     * You can return a new {@link Request} in this method, or you can
     * use relevant information to record and customize some of your
     * actions before the request without customizing the parameters.
     *
     * @param request     The original request parameters.
     * @param targetType  The target type being represented.
     * @param proxyMethod The target method of the agent.
     * @param args        The real parameters executed by the
     *                    proxy method.
     * @return Customized request parameters.
     */
    @NotNull
    default Request<?> postProcessRequestBeforeHandle(Request<?> request, Class<?> targetType, Method proxyMethod,
                                                      @Nullable Object[] args) {
        return request;
    }

    /**
     * In this method, a new response result can be returned, and the input
     * type is determined based on your requirements for the corresponding
     * action. Of course, customization is not necessary, and relevant
     * information can be used to record and customize some of your actions
     * after the request.
     *
     * @param result      The original corresponding parameters.
     * @param targetType  The target type being represented.
     * @param proxyMethod The target method of the agent.
     * @param args        The real parameters executed by the
     *                    proxy method.
     * @return Customized response result.
     */
    @Nullable
    default Object postProcessResultAfterHandle(@Nullable Object result, Class<?> targetType, Method proxyMethod,
                                                @Nullable Object[] args) {
        return result;
    }

    /**
     * Returns the proxy type specified by this {@link HandlerPostProcessor}.
     * <p>If you specify this type, then this {@link HandlerPostProcessor} will
     * only serve this target type, and by default {@literal null} will serve
     * all target types.
     *
     * @return the proxy type specified by this {@link HandlerPostProcessor}.
     */
    default Class<?> appointTarget() {
        return null;
    }
}
