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
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The call class that subscribes to sending but is not processed by downstream
 * can receive the response content after the subscriber sends the response body
 * (provided that no final exception occurs).
 *
 * @param <R> The type of response result must be the Response class or its subclass.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class BlockedFlowableCaller<R extends Response> extends AbstractFlowableCaller<R> implements FlowableBlock<R> {

    /* {@link AbstractFlowableCaller} */
    public BlockedFlowableCaller(@NotNull Supplier<R> runBody, int retryTimes,
                                 long retryIntervalMilliseconds, boolean whenResponseNonSuccessRetry,
                                 boolean whenResponseNonSuccessFinalThrow,
                                 @Nullable Predicate<? super Throwable> customRetryExceptionPredicate) {
        super(runBody, retryTimes, retryIntervalMilliseconds, whenResponseNonSuccessRetry,
                whenResponseNonSuccessFinalThrow, customRetryExceptionPredicate);
    }

    /**
     * This method will block until the response message sent by the subscriber is received.
     * When there is only one response message, it will be successfully obtained.
     * If there are multiple or zero response messages, it will violate the specifications of RXJava3.
     *
     * @return The response body sent by the subscriber thread.
     */
    @Override
    public R get() {
        return getFlowable().blockingSingle();
    }

    @Override
    public <T> T get(Class<T> requiredType) {
        return requiredType.cast(get());
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody    {@link #getRunBody()}.
     * @param retryTimes {@link #getRetryTimes()}.
     * @param <R>        Generic R represents the type returned by an operation, which must
     *                   inherit from the {@link Response} class.
     * @return a callback {@link Response}.
     * @throws NullPointerException if input runBody is {@literal null}.
     */
    public static <R extends Response> R get(@NotNull Supplier<R> runBody,
                                             int retryTimes) {
        return get(runBody, retryTimes, 0);
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody                   {@link #getRunBody()}.
     * @param retryTimes                {@link #getRetryTimes()}.
     * @param retryIntervalMilliseconds {@link #getRetryIntervalMilliseconds()}.
     * @param <R>                       Generic R represents the type returned by an operation, which must
     *                                  inherit from the {@link Response} class.
     * @return a callback {@link Response}.
     * @throws NullPointerException if input runBody is {@literal null}.
     */
    public static <R extends Response> R get(@NotNull Supplier<R> runBody,
                                             int retryTimes,
                                             long retryIntervalMilliseconds) {
        return get(runBody, retryTimes, retryIntervalMilliseconds, false);
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody                     {@link #getRunBody()}.
     * @param retryTimes                  {@link #getRetryTimes()}.
     * @param retryIntervalMilliseconds   {@link #getRetryIntervalMilliseconds()}.
     * @param whenResponseNonSuccessRetry {@link #isWhenResponseNonSuccessRetry()}.
     * @param <R>                         Generic R represents the type returned by an operation, which must
     *                                    inherit from the {@link Response} class.
     * @return a callback {@link Response}.
     * @throws NullPointerException if input runBody is {@literal null}.
     */
    public static <R extends Response> R get(@NotNull Supplier<R> runBody,
                                             int retryTimes,
                                             long retryIntervalMilliseconds,
                                             boolean whenResponseNonSuccessRetry) {
        return get(runBody, retryTimes, retryIntervalMilliseconds,
                whenResponseNonSuccessRetry, false);
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody                          {@link #getRunBody()}.
     * @param retryTimes                       {@link #getRetryTimes()}.
     * @param retryIntervalMilliseconds        {@link #getRetryIntervalMilliseconds()}.
     * @param whenResponseNonSuccessRetry      {@link #isWhenResponseNonSuccessRetry()}.
     * @param whenResponseNonSuccessFinalThrow {@link #isWhenResponseNonSuccessFinalThrow()}.
     * @param <R>                              Generic R represents the type returned by an operation, which must
     *                                         inherit from the {@link Response} class.
     * @return a callback {@link Response}.
     * @throws NullPointerException if input runBody is {@literal null}.
     */
    public static <R extends Response> R get(@NotNull Supplier<R> runBody,
                                             int retryTimes,
                                             long retryIntervalMilliseconds,
                                             boolean whenResponseNonSuccessRetry,
                                             boolean whenResponseNonSuccessFinalThrow) {
        return get(runBody, retryTimes, retryIntervalMilliseconds,
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow, null);
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody                          {@link #getRunBody()}.
     * @param retryTimes                       {@link #getRetryTimes()}.
     * @param retryIntervalMilliseconds        {@link #getRetryIntervalMilliseconds()}.
     * @param whenResponseNonSuccessRetry      {@link #isWhenResponseNonSuccessRetry()}.
     * @param whenResponseNonSuccessFinalThrow {@link #isWhenResponseNonSuccessFinalThrow()}.
     * @param customRetryExceptionPredicate    {@link #getCustomRetryExceptionPredicate()}.
     * @param <R>                              Generic R represents the type returned by an operation, which must
     *                                         inherit from the {@link Response} class.
     * @return a callback {@link Response}.
     * @throws NullPointerException if input runBody is {@literal null}.
     */
    public static <R extends Response> R get(@NotNull Supplier<R> runBody,
                                             int retryTimes,
                                             long retryIntervalMilliseconds,
                                             boolean whenResponseNonSuccessRetry,
                                             boolean whenResponseNonSuccessFinalThrow,
                                             @Nullable Predicate<? super Throwable> customRetryExceptionPredicate) {
        return new BlockedFlowableCaller<>(runBody, retryTimes, retryIntervalMilliseconds,
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow, customRetryExceptionPredicate).get();
    }
}
