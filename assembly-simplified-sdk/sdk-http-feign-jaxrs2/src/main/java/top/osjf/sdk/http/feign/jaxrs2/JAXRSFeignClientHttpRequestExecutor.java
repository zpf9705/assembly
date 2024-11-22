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

package top.osjf.sdk.http.feign.jaxrs2;

import feign.Request;
import feign.Response;
import feign.jaxrs2.JAXRSClient;
import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.http.feign.bridging.FeignClientHttpRequestExecutor;

import javax.ws.rs.client.ClientBuilder;
import java.io.IOException;

/**
 * The calling method of {@code Hc5Http} uses the
 * {@code feign-hc5-http} component integrated with open feign.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@LoadOrder(Integer.MIN_VALUE + 14)
public class JAXRSFeignClientHttpRequestExecutor implements FeignClientHttpRequestExecutor {

    /**
     * Define a private, final JAX-RS client instance variable for executing HTTP
     * requests based on the JAX-RS specification.
     */
    private final JAXRSClient jaxrsClient;

    /**
     * No-argument constructor.
     * Initializes the {@code JAXRSHttpRequestExecutor} object with a
     * default {@code JAXRSClient} instance.
     * <p>
     * Provides a simple construction method that requires no external parameters.
     */
    public JAXRSFeignClientHttpRequestExecutor() {
        this(new JAXRSClient());
    }

    /**
     * Constructor with a {@code ClientBuilder} parameter.
     * <p>
     * Allows the user to pass in a custom {@code ClientBuilder} instance
     * to construct and initialize a customized {@code JAXRSClient} instance,
     * which in turn initializes the {@code JAXRSHttpRequestExecutor} object.
     *
     * @param clientBuilder The custom {@code ClientBuilder} instance used to
     *                      construct the JAX-RS client.
     */
    public JAXRSFeignClientHttpRequestExecutor(ClientBuilder clientBuilder) {
        this(new JAXRSClient(clientBuilder));
    }

    /**
     * Constructor with a {@code JAXRSClient} parameter.
     * <p>
     * Allows the user to directly pass in a pre-configured {@code JAXRSClient}
     * instance to initialize the {@code JAXRSHttpRequestExecutor} object.
     * <p>
     * This method provides the greatest flexibility, allowing the user to customize
     * the configuration of the {@code JAXRSClient}.
     *
     * @param jaxrsClient The pre-configured {@code JAXRSClient} instance
     *                    used to execute HTTP requests.
     */
    public JAXRSFeignClientHttpRequestExecutor(JAXRSClient jaxrsClient) {
        this.jaxrsClient = jaxrsClient;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return jaxrsClient.execute(request, options);
    }
}
