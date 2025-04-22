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
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.http.client.ServiceLoaderLoggerHttpClient;

/**
 * Extends for {@code UrlQuerySPILoggerHttpRequest} to provider a {@code Client}
 * is {@code ServiceLoaderLoggerHttpClient}.
 *
 * @param <R> Subclass generic type of {@code AbstractHttpResponse}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SuppressWarnings("rawtypes")
public abstract class UrlQuerySPILoggerHttpRequest<R extends AbstractHttpResponse>
        extends UrlQueryHttpRequest<R> {

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
    public final Class<? extends Client> getClientType() {
        return ServiceLoaderLoggerHttpClient.class;
    }
}
