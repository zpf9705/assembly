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
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.ByteArrayEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import top.osjf.sdk.core.lang.Nullable;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.support.HttpSdkSupport;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * A simple HTTP calling utility class encapsulated with Apache HTTP5 packages.
 * <p>
 * It provides a series of static methods for executing HTTP {@code GET}, {@code POST},
 * {@code PUT}, {@code DELETE},{@code TRACE}, {@code OPTIONS}, {@code HEAD}, and {@code PATCH}
 * requests.
 * <p>Provide a default static global client {@code HttpClient} to be used by default
 * when not provided.
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
        return doRequest(null, new HttpGet(url), headers, body, charset);
    }

    /**
     * Apache HTTP5 request for {@code Post}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpPost(url), headers, body, charset);
    }

    /**
     * Apache HTTP5 request for {@code Put}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpPut(url), headers, body, charset);
    }

    /**
     * Apache HTTP5 request for {@code Delete}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpDelete(url), headers, body, charset);
    }

    /**
     * Apache HTTP5 request for {@code trace}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpTrace(url), headers, body, charset);
    }

    /**
     * Apache HTTP5 request for {@code options}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpOptions(url), headers, body, charset);
    }

    /**
     * Apache HTTP5 request for {@code head}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpHead(url), headers, body, charset);
    }

    /**
     * Apache HTTP5 request for {@code patch}.
     * <p>
     * The default format is {@link CloseableHttpClient} in <pre>{@code HttpClients.custom().build()}</pre>
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
        return doRequest(null, new HttpPatch(url), headers, body, charset);
    }

    /**
     * The HTTP5 request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client      Apache's HTTP request client.
     * @param requestBase HTTP Public Request Class {@link HttpUriRequestBase}.
     * @param headers     Optional HTTP header information used to control the behavior of requests.
     * @param body        Optional request body.
     * @param charset     Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static String doRequest(@Nullable HttpClient client,
                                   HttpUriRequestBase requestBase,
                                   @Nullable Map<String, String> headers,
                                   @Nullable Object body,
                                   @Nullable Charset charset) throws Exception {

        return getResponse(client, requestBase, headers, body, charset).getBody();
    }

    /**
     * The HTTP5 request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client      Apache's HTTP request client.
     * @param requestBase HTTP Public Request Class {@link HttpUriRequestBase}.
     * @param headers     Optional HTTP header information used to control the behavior of requests.
     * @param body        Optional request body.
     * @param charset     Encoding character set.
     * @return Returns a {@link Hc5ClosedResponse} representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static Hc5ClosedResponse getResponse(@Nullable HttpClient client,
                                                HttpUriRequestBase requestBase,
                                                @Nullable Map<String, String> headers,
                                                @Nullable Object body,
                                                @Nullable Charset charset) throws Exception {
        if (client == null) {
            client = DEFAULT;
        }
        addHeaders(headers, requestBase);
        setEntity(body, requestBase, headers, charset);
        return client.execute(requestBase, response -> {
            Charset responseCharset = getCharsetByResponse(response);
            return new Hc5ClosedResponse
                    (response, EntityUtils.toString(response.getEntity(), responseCharset), responseCharset);
        });
    }

    /**
     * Returns the encoded character set based on the returned response body
     * , default to {@link StandardCharsets#UTF_8}.
     *
     * @param response the input apache hc5 http response.
     * @return response charset encoding.
     * @throws NullPointerException if input response is {@literal null}.
     */
    public static Charset getCharsetByResponse(ClassicHttpResponse response) {
        HttpEntity entity = response.getEntity();
        String contentEncoding = entity.getContentEncoding();
        if (StringUtils.isNotBlank(contentEncoding)) {
            return Charset.forName(contentEncoding);
        }
        ContentType contentType = ContentType.parse(entity.getContentType());
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        for (Header header : response.getHeaders()) {
            if (header.getName().equalsIgnoreCase(HttpSdkSupport.CONTENT_TYPE_NAME)) {
                String value = header.getValue();
                String[] contentTypeSplitParams = value.split(";");
                if (contentTypeSplitParams.length > 1) {
                    String[] charsetParts = contentTypeSplitParams[1].split("=");
                    if (charsetParts.length == 2 && "charset".equalsIgnoreCase(charsetParts[0].trim())) {
                        String charsetString = charsetParts[1].replaceAll("\"", "");
                        return Charset.forName(charsetString);
                    }
                }
            }
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * Set {@link HttpEntity} with a {@link StringEntity}.
     *
     * @param requestBase HTTP Public Request Class {@link HttpUriRequestBase}
     * @param headers     Optional HTTP header information used to control the behavior of requests.
     * @param body        Optional request body.
     * @param charset     Encoding character set.
     */
    private static void setEntity(@Nullable Object body,
                                  HttpUriRequestBase requestBase,
                                  @Nullable Map<String, String> headers,
                                  @Nullable Charset charset) {
        HttpEntity httpEntity;
        if (body == null) {
            httpEntity = new ByteArrayEntity(new byte[0], null);
        } else {
            String contentType = null;
            if (MapUtils.isNotEmpty(headers)) {
                contentType = headers.get(HttpSdkSupport.CONTENT_TYPE_NAME);
            }
            if (StringUtils.isBlank(contentType)) {
                contentType = HttpSdkSupport.getContentTypeWithBody(body, charset);
            }
            String bodyStr = body.toString();
            if (contentType != null) {
                httpEntity = new StringEntity(bodyStr, ContentType.parse(contentType));
            } else {
                byte[] buf = charset != null ? bodyStr.getBytes(charset) : bodyStr.getBytes();
                httpEntity = new ByteArrayEntity(buf, null);
            }
        }
        requestBase.setEntity(httpEntity);
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
}
