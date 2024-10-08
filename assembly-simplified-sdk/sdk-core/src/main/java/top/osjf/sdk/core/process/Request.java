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

import com.google.common.reflect.TypeToken;
import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.core.enums.SdkEnum;
import top.osjf.sdk.core.exception.SdkException;

import java.io.Serializable;
import java.util.Map;

/**
 * The definition method of the requested public node.
 *
 * <p>It includes parameter acquisition {@link RequestParamCapable} required
 * for the request, parameter verification (intercepted in the form of
 * {@link SdkException}), recording the response body type for subsequent encapsulation
 * conversion, and verifying the request header.</p>
 *
 * <p>The class object that can be rewritten to implement {@link Client} can be
 * customized for logical implementation of methods in {@link Client}.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface Request<R extends Response> extends RequestParamCapable<Object>, Serializable {

    /**
     * Return the formatted real access address based
     * on the host address.
     *
     * @param host The real host address.
     * @return After formatting, it can be used as the
     * real address for access.
     */
    String getUrl(String host);

    /*** {@inheritDoc}*/
    @Override
    default Object getRequestParam() {
        return null;
    }

    /**
     * The SDK will perform custom validation on {@link #getRequestParam()}
     * before requesting, so the rewriting of this method is for this implementation.
     *
     * <p>Users can write validation logic in this method and hope to throw
     * {@link SdkException} when the conditions are not met.
     *
     * @throws SdkException Parameter validation before request, hoping
     *                      to throw this exception type and be captured by transmission.
     */
    void validate() throws SdkException;

    /**
     * Returns the class object of the response transformation entity,
     * implemented in {@link Response}.
     *
     * <p>If you need a {@link Response} transformation of composite
     * generics, please refer to {@link #getResponseTypeToken()}.
     *
     * @return The class object of the response class encapsulated by this SDK.
     */
    default Class<R> getResponseCls() {
        return null;
    }

    /**
     * Use Google's tool {@link TypeToken} to obtain the data paradigm of the
     * response type after the request parameter, in order to inform its true
     * type when the response type is not clearly defined (i.e. {@code getResponseCls() == null}).
     *
     * @return Refer to {@link TypeToken}.
     */
    default TypeToken<R> getResponseTypeToken() {
        return null;
    }

    /**
     * Return a map data format encapsulated by a key/value request
     * header, iteratively placed in the actual request header during
     * subsequent request processing.
     *
     * @return The request header encapsulation type for {@code Map} data format.
     */
    Map<String, String> getHeadMap();

    /**
     * Return the {@link Client} type held by the current request class. The
     * definition of {@link Client} is open, and developers can customize
     * the relevant behavior of {@link Client}.
     *
     * @return The type of {@link Client} held,must not be {@literal null}.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Client> getClientCls();

    /**
     * Based on {@link #getResponseCls()} and {@link #getResponseTypeToken()},
     * obtain the corresponding type that should be converted.
     *
     * <p>{@link #getResponseCls()} has a higher priority than {@link #getResponseTypeToken()}.
     *
     * <p>If none of the above are provided, then only an exception with no response
     * type found can be thrown.
     *
     * @return type object.
     */
    default Object getResponseRequiredType() {
        if (getResponseCls() != null) return getResponseCls();
        if (getResponseTypeToken() != null) return getResponseTypeToken().getType();
        throw new IllegalStateException("Unknown response type!");
    }

    /**
     * Return matching {@link SdkEnum} types.
     *
     * @return matching {@link SdkEnum} types.
     */
    SdkEnum matchSdkEnum();

    /**
     * When {@link #getResponseCls()} and {@link #getResponseTypeToken()}
     * are not provided, attempting to obtain the generic indicators of
     * an inherited class or interface must satisfy the requirement that
     * it is a subclass of the requesting main interface, and the criteria
     * for determining whether it is true need to be determined through this method.
     *
     * @param clazz Determine type.
     * @return If {@code true} is judged successful, {@code false} otherwise .
     */
    default boolean isAssignableRequest(Class<?> clazz) {
        return Request.class.isAssignableFrom(clazz);
    }
}
