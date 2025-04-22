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

package top.osjf.sdk.http.spi;

import top.osjf.sdk.core.lang.NotNull;

import java.nio.charset.Charset;
import java.util.Map;

/**
 * {@code AbstractMultiHttpMethodExecutor} is an abstract class that implements
 * {@code MultiHttpMethodExecutor}, which rewrites {@link #execute} methods to
 * obtain the source {@code HttpRequest} and reflects the execution of relevant
 * request methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class AbstractMultiHttpMethodExecutor implements MultiHttpMethodExecutor {

    @Override
    public final HttpResponse execute(@NotNull HttpRequest httpRequest) throws Exception {
        return (HttpResponse) getClass().getMethod(httpRequest.getMethodName().toLowerCase(),
                String.class,
                Map.class,
                Object.class,
                Charset.class).invoke(this, httpRequest.getUrl(),
                httpRequest.getHeaders(),
                httpRequest.getBody(), httpRequest.getCharset());
    }
}
