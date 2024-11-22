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

package top.osjf.sdk.http.feign.hc5;

import feign.Request;
import feign.Response;
import feign.hc5.ApacheHttp5Client;
import org.apache.hc.client5.http.classic.HttpClient;
import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.http.feign.bridging.FeignClientHttpRequestExecutor;

import java.io.IOException;

/**
 * The calling method of {@code Hc5Http} uses the
 * {@code feign-hc5-http} component integrated with open feign.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@LoadOrder(Integer.MIN_VALUE + 13)
public class Hc5FeignClientHttpRequestExecutor implements FeignClientHttpRequestExecutor {

    /**
     * Define a private, final instance variable of type {@code ApacheHttp5Client} for executing HTTP requests.
     */
    private final ApacheHttp5Client http5Client;

    /**
     * A no-argument constructor.
     * Initializes the {@code Hc5HttpRequestExecutor} object with a
     * default {@code ApacheHttp5Client} instance.
     * <p>
     * This is to provide a simple construction method without requiring
     * any external parameters.
     */
    public Hc5FeignClientHttpRequestExecutor() {
        this(new ApacheHttp5Client());
    }

    /**
     * A constructor with a {@code HttpClient} parameter.
     * Allows the user to pass a custom {@code HttpClient} instance for
     * initializing the {@code Hc5HttpRequestExecutor} object.
     * <p>
     * Note: This actually wraps the passed-in {@code HttpClient} in an
     * {@code ApacheHttp5Client} instance.
     *
     * @param httpClient A custom {@code HttpClient} instance for executing
     *                   HTTP requests.
     */
    public Hc5FeignClientHttpRequestExecutor(HttpClient httpClient) {
        this(new ApacheHttp5Client(httpClient));
    }

    /**
     * A constructor with a {@code ApacheHttp5Client} parameter.
     * Allows the user to directly pass an {@code ApacheHttp5Client} instance
     * for initializing the {@code Hc5HttpRequestExecutor} object.
     * <p>
     * This method provides the greatest flexibility, allowing users to customize
     * the configuration of the {@code ApacheHttp5Client}.
     *
     * @param http5Client An {@code ApacheHttp5Client} instance for executing
     *                    HTTP requests.
     */
    public Hc5FeignClientHttpRequestExecutor(ApacheHttp5Client http5Client) {
        this.http5Client = http5Client;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return http5Client.execute(request, options);
    }
}
