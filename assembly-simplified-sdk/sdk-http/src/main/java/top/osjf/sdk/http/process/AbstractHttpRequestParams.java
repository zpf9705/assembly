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
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.client.DefaultHttpClient;
import top.osjf.sdk.http.support.HttpSdkSupport;

import java.util.LinkedHashMap;
import java.util.Map;

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
     * <p>By default, this method is used. Can override method {@link #additionalHeaders()}
     * to add custom request headers.
     *
     * @return {@inheritDoc}
     */
    @Override
    public Map<String, Object> getHeadMap() {
        Object requestParam = getRequestParam();
        Map<String, Object> headers = null;
        if (requestParam != null) {
            String contentType = HttpSdkSupport.getContentTypeWithBody(requestParam, getCharset());
            if (StringUtils.isNotBlank(contentType)) {
                headers = new LinkedHashMap<>();
                headers.put("Content-Type", contentType);
            }
        }
        headers = resolveAdditionalHeaders(headers);
        if (MapUtils.isNotEmpty(headers)) return headers;
        return super.getHeadMap();
    }

    /**
     * Given a request header map, process adding additional user-defined
     * request headers. When the provided request header is empty, provide
     * a new {@code LinkedHashMap} and add the custom request header.
     * @param headers given header map.
     * @return resolve result header map.
     */
     Map<String, Object> resolveAdditionalHeaders(Map<String, Object> headers) {
        Map<String, Object> additionalHeads = additionalHeaders();
        if (MapUtils.isNotEmpty(additionalHeads)) {
            if (MapUtils.isEmpty(headers)) headers = new LinkedHashMap<>();
            headers.putAll(additionalHeads);
        }
        return headers;
    }

    /**
     * Return an additional header information in map format.
     *
     * <p>Combine with the default {@link #getHeadMap()} rewrite
     * logic in this class and add relevant user-defined request
     * headers other than {@code Content-Type}.
     *
     * @return additional headers.
     * @since 1.0.2
     */
    public Map<String, Object> additionalHeaders() {
        return null;
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
