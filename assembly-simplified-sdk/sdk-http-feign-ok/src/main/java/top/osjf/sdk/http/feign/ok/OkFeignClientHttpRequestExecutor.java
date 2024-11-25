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

package top.osjf.sdk.http.feign.ok;

import feign.Request;
import feign.Response;
import feign.okhttp.OkHttpClient;
import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.http.feign.bridging.AbstractFeignClientHttpRequestExecutor;

import java.io.IOException;

/**
 * The calling method of {@code OkHttp} uses the
 * {@code feign-ok-http} component integrated with open feign.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@LoadOrder(Integer.MIN_VALUE + 11)
public class OkFeignClientHttpRequestExecutor extends AbstractFeignClientHttpRequestExecutor {

    /**
     * Define a private, immutable {@code feign.okhttp.OkHttpClient} instance variable for executing HTTP requests.
     */
    private final OkHttpClient okHttpClient;

    /**
     * Nonparametric construction method.
     * <p>
     * Create a new {@code OkHttpRequestExecutor} instance using the
     * default configuration of {@code feign.okhttp.okHttpClient}.
     * This is a convenient construction method that does not require
     * any external configuration input.
     */
    public OkFeignClientHttpRequestExecutor() {
        this(new OkHttpClient());
    }

    /**
     * Create a new {@code OkHttpRequestExecutor} that constructs a
     * {@code feign.okhttp.okHttpClient}. based on the parameter
     * {@code okhttp3.OkHttpClient}, paying attention to the package
     * categories within it.
     * <p>
     * This construction method allows users to customize
     * {@code okhttp3.OkHttpClient} to create new {@code feign.okhttp.okHttpClient}
     * and further create a {@code OkHttpRequestExecutor}.
     *
     * @param okHttpClient HTTP request client under package {@code okhttp3}.
     */
    public OkFeignClientHttpRequestExecutor(okhttp3.OkHttpClient okHttpClient) {
        this(new OkHttpClient(okHttpClient));
    }

    /**
     * Create a new {@code OkHttpRequestExecutor} with parameters
     * that need to handle HTTP request clients under package
     * {@code feign.okhttp.okHttpClient}.
     * <p>
     * This construction method allows users to customize
     * {@code feign.okhttp.OkHttpClient} to create new {@code OkHttpRequestExecutor}.
     *
     * @param okHttpClient HTTP request client under package {@code feign.okhttp}.
     */
    public OkFeignClientHttpRequestExecutor(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return okHttpClient.execute(request, options);
    }
}
