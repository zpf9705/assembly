/*
 * Copyright 2023-2024 the original author or authors.
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

import top.osjf.sdk.core.client.LoggerConsumer;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.ServiceLoadManager;
import top.osjf.sdk.http.process.HttpResponse;

import java.util.function.BiConsumer;

/**
 * A {@code Client} implementation class that can utilize Java's
 * {@link java.util.ServiceLoader} mechanism to obtain the highest
 * priority {@code LoggerConsumer} for use.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class ServiceLoaderLoggerHttpClient<R extends HttpResponse> extends DefaultHttpClient<R> {

    private static final long serialVersionUID = 4669129398883621311L;

    private LoggerConsumer consumer;

    /*** Constructing for {@link HttpClient} objects using access URLs.
     * @param url The real URL address of the SDK request.
     */
    public ServiceLoaderLoggerHttpClient(String url) {
        super(url);
        loadHighPriorityLoggerConsumer();
    }

    /**
     * Load a {@code LoggerConsumer},if null use {@link ServiceLoadManager}
     * load a high priority {@code LoggerConsumer} to use in this {@code Client}.
     *
     * @throws IllegalStateException if not found available {@code LoggerConsumer}.
     */
    private void loadHighPriorityLoggerConsumer() {
        if (consumer == null) {
            consumer = ServiceLoadManager.loadHighPriority(LoggerConsumer.class);
            if (consumer == null)
                throw new IllegalStateException("Not found available LoggerConsumer using java.util.ServiceLoader !");
        }
    }

    @Override
    @NotNull
    public BiConsumer<String, Object[]> normal() {
        return consumer.normal();
    }

    @Override
    @NotNull
    public BiConsumer<String, Object[]> sdkError() {
        return consumer.sdkError();
    }

    @Override
    @NotNull
    public BiConsumer<String, Object[]> unKnowError() {
        return consumer.unKnowError();
    }
}
