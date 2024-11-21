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
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Definition of a generic request interface, where the generic type R represents the
 * response type, which must be a subclass of Response.
 * It extends the RequestParamCapable interface (for obtaining request parameters) and
 * implements the Serializable interface (for serialization).
 * This interface encapsulates the common methods and properties required to initiate a
 * request, such as obtaining the access address, request parameters, character set, etc.
 * By implementing this interface, all request objects can ensure these basic functionalities
 * and allow custom validation logic.
 * <p>
 * Main method descriptions:
 * <ul>
 *     <li>{@link #getUrl}:Obtains the formatted actual access address based on the host address.</li>
 *     <li>{@link #getRequestParam}:Obtains the request parameters, returning null by default and
 *     can be overridden.</li>
 *     <li>{@link #getCharset}:Obtains the request character set, using UTF-8 by default.</li>
 *     <li>{@link #validate}:Performs custom parameter validation before the request, throwing an
 *     {@code SdkException} if validation fails.</li>
 *     <li>{@link #getResponseCls}:Returns the Class object of the response class, used internally
 *     by the SDK to process response data.</li>
 *     <li>{@link #getResponseTypeToken}:Uses Google's {@code TypeToken} tool to obtain the Type object of
 *     the response type for generic processing.</li>
 *     <li>{@link #getHeadMap}:Returns the Map data format encapsulated by the request headers for
 *     subsequent request processing.</li>
 *     <li>{@link #getClientCls}:Returns the Client type held by the current request class, with an
 *     open definition allowing custom behavior.</li>
 *     <li>{@link #getResponseRequiredType}:Obtains the response type that should be converted based
 *     on {@link #getResponseCls()} and {@link #getResponseTypeToken()}./li>
 *     <li>{@link #matchSdkEnum}:Returns the matching {@code SdkEnum} type.</li>
 *     <li>{@link #isAssignableRequest}:Determines whether the given class is a subclass or implementer
 *     of the main request interface.</li>
 * </ul>
 *
 * <p>It includes parameter acquisition {@link RequestParamCapable} required
 * for the request, parameter verification (intercepted in the form of
 * {@link SdkException}), recording the response body type for subsequent encapsulation
 * conversion, and verifying the request header.
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
     * {@inheritDoc}
     * The default character set format is UTF-8.
     *
     * @return request parm character type.
     */
    @Override
    default Charset getCharset() {
        return StandardCharsets.UTF_8;
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
