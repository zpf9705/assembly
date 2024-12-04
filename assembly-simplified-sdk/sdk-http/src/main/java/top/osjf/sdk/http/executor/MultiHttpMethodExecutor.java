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

package top.osjf.sdk.http.executor;

import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * The {@code MultiHttpMethodExecutor} interface defines various HTTP request methods and
 * provides a unified request execution method.
 * <p>
 * This interface allows mapping lowercase HTTP method names to specific methods of the
 * current class through a unified {@link #execute} method,And execute the corresponding
 * HTTP request. In addition, GET, POST, PUT, DELETE, TRACE,OPTIONS, HEAD, PATCH, etc. are
 * directly defined Specific HTTP request methods.
 * <p>
 * In each request method, it is allowed to specify the request address (URL), request headers,
 * request parameters (params), and Whether to concatenate the parameters in Map or Json format
 * to the URL and use them as a key value pair (month).
 * <p>
 * All request methods return a return value of type String and may throw exceptions.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface MultiHttpMethodExecutor extends HttpRequestExecutor {

    /**
     * HTTP request method for {@code Get}.
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body,It can be of type {@code String} or object type,
     *                and finally call {@code ToString} to obtain it.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    String get(@NotNull String url, @Nullable Map<String, String> headers, @Nullable Object body,
               @Nullable Charset charset)
            throws Exception;

    /**
     * HTTP request method for {@code Post}.
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body,It can be of type {@code String} or object type,
     *                and finally call {@code ToString} to obtain it.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    String post(@NotNull String url, @Nullable Map<String, String> headers, @Nullable Object body,
                @Nullable Charset charset)
            throws Exception;

    /**
     * HTTP request method for {@code Put}.
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body,It can be of type {@code String} or object type,
     *                and finally call {@code ToString} to obtain it.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    String put(@NotNull String url, @Nullable Map<String, String> headers, @Nullable Object body,
               @Nullable Charset charset)
            throws Exception;

    /**
     * HTTP request method for {@code Delete}.
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body,It can be of type {@code String} or object type,
     *                and finally call {@code ToString} to obtain it.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    String delete(@NotNull String url, @Nullable Map<String, String> headers, @Nullable Object body,
                  @Nullable Charset charset)
            throws Exception;

    /**
     * HTTP request method for {@code trace}.
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body,It can be of type {@code String} or object type,
     *                and finally call {@code ToString} to obtain it.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    String trace(@NotNull String url, @Nullable Map<String, String> headers, @Nullable Object body,
                 @Nullable Charset charset)
            throws Exception;

    /**
     * HTTP request method for {@code options}.
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body,It can be of type {@code String} or object type,
     *                and finally call {@code ToString} to obtain it.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    String options(@NotNull String url, @Nullable Map<String, String> headers, @Nullable Object body,
                   @Nullable Charset charset)
            throws Exception;

    /**
     * HTTP request method for {@code head}.
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body,It can be of type {@code String} or object type,
     *                and finally call {@code ToString} to obtain it.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    String head(@NotNull String url, @Nullable Map<String, String> headers, @Nullable Object body,
                @Nullable Charset charset)
            throws Exception;

    /**
     * HTTP request method for {@code patch}.
     *
     * @param url     The target URL of the request.
     * @param headers Optional HTTP header information used to control the behavior of requests.
     * @param body    Optional request body,It can be of type {@code String} or object type,
     *                and finally call {@code ToString} to obtain it.
     * @param charset Encoding character set.
     * @return Returns a string representation of the server response body.
     * The specific content depends on the server's response.
     * @throws Exception This method may throw various exceptions, including but not limited
     *                   to network exceptions (such as SocketTimeoutException, IOException)URL format error
     *                   (MalformedURLException), server error response (such as HTTP 4xx or 5xx errors), etc.
     *                   The caller needs to capture and handle these exceptions appropriately.
     */
    String patch(@NotNull String url, @Nullable Map<String, String> headers, @Nullable Object body,
                 @Nullable Charset charset)
            throws Exception;
}
