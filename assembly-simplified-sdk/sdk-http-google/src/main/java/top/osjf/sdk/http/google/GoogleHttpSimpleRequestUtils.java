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

package top.osjf.sdk.http.google;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.IOUtils;
import top.osjf.sdk.core.util.JSONUtil;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Google HTTP Simple Request Tool Class.
 * <p>
 * This class provides a simple way to execute Google HTTP requests, including {@code GET},
 * {@code POST},{@code PUT}, {@code DELETE}, {@code TRACE}, {@code OPTIONS}, {@code HEAD},
 * {@code PATCH}, and other methods.
 * <p>
 * These methods encapsulate the underlying HTTP request details, allowing users to only
 * provide basic information such as URL, request header, request parameters, and whether
 * to concatenate parameters into URL strings or JSON format,You can easily send HTTP requests
 * and receive responses.
 * <p>
 * This utility class uses Google's HTTP client libraries such as {@code HttpRequestFactory} and
 * {@code NetHttpTransport} to create and execute HTTP requests.
 * Users don't need to worry about these underlying details, just focus on the business logic.
 * <p>
 * Please note that this class is a utility class, and all its methods are static methods, so
 * there is no need to instantiate this class.
 * The constructor is privatized and throws an {@code AttributeError} exception to prevent external
 * instantiation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class GoogleHttpSimpleRequestUtils {

    private static final HttpRequestFactory DEFAULT = new NetHttpTransport().createRequestFactory();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                DEFAULT.getTransport().shutdown();
            } catch (IOException ignored) {
            }
        }));
    }

    /**
     * Google HTTP request for {@code Get}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
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
        return doRequest(null, "GET", url, headers, requestParam, montage);
    }

    /**
     * Google HTTP request for {@code Post}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
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
        return doRequest(null, "POST", url, headers, requestParam, montage);
    }

    /**
     * Google HTTP request for {@code Put}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
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
        return doRequest(null, "PUT", url, headers, requestParam, montage);
    }

    /**
     * Google HTTP request for {@code Delete}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
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
        return doRequest(null, "DELETE", url, headers, requestParam, montage);
    }

    /**
     * Google HTTP request for {@code trace}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
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
        return doRequest(null, "TRACE", url, headers, requestParam, montage);
    }

    /**
     * Google HTTP request for {@code options}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
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
        return doRequest(null, "OPTIONS", url, headers, requestParam, montage);
    }

    /**
     * Google HTTP request for {@code head}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
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
        return doRequest(null, "HEAD", url, headers, requestParam, montage);
    }

    /**
     * Google HTTP request for {@code patch}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
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
        return doRequest(null, "PATCH", url, headers, requestParam, montage);
    }

    /**
     * The Google request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param requestFactory Google's HTTP request client.
     * @param methodName     HTTP request method name .
     * @param url            The actual request address,must not be {@literal null}.
     * @param headers        Header information map,can be {@literal null}.
     * @param requestParam   Request parameters,can be {@literal null}.
     * @param montage        Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value
     * @throws Exception A specific exception occurred due to an HTTP request error.
     */
    @SuppressWarnings("unchecked")
    public static String doRequest(HttpRequestFactory requestFactory,
                                   String methodName,
                                   String url,
                                   Map<String, String> headers,
                                   Object requestParam,
                                   boolean montage) throws Exception {
        if (requestFactory == null) {
            requestFactory = new NetHttpTransport().createRequestFactory();
        }
        // Setup the request body
        HttpContent content = null;
        String contentType;
        if (MapUtils.isNotEmpty(headers)) {
            contentType = headers.get("Content-Type");
            if (StringUtils.isBlank(contentType)) {
                contentType = "application/json";
            }
        } else {
            contentType = "application/json";
        }
        GenericUrl genericUrl = new GenericUrl(url);
        if (montage) {
            Map<String, Object> queryParams = null;
            if (requestParam instanceof Map) {
                queryParams = (Map<String, Object>) requestParam;
            } else if (requestParam instanceof String) {
                queryParams = JSONUtil.getInnerMapByJsonStr(requestParam.toString());
            }
            if (queryParams == null) {
                throw new IllegalArgumentException("If you need to concatenate parameters onto the URL, " +
                        "please provide parameters of map type or JSON type of key/value " +
                        "(which will automatically convert map concatenation). " +
                        "If you provide a simple string type, then the URL parameter will be directly returned.");
            }
            genericUrl.putAll(queryParams);
        } else {
            content = new ByteArrayContent(contentType, IOUtils.serialize(requestParam));
        }
        // Build the request
        HttpRequest request =
                requestFactory.buildRequest(methodName, genericUrl, content);
        // Setup headers
        if (MapUtils.isNotEmpty(headers)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            for (final Map.Entry<String, String> header : headers.entrySet()) {
                httpHeaders.set(header.getKey(), header.getValue());
            }
            request.setHeaders(httpHeaders);
        }
        HttpResponse response = request.execute();
        return response.parseAsString();
    }
}
