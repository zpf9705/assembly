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

import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The Builder class is used to build {@link FlowableCaller} or {@link BlockedFlowableCaller}
 * instances.Set various configuration parameters through chain calls, including execution body,
 * retry count, retry behavior in case of response failure, whether to ultimately throw
 * an exception in case of response failure Customize retry exception judgment logic,
 * as well as normal and abnormal subscription consumption logic.
 *
 * @param <R> Generic R represents the type returned by an operation, which must
 *            inherit from the {@link Response} class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class FlowableCallerBuilder<R extends Response> {

    /*** {@code FlowableCaller#runBody}*/
    private Supplier<R> runBody;
    /*** {@code FlowableCaller#retryTimes}*/
    private int retryTimes;
    /*** {@code FlowableCaller#retryIntervalMilliseconds}*/
    private long retryIntervalMilliseconds;
    /*** {@code FlowableCaller#whenResponseNonSuccessRetry}*/
    private boolean whenResponseNonSuccessRetry;
    /*** {@code FlowableCaller#whenResponseNonSuccessFinalThrow}*/
    private boolean whenResponseNonSuccessFinalThrow;
    /*** {@code FlowableCaller#customRetryExceptionPredicate}*/
    private Predicate<? super Throwable> customRetryExceptionPredicate;
    /*** {@code FlowableCaller#customSubscriptionRegularConsumer}*/
    private Consumer<R> customSubscriptionRegularConsumer;
    /*** {@code FlowableCaller#customSubscriptionExceptionConsumer}*/
    private Consumer<Throwable> customSubscriptionExceptionConsumer;

    /**
     * Set a {@link #runBody} for {@link FlowableCallerBuilder}.
     *
     * @param runBody {@code FlowableCaller#runBody}
     * @return this.
     */
    public FlowableCallerBuilder<R> runBody(Supplier<R> runBody) {
        this.runBody = runBody;
        return this;
    }

    /**
     * Set a {@link #retryTimes} for {@link FlowableCallerBuilder}.
     *
     * @param retryTimes {@code FlowableCaller#retryTimes}
     * @return this.
     */
    public FlowableCallerBuilder<R> retryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    /**
     * Set a {@link #retryIntervalMilliseconds} for {@link FlowableCallerBuilder}.
     *
     * @param retryIntervalMilliseconds {@code FlowableCaller#retryIntervalMilliseconds}
     * @return this.
     */
    public FlowableCallerBuilder<R> retryIntervalMilliseconds(long retryIntervalMilliseconds) {
        this.retryIntervalMilliseconds = retryIntervalMilliseconds;
        return this;
    }

    /**
     * Set {@code true} value to {@code whenResponseNonSuccessRetry} for {@link FlowableCallerBuilder}.
     *
     * @return this.
     */
    public FlowableCallerBuilder<R> whenResponseNonSuccessRetry() {
        this.whenResponseNonSuccessRetry = true;
        return this;
    }

    /**
     * Set {@code true} value to {@code whenResponseNonSuccessFinalThrow} for {@link FlowableCallerBuilder}.
     *
     * @return this.
     */
    public FlowableCallerBuilder<R> whenResponseNonSuccessFinalThrow() {
        this.whenResponseNonSuccessFinalThrow = true;
        return this;
    }

    /**
     * Set a {@link #customRetryExceptionPredicate} for {@link FlowableCallerBuilder}.
     *
     * @param customRetryExceptionPredicate {@code FlowableCaller#customRetryExceptionPredicate}
     * @return this.
     */
    public FlowableCallerBuilder<R> customRetryExceptionPredicate(
            Predicate<? super Throwable> customRetryExceptionPredicate) {
        this.customRetryExceptionPredicate = customRetryExceptionPredicate;
        return this;
    }

    /**
     * Set a {@link #customSubscriptionRegularConsumer} for {@link FlowableCallerBuilder}.
     *
     * @param customSubscriptionRegularConsumer {@code FlowableCaller#customSubscriptionRegularConsumer}
     * @return this.
     */
    public FlowableCallerBuilder<R> customSubscriptionRegularConsumer(Consumer<R> customSubscriptionRegularConsumer) {
        this.customSubscriptionRegularConsumer = customSubscriptionRegularConsumer;
        return this;
    }

    /**
     * Set a {@link #customSubscriptionExceptionConsumer} for {@link FlowableCallerBuilder}.
     *
     * @param customSubscriptionExceptionConsumer {@code FlowableCaller#customSubscriptionRegularConsumer}
     * @return this.
     */
    public FlowableCallerBuilder<R> customSubscriptionExceptionConsumer(Consumer<Throwable> customSubscriptionExceptionConsumer) {
        this.customSubscriptionExceptionConsumer = customSubscriptionExceptionConsumer;
        return this;
    }

    /**
     * Build and return a {@link FlowableCaller} instance based on the current configuration.
     *
     * @return {@link FlowableCaller}.
     */
    public FlowableCaller<R> build() {
        return new FlowableCaller<>
                (runBody, retryTimes, retryIntervalMilliseconds, whenResponseNonSuccessRetry,
                        whenResponseNonSuccessFinalThrow,
                        customRetryExceptionPredicate, customSubscriptionRegularConsumer,
                        customSubscriptionExceptionConsumer);
    }

    /**
     * Build and return a {@link BlockedFlowableCaller} instance based on the current configuration.
     *
     * @return {@link BlockedFlowableCaller}.
     */
    public BlockedFlowableCaller<R> buildBlock() {
        return new BlockedFlowableCaller<>
                (runBody, retryTimes, retryIntervalMilliseconds, whenResponseNonSuccessRetry,
                        whenResponseNonSuccessFinalThrow,
                        customRetryExceptionPredicate);
    }
}
