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

package top.osjf.sdk.http.client;

import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.http.HttpResponse;

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
     * Return a Controls the per-request settings currently required to be
     * implemented by all {@link Client}.
     * <p>Currently only takes effect when <pre>{@code HttpRequestExecutor#useCustomize == false}</pre>.
     * <p>By default, this method returns {@link HttpRequestOptions#DEFAULT_OPTIONS}.
     *
     * @return Controls the per-request settings currently required to be
     * implemented by all {@link Client clients}
     * @since 1.0.2
     */
    default HttpRequestOptions getOptions() {
        return HttpRequestOptions.DEFAULT_OPTIONS;
    }
}
