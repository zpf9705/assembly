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

import top.osjf.sdk.core.lang.Nullable;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;

/**
 * The default implementation encapsulation class for {@link HttpResponse} interface
 * description information provides support for obtaining it.
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
    private final Object protocolVersion;
    /**
     * Creates a new {@code DefaultHttpRequest} by given original http request
     * and access url and a {@code HttpRequestOptions}.
     *
     * @param statusCode        the status code of the HTTP response.
     * @param statusMessage     the status message of the HTTP response.
     * @param headerMap         the header information of the HTTP response.
     * @param charset           the {@code Charset} set of the HTTP response.
     * @param body              the body content of the HTTP response.
     * @param protocolVersion   the http protocol version instance.
     */
    public DefaultHttpResponse(int statusCode,
                               String statusMessage,
                               Map<String, Object> headerMap,
                               Charset charset,
                               String body,
                               Object protocolVersion) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headerMap = Collections.unmodifiableMap(headerMap);
        this.charset = charset;
        this.body = body;
        this.protocolVersion = protocolVersion;
    }
    @Override public int getStatusCode() {
        return statusCode;
    }
    @Override public String getStatusMessage() {
        return statusMessage;
    }
    @Override public Map<String, Object> getHeadMap() {
        return headerMap;
    }
    @Override public Charset getCharset() {
        return charset;
    }
    @Override public String getBody() {
        return body;
    }
    @Nullable @Override public Object getProtocolVersion() {
        return protocolVersion;
    }
}
