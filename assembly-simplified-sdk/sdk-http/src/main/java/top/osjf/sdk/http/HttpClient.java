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
import top.osjf.sdk.core.process.Response;

import java.util.Map;

/**
 * Interface to provide configuration for a http client.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface HttpClient<R extends Response> extends Client<R>, HttpResultSolver {

    /**
     * Use the existing parameters given by {@link HttpRequest} to conduct
     * an HTTP call and return the result.
     *
     * @param method       {@link HttpRequest#matchSdkEnum()}
     * @param headers      {@link HttpRequest#getHeadMap()}
     * @param requestParam {@link HttpRequest#getRequestParam()}
     * @param montage      {@link HttpRequest#montage()}
     * @return http request result.
     * @throws Exception maybe exceptions when http request.
     */
    String doHttpRequest(HttpRequestMethod method, Map<String, String> headers,
                         Object requestParam, Boolean montage) throws Exception;
}
