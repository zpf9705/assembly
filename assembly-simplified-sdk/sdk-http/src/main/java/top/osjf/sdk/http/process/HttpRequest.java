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

import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.URL;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.http.*;
import top.osjf.sdk.http.exception.UnknownResponseRequiredTypeException;
import top.osjf.sdk.http.support.HttpSdkSupport;

/**
 * Request node information interface defined by SDK of http type.
 *
 * <p>You need to define an implementation {@link #matchSdkEnum()} interface
 * to provide a fixed description of SDK information.
 *
 * <p>It is recommended to define an enumeration class.</p>
 *
 * <p>Generally, configure the host name as a configurable item, and
 * dynamically format the input, which is the {@link #formatUrl(String)} method.
 * <p>The corresponding request header information can be easily added through
 * the {@link #getHeadMap()} method.
 * <p>The body input here is set to {@link Object} to mask all parameter differences
 * and be processed uniformly in the future, Through the {@link #getClientCls()}
 * method, you can customize the request process for {@link top.osjf.sdk.core.client.Client}.
 *
 * <p>Of course, the final conversion is still the response implementation
 * class {@link HttpRequest} that you defined for {@link #getResponseCls()}.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface HttpRequest<R extends HttpResponse> extends Request<R> {

    /**
     * {@inheritDoc}
     * Default to use {@link #formatUrl} get url.
     *
     * @param host {@inheritDoc}.
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    default URL getUrl(@Nullable String host) {
        return URL.same(formatUrl(matchSdkEnum().getUrl(host)));
    }

    /**
     * {@inheritDoc}
     *
     * <p>If neither is provided, {@link HttpSdkSupport#getResponseRequiredType}
     * will be used for generic retrieval of the response.
     * <p><strong>The specific supported formats are shown below.</strong></p>
     * <p><strong>The abstract {@link AbstractHttpRequestParams} provided directly is followed
     * by a response implementation with a generic type.</strong>
     * <hr><blockquote><pre>
     *     {@code
     *   class ExampleRequestParam
     *   extends AbstractHttpRequestParams<HttpResultResponse<Character>> {
     *      private static final long serialVersionUID = 6115216307330001269L;
     *         Override
     *         public HttpSdkEnum matchHttpSdk() {
     *             return null;
     *         }
     *       }
     *     }
     * </pre></blockquote><hr>
     * <p><strong>Implement an interface that carries the main class generic request.</strong>
     * <hr><blockquote><pre>
     *     {@code
     *     interface
     *     ExampleHttpRequest extends HttpRequest<HttpResultResponse<String>> {
     *     }
     *     class ExampleRequestParam implements ExampleHttpRequest {
     *         private static final long serialVersionUID = 7371775319382181179L;
     *     }
     *     }
     * </pre></blockquote><hr>
     * <p><strong>Nested inheritance type.</strong>
     * <hr><blockquote><pre>
     *     {@code
     *      class ExampleRequestParam extends AbstractHttpRequestParams<HttpResultResponse<String>> {
     *         private static final long serialVersionUID = 6115216307330001269L;
     *         Override
     *         public HttpSdkEnum matchHttpSdk() {
     *             return null;
     *         }
     *     }
     *     class ExampleRequestParamSon extends ExampleRequestParam {
     *         private static final long serialVersionUID = 2463029032762347802L;
     *     }
     *     }
     * </pre></blockquote><hr>
     * <p><strong>Nested implementation type.</strong>
     * <hr><blockquote><pre>
     *     {@code
     *     class ExampleRequestParam implements HttpRequest<HttpResultResponse<String>> {
     *         private static final long serialVersionUID = 6115216307330001269L;
     *         ...
     *     }
     *     class ExampleRequestParamSon extends ExampleRequestParam {
     *         private static final long serialVersionUID = 2463029032762347802L;
     *     }
     *     }
     * </pre></blockquote><hr>
     * <h3>Warn</h3>
     * <p>If a custom response type has a generic indicator, it will not be supported
     * and will obtain an unpredictable type. For example, {@code HttpResultResponse<T>}
     * in {@code T} cannot locate a specific type from the subclass's generic.
     *
     * @return {@inheritDoc}
     */
    @Override
    default Object getResponseRequiredType() {
        Object responseRequiredType;
        try {
            responseRequiredType = Request.super.getResponseRequiredType();
        } catch (IllegalStateException e) {
            //If you have not provided the corresponding types of
            // acquisition methods for the above two suggestions,
            // then the following will dynamically obtain them for you.
            responseRequiredType = HttpSdkSupport
                    .getResponseRequiredType(this, HttpResultResponse.class);
        } catch (Throwable e) {
            throw new UnknownResponseRequiredTypeException(e);
        }
        return responseRequiredType;
    }

    @Override
    default boolean isAssignableRequest(Class<?> clazz) {
        return HttpRequest.class.isAssignableFrom(clazz);
    }

    /**
     * The manual URL concatenation method allows users to concatenate parameters on URLs
     * other than the {@link HttpSdkEnum#getRequestMethod()} request mode, which needs to be rewritten directly.
     * <p>The format should refer to the get request.</p>
     *
     * @return Splicing item
     */
    default String urlJoin() {
        return "";
    }

    /**
     * Format the actual request address of the SDK and concatenate subsequent URLs.
     *
     * <p>Here, the splicing parameters of the {@link #urlJoin()} method will be
     * automatically added for you. If you don't need to rewrite {@link #urlJoin()},
     * you can do so.
     *
     * <p>Since version 1.0.2, if you provide {@link HttpProtocol}, you will no longer
     * need to pay attention to the HTTP protocol type in URL formatting. The following
     * method will be used to properly concatenate when {@code HttpProtocol} is not empty.
     *
     * @param url The URL address obtained after matching {@code HttpSdkEnum}.
     * @return The request address for the SDK.
     */
    default String formatUrl(String url) {
        HttpProtocol protocol = matchSdkEnum().getProtocol();
        if (protocol != null) url = protocol.formatUrl(url);
        return url + urlJoin();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Overrides the {@code matchSdkEnum} method from the parent class or interface to match
     * and return an {@code matchSdkEnum} instance that fits the current context.
     *
     * <p>This method is specific to the {@code matchSdkEnum} type, meaning that the returned
     * enumeration instance is dedicated to SDK enumerations for HTTP requests.
     *
     * <p>Compared to the {@code matchSdkEnum} method in the parent class or interface, this
     * method provides a more specific return type, {@code matchSdkEnum}, allowing the caller
     * to directly obtain HTTP-related SDK configuration information.
     *
     * @return An {@code HttpSdkEnum} instance that fits the current context.
     */
    @Override
    @NotNull
    HttpSdkEnum matchSdkEnum();

    /**
     * Do you want to concatenate the given {@link #getRequestParam()} parameters with rules after the URL.
     * <p>The prerequisite is to provide parameters in the form of a map or JSON strings for key/value.
     *
     * @deprecated 1.0.2 , refer to {@link AbstractUrlQueryHttpRequestParams}.
     * @return If true, it will concatenate the provided parameters for you, otherwise it will be determined
     * based on the request header.
     */
    @Deprecated
    boolean montage();
}
