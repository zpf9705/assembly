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
import top.osjf.sdk.core.caller.Callback;
import top.osjf.sdk.core.caller.RequestCaller;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.support.SdkSupport;
import top.osjf.sdk.core.util.CollectionUtils;
import top.osjf.sdk.core.util.Pair;
import top.osjf.sdk.proxy.cglib.CglibDelegationCallback;
import top.osjf.sdk.proxy.jdk.JDKDelegationCallback;
import top.osjf.sdk.proxy.springcglib.SpringCglibDelegationCallback;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class ComprehensiveDelegationCallback implements
        JDKDelegationCallback, CglibDelegationCallback, SpringCglibDelegationCallback {

    /**
     * A {@code host} for sdk execute.
     */
    @Nullable
    private String host;

    /**
     * {@code HandlerPostProcessor} list for enhance the relevant
     * functions of SDK execution.
     */
    @Nullable
    private List<HandlerPostProcessor> postProcessors;

    /**
     * A {@code RequestCaller} for sdk execute.
     */
    @Nullable
    private RequestCaller requestCaller;

    /**
     * Empty constructor, all related properties of this callback class
     * are optional and can be set using the set method according to one's
     * own needs.
     */
    public ComprehensiveDelegationCallback() {
    }

    /**
     * Sets the host name.
     * <p>
     * This method is used to set the host name for the
     * current object. If the passed host parameter is null,
     * it means no host name is set.
     *
     * @param host The host name, which can be null.
     */
    public void setHost(@Nullable String host) {
        this.host = host;
    }

    /**
     * Gets the host name.
     * <p>
     * This method is used to retrieve the host name set
     * for the current object. If no host name has been
     * set before, it will return null.
     *
     * @return The host name, which may be null.
     */
    @Nullable
    public String getHost() {
        return host;
    }

    /**
     * Sets the list of post-processors {@code HandlerPostProcessor}.
     * <p>
     * This method is used to set a set of post-processors {@code HandlerPostProcessor}
     * for the current object. These post-processors can perform some additional
     * logic after the request processing is completed.
     * If the passed postProcessors parameter is null, it means no post-processor
     * list is set.
     *
     * @param postProcessors The list of post-processors, which can be null.
     */
    public void setPostProcessors(@Nullable List<HandlerPostProcessor> postProcessors) {
        this.postProcessors = postProcessors;
    }

    /**
     * Gets the list of post-processors {@code HandlerPostProcessor}.
     *
     * <p>This method is used to retrieve the list of post-processors
     * {@code HandlerPostProcessor} set for the current object. If no
     * post-processor list has been set before, it will return null.
     *
     * @return The list of post-processors, which may be null.
     */
    @Nullable
    public List<HandlerPostProcessor> getPostProcessors() {
        return postProcessors;
    }

    /**
     * Sets the request caller {@code RequestCaller}.
     *
     * <p>This method is used to set a request caller {@code RequestCaller}
     * for the current object,if the passed {@code RequestCaller} parameter
     * is null, it means no request caller is set.
     *
     * @param requestCaller The request caller, which can be null.
     */
    public void setRequestCaller(@Nullable RequestCaller requestCaller) {
        this.requestCaller = requestCaller;
    }

    /**
     * Gets the request caller {@code RequestCaller}.
     *
     * <p>This method is used to retrieve the request caller
     * {@code RequestCaller} set for the current object. If no
     * request caller has been set before, it will return null.
     *
     * @return The request caller, which may be null.
     */
    @Nullable
    public RequestCaller getRequestCaller() {
        return requestCaller;
    }

    /**
     * The relevant logic for executing SDK requests includes
     * the following steps:
     * <ul>
     *     <li>Parse method parameters and create request base class {@code Request}.</li>
     *     <li>Execute the pre method of the processor and return the enhanced base class
     *     instance {@code Request}.</li>
     *     <li>Execute SDK related requests based on the presence or absence of {@code RequestCaller}
     *     instances.</li>
     *     <li>Parse the corresponding data based on the type of response {@code Response}
     *     returned.</li>
     *     <li>Execute the post-processing method of the processor and return an enhanced SDK
     *     return value.</li>
     * </ul>
     *
     * @param method   {@inheritDoc}
     * @param args     {@inheritDoc}
     * @param variable {@inheritDoc}
     * @return The return value of SDK execution completion.
     * @throws Throwable {@inheritDoc}
     */
    @Override
    public Object callback(Method method, Object[] args, SpecificProxyOtherVariable variable) throws Throwable {
        Pair<Request<?>, List<Callback>> pair = SdkSupport.createRequest(method, args);
        Request<?> request = pair.getFirst();
        if (CollectionUtils.isNotEmpty(postProcessors)) {
            for (HandlerPostProcessor postProcessor : postProcessors) {
                request = postProcessor.postProcessRequestBeforeHandle(request, method, args, variable);
            }
        }
        List<Callback> callbacks = pair.getSecond();
        Response response;
        if (requestCaller == null) {
            response = request.execute(host);
        } else {
            response = requestCaller.resolveRequestExecuteWithOptions(request, host, method, callbacks);
        }
        Object result = SdkSupport.resolveResponse(method, response);
        if (CollectionUtils.isNotEmpty(postProcessors)) {
            for (HandlerPostProcessor postProcessor : postProcessors) {
                result = postProcessor.postProcessResultAfterHandle(result, request, method, args, variable);
            }
        }
        return result;
    }
}
