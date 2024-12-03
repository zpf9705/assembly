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

package top.osjf.sdk.http.executor;

import top.osjf.sdk.http.process.HttpRequest;

import java.util.Map;

/**
 * {@code AbstractSourceHttpRequestExecutor} is an abstract class that implements
 * {@code SourceHttpRequestExecutor}, which rewrites {@link #execute} methods to
 * obtain the source {@code HttpRequest} and reflects the execution of relevant
 * request methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractSourceHttpRequestExecutor implements SourceHttpRequestExecutor {

    @Override
    public final String execute(ExecutableHttpRequest httpRequest) throws Exception {
        Source source = httpRequest.getSource();
        HttpRequest sourceRequest = source.getSourceRequest();
        return getClass().getMethod(sourceRequest.matchSdkEnum().name().toLowerCase(),
                String.class,
                Map.class,
                Object.class,
                boolean.class).invoke(this, source.getSourceUrl(),
                sourceRequest.getHeadMap(),
                sourceRequest.getRequestParam(), sourceRequest.montage()).toString();
    }
}
