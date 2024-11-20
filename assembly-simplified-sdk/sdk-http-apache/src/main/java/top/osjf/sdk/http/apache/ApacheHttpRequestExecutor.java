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

package top.osjf.sdk.http.apache;

import feign.Request;
import feign.Response;
import feign.httpclient.ApacheHttpClient;
import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.http.DeprecatedHttpRequestExecutor;
import top.osjf.sdk.http.HttpRequestExecutor;

import java.io.IOException;

/**
 * One of the implementation classes of {@link HttpRequestExecutor}, please
 * refer to{@link ApacheHttpSimpleRequestUtils} for implementation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@LoadOrder(Integer.MIN_VALUE + 10)
public class ApacheHttpRequestExecutor extends DeprecatedHttpRequestExecutor implements HttpRequestExecutor {

    private final ApacheHttpClient apacheHttpClient;

    public ApacheHttpRequestExecutor() {
        this(new ApacheHttpClient());
    }

    public ApacheHttpRequestExecutor(ApacheHttpClient apacheHttpClient) {
        this.apacheHttpClient = apacheHttpClient;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        return apacheHttpClient.execute(request, options);
    }

    @Override
    protected Class<?> toolClass() {
        return ApacheHttpSimpleRequestUtils.class;
    }
}
