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

package top.osjf.sdk.http.apache;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import top.osjf.sdk.core.util.JSONUtil;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * The Apache HTTP client request tool class mainly includes four request methods: post, get, put, and del.
 *
 * <p>And other methods that can be customized with HTTP support, link to method {@link #doRequest}.
 *
 * <p>This class is a simple request tool for {@link CloseableHttpClient} and does not provide any other special
 * functions.
 *
 * <p>If necessary, please implement it yourself.</p>
 *
 * <p>Only suitable for use in this project.</p>
 *
 * <p>You need to note that the {@code montage} parameter determines whether parameters that are
 * not {@literal null} need to be concatenated after the URL in the form of key/value,
 * and you need to pay attention to the concatenation rules of the {@link #getUri(String, Object, boolean)}
 * method and the format rules of the parameters when the parameter is <pre>{@code montage == true}</pre>.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class ApacheHttpSimpleRequestUtils {

    private final static HttpClient DEFAULT_HTTP_CLIENT;

    static {
        DEFAULT_HTTP_CLIENT = HttpClients.createDefault();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (DEFAULT_HTTP_CLIENT != null) {
                    try {
                        ((CloseableHttpClient) DEFAULT_HTTP_CLIENT).close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }));
    }

    /**
     * Apache HTTP request for {@code Get}.
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
        return doRequest(null, new HttpGet(), url, headers, montage, requestParam);
    }

    /**
     * Apache HTTP request for {@code Post}.
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
        return doRequest(null, new HttpPost(), url, headers, montage, requestParam);
    }

    /**
     * Apache HTTP request for {@code Put}.
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
        return doRequest(null, new HttpPut(), url, headers, montage, requestParam);
    }

    /**
     * Apache HTTP request for {@code Delete}.
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
        return doRequest(null, new HttpDelete(), url, headers, montage, requestParam);
    }

    /**
     * Apache HTTP request for {@code trace}.
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
        return doRequest(null, new HttpTrace(), url, headers, montage, requestParam);
    }

    /**
     * Apache HTTP request for {@code options}.
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
        return doRequest(null, new HttpOptions(), url, headers, montage, requestParam);
    }

    /**
     * Apache HTTP request for {@code head}.
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
        return doRequest(null, new HttpHead(), url, headers, montage, requestParam);
    }

    /**
     * Apache HTTP request for {@code patch}.
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
        return doRequest(null, new HttpPatch(), url, headers, montage, requestParam);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client       Apache's HTTP request client.
     * @param requestBase  HTTP Public Request Class {@link HttpRequestBase}.
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @param requestParam Request parameters,can be {@literal null}.
     * @return The {@code String} type of the return value
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String doRequest(HttpClient client,
                                   HttpRequestBase requestBase,
                                   String url,
                                   Map<String, String> headers,
                                   boolean montage,
                                   Object requestParam) throws Exception {
        if (client == null) {
            client = DEFAULT_HTTP_CLIENT;
        }
        requestBase.setURI(getUri(url, requestParam, montage));
        HttpResponse response = null;
        String result;
        try {
            addHeaders(headers, requestBase);
            //Set the request body when there are no parameters attached to the query.
            if (!montage) setEntity(requestParam, requestBase, headers);
            response = client.execute(requestBase);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } finally {
            if (response != null) {
                if (response instanceof CloseableHttpResponse) {
                    ((CloseableHttpResponse) response).close();
                }
            }
        }
        return result;
    }

    /**
     * Set {@link HttpEntity} with a {@link StringEntity}.
     *
     * @param requestParam Request parameters,can be {@literal null}.
     * @param requestBase  HTTP Public Request Class {@link HttpRequestBase}
     * @param headers      Header information map,can be {@literal null}.
     */
    public static void setEntity(Object requestParam, HttpRequestBase requestBase, Map<String, String> headers) {
        if (requestParam == null || !(requestBase instanceof HttpEntityEnclosingRequestBase)) {
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
        HttpEntityEnclosingRequestBase base = (HttpEntityEnclosingRequestBase) requestBase;
        base.setEntity(stringEntity);
    }

    /**
     * Add header information for HTTP requests.
     *
     * @param headers     Header information map,can be {@literal null}.
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}.
     */
    public static void addHeaders(Map<String, String> headers, HttpRequestBase requestBase) {
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
    @SuppressWarnings("unchecked")
    public static URI getUri(String url, Object requestParam, boolean montage) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (montage && requestParam != null) {
            if (!(requestParam instanceof Map)
                    && !(requestParam instanceof String))
                throw new IllegalArgumentException("If you need to concatenate parameters onto the URL, " +
                        "please provide parameters of map type or JSON type of key/value " +
                        "(which will automatically convert map concatenation). " +
                        "If you provide a simple string type, then the URL parameter will be directly returned.");
            //If the type is a map concatenated after the address
            Map<String, Object> params = null;
            if (requestParam instanceof Map) {
                params = (Map<String, Object>) requestParam;
            } else {
                String jsonStr = requestParam.toString();
                if (JSONUtil.isValidObject(jsonStr)) {
                    params = JSONUtil.getInnerMapByJsonStr(jsonStr);
                }
            }
            if (MapUtils.isNotEmpty(params)) {
                for (String paramKey : params.keySet()) {
                    uriBuilder.addParameter(paramKey, String.valueOf(params.get(paramKey)));
                }
            }
        }
        //If it is null or a string, it can be directly used as a paparazzi
        return uriBuilder.build();
    }
}
