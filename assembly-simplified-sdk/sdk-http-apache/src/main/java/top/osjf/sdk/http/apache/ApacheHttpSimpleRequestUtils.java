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

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.support.HttpSdkSupport;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * A simple HTTP calling utility class encapsulated with Apache HTTP packages.
 * <p>
 * It provides a series of static methods for executing HTTP {@code GET}, {@code POST},
 * {@code PUT}, {@code DELETE},{@code TRACE}, {@code OPTIONS}, {@code HEAD}, and {@code PATCH}
 * requests.
 *
 * <p>Provide a default static global client {@code HttpClient} to be used by default
 * when not provided.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class ApacheHttpSimpleRequestUtils {

    private final static HttpClient DEFAULT = HttpClients.createDefault();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                ((CloseableHttpClient) DEFAULT).close();
            } catch (IOException ignored) {
            }
        }));
    }

    /**
     * Apache HTTP request for {@code Get}.
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
     * Apache HTTP request for {@code Post}.
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
     * Apache HTTP request for {@code Put}.
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
     * Apache HTTP request for {@code Delete}.
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
     * Apache HTTP request for {@code trace}.
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
     * Apache HTTP request for {@code options}.
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
     * Apache HTTP request for {@code head}.
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
     * Apache HTTP request for {@code patch}.
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
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client      Apache's HTTP request client.
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}
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
                                   HttpRequestBase requestBase,
                                   @Nullable Map<String, String> headers,
                                   @Nullable Object body,
                                   @Nullable Charset charset) throws Exception {
        if (client == null) {
            client = DEFAULT;
        }
        HttpResponse response = null;
        String result;
        try {
            response = getResponse(client, requestBase, headers, body, charset);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, getCharsetByResponse(response));
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
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client      Apache's HTTP request client.
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}
     * @param headers     Optional HTTP header information used to control the behavior of requests.
     * @param body        Optional request body.
     * @param charset     Encoding character set.
     * @return Returns a string representation of the server response {@link HttpResponse}.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    public static HttpResponse getResponse(@Nullable HttpClient client,
                                           HttpRequestBase requestBase,
                                           @Nullable Map<String, String> headers,
                                           @Nullable Object body,
                                           @Nullable Charset charset) throws Exception {
        if (client == null) {
            client = DEFAULT;
        }
        addHeaders(headers, requestBase);
        setEntity(body, requestBase, headers, charset);
        return client.execute(requestBase);
    }

    /**
     * Returns the encoded character set based on the returned response body
     * , default to {@link StandardCharsets#UTF_8}.
     *
     * @param response the input apache http response.
     * @return charset encoding.
     * @throws NullPointerException if input response is {@literal null}.
     */
    public static Charset getCharsetByResponse(HttpResponse response) {
        Charset charset;
        HttpEntity entity = response.getEntity();
        ContentType contentType = ContentType.get(entity);
        if (contentType != null) {
            charset = contentType.getCharset();
            if (charset != null) {
                return charset;
            }
        } else {
            for (Header header : response.getAllHeaders()) {
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
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * Set {@link HttpEntity} with a {@link StringEntity}.
     *
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}
     * @param headers     Optional HTTP header information used to control the behavior of requests.
     * @param body        Optional request body.
     * @param charset     Encoding character set.
     */
    private static void setEntity(@Nullable Object body, HttpRequestBase requestBase,
                                  @Nullable Map<String, String> headers, @Nullable Charset charset) {
        if (!(requestBase instanceof HttpEntityEnclosingRequestBase)) {
            return;
        }
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
        ((HttpEntityEnclosingRequestBase) requestBase).setEntity(httpEntity);
    }

    /**
     * Add header information for HTTP requests.
     *
     * @param headers     Header information map,can be {@literal null}.
     * @param requestBase HTTP Public Request Class {@link HttpRequestBase}.
     */
    private static void addHeaders(Map<String, String> headers, HttpRequestBase requestBase) {
        if (MapUtils.isNotEmpty(headers)) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBase.addHeader(header.getKey(), header.getValue());
            }
        }
    }
}
