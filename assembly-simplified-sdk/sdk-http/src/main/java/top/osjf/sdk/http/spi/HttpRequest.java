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


package top.osjf.sdk.http.spi;

import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.http.client.HttpRequestOptions;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface HttpRequest extends Serializable {

    /**
     * Get the URL address of the HTTP request.
     * The retrieved URL is the complete request address, including protocol,
     * host name, port number, path, etc.
     *
     * @return The URL address of the request.
     */
    @NotNull
    String getUrl();

    /**
     * Get the name of the HTTP request method, such as GET, POST, etc.
     * This method returns a string indicating the action the client
     * hopes the server will perform on the resource.
     *
     * @return The name of the request method.
     */
    @NotNull
    String getMethodName();

    /**
     * Get the body content of the HTTP request, usually used in POST and
     * PUT requests.
     * The type of the returned object depends on the actual request content,
     * which may be a string, JSON object, form data, etc.
     *
     * @return The body content of the request.
     */
    @Nullable
    Object getBody();

    /**
     * Get the body content of the HTTP request and attempt to convert it to
     * the specified type.
     * If the conversion fails, a {@code ClassCastException} exception is thrown.
     *
     * @param requiredType The converted required {@code Class} type.
     * @param <T>          The converted required {@code Java Generic}type.
     * @return The converted body content of the request.
     * @throws ClassCastException Throws this exception if the conversion fails.
     */
    @Nullable
    <T> T getBody(Class<T> requiredType) throws ClassCastException;

    /**
     * Get the body content of the HTTP request and attempt to convert it to
     * the specified type.
     * If the direct conversion fails, it will attempt to convert again using
     * the provided custom conversion function.
     *
     * @param requiredType                The converted required {@code Class} type.
     * @param customConversionAfterFailed The custom conversion function used when the
     *                                    default conversion fails.
     * @param <T>                         The converted required {@code Java Generic}type.
     * @return The converted body content of the request.
     * @throws ClassCastException Throws this exception if the conversion fails,
     *                            When {@code customConversionAfterFailed} is not provided,
     *                            this error is still thrown.
     */
    @Nullable
    <T> T getBody(Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
            throws ClassCastException;

    /**
     * Get the character set encoding used for the HTTP request.
     * The character set encoding determines how to interpret text
     * data in requests and responses.
     *
     * @return The character set encoding used for the request.
     */
    @Nullable
    Charset getCharset();

    /**
     * Get all header information for the HTTP request, returned as a key-value
     * pair mapping.
     * Header information contains various metadata sent by the client to the
     * server, such as content type, authentication information, etc.
     *
     * @return The header information mapping for the request,If there is no
     * value return, return {@link Collections#emptyMap()}.
     */
    Map<String, Object> getHeaders();

    /**
     * Get all header information for the HTTP request and attempt to convert
     * each value to the specified type.
     * If the conversion fails, a {@code ClassCastException} exception is thrown.
     *
     * @param requiredType The converted required {@code Class} type.
     * @param <T>          The converted required {@code Java Generic}type.
     * @return The converted header information mapping for the request,If there is no
     * value return, return {@link Collections#emptyMap()}.
     * @throws ClassCastException Throws this exception if the conversion fails.
     */
    <T> Map<String, T> getHeaders(Class<T> requiredType) throws ClassCastException;

    /**
     * Get all header information for the HTTP request and attempt to convert
     * each value to the specified type.
     * If the direct conversion fails, it will attempt to convert again using
     * the provided custom conversion function.
     *
     * @param requiredType                The converted required {@code Class} type.
     * @param customConversionAfterFailed The custom conversion function used when the
     *                                    default conversion fails.
     * @param <T>                         The converted required {@code Java Generic}type.
     * @return The converted body content of the request,If there is no
     * value return, return {@link Collections#emptyMap()}.
     * @throws ClassCastException Throws this exception if the conversion fails,
     *                            When {@code customConversionAfterFailed} is not provided,
     *                            this error is still thrown.
     */
    <T> Map<String, T> getHeaders(Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
            throws ClassCastException;

    /**
     * Gets a Map where the key is a String and the value is a Collection of a
     * generic type T.
     * The method determines the type of elements in the collection by the given
     * {@code requiredType} and attempts to convert the values in the map to this type.
     * <p>
     * If a direct conversion fails, the {@code customConversionAfterFailed} function
     * is called for a custom conversion attempt.
     * If the conversion still fails, a {@code ClassCastException} is thrown.
     * <p>
     * This is a generic method that allows you to specify the type T of the elements
     * in the collection and obtain a map of collections of that type.
     *
     * @param requiredType                The converted required {@code Class} type.
     * @param customConversionAfterFailed The custom conversion function used when the
     *                                    default conversion fails.
     * @param <T>                         The converted required {@code Java Generic}type.
     * @return The converted body content of the request,If there is no
     * value return, return {@link Collections#emptyMap()}.
     * @throws ClassCastException Throws this exception if the conversion fails,
     *                            When {@code customConversionAfterFailed} is not provided,
     *                            this error is still thrown.
     */
    <T> Map<String, Collection<T>> getCollectionValueHeaders(Class<T> requiredType,
                                                             Function<Object, T> customConversionAfterFailed)
            throws ClassCastException;

    /**
     * Get a single value from the HTTP request headers based on the specified key name.
     * If the key name exists in the header information, the corresponding value is
     * returned; otherwise, null is returned.
     *
     * @param key The key name to retrieve.
     * @return The corresponding header information value, or null if it does not exist.
     */
    @Nullable
    String getHeader(String key);

    /**
     * Get a single value from the HTTP request headers based on the specified
     * key name and attempt to convert it to the specified type.
     * If the conversion fails, a {@code ClassCastException} exception is thrown.
     *
     * @param key          The key name to retrieve.
     * @param requiredType The converted required {@code Class} type.
     * @param <T>          The converted required {@code Java Generic}type.
     * @return The converted header information value, or null if it does not exist.
     * @throws ClassCastException Throws this exception if the conversion fails.
     */
    @Nullable
    <T> T getHeader(String key, Class<T> requiredType) throws ClassCastException;

    /**
     * Get a single value from the HTTP request headers based on the specified
     * key name and attempt to convert it to the specified type.
     * If the direct conversion fails, it will attempt to convert again using
     * the provided custom conversion function.
     *
     * @param key                         The key name to retrieve.
     * @param requiredType                The converted required {@code Class} type.
     * @param <T>                         The converted required {@code Java Generic}type.
     * @param customConversionAfterFailed The custom conversion function used when the
     *                                    default conversion fails.
     * @return The converted body content of the request.
     * @throws ClassCastException Throws this exception if the conversion fails,
     *                            When {@code customConversionAfterFailed} is not provided,
     *                            this error is still thrown.
     */
    @Nullable
    <T> T getHeader(String key, Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
            throws ClassCastException;

    /**
     * Get other configuration options for the HTTP request.
     * These options may include timeout settings, connection
     * configurations, proxy settings, etc.
     *
     * @return The configuration options for the request.
     */
    @NotNull
    HttpRequestOptions getOptions();
}
