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

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import top.osjf.sdk.http.spi.DefaultHttpResponse;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * HC5 response result processing encapsulates the {@link top.osjf.sdk.http.spi.HttpResponse} subclass.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class Hc5ClosedResponse extends DefaultHttpResponse {
    private static final long serialVersionUID = -470351131493467831L;

    public Hc5ClosedResponse(ClassicHttpResponse rawResponse, String result, Charset charset) {
        super(rawResponse.getCode(), rawResponse.getReasonPhrase(),
                toHeaderMap(rawResponse.getHeaders()), charset, result, rawResponse.getVersion());
    }

    private static Map<String, Object> toHeaderMap(Header[] headers) {
        Map<String, Object> responseHeaders = new HashMap<>();
        for (Header header : headers) {
            responseHeaders.put(header.getName(), header.getValue());
        }
        return responseHeaders;
    }
}
