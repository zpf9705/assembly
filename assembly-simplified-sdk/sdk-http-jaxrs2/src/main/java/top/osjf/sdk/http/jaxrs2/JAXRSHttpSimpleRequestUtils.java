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

package top.osjf.sdk.http.jaxrs2;

import top.osjf.sdk.core.lang.Nullable;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.support.HttpSdkSupport;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * The {@code JAXRSHttpSimpleRequestUtils} class serves as a utility for simplifying
 * JAX-RS HTTP requests.
 * It provides a series of static methods for executing HTTP requests such as {@code GET},
 * {@code POST},{@code PUT}, {@code DELETE}, {@code TRACE}, {@code OPTIONS}, {@code HEAD},
 * {@code PATCH}.
 * <p>
 * The class utilizes a global JAX-RS Client instance, which is created upon class loading
 * and automatically closed when the JVM shuts down via the {@code Runtime.getRuntime().
 * addShutdownHook} method.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class JAXRSHttpSimpleRequestUtils {
    private static final Client DEFAULT = ClientBuilder.newBuilder().build();

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(DEFAULT::close));
    }

    /**
     * JAXRS's HTTP request for {@code Get}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static String get(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) {
        return doRequest(null, "GET", url, headers, body, charset);
    }

    /**
     * JAXRS's HTTP request for {@code Post}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static String post(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) {
        return doRequest(null, "POST", url, headers, body, charset);
    }

    /**
     * JAXRS's HTTP request for {@code Put}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static String put(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) {
        return doRequest(null, "PUT", url, headers, body, charset);
    }

    /**
     * JAXRS's HTTP request for {@code Delete}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static String delete(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) {
        return doRequest(null, "DELETE", url, headers, body, charset);
    }

    /**
     * JAXRS's HTTP request for {@code trace}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static String trace(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) {
        return doRequest(null, "TRACE", url, headers, body, charset);
    }

    /**
     * JAXRS's HTTP request for {@code options}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static String options(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) {
        return doRequest(null, "OPTIONS", url, headers, body, charset);
    }

    /**
     * JAXRS's HTTP request for {@code head}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static String head(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) {
        return doRequest(null, "HEAD", url, headers, body, charset);
    }

    /**
     * JAXRS's HTTP request for {@code patch}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static String patch(String url, @Nullable Map<String, String> headers, @Nullable Object body, @Nullable Charset charset) {
        return doRequest(null, "PATCH", url, headers, body, charset);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client     JAXRS's HTTP request client,can be {@literal null}.
     * @param methodName HTTP request method name .
     * @param url        The target URL of the request.
     * @param headers    Optional HTTP header information used to control the behavior of requests.
     * @param body       Optional request body.
     * @param charset    Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static String doRequest(@Nullable Client client,
                                   String url,
                                   String methodName,
                                   @Nullable Map<String, String> headers,
                                   @Nullable Object body, @Nullable Charset charset) {
        String result;
        try (Response response = getResponse(client, url, methodName, headers, body, charset)) {
            result = response.readEntity(String.class);
        }
        return result;
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client     JAXRS's HTTP request client,can be {@literal null}.
     * @param methodName HTTP request method name .
     * @param url        The target URL of the request.
     * @param headers    Optional HTTP header information used to control the behavior of requests.
     * @param body       Optional request body.
     * @param charset    Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     */
    public static Response getResponse(@Nullable Client client,
                                       String url,
                                       String methodName,
                                       @Nullable Map<String, String> headers,
                                       @Nullable Object body, @Nullable Charset charset) {
        if (client == null) {
            client = DEFAULT;
        }
        Invocation.Builder builder = client.target(UriBuilder.fromUri(url))
                .request();
        if (headers != null) builder.headers(new MultivaluedHashMap<>(headers));
        return builder.method(methodName, toEntity(body, charset, headers));
    }


    @Nullable
    private static Entity<Object> toEntity(@Nullable Object body, @Nullable Charset charset,
                                           @Nullable Map<String, String> headers) {
        if (body == null) return null;
        String type = null;
        String subtype = null;
        String charsetName = charset != null ? charset.name() : null;
        String contentType = null;
        if (MapUtils.isNotEmpty(headers)) {
            contentType = headers.get(HttpSdkSupport.CONTENT_TYPE_NAME);
        }
        if (StringUtils.isBlank(contentType)) {
            contentType = HttpSdkSupport.getContentTypeWithBody(body, charset);
        }
        if (contentType != null) {
            String[] types = contentType.split("/");
            if (types.length != 2) {
                throw new IllegalArgumentException("Incorrect context type [" + contentType + "]");
            }
            type = types[0];
            subtype = types[1];
        }
        MediaType mediaType = new MediaType(type, subtype, charsetName);
        return Entity.entity(body, mediaType);
    }

    /**
     * Returns the encoded character set based on the returned response body.
     *
     * @param response the input apache JAXRS http response.
     * @return response charset encoding.
     * @throws NullPointerException     if input response is {@literal null}.
     * @throws IllegalArgumentException if null charset name.
     */
    public static Charset getCharsetByResponse(Response response) {
        MediaType mediaType = response.getMediaType();
        mediaType = mediaType == null ? MediaType.APPLICATION_OCTET_STREAM_TYPE : mediaType;
        return Charset.forName(mediaType.getParameters().get(MediaType.CHARSET_PARAMETER));
    }
}
