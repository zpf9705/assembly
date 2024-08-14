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

package top.osjf.sdk.http.apache;

import top.osjf.sdk.core.client.Client;
import top.osjf.sdk.http.AbstractHttpRequestParams;
import top.osjf.sdk.http.AbstractHttpResponse;

/**
 * Provider default {@code Client} {@link ApacheHttpClient}
 * for {@link AbstractHttpRequestParams}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class ApacheHttpRequestParams<R extends AbstractHttpResponse>
        extends AbstractHttpRequestParams<R>
{

    private static final long serialVersionUID = -8339805842695497696L;

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends Client> getClientCls() {
        return ApacheHttpClient.class;
    }
}
