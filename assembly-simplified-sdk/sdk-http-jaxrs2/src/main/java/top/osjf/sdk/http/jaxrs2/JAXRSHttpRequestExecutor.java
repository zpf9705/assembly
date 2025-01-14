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

package top.osjf.sdk.http.jaxrs2;

import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.http.spi.AbstractMultiHttpMethodExecutor;
import top.osjf.sdk.http.spi.DefaultHttpResponse;
import top.osjf.sdk.http.spi.HttpRequestExecutor;
import top.osjf.sdk.http.spi.HttpResponse;

import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * One of the implementation classes of {@link HttpRequestExecutor}, please
 * refer to{@link JAXRSHttpSimpleRequestUtils} for implementation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@LoadOrder(Integer.MIN_VALUE + 20)
public class JAXRSHttpRequestExecutor extends AbstractMultiHttpMethodExecutor {
    @Override public HttpResponse get(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getJAXRSResponseAsSpiResponse("GET", url, headers, body, charset);
    }
    @Override public HttpResponse post(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getJAXRSResponseAsSpiResponse("POST", url, headers, body, charset);
    }
    @Override public HttpResponse put(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getJAXRSResponseAsSpiResponse("PUT", url, headers, body, charset);
    }
    @Override public HttpResponse delete(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getJAXRSResponseAsSpiResponse("DELETE", url, headers, body, charset);
    }
    @Override public HttpResponse trace(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getJAXRSResponseAsSpiResponse("TRACE", url, headers, body, charset);
    }
    @Override public HttpResponse options(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getJAXRSResponseAsSpiResponse("OPTIONS", url, headers, body, charset);
    }
    @Override public HttpResponse head(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getJAXRSResponseAsSpiResponse("HEAD", url, headers, body, charset);
    }
    @Override public HttpResponse patch(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getJAXRSResponseAsSpiResponse("PATCH", url, headers, body, charset);
    }
    private static HttpResponse getJAXRSResponseAsSpiResponse(String methodName, String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        try (Response response = JAXRSHttpSimpleRequestUtils.getResponse(null, url, methodName, headers, body, charset)) {
            Response.StatusType statusInfo = response.getStatusInfo();
            int statusCode = statusInfo.getStatusCode();
            String message = statusInfo.getReasonPhrase();
            Charset responseCharset = JAXRSHttpSimpleRequestUtils.getCharsetByResponse(response);
            return new DefaultHttpResponse(statusCode,
                    message,
                    new HashMap<>(response.getHeaders()),
                    responseCharset,
                    response.readEntity(String.class));
        }
    }
}
