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
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.lang.Nullable;

import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The Builder class is used to build instances of {@link AsyncFlowableCaller}
 * or {@link BlockedAsyncFlowableCaller}, which extends from {@link FlowableCallerBuilder}
 * to support customization of asynchronous flow operations.
 * Through this builder, users can set custom subscription executors and observation
 * executors to control the execution and result processing of asynchronous operations.
 *
 * @param <R> Generic R represents the type returned by an operation, which must
 *            inherit from the {@link Response} class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class AsyncFlowableCallerBuilder<R extends Response> extends FlowableCallerBuilder<R> {

    /*** {@code AsyncFlowableCaller#customSubscriptionExecutor}*/
    @Nullable
    private Executor customSubscriptionExecutor;

    /*** {@code AsyncFlowableCaller#customObserveExecutor}*/
    @Nullable
    private Executor customObserveExecutor;

    /**
     * A static method for creating a new {@link AsyncFlowableCallerBuilder}.
     *
     * @param <R> Generic R represents the type returned by an operation, which must
     *            inherit from the {@link Response} class.
     * @return a new {@code AsyncFlowableCallerBuilder}.
     */
    public static <R extends Response> AsyncFlowableCallerBuilder<R> newBuilder() {
        return new AsyncFlowableCallerBuilder<>();
    }

    @Override
    public AsyncFlowableCallerBuilder<R> runBody(@NotNull Supplier<R> runBody) {
        super.runBody(runBody);
        return this;
    }

    @Override
    public AsyncFlowableCallerBuilder<R> retryTimes(int retryTimes) {
        super.retryTimes(retryTimes);
        return this;
    }

    @Override
    public AsyncFlowableCallerBuilder<R> retryIntervalMilliseconds(long retryIntervalMilliseconds) {
        super.retryIntervalMilliseconds(retryIntervalMilliseconds);
        return this;
    }

    @Override
    public AsyncFlowableCallerBuilder<R> whenResponseNonSuccessRetry() {
        super.whenResponseNonSuccessRetry();
        return this;
    }

    @Override
    public AsyncFlowableCallerBuilder<R> whenResponseNonSuccessFinalThrow() {
        super.whenResponseNonSuccessFinalThrow();
        return this;
    }

    @Override
    public AsyncFlowableCallerBuilder<R> customRetryExceptionPredicate
            (Predicate<? super Throwable> customRetryExceptionPredicate) {
        super.customRetryExceptionPredicate(customRetryExceptionPredicate);
        return this;
    }

    /*** {@inheritDoc}*/
    @Override
    public AsyncFlowableCallerBuilder<R> customSubscriptionRegularConsumer
    (@Nullable Consumer<R> customSubscriptionRegularConsumer) {
        super.customSubscriptionRegularConsumer(customSubscriptionRegularConsumer);
        return this;
    }

    /*** {@inheritDoc}*/
    @Override
    public AsyncFlowableCallerBuilder<R> customSubscriptionExceptionConsumer
    (@Nullable Consumer<Throwable> customSubscriptionExceptionConsumer) {
        super.customSubscriptionExceptionConsumer(customSubscriptionExceptionConsumer);
        return this;
    }

    /**
     * Set a {@link #customSubscriptionExecutor} for {@link AsyncFlowableCallerBuilder}.
     *
     * @param customSubscriptionExecutor {@code AsyncFlowableCaller#customSubscriptionExecutor}
     * @return this.
     */
    public AsyncFlowableCallerBuilder<R> customSubscriptionExecutor(@Nullable Executor customSubscriptionExecutor) {
        this.customSubscriptionExecutor = customSubscriptionExecutor;
        return this;
    }

    /**
     * Set a {@link #customObserveExecutor} for {@link AsyncFlowableCallerBuilder}.
     * <p>The blocking version of {@link BlockedAsyncFlowableCaller} does not require this value,
     * the setting is invalid {@link #buildBlock()}.
     *
     * @param customObserveExecutor {@code AsyncFlowableCaller#customObserveExecutor}
     * @return this.
     */
    public AsyncFlowableCallerBuilder<R> customObserveExecutor(@Nullable Executor customObserveExecutor) {
        this.customObserveExecutor = customObserveExecutor;
        return this;
    }

    /**
     * Build and return a {@link AsyncFlowableCaller} instance based on the current configuration.
     *
     * @return {@link AsyncFlowableCaller}.
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public AsyncFlowableCaller<R> build() {
        FlowableCaller<R> flowableCaller = super.build();
        return new AsyncFlowableCaller<>
                (flowableCaller.getRunBody(), flowableCaller.getRetryTimes(),
                        flowableCaller.getRetryIntervalMilliseconds(),
                        flowableCaller.isWhenResponseNonSuccessRetry(),
                        flowableCaller.isWhenResponseNonSuccessFinalThrow(),
                        flowableCaller.getCustomRetryExceptionPredicate(),
                        flowableCaller.getCustomSubscriptionRegularConsumer(),
                        flowableCaller.getCustomSubscriptionExceptionConsumer(),
                        customSubscriptionExecutor, customObserveExecutor);
    }

    /**
     * Build and return a {@link BlockedAsyncFlowableCaller} instance based on the current configuration.
     *
     * @return {@link BlockedAsyncFlowableCaller}.
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public BlockedAsyncFlowableCaller<R> buildBlock() {
        FlowableCaller<R> flowableCaller = super.build();
        return new BlockedAsyncFlowableCaller<>
                (flowableCaller.getRunBody(), flowableCaller.getRetryTimes(),
                        flowableCaller.getRetryIntervalMilliseconds(),
                        flowableCaller.isWhenResponseNonSuccessRetry(),
                        flowableCaller.isWhenResponseNonSuccessFinalThrow(),
                        flowableCaller.getCustomRetryExceptionPredicate(),
                        customSubscriptionExecutor);
    }
}
