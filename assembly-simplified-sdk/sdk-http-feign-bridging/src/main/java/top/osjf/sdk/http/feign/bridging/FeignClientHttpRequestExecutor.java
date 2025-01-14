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

package top.osjf.sdk.http.feign.bridging;

import feign.Client;
import top.osjf.sdk.http.spi.HttpRequestExecutor;

/**
 * Feign client HTTP request executor interface.
 * <p>
 * This interface extends Feigns Client interface and a custom {@code HttpRequestExecutor}
 * interface for executing HTTP requests. It provides a default execute method that takes
 * an {@code ExecutorHttpRequest} object encapsulating HTTP request information, converts
 * it into a Feign request object,executes the request, and returns the response result.
 * <p>
 * In this method, the Feign request body and headers are first created based on the
 * {@code ExecutorHttpRequest} object. Then, a Feign request object is created and
 * execution options are set. Finally,the Feign request is executed, and the response result
 * is read and converted to a string for return.
 * <p>
 * The final call to the encapsulated component of {@code Feign} is to implement HTTP
 * calling,and its calling process completely depends on the encapsulated component of
 * {@code Feign}.Here, only the string response result is obtained in the final parsing
 * result.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface FeignClientHttpRequestExecutor extends Client, HttpRequestExecutor {
}
