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
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.support.HttpSdkSupport;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * A simple HTTP calling utility class encapsulated with Google HTTP packages.
 * <p>
 * It provides a series of static methods for executing HTTP {@code GET}, {@code POST},
 * {@code PUT}, {@code DELETE},{@code TRACE}, {@code OPTIONS}, {@code HEAD}, and {@code PATCH}
 * requests.
 * <p>Provide a default static global client {@code HttpRequestFactory} to be used by default
 * when not provided.
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
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String get(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset)
            throws Exception {
        return doRequest(null, "GET", url, headers, body, charset);
    }

    /**
     * Google HTTP request for {@code Post}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String post(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset)
            throws Exception {
        return doRequest(null, "POST", url, headers, body, charset);
    }

    /**
     * Google HTTP request for {@code Put}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String put(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset)
            throws Exception {
        return doRequest(null, "PUT", url, headers, body, charset);
    }

    /**
     * Google HTTP request for {@code Delete}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String delete(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset)
            throws Exception {
        return doRequest(null, "DELETE", url, headers, body, charset);
    }

    /**
     * Google HTTP request for {@code trace}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String trace(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset)
            throws Exception {
        return doRequest(null, "TRACE", url, headers, body, charset);
    }

    /**
     * Google HTTP request for {@code options}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String options(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset)
            throws Exception {
        return doRequest(null, "OPTIONS", url, headers, body, charset);
    }

    /**
     * Google HTTP request for {@code head}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String head(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset)
            throws Exception {
        return doRequest(null, "HEAD", url, headers, body, charset);
    }

    /**
     * Google HTTP request for {@code patch}.
     * <p>
     * The default format is {@link HttpRequestFactory} in
     * <pre>{@code new NetHttpTransport().createRequestFactory()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String patch(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset)
            throws Exception {
        return doRequest(null, "PATCH", url, headers, body, charset);
    }

    /**
     * The Google request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param requestFactory Google's HTTP request client.
     * @param methodName     HTTP request method name .
     * @param url            The target URL of the request.
     * @param headers        Optional HTTP header information used to control the behavior of requests.
     * @param body           Optional request body.
     * @param charset        Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String doRequest(@Nullable HttpRequestFactory requestFactory,
                                   String methodName,
                                   String url,
                                   @Nullable Map<String, String> headers,
                                   @Nullable Object body,
                                   @Nullable Charset charset) throws Exception {
        if (requestFactory == null) {
            requestFactory = DEFAULT;
        }
        HttpContent content = null;
        if (body != null) {
            String contentType = null;
            if (MapUtils.isNotEmpty(headers)) {
                contentType = headers.get("Content-Type");
            }
            if (StringUtils.isBlank(contentType)) {
                contentType = HttpSdkSupport.getContentTypeWithBody(body, charset);
            }
            String bodyStr = body.toString();
            byte[] array = charset != null ? bodyStr.getBytes(charset) : bodyStr.getBytes();
            content = new ByteArrayContent(contentType, array);
        }
        HttpRequest request =
                requestFactory.buildRequest(methodName, new GenericUrl(url), content);
        if (MapUtils.isNotEmpty(headers)) {
            HttpHeaders httpHeaders = new HttpHeaders();
            for (Map.Entry<String, String> header : headers.entrySet()) {
                httpHeaders.set(header.getKey(), header.getValue());
            }
            request.setHeaders(httpHeaders);
        }
        HttpResponse response = request.execute();
        return response.parseAsString();
    }
}
