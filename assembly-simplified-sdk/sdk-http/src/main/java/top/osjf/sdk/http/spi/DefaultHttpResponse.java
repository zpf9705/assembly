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


package top.osjf.sdk.http.spi;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * Default impl for {@link HttpResponse}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class DefaultHttpResponse implements HttpResponse {
    private static final long serialVersionUID = -1000743667370052276L;

    private final int statusCode;
    private final String statusMessage;
    private final Map<String, Object> headerMap;
    private final Charset charset;
    private final String body;

    public DefaultHttpResponse(int statusCode,
                               String statusMessage,
                               Map<String, Object> headerMap,
                               Charset charset,
                               String body) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headerMap = headerMap;
        this.charset = charset;
        this.body = body;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getStatusMessage() {
        return statusMessage;
    }

    @Override
    public Map<String, Object> getHeadMap() {
        return headerMap;
    }

    @Override
    public Charset getCharset() {
        return charset;
    }

    @Override
    public String getBody() {
        return body;
    }
}
