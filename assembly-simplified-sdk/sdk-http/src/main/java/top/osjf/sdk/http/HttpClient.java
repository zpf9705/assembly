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

package top.osjf.sdk.http;

import top.osjf.sdk.core.client.Client;

/**
 * The HTTP client interface extends the client interface and adds the ability to resolve
 * HTTP request results.
 * <p>
 * This interface is a generic interface, and the generic parameter R must be {@link HttpResponse}
 * or its subclass, representing the type of HTTP response
 * <p>
 * The interface integrates the following functions:
 * <ul>
 * <li>{@link Client}:  Inherited  from the Client interface, it includes core behaviors
 * for handling requests and responses, as well as functions for preprocessing responses,
 * converting responses,  logging , and automatically closing resources</li>
 * <li>{@link HttpResultSolver}: defines methods for resolving HTTP request results, which
 * may include parsing response bodies, handling error status codes, etc</li>
 * </ul>
 *
 * @param <R> The response type must be {@link HttpResponse} or its subclass.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface HttpClient<R extends HttpResponse> extends Client<R>, HttpResultSolver {

    /**
     * Execute an HTTP request and return a string representation of the response.
     * <p>
     * This method accepts an HTTP request object, sends the request to the server,
     * and returns the server's response content (usually a string).
     * <p>
     * During execution, this method may throw exceptions if encountering network issues,
     * request timeouts, or server error status codes.
     *
     * @param request The HTTP request object to be executed for request.
     * @return The string representation of the response from the server.
     * @throws Exception If any exception occurs during the execution of the request,
     *                   throw this exception.
     */
    String execute(HttpRequest<R> request) throws Exception;

    /**
     * Return an executable {@link HttpRequest} as {@code HttpRequestExecutor.ExecutableHttpRequest}.
     * <p>
     * The default is {@code HttpRequestExecutor.Default}, and this parameter can be
     * customized as needed.
     *
     * @param request An object encapsulating HTTP request information.
     * @param url     The URL address of the request.
     * @return The executable {@code HttpRequest} after initialization is completed.
     * @throws Exception Format and encoding set related errors that
     *                   occur during the URL formatting process.
     */
    default HttpRequestExecutor.ExecutableHttpRequest asRequestToExecutable(HttpRequest<R> request,
                                                                            String url) throws Exception {
        return new HttpRequestExecutor.Default(request, url, getOptions());
    }

    /**
     * Return a Controls the per-request settings currently required to be
     * implemented by all {@link Client}.
     * <p>Currently only takes effect when
     * <pre>{@code HttpRequestExecutor#useCustomize == false}</pre>.
     * <p>By default, this method returns
     * {@link top.osjf.sdk.http.HttpRequestExecutor.RequestOptions#DEFAULT_OPTIONS}.
     *
     * @return Controls the per-request settings currently required to be
     * implemented by all {@link Client clients}
     * @since 1.0.2
     */
    default HttpRequestExecutor.RequestOptions getOptions() {
        return HttpRequestExecutor.RequestOptions.DEFAULT_OPTIONS;
    }
}
