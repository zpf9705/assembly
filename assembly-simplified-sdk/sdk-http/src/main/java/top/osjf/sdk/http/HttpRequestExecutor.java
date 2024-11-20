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

import feign.Client;
import feign.Request;
import feign.Response;

import java.io.IOException;

/**
 * The HttpRequestExecutor interface inherits from the feature client interface since version 1.0.2,
 * Adopting open label technology support for executing HTTP requests.
 * <p>
 * This interface defines an 'execute' method for sending HTTP requests and receiving responses.
 * The method takes a request object and a request option object as parameters and returns a response object.
 * If an I/O exception occurs during the request process, an IOException will be thrown.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface HttpRequestExecutor extends Client, CustomizeHttpRequestExecutor {
    /**
     * {@inheritDoc}
     *
     * @param request {@inheritDoc}
     * @param options {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IOException {@inheritDoc}
     * @since 1.0.2
     */
    @Override
    Response execute(Request request, Request.Options options) throws IOException;

    /**
     * Return whether to use the custom {@link CustomizeHttpRequestExecutor}
     * request method.
     * <p>By default, the open flag client parameter leader {@link #execute}
     * scheme is used，that is to say, the return value is {@code false}.
     * <p>Set the return value to {@code true} to execute custom logic.
     *
     * @return whether to use the custom {@link CustomizeHttpRequestExecutor}
     * request method.
     */
    default boolean useCustomize() {
        return false;
    }
}
