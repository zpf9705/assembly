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
 * <p>When the subscription execution thread pool or observe Executor is not provided, it is called
 * in the current main thread as {@link FlowableCaller}.
 *
 * @param <R> Generic R represents the type returned by an operation, which must
 *            inherit from the {@link Response} class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class AsyncFlowableCaller<R extends Response>
        extends FlowableCaller<R> implements ResponseAsyncFlowableCallerElement<R> {

    /*** {@link ResponseAsyncFlowableCallerElement#getCustomSubscriptionExecutor()}*/
    private final Executor customSubscriptionExecutor;

    /*** {@link ResponseAsyncFlowableCallerElement#getCustomObserveExecutor()} ()}*/
    private final Executor customObserveExecutor;

    /**
     * Since the above thread pool is not required to be provided, when it is not provided, it
     * will still be executed synchronously like {@link FlowableCaller}, and the corresponding
     * subscription resources will be released and executed synchronously.
     *
     * <p>This flag is used to indicate whether the above thread pool is provided.
     */
    private boolean disposeSync = true;

    /* {@link AbstractFlowableCaller} */
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
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow, customRetryExceptionPredicate,
                customSubscriptionRegularConsumer, customSubscriptionExceptionConsumer);
        this.customSubscriptionExecutor = customSubscriptionExecutor;
        this.customObserveExecutor = customObserveExecutor;
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
    @Override
    protected Flowable<R> createFlowable() {
        Flowable<R> flowable = super.createFlowable();

        Executor customSubscriptionExecutor0 = getCustomSubscriptionExecutor();
        if (customSubscriptionExecutor0 != null) {

            flowable = flowable.subscribeOn(Schedulers.from(customSubscriptionExecutor0));

            disposeSync = false;
        }

        Executor customObserveExecutor0 = getCustomObserveExecutor();
        if (customObserveExecutor0 != null) {

            flowable = flowable.observeOn(Schedulers.from(customObserveExecutor0));

            disposeSync = false;
        }
        return flowable;
    }

    @Override
    public Executor getCustomSubscriptionExecutor() {
        return customSubscriptionExecutor;
    }

    @Override
    public Executor getCustomObserveExecutor() {
        return customObserveExecutor;
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
