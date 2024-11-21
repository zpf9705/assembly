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

package top.osjf.sdk.http;

import top.osjf.sdk.core.process.Request;

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

    /*** {@inheritDoc}*/
    @Override
    default String getUrl(String host) {
        return formatUrl(host);
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
     *   class RequestParam
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
     *     InterHttpRequest extends HttpRequest<HttpResultResponse<String>> {
     *     }
     *     class RequestParam implements Inter {
     *         private static final long serialVersionUID = 7371775319382181179L;
     *     }
     *     }
     * </pre></blockquote><hr>
     * <p><strong>Nested inheritance type.</strong>
     * <hr><blockquote><pre>
     *     {@code
     *      class RequestParam extends AbstractHttpRequestParams<HttpResultResponse<String>> {
     *         private static final long serialVersionUID = 6115216307330001269L;
     *         Override
     *         public HttpSdkEnum matchHttpSdk() {
     *             return null;
     *         }
     *     }
     *     class RequestParam0 extends RequestParam {
     *         private static final long serialVersionUID = 2463029032762347802L;
     *     }
     *     }
     * </pre></blockquote><hr>
     * <p><strong>Nested implementation type.</strong>
     * <hr><blockquote><pre>
     *     {@code
     *     class RequestParam implements HttpRequest<HttpResultResponse<String>> {
     *         private static final long serialVersionUID = 6115216307330001269L;
     *         ...
     *     }
     *     class RequestParam1 extends RequestParam {
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

    /*** {@inheritDoc}*/
    @Override
    HttpSdkEnum matchSdkEnum();

    /**
     * Do you want to concatenate the given {@link #getRequestParam()} parameters with rules after the URL.
     * <p>The prerequisite is to provide parameters in the form of a map or JSON strings for key/value.</p>
     *
     * @return If true, it will concatenate the provided parameters for you, otherwise it will be determined
     * based on the request header.
     */
    boolean montage();

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
     * <p>Here, the splicing parameters of the {@link #urlJoin()} method will be
     * automatically added for you. If you don't need to rewrite {@link #urlJoin()}, you can do so.</p>
     *
     * @param host The host name of the SDK.
     * @return The request address for the SDK.
     */
    default String formatUrl(String host) {
        return matchSdkEnum().getUrl(host) + urlJoin();
    }
}
