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

import okhttp3.*;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.HttpSdkSupport;
import top.osjf.sdk.http.ResponseFailedException;

import java.util.Map;

/**
 * The Square's HTTP client request tool class mainly includes four request methods: post, get, put, and del.
 *
 * <p>And other methods that can be customized with HTTP support, link to method
 * {@link #doRequest}
 *
 * <p>This class is a simple request tool for {@link OkHttpClient} and does not provide any other special functions.
 *
 * <p>If necessary, please implement it yourself.</p>
 *
 * <p>Only suitable for use in this project.</p>
 *
 * <p>You need to note that the {@code montage} parameter determines whether parameters that are
 * not {@literal null} need to be concatenated after the URL in the form of key/value,
 * and you need to pay attention to the concatenation rules of the
 * {@link #getRequestBuilder(String, Object, boolean, Map, String)}} method and the
 * format rules of the parameters when the parameter is <pre>{@code montage == true}</pre>.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class OkHttpSimpleRequestUtils {
    private static final OkHttpClient DEFAULT = new okhttp3.OkHttpClient().newBuilder().build();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Dispatcher dispatcher = DEFAULT.dispatcher();
            dispatcher.executorService().shutdownNow();
        }));
    }

    /**
     * Square's HTTP request for {@code Get}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String get(String url, Map<String, String> headers, Object requestParam, boolean montage)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "GET"),
                headers);
    }

    /**
     * Square's HTTP request for {@code Post}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
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
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "POST"), headers);
    }

    /**
     * Square's HTTP request for {@code Put}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
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
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "PUT"),
                headers);
    }

    /**
     * Square's HTTP request for {@code Delete}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
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
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "DELETE"),
                headers);
    }

    /**
     * Square's HTTP request for {@code trace}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
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
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "TRACE"),
                headers);
    }

    /**
     * Square's HTTP request for {@code options}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
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
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "OPTIONS"),
                headers);
    }

    /**
     * Square's HTTP request for {@code head}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
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
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "HEAD"),
                headers);
    }

    /**
     * Square's HTTP request for {@code patch}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
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
        return doRequest(null, getRequestBuilder(url, requestParam, montage, headers, "PATCH"),
                headers);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client  Square's HTTP request client,can be {@literal null}.
     * @param builder HTTP Public Request Class {@link Request.Builder}.
     * @param headers Header information map,can be {@literal null}.
     * @return The {@code String} type of the return value
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    public static String doRequest(okhttp3.OkHttpClient client,
                                   Request.Builder builder,
                                   Map<String, String> headers) throws Exception {
        if (client == null) {
            client = DEFAULT;
        }
        Response response = null;
        String result;
        try {
            addHeaders(headers, builder);
            response = client.newCall(builder.build()).execute();
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body != null) {
                    result = body.string();
                } else {
                    result = "";
                }
            } else {
                throw new ResponseFailedException(response.message());
            }
        } finally {
            if (response != null) response.close();
        }
        return result;
    }

    /**
     * Add header information for HTTP Request.
     *
     * @param headers Header information map,can be {@literal null}.
     * @param builder HTTP Public Request Class {@link Request.Builder}.
     */
    public static void addHeaders(Map<String, String> headers, Request.Builder builder) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                builder.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    /**
     * Use relevant parameters to obtain the {@link Request} parameters required for {@link OkHttpClient} execution.
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @param headers      Header information map,can be {@literal null}.
     * @param method       Distinguish request types,must not be {@literal null}.
     * @return Obtain the condition builder for {@link Request}.
     */
    public static Request.Builder getRequestBuilder(String url, Object requestParam, boolean montage,
                                                    Map<String, String> headers,
                                                    String method) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            throw new IllegalArgumentException("Url is not valid");
        }
        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        Map<String, Object> params = null;
        if (montage) params = HttpSdkSupport.resolveMontageObj(requestParam);
        if (params != null) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        Request.Builder requestBuild = new Request.Builder().url(urlBuilder.build());
        String value;
        if (MapUtils.isNotEmpty(headers)) {
            value = headers.get("Content-type");
            if (StringUtils.isBlank(value)) {
                value = "application/json";
            }
        } else {
            value = "application/json";
        }
        value = value.concat(";charset=utf-8");
        RequestBody body = RequestBody.create(MediaType.parse(value),
                requestParam == null ? "" : requestParam.toString());
        switch (method) {
            case "GET":
                requestBuild = requestBuild.get();
                break;
            case "POST":
                requestBuild = requestBuild.post(body);
                break;
            case "PUT":
                requestBuild = requestBuild.put(body);
                break;
            case "DELETE":
                requestBuild = requestBuild.delete(body);
                break;
            case "TRACE":
                requestBuild = requestBuild.method("TRACE", body);
            case "OPTIONS":
                requestBuild = requestBuild.method("OPTIONS", body);
            case "HEAD":
                requestBuild = requestBuild.head();
            case "PATCH":
                requestBuild = requestBuild.patch(body);
        }
        return requestBuild;
    }
}
