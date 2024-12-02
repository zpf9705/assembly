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

/**
 * The {@code HttpRequestMethod} enumeration class, which defines the commonly used method
 * types in HTTP requests.
 *
 * <p>This enumeration class contains eight standard request methods defined in the
 * HTTP/1.1 protocol,each corresponding to different semantics and operations. For
 * example, the GET method is used to request a resource from the server,and the POST
 * method is used to submit data to the server.
 *
 * <p>By defining this enumeration class, it is convenient to manage and use HTTP
 * request methods,avoiding hard-coded string values in the code, thereby improving
 * code readability and maintainability.
 *
 * <p>When using it, you can obtain the corresponding enumeration instance by the way
 * of HttpRequestMethod. XXX, and then pass it to the function or method that requires
 * the HTTP request method.
 *
 * <p>In the current methods for implementing SDK using HTTP, we have not restricted
 * the specifications of these methods, such as certain requests that cannot carry the
 * body parameter according to regulations. We hope that you can exercise self-restraint
 * in the implementation of these specifications.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public enum HttpRequestMethod {

    GET, POST, PUT, DELETE, TRACE, OPTIONS, HEAD, PATCH
}
