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
 * Abstract request parameter class, defining common behaviors and properties for all
 * request parameters.
 * <p>This class is an abstract class that implements the {@code Request<R>} interface,
 * where {@code R} is a generic parameter for the response type, and this response type
 * must be {@code AbstractResponse} or its subclass.
 *
 * <p>It provides two main method implementations:
 * <ul>
 * <li> {@link #getHeadMap}: Returns an {@link Collections#emptyMap()}, indicating that
 * this abstract class does not contain any request header information by default.
 * Subclasses can override this method to provide specific request headers as needed.</li>
 * <li> {@link #validate}: An empty implementation used to validate the validity of the
 * request parameters before sending the request. Subclasses should override this method
 * to add specific validation logic.</li>
 * </ul>
 *
 * <p>This abstract class can serve as a base class for creating specific request parameter
 * classes. By inheriting and extending this class, it is convenient to implement the
 * {@code Request<R>} interface and define specific request parameters and validation logic.
 *
 * <p>Note: The `serialVersionUID` field is used for serialization version control to ensure
 * compatibility during deserialization.
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
     */
    @NotNull
    public URL getUrl(@Nullable String host) {
        return URL.same(matchSdkEnum().getUrl(host));
    }

    /**
     * {@inheritDoc}
     *
     * @return the {@literal null}.
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
     */
    @Override
    public R execute(@Nullable String host) {
        return ClientExecutors.executeRequestClient(host, this);
    }
}
