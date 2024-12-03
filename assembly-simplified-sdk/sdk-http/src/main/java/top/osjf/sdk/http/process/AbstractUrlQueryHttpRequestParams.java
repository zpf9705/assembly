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

import top.osjf.sdk.core.process.URL;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.http.support.HttpSdkSupport;

/**
 * The abstract class {@code AbstractUrlQueryHttpRequestParams} defines an HTTP request
 * with URL query parameters.
 * It extends from {@code AbstractHttpRequestParams} for building and handling HTTP
 * requests that include query parameters.
 * <p>
 *     <strong>The code usage example is as follows:</strong>
 * </p>
 * <pre>
 * {@code
 * public class ExampleUrlQueryHttpRequestParams extends AbstractUrlQueryHttpRequestParams<...//Omitted here> {
 *     public HttpSdkEnum matchSdk() {
 *         ... //Omitted here.
 *     }
 *     public Object getRequestParamInternal() {
 *         return "Json" or new ExampleObj() or new HashMap<>();
 *     }
 * }}
 * </pre>
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class AbstractUrlQueryHttpRequestParams<R extends AbstractHttpResponse>
        extends AbstractHttpRequestParams<R> {

    private static final long serialVersionUID = -6897963235159228613L;

    /**
     * Possible request body parameters based on splicing requirements.
     */
    @Nullable
    private Object requestParam;

    /**
     * {@inheritDoc}
     * After matching the necessary metadata with the URL,
     * concatenate the query parameters.
     *
     * @param host {@inheritDoc}.
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public final URL getUrl(String host) {
        Object queryParam;
        if (this instanceof UrlQueryParam) {
            queryParam = ((UrlQueryParam) this).getParam();
            if (queryParam != null) requestParam = getRequestParamInternal();
        } else {
            queryParam = getRequestParamInternal();
            requestParam = null;
        }
        String url = formatUrl(matchSdkEnum().getUrl(host));
        return URL.of(url,
                HttpSdkSupport.buildAndFormatUrlWithQueryParams(url, queryParam, getCharset()));
    }

    /**
     * {@inheritDoc}
     * The parameter body may be empty, depending on the value of this
     * method {@link #getUrl}.
     *
     * @return {@inheritDoc}
     */
    @Override
    @Nullable
    public final Object getRequestParam() {
        return requestParam;
    }

    /**
     * This method can return a request body content.
     * <p>
     * When the object does not implement {@link UrlQueryParam} or when
     * the implementation {@link UrlQueryParam} provides the parameter {@literal null},
     * it will be concatenated as a URL query parameter after the URL.
     * <p>
     * If the previous condition is met, the parameter returned by this method
     * will be used as the request body parameter.
     *
     * @return Query parameters for request body or URL concatenation.
     */
    @Nullable
    public Object getRequestParamInternal() {
        return null;
    }
}
