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

import feign.Request;
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

    /*** A default global {@code Request.Options}*/
    Request.Options DEFAULT_FEIGN_OPTIONS = new Request.Options();

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

    /* Facilitating the rewriting of processing methods for response data of AbstractFHIR in the later stage. */

    /**
     * Convert the request parameter to {@link HttpRequest},
     * and refer to {@link top.osjf.sdk.core.client.PreProcessingResponseHandler#preResponseStrHandler}
     * for logical requirements and other parameters.
     *
     * @param request     The type of {@link HttpRequest} parameter required for {{@link #execute}} execution.
     * @param responseStr top.osjf.sdk.core.client.PreProcessingResponseHandler#preResponseStrHandler#responseStr
     * @return top.osjf.sdk.core.client.PreProcessingResponseHandler#preResponseStrHandler
     * @see top.osjf.sdk.core.client.PreProcessingResponseHandler#preResponseStrHandler
     * @since 1.0.2
     */
    String preResponseStrHandler(HttpRequest<R> request, String responseStr);

    /**
     * Convert the request parameter to {@link HttpRequest},
     * and refer to {@link top.osjf.sdk.core.client.ResponseConvert#convertToResponse}
     * for logical requirements and other parameters.
     *
     * @param request     The type of {@link HttpRequest} parameter required for {{@link #execute}} execution.
     * @param responseStr top.osjf.sdk.core.client.ResponseConvert#convertToResponse#responseStr
     * @return top.osjf.sdk.core.client.ResponseConvert#convertToResponse
     * @see top.osjf.sdk.core.client.ResponseConvert#convertToResponse
     * @since 1.0.2
     */
    R convertToResponse(HttpRequest<R> request, String responseStr);

    /**
     * Return a Controls the per-request settings currently required to be
     * implemented by all {@link feign.Client clients}
     *
     * @return Controls the per-request settings currently required to be
     * implemented by all {@link feign.Client clients}
     * @since 1.0.2
     */
    default Request.Options getOptions() {
        return DEFAULT_FEIGN_OPTIONS;
    }
}
