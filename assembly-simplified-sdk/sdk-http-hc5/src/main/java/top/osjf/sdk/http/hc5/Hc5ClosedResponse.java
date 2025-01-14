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

import java.nio.charset.Charset;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class Hc5ClosedResponse {
    private final ClassicHttpResponse rawResponse;
    private final String result;
    private final Charset charset;

    public Hc5ClosedResponse(ClassicHttpResponse rawResponse, String result, Charset charset) {
        this.rawResponse = rawResponse;
        this.result = result;
        this.charset = charset;
    }

    public ClassicHttpResponse getRawResponse() {
        return rawResponse;
    }

    public String getResult() {
        return result;
    }

    public Charset getCharset() {
        return charset;
    }
}
