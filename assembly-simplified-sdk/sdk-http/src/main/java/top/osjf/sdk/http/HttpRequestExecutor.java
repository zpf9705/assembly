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

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
public interface HttpRequestExecutor {

    /**
     * Executes an HTTP request against its {@link ExecutorHttpRequest#getUrl() url}
     * and returns a {@link String} response.
     *
     * @param httpRequest An object encapsulating HTTP request
     *                    information, including URL, method name,
     *                    request body, charset, headers, and options.
     * @return The response {@code String} result after executing the HTTP request.
     * @throws Exception The request execution process is abnormal.
     */
    String execute(ExecutorHttpRequest httpRequest) throws Exception;

    /**
     * An interface encapsulating HTTP request information.
     */
    interface ExecutorHttpRequest {
        /**
         * Gets the URL of the request.
         *
         * @return The URL of the request.
         */
        String getUrl();

        /**
         * Gets the method name of the HTTP request (e.g., GET, POST, etc.).
         *
         * @return The method name of the HTTP request.
         */
        String getMethodName();

        /**
         * Gets the request body of the HTTP request.
         *
         * @return The request body of the HTTP request.
         */
        Object getBody();

        /**
         * Gets the charset used in the request.
         *
         * @return The charset used in the request.
         */
        Charset getCharset();

        /**
         * Gets the header information of the HTTP request.
         *
         * @return A map containing key-value pairs of request headers.
         */
        Map<String, String> getHeaders();

        /**
         * Gets the options of the HTTP request.
         *
         * @return An object containing request options.
         */
        RequestOptions getOptions();
    }

    /**
     * The default hosting implementation class for {@link ExecutorHttpRequest}.
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    class Default implements ExecutorHttpRequest, Serializable {
        private static final long serialVersionUID = -375300629962316312L;

        private final String url;
        private final HttpRequest request;
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
         * @param request An object encapsulating HTTP request information
         * @param url     The URL address of the request
         * @param options The options for the HTTP request, can be null
         */
        public Default(HttpRequest request, String url, RequestOptions options) {
            if (request == null) throw new NullPointerException("HttpRequest not be null");
            if (url == null) throw new NullPointerException("url not be null");
            this.request = request;
            this.url = url;
            this.options = options == null ? RequestOptions.DEFAULT_OPTIONS : options;
        }

        @Override
        public String getUrl() {
            return url;
        }

        @Override
        public String getMethodName() {
            return request.matchSdkEnum().getRequestMethod().name();
        }

        @Override
        public Object getBody() {
            //If parameter concatenation has already been performed,
            // the body parameter will no longer be passed here.
            return request.montage() && request.getRequestParam() != null ? null : request.getRequestParam();
        }

        @Override
        public Charset getCharset() {
            return request.getCharset();
        }

        @Override
        public Map<String, String> getHeaders() {
            return request.getHeadMap();
        }

        @Override
        public RequestOptions getOptions() {
            return options;
        }
    }

    /**
     * A set of settings for network requests.
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

    /**
     * Return whether to use the custom {@link CustomizeHttpRequestExecutor}
     * request method.
     * <p>By default, the open flag client parameter leader {@link #execute}
     * scheme is used，that is to say, the return value is {@code false}.
     * <p>Set the return value to {@code true} to execute custom logic.
     *
     * @return whether to use the custom {@link CustomizeHttpRequestExecutor}
     * request method.
     */
    default boolean useCustomize() {
        return false;
    }
}
