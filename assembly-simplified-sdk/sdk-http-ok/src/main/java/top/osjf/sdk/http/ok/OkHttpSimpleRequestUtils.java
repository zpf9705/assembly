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
import top.osjf.sdk.http.exception.ResponseFailedException;
import top.osjf.sdk.http.support.HttpSdkSupport;

import java.nio.charset.Charset;
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
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String get(String url, Map<String, String> headers, Object body, Charset charset)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, body, headers, "GET", charset),
                headers);
    }

    /**
     * Square's HTTP request for {@code Post}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String post(String url, Map<String, String> headers, Object body)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, body, headers, "POST"), headers);
    }

    /**
     * Square's HTTP request for {@code Put}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String put(String url, Map<String, String> headers, Object body)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, body, headers, "PUT"),
                headers);
    }

    /**
     * Square's HTTP request for {@code Delete}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String delete(String url, Map<String, String> headers, Object body)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, body, headers, "DELETE"),
                headers);
    }

    /**
     * Square's HTTP request for {@code trace}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String trace(String url, Map<String, String> headers, Object body)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, body, headers, "TRACE"),
                headers);
    }

    /**
     * Square's HTTP request for {@code options}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String options(String url, Map<String, String> headers, Object body)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, body, headers, "OPTIONS"),
                headers);
    }

    /**
     * Square's HTTP request for {@code head}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String head(String url, Map<String, String> headers, Object body)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, body, headers, "HEAD"),
                headers);
    }

    /**
     * Square's HTTP request for {@code patch}.
     * <p>
     * The default format is {@link OkHttpClient} in <pre>{@code new OkHttpClient().newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String patch(String url, Map<String, String> headers, Object body)
            throws Exception {
        return doRequest(null, getRequestBuilder(url, body, headers, "PATCH"),
                headers);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client  Square's HTTP request client,can be {@literal null}.
     * @param builder HTTP Public Request Class {@link Request.Builder}.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
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
                //no call Callback#onResponse no NullPointerException
                result = response.body().string();
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
     * Use relevant parameters to obtain the {@link Request} parameters required for
     * {@link OkHttpClient} execution.
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @param method  Distinguish request types,must not be {@literal null}.
     * @return Obtain the condition builder for {@link Request}.
     */
    public static Request.Builder getRequestBuilder(String url,
                                                    Object body,
                                                    Charset charset,
                                                    Map<String, String> headers,
                                                    String method) {
        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            throw new IllegalArgumentException("Url is not valid");
        }
        HttpUrl.Builder urlBuilder = httpUrl.newBuilder();
        Request.Builder requestBuild = new Request.Builder().url(urlBuilder.build());
        MediaType mediaType = null;
        String contentType = null;
        if (MapUtils.isNotEmpty(headers)) {
            contentType = headers.get("Content-type");
            if (StringUtils.isBlank(contentType)) {
                contentType = HttpSdkSupport.getContentTypeWithBody(body, charset);
            }
        }
        if (contentType != null) {
            mediaType = MediaType.parse(contentType);
        }
        if (charset != null && mediaType != null) {
            mediaType.charset(charset);
        }
        RequestBody requestBody = RequestBody.create(mediaType, body == null ? "" : body.toString());
        switch (method) {
            case "GET":
                requestBuild = requestBuild.get();
                break;
            case "POST":
                requestBuild = requestBuild.post(requestBody);
                break;
            case "PUT":
                requestBuild = requestBuild.put(requestBody);
                break;
            case "DELETE":
                requestBuild = requestBuild.delete(requestBody);
                break;
            case "TRACE":
                requestBuild = requestBuild.method("TRACE", requestBody);
            case "OPTIONS":
                requestBuild = requestBuild.method("OPTIONS", requestBody);
            case "HEAD":
                requestBuild = requestBuild.head();
            case "PATCH":
                requestBuild = requestBuild.patch(requestBody);
        }
        return requestBuild;
    }
}
