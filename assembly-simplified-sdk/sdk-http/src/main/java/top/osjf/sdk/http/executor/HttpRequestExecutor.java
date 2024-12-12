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

package top.osjf.sdk.http.executor;

import com.google.common.collect.Lists;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.http.process.HttpRequest;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The {@code HttpRequestExecutor} interface inherits from the feature client
 * interface since version 1.0.2,adopting open label technology support for
 * executing HTTP requests.
 * <p>
 * This interface defines an 'execute' method for sending HTTP requests and
 * receiving responses.
 * The method takes a request object and a request option object as parameters
 * and returns a response object.If an I/O exception occurs during the request
 * process, an IOException will be thrown.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public interface HttpRequestExecutor {

    /**
     * Executes an HTTP request against its {@link ExecutableHttpRequest#getUrl() url}
     * and returns a {@link String} response.
     *
     * @param httpRequest An object encapsulating HTTP request
     *                    information, including URL, method name,
     *                    request body, charset, headers, and options.
     * @return The response {@code String} result after executing the HTTP request.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     * @since 1.0.2
     */
    String execute(@NotNull ExecutableHttpRequest httpRequest) throws Exception;

    /**
     * An interface encapsulating HTTP request information.
     *
     * @since 1.0.2
     */
    interface ExecutableHttpRequest extends Serializable {

        /**
         * Get the URL address of the HTTP request.
         * The retrieved URL is the complete request address, including protocol,
         * host name, port number, path, etc.
         *
         * @return The URL address of the request.
         */
        String getUrl();

        /**
         * Get the name of the HTTP request method, such as GET, POST, etc.
         * This method returns a string indicating the action the client
         * hopes the server will perform on the resource.
         *
         * @return The name of the request method.
         */
        String getMethodName();

        /**
         * Get the body content of the HTTP request, usually used in POST and
         * PUT requests.
         * The type of the returned object depends on the actual request content,
         * which may be a string, JSON object, form data, etc.
         *
         * @return The body content of the request.
         */
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
        <T> T getBody(Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
                throws ClassCastException;

        /**
         * Get the character set encoding used for the HTTP request.
         * The character set encoding determines how to interpret text
         * data in requests and responses.
         *
         * @return The character set encoding used for the request.
         */
        Charset getCharset();

        /**
         * Get all header information for the HTTP request, returned as a key-value
         * pair mapping.
         * Header information contains various metadata sent by the client to the
         * server, such as content type, authentication information, etc.
         *
         * @return The header information mapping for the request.
         */
        Map<String, Object> getHeaders();

        /**
         * Get all header information for the HTTP request and attempt to convert
         * each value to the specified type.
         * If the conversion fails, a {@code ClassCastException} exception is thrown.
         *
         * @param requiredType The converted required {@code Class} type.
         * @param <T>          The converted required {@code Java Generic}type.
         * @return The converted header information mapping for the request.
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
         * @return The converted body content of the request.
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
         * @return The converted body content of the request.
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
        <T> T getHeader(String key, Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
                throws ClassCastException;

        /**
         * Get other configuration options for the HTTP request.
         * These options may include timeout settings, connection
         * configurations, proxy settings, etc.
         *
         * @return The configuration options for the request.
         */
        RequestOptions getOptions();
    }

    /**
     * The default hosting implementation class for {@link ExecutableHttpRequest}.
     *
     * @since 1.0.2
     */
    class Default implements ExecutableHttpRequest {
        private static final long serialVersionUID = -375300629962316312L;

        private final String url;
        private final Object body;
        private final String methodName;
        private final Charset charset;
        private final Map<String, Object> headers;
        private final RequestOptions options;

        /**
         * Constructs a default {@code HTTP request executor}.
         * <p>
         * This constructor is used to create a new instance of the HTTP
         * request executor. It takes three parameters:
         * <p>
         * - HttpRequest request: An object encapsulating HTTP request
         * information, which must not be null.
         * <p>
         * - String url: The URL address of the request, which must not be null.
         * <p>
         * - RequestOptions options: The options for the HTTP request, which can
         * be null. If null, default request options will be used.
         * <p>
         * If the passed-in HttpRequest or url parameter is null, a {@code NullPointerException}
         * will be thrown.
         *
         * @param request An object encapsulating HTTP request information.
         * @param url     The URL address of the request.
         * @param options The options for the HTTP request, can be null.
         */
        public Default(HttpRequest request, String url, RequestOptions options) {
            if (request == null) throw new NullPointerException("HttpRequest not be null");
            if (url == null) throw new NullPointerException("url not be null");
            this.url = url;
            this.body = request.getRequestParam();
            this.methodName = request.matchSdkEnum().getRequestMethod().name();
            this.charset = request.getCharset();
            this.headers = request.getHeadMap();
            this.options = options == null ? RequestOptions.DEFAULT_OPTIONS : options;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getMethodName() {
            return methodName;
        }

        @Override
        public Object getBody() {
            return body;
        }

        @Override
        public <T> T getBody(Class<T> requiredType) {
            return getBody(requiredType, null);
        }

        @Override
        public <T> T getBody(Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
                throws ClassCastException {
            return convertValueToRequired(getBody(), requiredType, customConversionAfterFailed);
        }

        @Override
        public Charset getCharset() {
            return charset;
        }

        @Override
        public Map<String, Object> getHeaders() {
            return headers;
        }

        @Override
        public <T> Map<String, T> getHeaders(Class<T> requiredType) {
            return getHeaders(requiredType, null);
        }

        @Override
        public <T> Map<String, T> getHeaders(Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
                throws ClassCastException {
            Map<String, Object> headers = getHeaders();
            if (MapUtils.isEmpty(headers)) return Collections.emptyMap();
            return headers.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> getHeader(entry.getKey(), requiredType
                            , customConversionAfterFailed)));
        }

        @Override
        public <T> Map<String, Collection<T>> getCollectionValueHeaders(Class<T> requiredType,
                                                                        Function<Object, T> customConversionAfterFailed)
                throws ClassCastException {
            Map<String, Object> headers = getHeaders();
            if (MapUtils.isEmpty(headers)) return Collections.emptyMap();
            return headers.entrySet()
                    .stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> {
                        Object value = entry.getValue();
                        if (value instanceof Collection) {
                            Collection<T> delegate = new ArrayList<>();
                            ((Collection<?>) value)
                                    .forEach((Consumer<Object>) o -> delegate
                                            .add(convertValueToRequired(o, requiredType, customConversionAfterFailed)));
                            return delegate;
                        }
                        return Lists.newArrayList(convertValueToRequired(value, requiredType,
                                customConversionAfterFailed));
                    }));
        }

        @Override
        public String getHeader(String key) {
            return getHeader(key, String.class);
        }

        @Override
        public <T> T getHeader(String key, Class<T> requiredType) {
            return getHeader(key, requiredType, null);
        }

        @Override
        public <T> T getHeader(String key, Class<T> requiredType, Function<Object, T> customConversionAfterFailed)
                throws ClassCastException {
            Map<String, Object> headers = getHeaders();
            if (MapUtils.isEmpty(headers)) return null;
            return convertValueToRequired(headers.get(key), requiredType, customConversionAfterFailed);
        }

        @Override
        public RequestOptions getOptions() {
            return options;
        }

        private <T> T convertValueToRequired(Object value,
                                             Class<T> requiredType,
                                             Function<Object, T> customConversionAfterFailed)
                throws ClassCastException {
            if (value == null) return null;
            try {
                return requiredType.cast(value);
            } catch (ClassCastException e) {
                if (customConversionAfterFailed != null) return customConversionAfterFailed.apply(value);
                throw e;
            }
        }
    }

    /**
     * A set of settings for network requests.
     *
     * @since 1.0.2
     */
    class RequestOptions {

        /*** A default global {@code RequestOptions}*/
        public static final RequestOptions DEFAULT_OPTIONS = new RequestOptions();
        /**
         * The connection timeout duration, specified in the given time unit.
         * If the connection is not established successfully within the specified time,
         * the connection attempt will be abandoned.
         */
        private final long connectTimeout;
        /**
         * The time unit for the connection timeout duration, such as
         * {@link TimeUnit#MILLISECONDS},{@link TimeUnit#SECONDS}, etc.
         */
        private final TimeUnit connectTimeoutUnit;
        /**
         * The read timeout duration, specified in the given time unit. If reading data
         * from the connection is not completed within the specified time after the
         * connection is established, the read operation will be abandoned.
         */
        private final long readTimeout;
        /**
         * The time unit for the read timeout duration, similar to connectTimeoutUnit, such as
         * {@link TimeUnit#MILLISECONDS},{@link TimeUnit#SECONDS}, etc.
         */
        private final TimeUnit readTimeoutUnit;
        /**
         * Whether to automatically follow redirects. If set to true, the client will
         * automatically follow redirect responses returned by the server; if set to false,
         * it will not.
         */
        private final boolean followRedirects;
        /**
         * Each thread can have different method option configurations and that it is
         * a thread safe map.
         * <p>
         * In a multi-threaded environment, different threads may require different request
         * configurations when processing HTTP requests. For example, one thread may handle
         * requests that require high priority, while another thread may handle backend requests
         * that can tolerate longer delays.
         */
        private final Map<String, Map<String, RequestOptions>> threadToMethodOptions;

        /**
         * Creates the new {@code RequestOptions} using any default values:
         *
         * <ul>
         *   <li>Connect Timeout: 10 seconds
         *   <li>Read Timeout: 60 seconds
         *   <li>Follow all 3xx redirects
         * </ul>
         */
        public RequestOptions() {
            this(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true);
        }

        /**
         * Creates the new {@code RequestOptions}.
         * <p>
         * This constructor is used to create an instance of {@code RequestOptions},
         * which is used to configure options for network requests.
         *
         * @param connectTimeout     The connection timeout duration, specified in the given
         *                           time unit. If the connection is not established successfully
         *                           within the specified time, the connection attempt will be
         *                           abandoned.
         * @param connectTimeoutUnit The time unit for the connection timeout duration, such
         *                           as {@link TimeUnit#MILLISECONDS} (milliseconds),
         *                           {@link TimeUnit#SECONDS} (seconds), e  tc.
         * @param readTimeout        The read timeout duration, specified in the given time unit.
         *                           If reading data from the connection is not
         *                           completed within the specified time after the connection is
         *                           established, the read operation will be abandoned.
         * @param readTimeoutUnit    The time unit for the read timeout duration, similar to
         *                           connectTimeoutUnit.
         * @param followRedirects    Whether to automatically follow redirects. If set to true,
         *                           the client will automatically follow redirect responses returned
         *                           by the server; if set to false, it will not.
         */
        public RequestOptions(
                long connectTimeout,
                TimeUnit connectTimeoutUnit,
                long readTimeout,
                TimeUnit readTimeoutUnit,
                boolean followRedirects) {
            this.connectTimeout = connectTimeout;
            this.connectTimeoutUnit = connectTimeoutUnit;
            this.readTimeout = readTimeout;
            this.readTimeoutUnit = readTimeoutUnit;
            this.followRedirects = followRedirects;
            this.threadToMethodOptions = new ConcurrentHashMap<>();
        }

        /**
         * When processing HTTP requests, retrieve the corresponding RequestOptions
         * based on thread identity and method name.
         * Refer to copying {@code feign.Options}.
         *
         * @param methodName it's your http method name.
         * @return http method Options
         */
        public RequestOptions getMethodOptions(String methodName) {
            Map<String, RequestOptions> methodOptions =
                    threadToMethodOptions.getOrDefault(getThreadIdentifier(), new HashMap<>());
            return methodOptions.getOrDefault(methodName, this);
        }

        /**
         * Set methodOptions by methodKey and options.
         * Refer to copying {@code feign.Options}.
         *
         * @param methodName it's your http method name.
         * @param options    it's the Options for this method.
         */
        public void setMethodOptions(String methodName, RequestOptions options) {
            String threadIdentifier = getThreadIdentifier();
            Map<String, RequestOptions> methodOptions =
                    threadToMethodOptions.getOrDefault(threadIdentifier, new HashMap<>());
            threadToMethodOptions.put(threadIdentifier, methodOptions);
            methodOptions.put(methodName, options);
        }

        // Refer to copying {@code feign.Options}.
        private String getThreadIdentifier() {
            Thread currentThread = Thread.currentThread();
            return currentThread.getThreadGroup()
                    + "_"
                    + currentThread.getName()
                    + "_"
                    + currentThread.getId();
        }

        public boolean isFollowRedirects() {
            return followRedirects;
        }

        public long connectTimeout() {
            return connectTimeout;
        }

        public TimeUnit connectTimeoutUnit() {
            return connectTimeoutUnit;
        }

        public long readTimeout() {
            return readTimeout;
        }

        public TimeUnit readTimeoutUnit() {
            return readTimeoutUnit;
        }
    }
}
