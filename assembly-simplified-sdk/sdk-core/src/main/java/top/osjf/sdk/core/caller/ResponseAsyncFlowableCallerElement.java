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

package top.osjf.sdk.core.caller;

import top.osjf.sdk.core.Response;

import java.util.concurrent.Executor;

/**
 * The {@code ResponseAsyncFlowableCallerElement} interface extends the
 * {@code ResponseFlowableCallerElement} interface,and added custom
 * subscription and executor configuration for executing observation tasks
 * for asynchronous stream call elements.
 *
 * <p>This interface extends the {@code AsyncPubSubExecutorProvider} interface,
 * providing custom subscription and observation of {@code Executor} instances,
 * allowing developers to subscribe and observe operations (such as subscribing
 * to messages, events or response events, handling callbacks, etc.).
 *
 * @param <R> the type of response result must be the Response class or
 *            its subclass.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface ResponseAsyncFlowableCallerElement<R extends Response> extends ResponseFlowableCallerElement<R>,
        AsyncPubSubExecutorProvider {

    /**
     * {@inheritDoc}
     *
     * <p>Provide asynchronous executors during the subscriber
     * {@code Executor} execution phase.
     *
     * @return {@inheritDoc}
     */
    @Override
    Executor getCustomSubscriptionExecutor();

    /**
     * {@inheritDoc}
     *
     * <p>Provide asynchronous observation actuators during the
     * observer {@code Executor} observation phase.
     *
     * @return {@inheritDoc}
     */
    @Override
    Executor getCustomObserveExecutor();
}
