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

package top.osjf.sdk.http;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;

/**
 * The default {@link HttpRequestExecutor} implementation class depends
 * on {@code hutool.http} for executing HTTP requests.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class DefaultHttpRequestExecutor implements HttpRequestExecutor {

    @Override
    public String execute(ExecutorHttpRequest httpRequest) throws Exception {
        HttpRequest request = toHttpRequest(httpRequest);
        try (HttpResponse response = request.execute()) {
            return response.body();
        }
    }

    /**
     * Convert a {@link top.osjf.sdk.http.HttpRequestExecutor.ExecutorHttpRequest} to
     * {@code Hutool} http request {@link HttpRequest}.
     *
     * @param httpRequest a {@code top.osjf.sdk.http.HttpRequestExecutor.ExecutorHttpRequest}.
     * @return {@code Hutool} http request.
     */
    private HttpRequest toHttpRequest(ExecutorHttpRequest httpRequest) {
        String methodName = httpRequest.getMethodName();
        HttpRequest request = HttpUtil.createRequest(Method.valueOf(methodName), httpRequest.getUrl());
        RequestOptions options = httpRequest.getOptions().getMethodOptions(methodName);
        if (httpRequest.getBody() != null)
            request.body(httpRequest.getBody(String.class), httpRequest.getHeader("Content-Type"));
        request.charset(httpRequest.getCharset());
        request.headerMap(httpRequest.getHeaders(), true);
        request.setConnectionTimeout((int) options.connectTimeoutUnit().toMillis(options.connectTimeout()));
        request.setReadTimeout((int) options.readTimeoutUnit().toMillis(options.readTimeout()));
        request.setFollowRedirects(options.isFollowRedirects());
        return request;
    }
}
