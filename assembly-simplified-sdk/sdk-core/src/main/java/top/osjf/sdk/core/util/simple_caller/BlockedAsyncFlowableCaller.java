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

package top.osjf.sdk.core.util.simple_caller;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import top.osjf.sdk.core.process.Response;

import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The {@code BlockedSyncFlowableCaller} class extends from {@link BlockedFlowableCaller} and is
 * specifically designed to handle asynchronous Flowable calls.
 * It allows users to specify a custom executor to perform subscription operations, which is particularly
 * useful when controlling subscription threads or resources.
 *
 * <p>This class inherits from BlockedFlowableCaller and adds support for custom subscription executors,
 * allowing Flowable subscriptions to be executed on specified executors.
 * Suitable for scenarios where asynchronous operations need to be performed in a specific thread context
 * , such as when network requests or database operations need to be processed in a specific thread pool
 *
 * @param <R> The type of response result must be the Response class or its subclass.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class BlockedAsyncFlowableCaller<R extends Response> extends BlockedFlowableCaller<R> {

    /*** {@link ResponseAsyncFlowableCallerElement#getCustomSubscriptionExecutor()}*/
    private final Executor customSubscriptionExecutor;

    /* {@link AbstractFlowableCaller} */
    public BlockedAsyncFlowableCaller(Supplier<R> runBody, int retryTimes,
                                      long retryIntervalMilliseconds,
                                      boolean whenResponseNonSuccessRetry,
                                      boolean whenResponseNonSuccessFinalThrow,
                                      Predicate<? super Throwable> customRetryExceptionPredicate,
                                      Executor customSubscriptionExecutor) {
        super(runBody, retryTimes, retryIntervalMilliseconds, whenResponseNonSuccessRetry,
                whenResponseNonSuccessFinalThrow, customRetryExceptionPredicate);
        this.customSubscriptionExecutor = customSubscriptionExecutor;
    }

    @Override
    protected Flowable<R> createFlowable() {

        Flowable<R> flowable = super.createFlowable();

        if (customSubscriptionExecutor != null) {

            flowable = flowable.subscribeOn(Schedulers.from(customSubscriptionExecutor));
        }

        return flowable;
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                    {@code FlowableCaller#runBody}.
     * @param retryTimes                 {@code FlowableCaller# retryTimes}.
     * @param customSubscriptionExecutor {@link #customSubscriptionExecutor}.
     * @param <R>                        Generic R represents the type returned by an operation, which must
     *                                   inherit from the {@link Response} class.
     */
    public static <R extends Response> R get(Supplier<R> runBody,
                                             int retryTimes,
                                             Executor customSubscriptionExecutor) {
        return get(runBody, retryTimes, 0, customSubscriptionExecutor);
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                    {@code FlowableCaller#runBody}.
     * @param retryTimes                 {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds  {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param customSubscriptionExecutor {@link #customSubscriptionExecutor}.
     * @param <R>                        Generic R represents the type returned by an operation, which must
     *                                   inherit from the {@link Response} class.
     */
    public static <R extends Response> R get(Supplier<R> runBody,
                                             int retryTimes,
                                             long retryIntervalMilliseconds,
                                             Executor customSubscriptionExecutor) {
        return get(runBody, retryTimes, retryIntervalMilliseconds, false,
                customSubscriptionExecutor);
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                     {@code FlowableCaller#runBody}.
     * @param retryTimes                  {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds   {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param whenResponseNonSuccessRetry {@code FlowableCaller#whenResponseNonSuccessRetry}.
     * @param customSubscriptionExecutor  {@link #customSubscriptionExecutor}.
     * @param <R>                         Generic R represents the type returned by an operation, which must
     *                                    inherit from the {@link Response} class.
     */
    public static <R extends Response> R get(Supplier<R> runBody,
                                             int retryTimes,
                                             long retryIntervalMilliseconds,
                                             boolean whenResponseNonSuccessRetry,
                                             Executor customSubscriptionExecutor) {
        return get(runBody, retryTimes, retryIntervalMilliseconds, whenResponseNonSuccessRetry,
                false, customSubscriptionExecutor);
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                          {@code FlowableCaller#runBody}.
     * @param retryTimes                       {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds        {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param whenResponseNonSuccessRetry      {@code FlowableCaller#whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow {@code FlowableCaller#whenResponseNonSuccessFinalThrow}.
     * @param customSubscriptionExecutor       {@link #customSubscriptionExecutor}.
     * @param <R>                              Generic R represents the type returned by an operation, which must
     *                                         inherit from the {@link Response} class.
     */
    public static <R extends Response> R get(Supplier<R> runBody,
                                             int retryTimes,
                                             long retryIntervalMilliseconds,
                                             boolean whenResponseNonSuccessRetry,
                                             boolean whenResponseNonSuccessFinalThrow,
                                             Executor customSubscriptionExecutor) {
        return get(runBody, retryTimes, retryIntervalMilliseconds,
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow,
                null,
                customSubscriptionExecutor);
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                          {@code FlowableCaller#runBody}.
     * @param retryTimes                       {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds        {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param whenResponseNonSuccessRetry      {@code FlowableCaller#whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow {@code FlowableCaller#whenResponseNonSuccessFinalThrow}.
     * @param customRetryExceptionPredicate    {@code FlowableCaller#customRetryExceptionPredicate}.
     * @param customSubscriptionExecutor       {@link #customSubscriptionExecutor}.
     * @param <R>                              Generic R represents the type returned by an operation, which must
     *                                         inherit from the {@link Response} class.
     */
    public static <R extends Response> R get(Supplier<R> runBody,
                                             int retryTimes,
                                             long retryIntervalMilliseconds,
                                             boolean whenResponseNonSuccessRetry,
                                             boolean whenResponseNonSuccessFinalThrow,
                                             Predicate<? super Throwable> customRetryExceptionPredicate,
                                             Executor customSubscriptionExecutor) {
        return new BlockedAsyncFlowableCaller<>(runBody, retryTimes, retryIntervalMilliseconds,
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow, customRetryExceptionPredicate,
                customSubscriptionExecutor).get();
    }
}
