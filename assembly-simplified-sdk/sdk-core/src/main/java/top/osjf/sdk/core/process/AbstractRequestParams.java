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
import top.osjf.sdk.core.util.ReflectUtil;

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
 * <li>Overrides the getResponseCls {@link #getResponseCls()} method,
 * obtaining the Class object of the response type using reflection.</li>
 * <li>Overrides the execute {@link #execute}method, executing the current
 * request using {@code ClientExecutors} by default and returning the response
 * object.</li>
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
     * @return {@inheritDoc}
     * @since 1.0.2
     */
    @Override
    @NotNull
    public Class<R> getResponseCls() {
        return ReflectUtil.getSuperGenericType(this, 0);
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
}
