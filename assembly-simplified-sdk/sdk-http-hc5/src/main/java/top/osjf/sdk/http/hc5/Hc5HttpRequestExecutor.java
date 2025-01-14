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

import org.apache.hc.client5.http.classic.methods.*;
import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.http.spi.AbstractMultiHttpMethodExecutor;
import top.osjf.sdk.http.spi.HttpRequestExecutor;
import top.osjf.sdk.http.spi.HttpResponse;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * One of the implementation classes of {@link HttpRequestExecutor}, please
 * refer to{@link ApacheHc5SimpleRequestUtils} for implementation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@LoadOrder(Integer.MIN_VALUE + 18)
public class Hc5HttpRequestExecutor extends AbstractMultiHttpMethodExecutor {
    @Override public HttpResponse get(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getApacheResponseAsSpiResponse(new HttpGet(url), headers, body, charset);
    }
    @Override public HttpResponse post(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getApacheResponseAsSpiResponse(new HttpPost(url), headers, body, charset);
    }
    @Override public HttpResponse put(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getApacheResponseAsSpiResponse(new HttpPut(url), headers, body, charset);
    }
    @Override public HttpResponse delete(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getApacheResponseAsSpiResponse(new HttpDelete(url), headers, body, charset);
    }
    @Override public HttpResponse trace(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getApacheResponseAsSpiResponse(new HttpTrace(url), headers, body, charset);
    }
    @Override public HttpResponse options(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getApacheResponseAsSpiResponse(new HttpOptions(url), headers, body, charset);
    }
    @Override public HttpResponse head(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getApacheResponseAsSpiResponse(new HttpHead(url), headers, body, charset);
    }
    @Override public HttpResponse patch(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return getApacheResponseAsSpiResponse(new HttpPatch(url), headers, body, charset);
    }
    private static HttpResponse getApacheResponseAsSpiResponse(HttpUriRequestBase requestBase, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) throws Exception {
        return ApacheHc5SimpleRequestUtils.getResponse(null, requestBase, headers, body, charset);
    }
}
