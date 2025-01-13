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
import top.osjf.sdk.core.RequestAttributes;
import top.osjf.sdk.core.RequestExecuteMetadata;
import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.caller.RequestCaller;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.support.SdkSupport;
import top.osjf.sdk.core.util.CollectionUtils;
import top.osjf.sdk.proxy.cglib.CglibDelegationCallback;
import top.osjf.sdk.proxy.jdk.JDKDelegationCallback;
import top.osjf.sdk.proxy.springcglib.SpringCglibDelegationCallback;

import java.lang.reflect.Method;
import java.util.List;

/**
 * The {@code ComprehensiveDelegationCallback} class is a callback class that
 * implements multiple proxy callback interfaces, mainly used to provide flexible
 * extension points during SDK request execution.
 *
 * <p>Through this type, custom logic can be executed before and after request
 * processing , such as {@code HandlerPostProcessor} pre-processing of request
 * parameters and post-processing of response results. In addition, this class
 * also supports setting the host name of the request, the caller {@code RequestCaller}
 * of the request, and the list of post processors.
 *
 * <p>This class implements {@code JDKDelegationCallback}, {@code CglibDelegationCallback},
 * {@code SpringCglibDelegate} callback interface, can be integrated with multiple
 * proxy frameworks to achieve unified management of proxy logic.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class ComprehensiveDelegationCallback implements RequestAttributes,
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
     * {@inheritDoc}
     */
    @Override
    public void setHost(@Nullable String host) {
        this.host = host;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void setRequestCaller(@Nullable RequestCaller requestCaller) {
        this.requestCaller = requestCaller;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
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
    public Object callback(Method method, Object[] args, PeculiarProxyVariable variable) throws Throwable {
        switch (method.getName()){
            case "toString": return toString();
            case "hashCode": return hashCode();
            case "equals": return equals(args[0]);
        }
        RequestExecuteMetadata metadata = SdkSupport.createRequest(method, args);
        Request<?> request = metadata.getRequest();
        if (CollectionUtils.isNotEmpty(postProcessors)) {
            for (HandlerPostProcessor postProcessor : postProcessors) {
                request = postProcessor.postProcessRequestBeforeHandle(request, method, args, variable);
            }
        }
        Response response;
        if (requestCaller == null) {
            response = request.execute(host);
        } else {
            response = requestCaller.resolveRequestExecuteWithOptions(metadata, host);
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
