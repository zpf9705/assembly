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

package top.osjf.sdk.core.client;

import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.lang.NotNull;

/**
 * The preprocessing of response strings is mainly to lay the groundwork for
 * the unified conversion of {@link ResponseConvert} response entities in the
 * future.
 *
 * <p>If encountering a type different from the general conversion scheme,
 * special conversion can be attempted here to convert to a unified type, and
 * subsequent conversion of response entities can be carried out.</p>
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface PreProcessingResponseHandler<R extends Response> {

    /**
     * Preprocessing method for response strings.
     * <p>There is no preprocessing method by default.</p>
     *
     * @param request     {@link Request} class model parameters of API.
     * @param responseStr String data for API response.
     * @return The formatted string after special processing of the response
     * string cannot return {@literal null}.
     */
    String preResponseStrHandler(@NotNull Request<R> request, @NotNull String responseStr);
}
