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

package top.osjf.sdk.http.process;

import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.http.client.ServiceLoaderLoggerHttpClient;

/**
 * Extends for {@code UrlQuerySPILoggerHttpRequestParams} to provider a {@code Client}
 * is {@code ServiceLoaderLoggerHttpClient}.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SuppressWarnings("rawtypes")
public abstract class UrlQuerySPILoggerHttpRequestParams<R extends AbstractHttpResponse>
        extends UrlQueryHttpRequestParams<R> {

    private static final long serialVersionUID = -3904631145903947614L;

    /**
     * {@inheritDoc}
     * <p>
     * Use {@code ServiceLoaderLoggerHttpClient}.
     *
     * @return {@inheritDoc}.
     */
    @Override
    @NotNull
    public final Class<? extends Client> getClientCls() {
        return ServiceLoaderLoggerHttpClient.class;
    }
}
