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

import top.osjf.sdk.core.URL;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.http.HttpResponse;

/**
 * Default impl for {@link top.osjf.sdk.core.client.Client}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class DefaultHttpClient<R extends HttpResponse> extends AbstractHttpClient<R> {

    private static final long serialVersionUID = -2139694263828485242L;

    /**
     * Constructing for {@code DefaultHttpClient} objects using access URLs.
     *
     * @param url {@code URL} Object of packaging tags and URL addresses
     *            and updated on version 1.0.2.
     * @throws NullPointerException If the input url is {@literal null}.
     */
    public DefaultHttpClient(@NotNull URL url) {
        super(url);
    }
}
