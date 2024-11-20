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
    private final GoogleHttpClient googleHttpClient;

    public GoogleHttpRequestExecutor() {
        this(new GoogleHttpClient());
    }

    public GoogleHttpRequestExecutor(HttpTransport transport) {
        this(new GoogleHttpClient(transport));
    }

    public GoogleHttpRequestExecutor(HttpRequestFactory httpRequestFactory) {
        this(new GoogleHttpClient(httpRequestFactory));
    }

    public GoogleHttpRequestExecutor(GoogleHttpClient googleHttpClient) {
        this.googleHttpClient = googleHttpClient;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return googleHttpClient.execute(request, options);
    }
}
