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

package top.osjf.sdk.http.feign.apache;

import feign.Request;
import feign.Response;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.client.HttpClient;
import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.http.feign.bridging.AbstractFeignClientHttpRequestExecutor;

import java.io.IOException;

/**
 * The calling method of {@code ApacheHttp} uses the
 * {@code feign-apache-http} component integrated with open feign.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@LoadOrder(Integer.MIN_VALUE + 10)
public class ApacheFeignClientHttpRequestExecutor extends AbstractFeignClientHttpRequestExecutor {

    /**
     * Define a private final variable of type {@code ApacheHttpClient} for executing HTTP requests.
     */
    private final ApacheHttpClient apacheHttpClient;

    /**
     * Nonparametric constructor method, using the default {@code ApacheHttpClient}
     * instance to initialize objects.
     * <p>
     * This is a convenient constructor method that internally calls a constructor
     * method with {@code ApacheHttpClient}  parameters,And passed on a newly created
     * {@code ApacheHttpClient} instance.
     */
    public ApacheFeignClientHttpRequestExecutor() {
        this(new ApacheHttpClient());
    }

    /**
     * A constructor method with an FHIR parameter that allows users to pass in a
     * custom {@code HttpClient} instance,
     * Then use this instance to create an {@code ApacheHttpClient} and use it to
     * initialize objects.
     * <p>
     * This construction method provides flexibility, allowing users to configure
     * {@code HttpClient} according to their own needs,
     * For example, setting timeout periods, proxies, etc.
     *
     * @param httpClient user custom {@code HttpClient} Instance.
     */
    public ApacheFeignClientHttpRequestExecutor(HttpClient httpClient) {
        this(new ApacheHttpClient(httpClient));
    }

    /**
     * A constructor method with an {@code ApacheHttpClient} parameter that can be
     * directly used to initialize an object.
     * <p>
     * This construction method allows users to directly pass in a preconfigured
     * {@code ApacheHttpClient} instance,Suitable for scenarios that require finer
     * grained control over {@code ApacheHttpClient} configuration.
     *
     * @param apacheHttpClient user defined and already configured {@code ApacheHttpClient}
     *                         instance.
     */
    public ApacheFeignClientHttpRequestExecutor(ApacheHttpClient apacheHttpClient) {
        this.apacheHttpClient = apacheHttpClient;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return apacheHttpClient.execute(request, options);
    }
}
