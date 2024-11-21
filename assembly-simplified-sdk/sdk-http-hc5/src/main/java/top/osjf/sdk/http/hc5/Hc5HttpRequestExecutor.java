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

package top.osjf.sdk.http.hc5;

import feign.Request;
import feign.Response;
import feign.hc5.ApacheHttp5Client;
import org.apache.hc.client5.http.classic.HttpClient;
import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.http.HttpRequestExecutor;
import top.osjf.sdk.http.UnsupportedCustomizeHttpRequestExecutor;

import java.io.IOException;

/**
 * Starting from version 1.0.2, the calling method of {@code Hc5Http} uses the
 * {@code feign-hc5-http} component integrated with open feign.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@LoadOrder(Integer.MIN_VALUE + 13)
public class Hc5HttpRequestExecutor extends UnsupportedCustomizeHttpRequestExecutor implements HttpRequestExecutor {

    private final ApacheHttp5Client http5Client;

    public Hc5HttpRequestExecutor() {
        this(new ApacheHttp5Client());
    }

    public Hc5HttpRequestExecutor(HttpClient httpClient) {
        this(new ApacheHttp5Client(httpClient));
    }

    public Hc5HttpRequestExecutor(ApacheHttp5Client http5Client) {
        this.http5Client = http5Client;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return http5Client.execute(request, options);
    }
}
