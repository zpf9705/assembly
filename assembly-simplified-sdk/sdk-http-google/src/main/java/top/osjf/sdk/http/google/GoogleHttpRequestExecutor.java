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

import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.http.spi.AbstractMultiHttpMethodExecutor;
import top.osjf.sdk.http.spi.DefaultHttpResponse;
import top.osjf.sdk.http.spi.HttpRequestExecutor;
import top.osjf.sdk.http.spi.HttpResponse;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * One of the implementation classes of {@link HttpRequestExecutor}, please
 * refer to{@link GoogleHttpSimpleRequestUtils} for implementation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@LoadOrder(Integer.MIN_VALUE + 16)
public class GoogleHttpRequestExecutor extends AbstractMultiHttpMethodExecutor {
    @Override public HttpResponse get(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getGoogleResponseAsSpiResponse("GET", url, headers, body, charset);
    }
    @Override public HttpResponse post(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getGoogleResponseAsSpiResponse("POST", url, headers, body, charset);
    }
    @Override public HttpResponse put(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getGoogleResponseAsSpiResponse("PUT", url, headers, body, charset);
    }
    @Override public HttpResponse delete(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getGoogleResponseAsSpiResponse("DELETE", url, headers, body, charset);
    }
    @Override public HttpResponse trace(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getGoogleResponseAsSpiResponse("TRACE", url, headers, body, charset);
    }
    @Override public HttpResponse options(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getGoogleResponseAsSpiResponse("OPTIONS", url, headers, body, charset);
    }
    @Override public HttpResponse head(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getGoogleResponseAsSpiResponse("HEAD", url, headers, body, charset);
    }
    @Override public HttpResponse patch(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getGoogleResponseAsSpiResponse("PATCH", url, headers, body, charset);
    }
    private static HttpResponse getGoogleResponseAsSpiResponse(String methodName, String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        com.google.api.client.http.HttpResponse response = GoogleHttpSimpleRequestUtils.getResponse(null, methodName, url, headers, body, charset);
        int statusCode = response.getStatusCode();
        String statusMessage = response.getStatusMessage();
        Charset responseCharset = GoogleHttpSimpleRequestUtils.getCharsetByResponse(response);
        return new DefaultHttpResponse(statusCode,
                statusMessage,
                response.getHeaders(),
                responseCharset,
                response.parseAsString());
    }
}
