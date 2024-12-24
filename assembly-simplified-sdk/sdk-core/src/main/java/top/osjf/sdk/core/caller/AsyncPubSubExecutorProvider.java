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

import java.util.concurrent.Executor;

/**
 * <p>Asynchronous publish subscribe {@code Executor} provider interface.
 *
 * <p>This interface provides methods for obtaining custom subscriptions
 * {@code Executor} and observing {@code Executor}. These actuators are
 * used to handle asynchronous tasks related to subscription and observation.
 * Developers can customize the execution threads or thread pools for
 * subscription and observation operations through these methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface AsyncPubSubExecutorProvider {

    /**
     * A custom subscription {@code Executor} that handles asynchronous tasks
     * related to subscriptions.
     * It allows users to customize the execution thread or thread pool for
     * subscription operations (e.g., subscribing to messages, events, etc.).
     *
     * @return custom subscription {@code Executor}.
     */
    Executor getCustomSubscriptionExecutor();

    /**
     * A custom observe {@code Executor} that handles asynchronous tasks related
     * to observation or responses.
     * It allows users to customize the execution thread or thread pool for observation
     * operations (e.g., responding to events, handling callbacks, etc.).
     *
     * @return custom observe {@code Executor}.
     */
    Executor getCustomObserveExecutor();
}
