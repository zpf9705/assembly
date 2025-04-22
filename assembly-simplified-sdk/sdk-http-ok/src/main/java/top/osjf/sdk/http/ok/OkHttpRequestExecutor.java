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

package top.osjf.sdk.http.ok;

import okhttp3.Request;
import okhttp3.Response;
import top.osjf.sdk.core.lang.Nullable;
import top.osjf.sdk.core.spi.Spi;
import top.osjf.sdk.core.util.Pair;
import top.osjf.sdk.http.spi.AbstractMultiHttpMethodExecutor;
import top.osjf.sdk.http.spi.DefaultHttpResponse;
import top.osjf.sdk.http.spi.HttpRequestExecutor;
import top.osjf.sdk.http.spi.HttpResponse;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * One of the implementation classes of {@link HttpRequestExecutor}, please
 * refer to {@link OkHttpSimpleRequestUtils} for implementation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Spi(order = Integer.MIN_VALUE + 14)
public class OkHttpRequestExecutor extends AbstractMultiHttpMethodExecutor {
    @Override public HttpResponse get(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getOkResponseAsSpiResponse("GET", url, headers, body, charset);
    }
    @Override public HttpResponse post(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getOkResponseAsSpiResponse("POST", url, headers, body, charset);
    }
    @Override public HttpResponse put(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getOkResponseAsSpiResponse("PUT", url, headers, body, charset);
    }
    @Override public HttpResponse delete(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getOkResponseAsSpiResponse("DELETE", url, headers, body, charset);
    }
    @Override public HttpResponse trace(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getOkResponseAsSpiResponse("TRACE", url, headers, body, charset);
    }
    @Override public HttpResponse options(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getOkResponseAsSpiResponse("OPTIONS", url, headers, body, charset);
    }
    @Override public HttpResponse head(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getOkResponseAsSpiResponse("HEAD", url, headers, body, charset);
    }
    @Override public HttpResponse patch(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getOkResponseAsSpiResponse("PATCH", url, headers, body, charset);
    }
    private static HttpResponse getOkResponseAsSpiResponse(String methodName, String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        Response response = null;
        try {
            Request.Builder builder = OkHttpSimpleRequestUtils.getRequestBuilder(url, body, charset, headers, methodName);
            response = OkHttpSimpleRequestUtils.getResponse(null, builder, headers);
            Map<String, Object> responseHeaders = new HashMap<>();
            for (String name : response.headers().names()) {
                responseHeaders.put(name, response.headers(name));
            }
            Pair<String, Charset> pair = OkHttpSimpleRequestUtils.getCharsetByResponse(response);
            return new DefaultHttpResponse(response.code(),
                    response.message(),
                    responseHeaders,
                    pair.getSecond(),
                    pair.getFirst(),
                    response.protocol());
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }
}
