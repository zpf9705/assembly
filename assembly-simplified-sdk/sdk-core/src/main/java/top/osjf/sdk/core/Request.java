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

package top.osjf.sdk.core;

import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.core.exception.SdkException;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.lang.Nullable;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * The {@code Request} interface defines a universal SDK request template for building
 * and executing related requests.
 *
 * <p>It extends the {@code RequestParamCapable} interface to support the setting of request
 * parameters and implements the {@code IsInstanceWrapper} interface to allow type wrapping,
 * and inherited the {@code Executable} interface to provide the ability to execute requests
 * and is serializable.
 *
 * <p>This interface provides the ability to retrieve request URLs, request parameters, character
 * sets, request header mappings, validate request parameters, match SDK enumeration instances and
 * method for obtaining client class object and response class object.
 *
 * <p>In addition, a method is provided to determine whether a class can be used as a subclass
 * or implementation class of that request type.
 *
 * <p>This interface is mainly used to build and execute service requests for specific services,
 * and to process metadata for requests and responses.
 *
 * @param <R> Subclass generic type of {@code Response},the existence of this generic type is to
 *            enable developers to define the response entity type for each SDK, in order to clearly
 *            describe the content of the response class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("rawtypes")
public interface Request<R extends Response> extends RequestParamCapable<Object>,
        ResponseMetadataAccessor, //Obtaining response metadata
        IsInstanceWrapper, //same type convert able
        Executable<R>,  // execute direct able
        Serializable // serial able
{

    /**
     * Return the {@code URL} instance built by the service host.
     * <p>
     * The service host name is not required and depends on the method
     * {@link SdkEnum#getUrl}.
     *
     * @param host the real server hostname.
     * @return {@code URL} Object of packaging tags and URL addresses
     * and updated on version 1.0.2.
     */
    @NotNull
    URL getUrl(@Nullable String host);

    /**
     * {@inheritDoc}
     *
     * @return Return based on usage, not mandatory.
     */
    @Nullable
    @Override
    Object getRequestParam();

    /**
     * {@inheritDoc}
     *
     * @return Return based on usage, not mandatory.
     */
    @Nullable
    @Override
    Charset getCharset();

    /**
     * Return a map data format encapsulated by a key/{@code Object}
     * request header, iteratively placed in the actual request header
     * during subsequent request processing.
     * <p>
     * In version 1.0.2, the value part of the request body was changed
     * to {@code Object} to enhance the range of values.
     *
     * @return The request header encapsulation type for {@code Map}
     * data format.
     */
    @Nullable
    Map<String, Object> getHeadMap();

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
     * Matches and returns an SDK enumeration instance that fits the current context.
     *
     * <p>This method is used to match and return an appropriate instance from the predefined
     * SDK enumeration type based on the current environment or configuration information.
     *
     * <p>The returned enumeration instance should implement the {@code SdkEnum} interface and provide
     * specific implementations of the {@link SdkEnum#getUrl} and {@link SdkEnum#name()} methods.
     *
     * <p>This method is typically used to dynamically determine which SDK enumeration instance
     * should be used at runtime for subsequent SDK request operations.
     *
     * <p>Notes:</p>
     * <ul>
     *     <li>The returned enumeration instance should not be {@literal null}, as this would violate the
     *     constraint of the {@code @NotNull} annotation.</li>
     *     <li>When implementing this method, ensure that it correctly matches and returns a valid
     *     SDK enumeration instance.</li>
     * </ul>
     *
     * @return An SDK enumeration instance that fits the current context.
     */
    @NotNull
    SdkEnum matchSdkEnum();

    /**
     * Return the {@code Class} object of the {@code Client}.
     *
     * <p>This method is used to return a {@code Class} object representing a
     * specific client type. This client type must be an instance of the
     * {@link Client} class or its subclass.
     *
     * @return the {@code Class} object of the {@code Client}
     */
    @NotNull
    Class<? extends Client> getClientType();

    /**
     * Return the {@code Type} type of the {@code Response} object for this
     * request class.
     *
     * <p>This attribute is used to convert the response data into the provided
     * {@code Response} type after the request is completed.
     *
     * @return the {@code Type} type of the {@code Response} object.
     */
    @NotNull
    Type getResponseType();

    /**
     * Attempting to obtain the generic indicators of an inherited class or
     * interface must satisfy the requirement that it is a subclass of the
     * requesting main interface, and the criteria for determining whether
     * it is true need to be determined through this method.
     *
     * @param clazz determine type.
     * @return If {@code true} is judged successful, {@code false} otherwise.
     */
    boolean isAssignableRequest(Class<?> clazz);
}
