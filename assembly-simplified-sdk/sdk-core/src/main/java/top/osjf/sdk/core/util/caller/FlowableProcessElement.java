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

import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.support.Nullable;

import java.util.function.Consumer;

/**
 * The {@code FlowableProcessElement} interface defines custom subscription behavior related to
 * flow processing elements.
 *
 * <p>It provides custom consumers for normal response results and exceptions that occur during the
 * subscription process.
 * By implementing this interface, specific behaviors can be configured for stream processing
 * elements to execute corresponding logic when receiving normal responses or encountering exceptions.
 *
 * @param <R> The type of response result must be the Response class or its subclass.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface FlowableProcessElement<R extends Response> {

    /**
     * Customized subscription for regular consumers, used to handle normal response results.
     *
     * @return Customized subscription for regular consumers.
     */
    @Nullable
    Consumer<R> getCustomSubscriptionRegularConsumer();

    /*** Custom subscription exception consumers, used to handle exceptions that occur during the
     * subscription process.
     * @return Custom subscription exception consumers.
     * */
    @Nullable
    Consumer<Throwable> getCustomSubscriptionExceptionConsumer();
}
