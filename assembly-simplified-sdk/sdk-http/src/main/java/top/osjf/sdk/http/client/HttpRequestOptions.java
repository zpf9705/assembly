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


package top.osjf.sdk.http.client;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * NOTE: This file has been copied and slightly modified from {feign.Request.Options}.
 * <p>
 * A class that encapsulates HTTP request configuration.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public final class HttpRequestOptions {

    /**
     * A default global {@code RequestOptions} instance.
     */
    public static final HttpRequestOptions DEFAULT_OPTIONS = new HttpRequestOptions();
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
    private final Map<String, Map<String, HttpRequestOptions>> threadToMethodOptions;

    /**
     * Creates the new {@code RequestOptions} using any default values:
     *
     * <ul>
     *   <li>Connect Timeout: 10 seconds
     *   <li>Read Timeout: 60 seconds
     *   <li>Follow all 3xx redirects
     * </ul>
     */
    public HttpRequestOptions() {
        this(10, TimeUnit.SECONDS, 60, TimeUnit.SECONDS, true);
    }

    /**
     * Creates the new {@code HttpRequestOptions}.
     * <p>
     * This constructor is used to create an instance of {@code HttpRequestOptions},
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
    public HttpRequestOptions(
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
     * When processing HTTP requests, retrieve the corresponding HttpRequestOptions
     * based on thread identity and method name.
     * Refer to copying {@code feign.Options}.
     *
     * @param methodName it's your http method name.
     * @return http method Options
     */
    @ApiStatus.Experimental
    public HttpRequestOptions getMethodOptions(String methodName) {
        Map<String, HttpRequestOptions> methodOptions =
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
    @ApiStatus.Experimental
    public void setMethodOptions(String methodName, HttpRequestOptions options) {
        String threadIdentifier = getThreadIdentifier();
        Map<String, HttpRequestOptions> methodOptions =
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
