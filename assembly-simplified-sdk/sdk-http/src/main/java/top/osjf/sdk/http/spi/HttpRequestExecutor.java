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

package top.osjf.sdk.http.spi;

/**
 * The {@code HttpRequestExecutor} interface serves as a functional interface that
 * defines the contract for executing HTTP requests {@code HttpRequest} and obtaining
 * responses {@code HttpResponse}. This interface is designed to act as an adapter
 * layer between different HTTP libraries, allowing users to send HTTP requests and
 * handle responses in a unified manner.
 *
 * <p>The provided method transforms a given {@code HttpRequest} object into an
 * {@code HttpResponse} object, where the {@code HttpRequest} object encapsulates all
 * request parameters and configurations, while the {@code HttpResponse} object packages
 * all information returned from the server.
 *
 * <p>By utilizing this interface, developers can seamlessly integrate with various
 * HTTP client libraries, leading to more modular, testable, and maintainable code.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@FunctionalInterface
public interface HttpRequestExecutor {

    /**
     * Executes the specified HTTP request and returns the corresponding HTTP response.
     *
     * <p>The request parameter body {@code HttpRequest} is given by this framework,
     * and the return request body {@code HttpResponse} needs to be converted according
     * to the protocol of the docking party.
     *
     * @param httpRequest the HTTP request instance object to be executed contains all the
     *                    parameters and header information of the request.
     * @return The HTTP response instance object obtained after executing an HTTP request
     * contains the response status code, header information, and response body.
     * @throws Exception Throws an exception if any error occurs during execution, such as
     *                   network issues, timeouts, or protocol errors.
     */
    HttpResponse execute(HttpRequest httpRequest) throws Exception;
}
