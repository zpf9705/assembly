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

package top.osjf.sdk.core.util.caller;

import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.support.Nullable;

import java.util.concurrent.Executor;

/**
 * The {@code ResponseAsyncFlowableCallerElement} interface extends the ResponseFlowableCallerElement interface,
 * And added custom subscription and executor configuration for executing observation tasks for asynchronous
 * stream call elements.
 *
 * <p>This interface allows users to subscribe to and observe operations (such as subscribing to messages,
 * events or response events, handling callbacks, etc.)
 * Customize execution threads or thread pools. If no custom executor is configured, these operations will
 * be executed in the main thread.
 *
 * @param <R> The type of response result must be the Response class or its subclass.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface ResponseAsyncFlowableCallerElement<R extends Response> extends ResponseFlowableCallerElement<R> {

    /**
     * A custom subscription executor that handles asynchronous tasks related to subscriptions.
     * It allows users to customize the execution thread or thread pool for subscription
     * operations (e.g., subscribing to messages, events, etc.).
     *
     * <p>If it is empty, the subscription will be executed in the main thread.
     *
     * @return custom subscription executor.
     */
    @Nullable
    Executor getCustomSubscriptionExecutor();

    /**
     * A custom observe executor that handles asynchronous tasks related to observation or responses.
     * It allows users to customize the execution thread or thread pool for observation
     * operations (e.g., responding to events, handling callbacks, etc.).
     *
     * <p>If it is empty, to observe will be executed in the main thread.
     *
     * @return custom observe executor.
     */
    @Nullable
    Executor getCustomObserveExecutor();
}
