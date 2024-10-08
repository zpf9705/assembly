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

package top.osjf.sdk.core.util;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import top.osjf.sdk.core.process.Response;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The Asynchronous FlowableCaller class extends {@link FlowableCaller}, providing
 * finer grained control over asynchronous operations.
 *
 * <p>It allows users to customize the executor for subscription and observation operations,
 * thereby controlling the execution context of these operations (such as threads or thread pools).
 *
 * @param <R> Generic R represents the type returned by an operation, which must
 *            inherit from the {@link Response} class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class AsyncFlowableCaller<R extends Response> extends FlowableCaller<R> {

    /**
     * A custom subscription executor that handles asynchronous tasks related to subscriptions.
     * It allows users to customize the execution thread or thread pool for subscription
     * operations (e.g., subscribing to messages, events, etc.).
     *
     * <p>If it is empty, the subscription will be executed in the main thread.
     */
    private final Executor customSubscriptionExecutor;

    /**
     * A custom observe executor that handles asynchronous tasks related to observation or responses.
     * It allows users to customize the execution thread or thread pool for observation
     * operations (e.g., responding to events, handling callbacks, etc.).
     *
     * <p>If it is empty, to observe will be executed in the main thread.
     */
    private final Executor customObserveExecutor;

    /**
     * Since the above thread pool is not required to be provided, when it is not provided, it
     * will still be executed synchronously like {@link FlowableCaller}, and the corresponding
     * subscription resources will be released and executed synchronously.
     *
     * <p>This flag is used to indicate whether the above thread pool is provided.
     */
    private boolean disposeSync = true;

    /**
     * Construct a {@code AsyncFlowableCaller} instance.
     *
     * @param runBody                             {@code FlowableCaller#runBody}.
     * @param retryTimes                          {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds           {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param whenResponseNonSuccessRetry         {@code FlowableCaller#whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow    {@code FlowableCaller#whenResponseNonSuccessFinalThrow}.
     * @param customRetryExceptionPredicate       {@code FlowableCaller#customRetryExceptionPredicate}.
     * @param customSubscriptionRegularConsumer   {@code FlowableCaller#customSubscriptionRegularConsumer}.
     * @param customSubscriptionExceptionConsumer {@code FlowableCaller#customSubscriptionExceptionConsumer}.
     * @param customSubscriptionExecutor          Custom Subscription Executor. This parameter is typically used
     *                                            to specify an executor that is responsible for executing code
     *                                            or tasks when a subscription occurs (e.g., in reactive programming
     *                                            , when a subscription to a stream is made). It allows developers
     *                                            to control the execution context of subscription operations,
     *                                            such as specifying which thread or thread pool to execute on.
     * @param customObserveExecutor               Custom Observe Executor. Similar to customSubscriptionExecutor,
     *                                            this parameter is also used to specify an executor, but it
     *                                            focuses on controlling the execution context for observation
     *                                            (or consumption) operations. In reactive programming, when data
     *                                            is produced and ready to be consumed, customObserveExecutor determines
     *                                            which thread or thread pool these consumption operations
     *                                            (e.g., processing the data) will execute on.
     */
    public AsyncFlowableCaller(Supplier<R> runBody, int retryTimes,
                               long retryIntervalMilliseconds,
                               boolean whenResponseNonSuccessRetry,
                               boolean whenResponseNonSuccessFinalThrow,
                               Predicate<? super Throwable> customRetryExceptionPredicate,
                               Consumer<R> customSubscriptionRegularConsumer,
                               Consumer<Throwable> customSubscriptionExceptionConsumer,
                               Executor customSubscriptionExecutor,
                               Executor customObserveExecutor) {
        super(runBody, retryTimes, retryIntervalMilliseconds,
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow,
                customRetryExceptionPredicate, customSubscriptionRegularConsumer, customSubscriptionExceptionConsumer);
        this.customSubscriptionExecutor = customSubscriptionExecutor;
        this.customObserveExecutor = customObserveExecutor;
        perfectFlowable();
    }

    /**
     * Optimize the execution thread of {@link Flowable}.
     *
     * <p>This method aims to adjust the execution and observation strategies of Flowable
     * to improve performance or meet specific thread requirements.
     * It first obtains the current Flowable instance, and then adjusts it through the '
     * subscribeOn' and 'observeOn' operators The thread that subscribes and executes callbacks.
     *
     * <p>If a custom Subscription Executor is provided, use it to specify
     * The thread at the time of Flowable subscription (i.e. before starting to process data).
     * If not provided, the {@link Schedulers#trampoline()} will be used by default,
     * It attempts to execute immediately in the current thread, but allows recursive calls without
     * causing stack overflow.
     *
     * <p>Similarly, if a custom ObserveExecutor is provided, use it to specify
     * The thread that receives data and triggers callbacks (such as onNext, onError, onComplete).
     * If not provided, the {@link Schedulers#trampoline()} is also used by default.
     *
     * <p>Finally, the method sets the adjusted Flowable instance back to its original position.
     */
    protected void perfectFlowable() {

        if (customSubscriptionExecutor != null) {

            flowable = flowable.subscribeOn(Schedulers.from(customSubscriptionExecutor));

            disposeSync = false;
        }

        if (customObserveExecutor != null) {

            flowable = flowable.observeOn(Schedulers.from(customObserveExecutor));

            disposeSync = false;
        }
    }

    @Override
    protected FlowableCaller<R>.OnNext getOnNext() {
        return new OnNext();
    }

    @Override
    protected FlowableCaller<R>.OnError getOnError() {
        return new OnError();
    }

    @Override
    protected boolean disposeSync() {
        return disposeSync;
    }

    /* AsyncFlowableCaller builder static method */

    /**
     * A static method for creating a new auxiliary construct for {@link AsyncFlowableCaller}.
     *
     * @param <R> Generic R represents the type returned by an operation, which must
     *            inherit from the {@link Response} class.
     * @return a new auxiliary construct.
     */
    public static <R extends Response> AsyncFlowableCallerBuilder<R> newBuilder() {
        return new AsyncFlowableCallerBuilder<>();
    }

    /* AsyncFlowableCaller void static method */

    /**
     * A static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                    {@code FlowableCaller#runBody}.
     * @param retryTimes                 {@code FlowableCaller# retryTimes}.
     * @param customSubscriptionExecutor {@link #customSubscriptionExecutor}.
     * @param customObserveExecutor      {@link #customObserveExecutor}.
     * @param <R>                        Generic R represents the type returned by an operation, which must
     *                                   inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 Executor customSubscriptionExecutor,
                                                 Executor customObserveExecutor) {
        call(runBody, retryTimes, 0, customSubscriptionExecutor, customObserveExecutor);
    }

    /**
     * A static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                    {@code FlowableCaller#runBody}.
     * @param retryTimes                 {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds  {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param customSubscriptionExecutor {@link #customSubscriptionExecutor}.
     * @param customObserveExecutor      {@link #customObserveExecutor}.
     * @param <R>                        Generic R represents the type returned by an operation, which must
     *                                   inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 long retryIntervalMilliseconds,
                                                 Executor customSubscriptionExecutor,
                                                 Executor customObserveExecutor) {
        call(runBody, retryTimes, retryIntervalMilliseconds, false,
                customSubscriptionExecutor, customObserveExecutor);
    }

    /**
     * A static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                     {@code FlowableCaller#runBody}.
     * @param retryTimes                  {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds   {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param whenResponseNonSuccessRetry {@code FlowableCaller#whenResponseNonSuccessRetry}.
     * @param customSubscriptionExecutor  {@link #customSubscriptionExecutor}.
     * @param customObserveExecutor       {@link #customObserveExecutor}.
     * @param <R>                         Generic R represents the type returned by an operation, which must
     *                                    inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 long retryIntervalMilliseconds,
                                                 boolean whenResponseNonSuccessRetry,
                                                 Executor customSubscriptionExecutor,
                                                 Executor customObserveExecutor) {
        call(runBody, retryTimes, retryIntervalMilliseconds, whenResponseNonSuccessRetry,
                false, customSubscriptionExecutor, customObserveExecutor);
    }

    /**
     * A static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                          {@code FlowableCaller#runBody}.
     * @param retryTimes                       {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds        {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param whenResponseNonSuccessRetry      {@code FlowableCaller#whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow {@code FlowableCaller#whenResponseNonSuccessFinalThrow}.
     * @param customSubscriptionExecutor       {@link #customSubscriptionExecutor}.
     * @param customObserveExecutor            {@link #customObserveExecutor}.
     * @param <R>                              Generic R represents the type returned by an operation, which must
     *                                         inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 long retryIntervalMilliseconds,
                                                 boolean whenResponseNonSuccessRetry,
                                                 boolean whenResponseNonSuccessFinalThrow,
                                                 Executor customSubscriptionExecutor,
                                                 Executor customObserveExecutor) {
        call(runBody, retryTimes, retryIntervalMilliseconds,
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow,
                null,
                customSubscriptionExecutor, customObserveExecutor);
    }

    /**
     * A static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                          {@code FlowableCaller#runBody}.
     * @param retryTimes                       {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds        {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param whenResponseNonSuccessRetry      {@code FlowableCaller#whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow {@code FlowableCaller#whenResponseNonSuccessFinalThrow}.
     * @param customRetryExceptionPredicate    {@code FlowableCaller#customRetryExceptionPredicate}.
     * @param customSubscriptionExecutor       {@link #customSubscriptionExecutor}.
     * @param customObserveExecutor            {@link #customObserveExecutor}.
     * @param <R>                              Generic R represents the type returned by an operation, which must
     *                                         inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 long retryIntervalMilliseconds,
                                                 boolean whenResponseNonSuccessRetry,
                                                 boolean whenResponseNonSuccessFinalThrow,
                                                 Predicate<? super Throwable> customRetryExceptionPredicate,
                                                 Executor customSubscriptionExecutor,
                                                 Executor customObserveExecutor) {
        call(runBody, retryTimes, retryIntervalMilliseconds, whenResponseNonSuccessRetry,
                whenResponseNonSuccessFinalThrow,
                customRetryExceptionPredicate,
                null,
                null, customSubscriptionExecutor, customObserveExecutor);
    }

    /**
     * A static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                             {@code FlowableCaller#runBody}.
     * @param retryTimes                          {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds           {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param whenResponseNonSuccessRetry         {@code FlowableCaller#whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow    {@code FlowableCaller#whenResponseNonSuccessFinalThrow}.
     * @param customRetryExceptionPredicate       {@code FlowableCaller#customRetryExceptionPredicate}.
     * @param customSubscriptionRegularConsumer   {@code FlowableCaller#customSubscriptionRegularConsumer}.
     * @param customSubscriptionExceptionConsumer {@code FlowableCaller#customSubscriptionExceptionConsumer}.
     * @param customSubscriptionExecutor          {@link #customSubscriptionExecutor}.
     * @param customObserveExecutor               {@link #customObserveExecutor}.
     * @param <R>                                 Generic R represents the type returned by an operation, which must
     *                                            inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 long retryIntervalMilliseconds,
                                                 boolean whenResponseNonSuccessRetry,
                                                 boolean whenResponseNonSuccessFinalThrow,
                                                 Predicate<? super Throwable> customRetryExceptionPredicate,
                                                 Consumer<R> customSubscriptionRegularConsumer,
                                                 Consumer<Throwable> customSubscriptionExceptionConsumer,
                                                 Executor customSubscriptionExecutor,
                                                 Executor customObserveExecutor) {
        new AsyncFlowableCaller<>(runBody, retryTimes, retryIntervalMilliseconds,
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow, customRetryExceptionPredicate,
                customSubscriptionRegularConsumer, customSubscriptionExceptionConsumer, customSubscriptionExecutor,
                customObserveExecutor).run();
    }

    /* AsyncFlowableCaller void static method */

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
        return get(runBody, retryTimes, retryIntervalMilliseconds, whenResponseNonSuccessRetry,
                whenResponseNonSuccessFinalThrow,
                customRetryExceptionPredicate,
                null,
                null, customSubscriptionExecutor);
    }

    /**
     * A retrieve the response body static method for SDK calls using the API of {@code AsyncFlowableCaller}.
     *
     * @param runBody                             {@code FlowableCaller#runBody}.
     * @param retryTimes                          {@code FlowableCaller#retryTimes}.
     * @param retryIntervalMilliseconds           {@code FlowableCaller#retryIntervalMilliseconds}.
     * @param whenResponseNonSuccessRetry         {@code FlowableCaller#whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow    {@code FlowableCaller#whenResponseNonSuccessFinalThrow}.
     * @param customRetryExceptionPredicate       {@code FlowableCaller#customRetryExceptionPredicate}.
     * @param customSubscriptionRegularConsumer   {@code FlowableCaller#customSubscriptionRegularConsumer}.
     * @param customSubscriptionExceptionConsumer {@code FlowableCaller#customSubscriptionExceptionConsumer}.
     * @param customSubscriptionExecutor          {@link #customSubscriptionExecutor}.
     * @param <R>                                 Generic R represents the type returned by an operation, which must
     *                                            inherit from the {@link Response} class.
     */
    public static <R extends Response> R get(Supplier<R> runBody,
                                                int retryTimes,
                                                long retryIntervalMilliseconds,
                                                boolean whenResponseNonSuccessRetry,
                                                boolean whenResponseNonSuccessFinalThrow,
                                                Predicate<? super Throwable> customRetryExceptionPredicate,
                                                Consumer<R> customSubscriptionRegularConsumer,
                                                Consumer<Throwable> customSubscriptionExceptionConsumer,
                                                Executor customSubscriptionExecutor) {
        return new AsyncFlowableCaller<>(runBody, retryTimes, retryIntervalMilliseconds,
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow, customRetryExceptionPredicate,
                customSubscriptionRegularConsumer, customSubscriptionExceptionConsumer, customSubscriptionExecutor,
                null).get();
    }

    /* AsyncFlowableCaller help class */

    /*** Ensure that thread counters can be operated after executing the
     * next notification asynchronously. */
    private class OnNext extends FlowableCaller<R>.OnNext {
        @Override
        public void accept(R r) {
            try {
                super.accept(r);
            } finally {
                dispose();
            }
        }
    }

    /*** Ensure that thread counters, including errors in the next step, can be
     * operated after executing error notifications asynchronously.*/
    private class OnError extends FlowableCaller<R>.OnError {
        @Override
        public void accept(Throwable e) {
            try {
                super.accept(e);
            } finally {
                dispose();
            }
        }
    }
}
