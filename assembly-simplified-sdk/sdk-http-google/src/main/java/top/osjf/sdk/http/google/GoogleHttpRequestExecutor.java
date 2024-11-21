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

package top.osjf.sdk.http.google;

import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import feign.Request;
import feign.Response;
import feign.googlehttpclient.GoogleHttpClient;
import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.http.HttpRequestExecutor;
import top.osjf.sdk.http.UnsupportedCustomizeHttpRequestExecutor;

import java.io.IOException;

/**
 * One of the implementation classes of {@link HttpRequestExecutor}, please
 * refer to{@link GoogleHttpSimpleRequestUtils} for implementation.
 * <p>
 * Starting from version 1.0.2, the calling method of {@code GoogleHttp} uses the
 * {@code feign-google-http} component integrated with open feign.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@LoadOrder(Integer.MIN_VALUE + 12)
public class GoogleHttpRequestExecutor extends UnsupportedCustomizeHttpRequestExecutor implements HttpRequestExecutor {
    /**
     * Define a private final variable of type {@code GoogleHttpClient} for executing HTTP requests
     */
    private final GoogleHttpClient googleHttpClient;

    /**
     * A no-argument constructor that initializes the object with a default
     * {@code GoogleHttpClient} instance.
     * <p>
     * This is a convenience constructor that internally calls a constructor
     * with a {@code GoogleHttpClient} parameter,passing a newly created
     * {@code GoogleHttpClient} instance with default configuration.
     */
    public GoogleHttpRequestExecutor() {
        this(new GoogleHttpClient());
    }

    /**
     * A constructor with an {@code HttpTransport} parameter that allows the
     * user to pass a custom {@code HttpTransport} instance,then uses this
     * instance to create a {@code GoogleHttpClient}, and initializes the object with it.
     * <p>
     * {@code HttpTransport} is an abstraction layer for handling the sending
     * and receiving of HTTP requests. By passing a custom {@code HttpTransport},
     * the user can control the details of the underlying communication, such as
     * using different HTTP client libraries, setting timeouts, etc.
     * <p>
     * This constructor provides flexibility, allowing users to configure the
     * {@code HttpTransport} according to their needs.
     *
     * @param transport A user-defined {@code HttpTransport} instance.
     */
    public GoogleHttpRequestExecutor(HttpTransport transport) {
        this(new GoogleHttpClient(transport));
    }

    /**
     * A constructor with an {@code HttpRequestFactory} parameter that allows
     * the user to pass a custom {@code HttpRequestFactory} instance,then uses
     * this instance to create a {@code GoogleHttpClient}, and initializes the
     * object with it.
     * <p>
     * {@code HttpRequestFactory} is a factory class for creating {@code HttpRequest}
     * objects. An {@code HttpRequest} object represents an HTTP request,
     * including the request method (e.g., GET, POST), request headers, request body,
     * etc. By passing a custom {@code HttpRequestFactory},
     * the user can control the creation process of {@code HttpRequest}, such as setting
     * default request headers, modifying the request body, etc.
     * <p>
     * This constructor also provides flexibility, allowing users to configure the
     * {@code HttpRequestFactory} according to their needs.
     *
     * @param httpRequestFactory A user-defined {@code HttpRequestFactory} instance.
     */
    public GoogleHttpRequestExecutor(HttpRequestFactory httpRequestFactory) {
        this(new GoogleHttpClient(httpRequestFactory));
    }

    /**
     * A constructor with a {@code GoogleHttpClient} parameter that directly uses
     * this parameter to initialize the object.
     * <p>
     * This constructor allows the user to directly pass a already-configured
     * {@code GoogleHttpClient} instance,which is suitable for scenarios where
     * finer-grained control over the {@code GoogleHttpClient} configuration is
     * required,or when the user already has a {@code GoogleHttpClient} instance
     * and wishes to reuse it.
     *
     * @param googleHttpClient A user-defined and already-configured
     *                         {@code GoogleHttpClient} instance.
     */
    public GoogleHttpRequestExecutor(GoogleHttpClient googleHttpClient) {
        this.googleHttpClient = googleHttpClient;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return googleHttpClient.execute(request, options);
    }
}
