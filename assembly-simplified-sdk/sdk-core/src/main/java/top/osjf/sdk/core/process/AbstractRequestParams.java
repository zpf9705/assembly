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

package top.osjf.sdk.core.process;

import top.osjf.sdk.core.client.ClientExecutors;
import top.osjf.sdk.core.exception.SdkException;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.support.SdkSupport;

import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

/**
 * The {@code AbstractRequestParams} class is an abstract class that implements
 * the {@code Request} interface and provides a set of default behaviors.
 *
 * <p>This class restricts the response type to be an instance of {@code AbstractResponse}
 * or its subclasses through the generic parameter {@code R}.
 *
 * <p>This class implements the following main functionalities:
 * <ul>
 * <li>Provides a method {@link #getUrl} to obtain the request URL, which
 * generates the URL based on the SDK enum value and ensures that the URL
 * is unique.</li>
 * <li>Overrides the {@link #getRequestParam} method, returning null by
 * default, indicating no request parameters.</li>
 * <li>Overrides the {@link #getCharset} method, returning the default
 * system charset by default.</li>
 * <li>Overrides the {@link #getHeadMap} method, returning an {@link Collections#emptyMap()}
 * by default, indicating no header information.</li>
 * <li>Overrides the validate {@link #validate()} method, not performing
 * any validation logic by default.</li>
 * <li>Overrides the getResponseCls {@link #getResponseType()} method,
 * obtaining the Class object of the response type using reflection.</li>
 * <li>Overrides the execute {@link #execute}method, executing the current
 * request using {@code ClientExecutors} by default and returning the response
 * object.</li>
 * <li>By rewriting the {@link #isAssignableRequest} method, check if the given
 * class implements the {@code Request} interface.</li>
 * </ul>
 *
 * <p>This class is designed to be inherited by specific request parameter
 * classes and provide concrete implementation logic.
 *
 * @param <R> Subclass generic type of {@code AbstractResponse}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractRequestParams<R extends AbstractResponse> implements Request<R> {

    private static final long serialVersionUID = 6875912567896987011L;

    /**
     * {@inheritDoc}
     *
     * @return {@code URL} instances of same {@code url}
     * and {@code unique}.
     * @since 1.0.2
     */
    @NotNull
    public URL getUrl(@Nullable String host) {
        return URL.same(matchSdkEnum().getUrl(host));
    }

    /**
     * {@inheritDoc}
     *
     * @return the {@literal null}.
     * @since 1.0.2
     */
    @Nullable
    @Override
    public Object getRequestParam() {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @return default {@code Charset} by {@link Charset#defaultCharset()}.
     * @since 1.0.2
     */
    @Nullable
    @Override
    public Charset getCharset() {
        return Charset.defaultCharset();
    }

    /**
     * {@inheritDoc}
     *
     * @return an empty {@code Map}.
     */
    @Override
    public Map<String, Object> getHeadMap() {
        return Collections.emptyMap();
    }

    /**
     * no validate logic.
     */
    @Override
    public void validate() throws SdkException {
    }

    /**
     * {@inheritDoc}
     *
     * <p>If neither is provided, {@link SdkSupport#getResponseType}
     * will be used for generic retrieval of the response.
     * <p><strong>The specific supported formats are shown below.</strong></p>
     * <p><strong>The abstract {@link AbstractRequestParams} provided directly is followed
     * by a response implementation with a generic type.</strong>
     * <hr><blockquote><pre>
     *     {@code
     *   class ExampleRequestParam
     *   extends AbstractRequestParams<ExampleResponse<Character>> {
     *      private static final long serialVersionUID = 6115216307330001269L;
     *         Override
     *         public SdkEnum matchSdkEnum() {
     *             return null;
     *         }
     *       }
     *     }
     * </pre></blockquote><hr>
     * <p><strong>Implement an interface that carries the main class generic request.</strong>
     * <hr><blockquote><pre>
     *     {@code
     *     interface
     *     ExampleRequest extends Request<ExampleResponse<String>> {
     *     }
     *     class ExampleRequestParam implements ExampleRequest {
     *         private static final long serialVersionUID = 7371775319382181179L;
     *     }
     *     }
     * </pre></blockquote><hr>
     * <p><strong>Nested inheritance type.</strong>
     * <hr><blockquote><pre>
     *     {@code
     *      class ExampleRequestParam extends AbstractRequestParams<ExampleResponse<String>> {
     *         private static final long serialVersionUID = 6115216307330001269L;
     *         Override
     *         public SdkEnum matchSdkEnum() {
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
     *     class ExampleRequestParam implements Request<ExampleResponse<String>> {
     *         private static final long serialVersionUID = 6115216307330001269L;
     *         ...
     *     }
     *     class ExampleRequestParamSon extends ExampleRequestParam {
     *         private static final long serialVersionUID = 2463029032762347802L;
     *     }
     *     }
     * </pre></blockquote><hr>
     *
     * @return {@inheritDoc}
     * @since 1.0.2
     */
    @Override
    @NotNull
    public Type getResponseType() {
        return SdkSupport.getResponseType(this, defResponseType());
    }

    /**
     * Return the default conversion response type for this request.
     * <p>
     * When using the mechanism of method {@link #getResponseType()} to obtain
     * the response type {@code Type}, there may be a situation where the retrieval
     * is empty, so default value retrieval is performed in this method.
     *
     * @return the default conversion response type.
     */
    @Nullable
    public Type defResponseType() {
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Default to use {@link ClientExecutors} execute current {@code Request}.
     *
     * @param host {@inheritDoc}
     * @return {@inheritDoc}
     * @since 1.0.2
     */
    @Override
    public R execute(@Nullable String host) {
        return ClientExecutors.executeRequestClient(host, this);
    }

    /**
     * {@inheritDoc}
     *
     * @param clazz {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isAssignableRequest(Class<?> clazz) {
        return Request.class.isAssignableFrom(clazz);
    }
}
