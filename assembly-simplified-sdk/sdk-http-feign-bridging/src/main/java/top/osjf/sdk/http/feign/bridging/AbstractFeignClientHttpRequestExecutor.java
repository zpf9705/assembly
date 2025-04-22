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

package top.osjf.sdk.http.feign.bridging;

import feign.Request;
import feign.Response;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.http.client.HttpRequestOptions;
import top.osjf.sdk.http.spi.DefaultHttpResponse;
import top.osjf.sdk.http.spi.HttpRequest;
import top.osjf.sdk.http.spi.HttpResponse;
import top.osjf.sdk.http.util.IOUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The abstract {@code FeignClientHttpRequestExecutor} class undergoes the following
 * instance conversion process:
 * <ul>
 * <li>Convert the {@link HttpRequest} instance to the {@link Request} object required
 * for integrating the HTTP framework with feign.</li>
 * <li>Converting the {@link Response} object requested by the feign framework into a
 * {@link HttpResponse} object meets the requirements of the SDK framework.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class AbstractFeignClientHttpRequestExecutor implements FeignClientHttpRequestExecutor {

    @Override
    public final HttpResponse execute(@NotNull HttpRequest httpRequest) throws Exception {
        //Create a request body for feign.
        feign.Request.Body feignBody;
        String requestBody = httpRequest.getBody(String.class, Object::toString);
        Charset charset = httpRequest.getCharset();

        if (requestBody != null) {
            if (charset != null) {
                feignBody = feign.Request.Body.create(requestBody, charset);
            } else {
                feignBody = feign.Request.Body.create(requestBody);
            }
        } else/* Need to provide a default value of null */
            feignBody = feign.Request.Body.create((byte[]) null, charset);

        //The value of the request header is converted into a collection, which meets the requirements of feature.
        Map<String, Collection<String>> feignHeaders = httpRequest.getCollectionValueHeaders(String.class,
                Object::toString);

        //Create a request object for feign.
        String methodName = httpRequest.getMethodName();
        feign.Request feignRequest = feign.Request
                .create(feign.Request.HttpMethod.valueOf(methodName),
                        //When integrating components, directly formatting the
                        // URL here involves attaching query parameters.
                        httpRequest.getUrl(),
                        feignHeaders,
                        feignBody,
                        null);/* We won't set up using third-party HTTP here.  */

        //Get the required configuration for the current thread.
        HttpRequestOptions options = httpRequest.getOptions().getMethodOptions(methodName);
        //Set the HTTP execution option for feign.
        Request.Options feignOptions =
                new Request.Options(options.connectTimeout(), options.connectTimeoutUnit(),
                        options.readTimeout(), options.readTimeoutUnit(), options.isFollowRedirects());
        //Put it into the thread configuration of feign.
        feignOptions.setMethodOptions(methodName, feignOptions);

        //Read the stream response result of the feature client and return the request result
        // in the form of a string for subsequent conversion operations.

        //Automatically close the resource information that responds.
        try (Response response = execute(feignRequest, feignOptions)) {
            return new DefaultHttpResponse
                    (response.status(),
                            response.reason(),
                            toValueObjHeaderMap(response.headers()),
                            response.charset(),
                            toStringBody(response),
                            response.protocolVersion());
        }
    }

    private static Map<String, Object> toValueObjHeaderMap(Map<String, Collection<String>> feignHeaders) {
        if (MapUtils.isEmpty(feignHeaders)) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<>(feignHeaders));
    }

    private static String toStringBody(Response response) throws IOException {
        byte[] bytes = IOUtils.readAllBytes(response.body().asInputStream());
        Charset charset = response.charset();
        return charset != null ? new String(bytes, charset) : new String(bytes);
    }
}
