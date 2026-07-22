/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.cron.core.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.Assert;
import top.osjf.commons.util.CollectionUtils;
import top.osjf.commons.util.StringUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Runnable standard interface for cron scheduled HTTP callback tasks.
 * <p>
 * Provides static factory method {@link #of(HttpRequestEntity)} to quickly create implementation instances,
 * the built-in {@link DefaultHttpUrlRunnable} completes the actual HTTP request sending, response parsing,
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 * @see CronTaskRepository.Builder#withTask(HttpRequestEntity)
 */
public interface HttpUrlRunnable extends Runnable {
    /**
     * Execute the HTTP callback request logic triggered by the scheduled task.
     */
    @Override
    void run();

    /**
     * Static method for quickly construct the default implementation object of {@link HttpUrlRunnable}.
     * @param requestEntity encapsulated complete HTTP request parameter entity
     * @return default http runnable implementation instance
     */
    static HttpUrlRunnable of(HttpRequestEntity requestEntity) {
        return new DefaultHttpUrlRunnable(requestEntity);
    }

    /**
     * Default implementation class of {@link HttpUrlRunnable}, based on JDK native {@code HttpURLConnection}
     * implementation. Complete the whole life cycle of HTTP request: parameter assembly {@code ->} request sending
     * {@code ->} response reading {@code ->} custom callback execution {@code ->} unified resource closing and
     * exception logging.
     */
    class DefaultHttpUrlRunnable implements HttpUrlRunnable {

        private static final Logger logger = LoggerFactory.getLogger(DefaultHttpUrlRunnable.class);

        private final HttpRequestEntity requestEntity;

        /**
         * Constructs a new {@link DefaultHttpUrlRunnable} with given {@code HttpRequestEntity}.
         * @param requestEntity http request parameter encapsulation entity
         */
        public DefaultHttpUrlRunnable(HttpRequestEntity requestEntity) {
            this.requestEntity = requestEntity;
        }

        @Override
        public void run() {

            Logger cLogger = requestEntity.getLogger();
            Logger logger = cLogger != null ? cLogger : DefaultHttpUrlRunnable.logger;

            HttpURLConnection connection = null;
            BufferedReader reader = null;
            OutputStream outStream = null;

            try {
                URL targetUrl = new URL(requestEntity.getUrl());
                connection = (HttpURLConnection) targetUrl.openConnection();

                // Basic config
                connection.setRequestMethod(requestEntity.getMethod());
                connection.setConnectTimeout(requestEntity.getConnectTimeout());
                connection.setReadTimeout(requestEntity.getReadTimeout());

                // Fill headers
                requestEntity.getHeaders().forEach(connection::setRequestProperty);

                // Handle request body (POST/PUT etc.)
                String requestBody = requestEntity.getRequestBody();
                if (StringUtils.isNotBlank(requestBody)) {
                    connection.setDoOutput(true);
                    outStream = new BufferedOutputStream(connection.getOutputStream());
                    outStream.write(requestBody.getBytes(requestEntity.getCharset()));
                    outStream.flush();
                } else {
                    connection.setDoOutput(false);
                }

                Charset charset = requestEntity.getCharset();
                int responseCode = connection.getResponseCode();
                if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
                    InputStream errStream = connection.getErrorStream();
                    reader = (errStream != null)
                            ? new BufferedReader(new InputStreamReader(errStream, charset))
                            : new BufferedReader(new StringReader(""));
                } else {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
                }

                // Read response body content
                StringBuilder responseSb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    responseSb.append(line);
                }

                // Retrieve all response header field names.
                Map<String, String> responseHeaders = new HashMap<>();
                for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                    String headerKey = entry.getKey();
                    List<String> headerValues = entry.getValue();
                    if (CollectionUtils.isNotEmpty(headerValues)) {
                        responseHeaders.put(headerKey, headerValues.get(0));
                    }
                }

                Consumer<ResponseEntity> responseConsumer = requestEntity.getResponseConsumer();
                if (responseConsumer != null) {
                    // Execute custom response callback processing logic
                    responseConsumer
                            .accept(new ResponseEntity(responseHeaders, responseSb.toString(), responseCode));
                }
                else {
                    logger.info("[DefaultHttpUrlRunnable] Request completed, url=[{}], method=[{}], code=[{}], " +
                                    "response=[{}]", requestEntity.getUrl(), requestEntity.getMethod(),
                            responseCode, responseSb);
                }
            }
            catch (IOException ex) {
                logger.error("[DefaultHttpUrlRunnable] Request failed, url=[{}], method=[{}], message=[{}]",
                        requestEntity.getUrl(), requestEntity.getMethod(), ex.getMessage(), ex);
            }
            finally {
                // Close all resources
                close(outStream);
                close(reader);

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        static void close(@Nullable Closeable closeable) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * Lightweight HTTP request entity, used for cron task HTTP callback trigger.
     *
     * <p>Support common HTTP methods, request headers, string request body and timeout control.
     * Built-in builder, and builder only allows single build to prevent accidental parameter
     * modification.
     */
    class HttpRequestEntity {

        /** Target request address, must start with http:// or https:// */
        private final String url;
        /** HTTP request method, such as GET, POST, PUT, DELETE */
        private final String method;
        /** Connection timeout, unit: milliseconds */
        private final int connectTimeout;
        /** Response read timeout, unit: milliseconds */
        private final int readTimeout;
        /** Custom request header collection */
        private final Map<String, String> headers;
        /** Request body string, effective for POST/PUT and other write requests, can be {@code null} */
        @Nullable private final String requestBody;
        /** Request and response content encoding */
        private final Charset charset;
        /** Response callback consumer */
        @Nullable private final Consumer<ResponseEntity> responseConsumer;
        /** Custom logger. */
        @Nullable private final Logger logger;

        /**
         * Private constructor, instantiate via {@link HttpRequestEntity.Builder#build()}.
         * @param builder builder instance
         */
        private HttpRequestEntity(Builder builder) {
            this.url = builder.url;
            this.method = builder.method;
            this.connectTimeout = builder.connectTimeout;
            this.readTimeout = builder.readTimeout;
            this.headers = Collections.unmodifiableMap(new HashMap<>(builder.headers));
            this.requestBody = builder.requestBody;
            this.charset = builder.charset;
            this.responseConsumer = builder.responseConsumer;
            this.logger = builder.logger;
        }

        /**
         * Get target request url.
         * @return target request url
         */
        public String getUrl() {
            return url;
        }

        /**
         * Get HTTP request method.
         * @return uppercase http method
         */
        public String getMethod() {
            return method;
        }

        /**
         * Get connection timeout value.
         * @return timeout value, unit: milliseconds
         */
        public int getConnectTimeout() {
            return connectTimeout;
        }

        /**
         * Get response read timeout value.
         * @return timeout value, unit: milliseconds
         */
        public int getReadTimeout() {
            return readTimeout;
        }

        /**
         * Get unmodifiable request header map.
         * @return request headers
         */
        public Map<String, String> getHeaders() {
            return headers;
        }

        /**
         * Get request body string.
         * @return request body, may be {@code null}
         */
        @Nullable
        public String getRequestBody() {
            return requestBody;
        }

        /**
         * Get content charset.
         * @return charset
         */
        public Charset getCharset() {
            return charset;
        }

        /**
         * Get response consumer.
         * @return response consumer
         */
        @Nullable
        public Consumer<ResponseEntity> getResponseConsumer() {
            return responseConsumer;
        }

        /**
         * Get custom logger.
         * @return custom logger
         */
        @Nullable
        public Logger getLogger() {
            return logger;
        }

        /**
         * Create builder for assembling {@link HttpRequestEntity}.
         * @return builder instance
         */
        public static Builder builder() {
            return new Builder();
        }

        /**
         * Builder for building {@link HttpRequestEntity}.
         */
        public static class Builder {

            private String url;
            private String method = "GET";
            private int connectTimeout = 5000;
            private int readTimeout = 8000;
            private final Map<String, String> headers = new HashMap<>();
            @Nullable private String requestBody;
            private Charset charset = StandardCharsets.UTF_8;
            @Nullable private Consumer<ResponseEntity> responseConsumer;
            @Nullable private Logger logger;

            /** Mark whether {@link #build()} has been executed */
            private final AtomicBoolean buildFlag = new AtomicBoolean(false);

            /**
             * Set target request url.
             *
             * @param url target url, must start with http:// or https://
             * @return this builder
             * @throws IllegalStateException if builder already built
             * @throws IllegalArgumentException if url blank or invalid protocol
             */
            public Builder url(String url) {
                checkBuildFlag();
                this.url = url;
                return this;
            }

            /**
             * Set HTTP request method, will convert to uppercase automatically.
             *
             * @param method http method name
             * @return this builder
             * @throws IllegalStateException if builder already built
             * @throws IllegalArgumentException if method blank
             */
            public Builder method(String method) {
                checkBuildFlag();
                Assert.hasText(method, "Method must not be null or blank");
                this.method = method.toUpperCase();
                return this;
            }

            /**
             * Set connection timeout.
             *
             * @param connectTimeout timeout value, unit: milliseconds, must greater than 0
             * @return this builder
             * @throws IllegalStateException if builder already built
             * @throws IllegalArgumentException if timeout less than or equal to 0
             */
            public Builder connectTimeout(int connectTimeout) {
                checkBuildFlag();
                Assert.isTrue(connectTimeout > 0, "Connect timeout must greater than 0");
                this.connectTimeout = connectTimeout;
                return this;
            }

            /**
             * Set response read timeout.
             *
             * @param readTimeout timeout value, unit: milliseconds, must greater than 0
             * @return this builder
             * @throws IllegalStateException if builder already built
             * @throws IllegalArgumentException if timeout less than or equal to 0
             */
            public Builder readTimeout(int readTimeout) {
                checkBuildFlag();
                Assert.isTrue(readTimeout > 0, "Read timeout must greater than 0");
                this.readTimeout = readTimeout;
                return this;
            }

            /**
             * Add single request header.
             *
             * @param key   header name
             * @param value header value
             * @return this builder
             * @throws IllegalStateException if builder already built
             * @throws IllegalArgumentException if key or value blank
             */
            public Builder header(String key, String value) {
                checkBuildFlag();
                Assert.hasText(key, "Header key must not be null or blank");
                Assert.hasText(value, "Header value must not be null or blank");
                headers.put(key, value);
                return this;
            }

            /**
             * Batch add request headers.
             *
             * @param map header key-value map
             * @return this builder
             * @throws IllegalStateException if builder already built
             * @throws IllegalArgumentException if map empty
             */
            public Builder headers(Map<String, String> map) {
                checkBuildFlag();
                Assert.notEmpty(map, "Header map must not be empty");
                headers.putAll(map);
                return this;
            }

            /**
             * Set string request body.
             *
             * @param body request body string, can be {@code null}
             * @return this builder
             * @throws IllegalStateException if builder already built
             */
            public Builder requestBody(@Nullable String body) {
                checkBuildFlag();
                this.requestBody = body;
                return this;
            }

            /**
             * Set request / response charset.
             *
             * @param charset charset instance
             * @return this builder
             * @throws IllegalStateException if builder already built
             * @throws IllegalArgumentException if charset is null
             */
            public Builder charset(Charset charset) {
                checkBuildFlag();
                Assert.notNull(charset, "Charset must not be null");
                this.charset = charset;
                return this;
            }

            /**
             * Set a consumer for post-processing the HTTP response result.
             *
             * @param responseConsumer the responseConsumer response callback consumer.
             * @return this builder
             * @throws IllegalStateException if builder already built
             */
            public Builder responseConsumer(@Nullable Consumer<ResponseEntity> responseConsumer) {
                checkBuildFlag();
                this.responseConsumer = responseConsumer;
                return this;
            }

            /**
             * Set custom logger instance for http request runtime log output.
             * If not assigned, the built-in class logger will be used by default.
             *
             * @param logger custom logger object, can be {@code null}
             * @return this builder
             * @throws IllegalStateException if builder already built
             */
            public Builder logger(@Nullable Logger logger) {
                checkBuildFlag();
                this.logger = logger;
                return this;
            }

            /**
             * Build immutable {@link HttpRequestEntity}.
             *
             * @return immutable HttpRequestEntity instance
             * @throws IllegalStateException if already invoked build()
             */
            public HttpRequestEntity build() {
                Assert.state(buildFlag.compareAndSet(false, true),
                        "The build() method can only be invoked once.");

                Assert.hasText(url, "Url must not be null or blank");
                String trimUrl = url.trim().toLowerCase();
                Assert.isTrue(trimUrl.startsWith("http://") || trimUrl.startsWith("https://"),
                        "Url must start with http:// or https://");

                return new HttpRequestEntity(this);
            }

            /**
             * Check whether the builder has completed construction.
             * If built, forbid modifying builder conditions.
             * @throws IllegalStateException if already built
             */
            private void checkBuildFlag() {
                Assert.state(!buildFlag.get(),
                        "This Builder has been finalized via build(), configuration cannot be updated.");
            }
        }
    }

    /**
     * Lightweight HTTP response encapsulation entity for cron http callback task.
     * <p>
     * Store standard response data returned by remote server after successful network communication,
     * including response status code, response header collection and response text body.
     * The internal header collection is wrapped as an unmodifiable view to ensure immutability.
     */
    class ResponseEntity {

        /** Response header key-value collection */
        private final Map<String, String> headers;

        /** Response text content, may be {@code null} */
        @Nullable private final String body;

        /** HTTP response status code (200/404/500 etc.) */
        private final int statusCode;

        public ResponseEntity(Map<String, String> headers, @Nullable String body, int statusCode) {
            this.headers = headers;
            this.body = body;
            this.statusCode = statusCode;
        }

        /**
         * Get immutable response header map.
         * @return unmodifiable headers map
         */
        public Map<String, String> getHeaders() {
            return Collections.unmodifiableMap(headers);
        }

        /**
         * Get response body text content.
         * @return response body string or {@code null}
         */
        @Nullable
        public String getBody() {
            return body;
        }

        /**
         * Get HTTP response status code.
         * @return numeric status code
         */
        public int getStatusCode() {
            return statusCode;
        }
    }
}
