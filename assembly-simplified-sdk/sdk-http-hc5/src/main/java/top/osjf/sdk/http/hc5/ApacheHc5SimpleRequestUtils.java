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

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.HttpSdkSupport;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * The {@code ApacheHc5SimpleRequestUtils} class is a utility class for simplifying
 * HTTP requests using Apache HttpClient 5.x.
 * It provides a series of static methods for executing HTTP {@code GET}, {@code POST},
 * {@code PUT}, {@code DELETE},{@code TRACE}, {@code OPTIONS}, {@code HEAD}, and {@code PATCH}
 * requests.
 * <p>
 * These methods allow users to initiate HTTP requests by passing in a URL, request
 * headers, request parameters, and a boolean value indicating whether to append
 * parameters.
 * <p>
 * The class internally uses a default HttpClient instance but also allows users to
 * pass in a custom HttpClient instance.
 * The result of the request is returned as a string, containing the content of the
 * HTTP response body.
 * <p>
 * When executing a request, this class builds the complete request URI based on the
 * passed parameters and sets the corresponding request headers and request body
 * (if the flag for not appending parameters is not set).
 * Then, it uses the HttpClient instance to execute the request and obtain the response.
 * Finally, it closes the response and returns a string representation of the response body.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class ApacheHc5SimpleRequestUtils {
    private final static HttpClient DEFAULT = HttpClientBuilder.create().build();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ((CloseableHttpClient) DEFAULT).close();
            } catch (IOException ignored) {
            }
        }));
    }

    /**
     * Apache HTTP5 request for {@code Get}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String get(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, new HttpGet(getUri(url, requestParam, montage)), headers, montage, requestParam);
    }

    /**
     * Apache HTTP5 request for {@code Post}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String post(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, new HttpPost(getUri(url, requestParam, montage)), headers, montage, requestParam);
    }

    /**
     * Apache HTTP5 request for {@code Put}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String put(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, new HttpPut(getUri(url, requestParam, montage)), headers, montage, requestParam);
    }

    /**
     * Apache HTTP5 request for {@code Delete}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String delete(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, new HttpDelete(getUri(url, requestParam, montage)), headers, montage, requestParam);
    }

    /**
     * Apache HTTP5 request for {@code trace}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String trace(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, new HttpTrace(getUri(url, requestParam, montage)), headers, montage, requestParam);
    }

    /**
     * Apache HTTP5 request for {@code options}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String options(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, new HttpOptions(getUri(url, requestParam, montage)), headers, montage, requestParam);
    }

    /**
     * Apache HTTP5 request for {@code head}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String head(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, new HttpHead(getUri(url, requestParam, montage)), headers, montage, requestParam);
    }

    /**
     * Apache HTTP5 request for {@code patch}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String patch(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, new HttpPatch(getUri(url, requestParam, montage)), headers, montage, requestParam);
    }

    /**
     * The HTTP5 request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client       Apache's HTTP request client.
     * @param requestBase  HTTP Public Request Class {@link HttpUriRequestBase}.
     * @param headers      Header information map,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @param requestParam Request parameters,can be {@literal null}.
     * @return The {@code String} type of the return value
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String doRequest(HttpClient client,
                                   HttpUriRequestBase requestBase,
                                   Map<String, String> headers,
                                   boolean montage,
                                   Object requestParam) throws Exception {
        if (client == null) {
            client = DEFAULT;
        }
        ClassicHttpResponse response = null;
        String result;
        try {
            addHeaders(headers, requestBase);
            //Set the request body when there are no parameters attached to the query.
            if (!montage) setEntity(requestParam, requestBase, headers);
            response = client.execute(requestBase, r -> r);
            result = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    /**
     * Set {@link HttpEntity} with a {@link StringEntity}.
     *
     * @param requestParam Request parameters,can be {@literal null}.
     * @param requestBase  HTTP Public Request Class {@link HttpUriRequestBase}
     * @param headers      Header information map,can be {@literal null}.
     */
    private static void setEntity(Object requestParam, HttpUriRequestBase requestBase, Map<String, String> headers) {
        if (requestParam == null) {
            return;
        }
        ContentType contentType = ContentType.APPLICATION_JSON;
        String str = requestParam.toString();
        StringEntity stringEntity;
        if (MapUtils.isNotEmpty(headers)) {
            String value = headers.get("Content-type");
            if (StringUtils.isNotBlank(value)) {
                contentType = ContentType.parse(value);
            }
        }
        stringEntity = new StringEntity(str, contentType);
        requestBase.setEntity(stringEntity);
    }

    /**
     * Add header information for HTTP requests.
     *
     * @param headers     Header information map,can be {@literal null}.
     * @param requestBase HTTP Public Request Class {@link HttpUriRequestBase}.
     */
    private static void addHeaders(Map<String, String> headers, HttpUriRequestBase requestBase) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBase.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    /**
     * The get request for building HTTP contains the construction object of {@link URI}.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return Uri object,Please pay attention to the format issue of the URL.
     * @throws Exception unknown exception.
     */
    private static URI getUri(String url, Object requestParam, boolean montage) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        Map<String, Object> params = HttpSdkSupport.urlMontageBody(montage, requestParam);
        if (params != null) {
            for (String paramKey : params.keySet()) {
                uriBuilder.addParameter(paramKey, String.valueOf(params.get(paramKey)));
            }
        }
        //If it is null or a string, it can be directly used as a paparazzi
        return uriBuilder.build();
    }
}
