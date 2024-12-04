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

package top.osjf.sdk.http.process;

import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.core.process.AbstractRequestParams;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.support.HttpSdkSupport;
import top.osjf.sdk.http.client.DefaultHttpClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code AbstractHttpRequestParams} is an abstract class about HTTP request
 * parameter encapsulation, which is an abstract extension of {@code AbstractRequestParams}.
 * <p>
 * It mainly has the following default points:
 * <ul>
 *     <li>{@link #getHeadMap()}By default, without any parameter related concatenation
 *     requirements, {@code Content-Type} is parsed based on the parameter string type
 *     and placed in the header information.</li>
 *     <li>{@link #getClientCls()}The default execution client.</li>
 * </ul>
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractHttpRequestParams<R extends AbstractHttpResponse> extends AbstractRequestParams<R>
        implements HttpRequest<R> {

    private static final long serialVersionUID = 7487068349280012103L;

    /**
     * {@inheritDoc}
     *
     * <p>This method defaults to determining the type of the current parameter
     * based on {@link #getRequestParam()}, and retrieves {@code "Content-Type"}
     * from the request header according to the type.
     *
     * <p>By default, adding request headers based on the type of body requires
     * the following two conditions to be met:
     * <ul>
     *     <li>{@link #getRequestParam()} not be null</li>
     *     <li>{@link #montage()} is false or {@link #montage()} is true and
     *     this instanceof {@code MontageParam} and {@code MontageParam#getParam}
     *     not be null</li>
     * </ul>
     *
     * @return {@inheritDoc}
     */
    @Override
    public Map<String, Object> getHeadMap() {
        Object requestParam = getRequestParam();
        if (requestParam != null) {
            String contentType = HttpSdkSupport.getContentTypeWithBody(requestParam, getCharset());
            if (StringUtils.isNotBlank(contentType)) {
                Map<String, Object> headers = new ConcurrentHashMap<>();
                headers.put("Content-Type", contentType);
                return headers;
            }
        }
        return super.getHeadMap();
    }

    /**
     * {@inheritDoc}
     * Default to {@literal false}.
     * <p>
     * Specific implementations can be customized in subclasses according
     * to relevant needs.
     *
     * @return {@inheritDoc}.
     */
    @Override
    public boolean montage() {
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default to use {@code DefaultHttpClient}.
     *
     * @return {@inheritDoc}.
     */
    @Override
    @NotNull
    public Class<? extends Client> getClientCls() {
        return DefaultHttpClient.class;
    }
}
