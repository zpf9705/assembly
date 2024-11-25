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

import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.http.HttpSdkSupport;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
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
 * <p>
 * The URL, headers, and request parameters for the request can be passed in through method
 * parameters.
 * The request parameters can optionally be appended to the URL or sent as a JSON request body.
 * This class is primarily used to simplify the process of sending HTTP requests and enhance
 * development efficiency.
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
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     */
    public static String get(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(null, "GET", getURI(url, montage, requestParam),
                headers, montage, requestParam);
    }

    /**
     * JAXRS's HTTP request for {@code Post}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value
     */
    public static String post(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(null, "POST", getURI(url, montage, requestParam),
                headers, montage, requestParam);
    }

    /**
     * JAXRS's HTTP request for {@code Put}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value
     */
    public static String put(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(null, "PUT", getURI(url, montage, requestParam),
                headers, montage, requestParam);
    }

    /**
     * JAXRS's HTTP request for {@code Delete}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     */
    public static String delete(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(null, "DELETE", getURI(url, montage, requestParam),
                headers, montage, requestParam);
    }

    /**
     * JAXRS's HTTP request for {@code trace}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     */
    public static String trace(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(null, "TRACE", getURI(url, montage, requestParam),
                headers, montage, requestParam);
    }

    /**
     * JAXRS's HTTP request for {@code options}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     */
    public static String options(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(null, "OPTIONS", getURI(url, montage, requestParam),
                headers, montage, requestParam);
    }

    /**
     * JAXRS's HTTP request for {@code head}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     */
    public static String head(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(null, "HEAD", getURI(url, montage, requestParam),
                headers, montage, requestParam);
    }

    /**
     * JAXRS's HTTP request for {@code patch}.
     * <p>
     * The default format is {@link Client} in <pre>{@code ClientBuilder.newBuilder().build()}</pre>
     *
     * @param url          The actual request address,must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param requestParam Request parameters,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @return The {@code String} type of the return value.
     */
    public static String patch(String url, Map<String, String> headers, Object requestParam, boolean montage) {
        return doRequest(null, "PATCH", getURI(url, montage, requestParam),
                headers, montage, requestParam);
    }

    /**
     * The HTTP request sending method includes the entire lifecycle of HTTP requests.
     *
     * @param client       JAXRS's HTTP request client,can be {@literal null}.
     * @param methodName   HTTP request method name .
     * @param uri          The actual request address to {@code URI},must not be {@literal null}.
     * @param headers      Header information map,can be {@literal null}.
     * @param montage      Whether to concatenate urls with {@code requestParam} be maps or json.
     * @param requestParam Request parameters,can be {@literal null}.
     * @return The {@code String} type of the return value
     */
    public static String doRequest(Client client,
                                   String methodName,
                                   URI uri,
                                   Map<String, String> headers,
                                   boolean montage,
                                   Object requestParam) {
        if (client == null) {
            client = DEFAULT;
        }
        Invocation.Builder builder = client.target(uri)
                .request();
        if (headers != null) builder.headers(new MultivaluedHashMap<>(headers));
        String result;
        Response response = null;
        try {
            if (montage) {
                response = builder.method(methodName);
            } else {
                response = builder.method(methodName, toEntity(requestParam, headers));
            }
            result = response.readEntity(String.class);
        } finally {
            if (response != null) {
                response.close();
            }
        }
        return result;
    }

    private static URI getURI(String url, boolean montage, Object requestParam) {
        UriBuilder uriBuilder = UriBuilder.fromUri(url);
        Map<String, Object> queryParams = null;
        if (montage) queryParams = HttpSdkSupport.resolveMontageObj(requestParam);
        if (queryParams != null) {
            for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
                uriBuilder.queryParam(entry.getKey(), entry.getValue());
            }
        }
        return uriBuilder.build();
    }

    private static Entity<Object> toEntity(Object requestParam, Map<String, String> headers) {
        MediaType mediaType = MediaType.APPLICATION_JSON_TYPE;
        if (MapUtils.isNotEmpty(headers)) {
            String value = headers.get("Content-type");
            if (StringUtils.isNotBlank(value)) {
                String[] types = value.split("/");
                if (types.length != 2) {
                    throw new IllegalArgumentException("Incorrect context type [" + value + "]");
                }
                mediaType = new MediaType(types[0], types[1]);
            }
        }
        return Entity.entity(requestParam, mediaType);
    }
}
