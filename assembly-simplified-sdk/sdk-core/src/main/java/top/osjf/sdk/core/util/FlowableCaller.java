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

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import org.reactivestreams.Publisher;
import top.osjf.sdk.core.exception.SdkResponseNonSuccessException;
import top.osjf.sdk.core.process.Response;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The {@code FlowableCaller} class is a utility class used to perform asynchronous operations
 * (see class {@link AsyncFlowableCaller}) and process response results.
 * It implements the Runnable interface, supports retry mechanism, and provides flexible error
 * handling and response handling strategies.
 *
 * <p>This class encapsulates the creation and subscription logic of {@code Flowable} objects
 * based on ReactiveX RxJava3.
 * Allow users to define the operator, retry count, retry strategy in case of unsuccessful
 * response, and custom exception and response handling logic.
 *
 * @param <R> Generic R represents the type returned by an operation, which must
 *            inherit from the {@link Response} class.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class FlowableCaller<R extends Response> implements Runnable {

    protected final Logger LOGGER = Logger.getLogger(getClass().getName());

    /*** The provider of the running entity, the subject used to generate or execute tasks. */
    protected final Supplier<R> runBody;

    /*** The maximum number of retries to attempt to re execute a task after it has failed. */
    protected int retryTimes;

    /*** The flag indicating whether to retry when the response is unsuccessful. If true, attempt
     *  to retry when the response does not meet the success criteria. */
    protected final boolean whenResponseNonSuccessRetry;

    /*** Do we need to throw an exception boolean symbol when the latest response result is
     * unsuccessful.*/
    protected final boolean whenResponseNonSuccessFinalThrow;

    /*** Custom retry exception predicate used to determine which exception types should trigger
     * the retry mechanism. */
    protected final Predicate<? super Throwable> customRetryExceptionPredicate;

    /*** Customized subscription for regular consumers, used to handle normal response results. */
    protected final Consumer<R> customSubscriptionRegularConsumer;

    /*** Custom subscription exception consumers, used to handle exceptions that occur during the
     * subscription process. */
    protected final Consumer<Throwable> customSubscriptionExceptionConsumer;

    /*** Flowable object, representing an observable data flow, can asynchronously emit zero or
     *  more data items. */
    protected Flowable<R> flowable;

    /*** The Disposable object represents the 'handle' of the subscription.*/
    private Disposable disposable;

    /*** The default response unsuccessful retry predicate is used to determine whether an exception
     *  is caused by a response unsuccessful and trigger a retry.When the exception is an instance of
     *  {@code SdkResponseNonSuccessException}, it is considered necessary to retry. */
    private static final Predicate<Throwable> RESPONSE_NON_SUCCESS_RETRY_PREDICATE
            = (e) -> e instanceof SdkResponseNonSuccessException;

    /**
     * Construct a {@code FlowableCaller} instance.
     *
     * @param runBody                             The asynchronous operation body, executed when the Flowable is
     *                                            subscribed to,and returns the operation result.
     * @param retryTimes                          The number of retries upon failure, 0 indicates no automatic
     *                                            retries,negative values will be treated as 1 retry.
     * @param whenResponseNonSuccessRetry         Do we need to retry when the response to the request is unsuccessful
     *                                            {@code Response#isSuccess() == false}.
     * @param whenResponseNonSuccessFinalThrow    Deciding a Boolean marker when the response is ultimately
     *                                            unsuccessful, if it is {@code true}, an exception will be thrown
     *                                            in the end, otherwise it will not.
     * @param customRetryExceptionPredicate       A custom predicate used to determine if an exception should trigger
     *                                            a retry. If null, all exceptions will trigger a retry (if retries are
     *                                            configured).
     * @param customSubscriptionRegularConsumer   A custom consumer invoked upon successful subscription completion,
     *                                            used to handle successful results.
     * @param customSubscriptionExceptionConsumer A custom consumer invoked upon exception during subscription,
     *                                            used to handle errors.
     */
    public FlowableCaller(Supplier<R> runBody,
                          int retryTimes,
                          boolean whenResponseNonSuccessRetry,
                          boolean whenResponseNonSuccessFinalThrow,
                          Predicate<? super Throwable> customRetryExceptionPredicate,
                          Consumer<R> customSubscriptionRegularConsumer,
                          Consumer<Throwable> customSubscriptionExceptionConsumer) {

        Objects.requireNonNull(runBody, "sdk runBody");
        this.runBody = runBody;
        this.retryTimes = Math.max(retryTimes, 0);
        this.whenResponseNonSuccessRetry = whenResponseNonSuccessRetry;
        this.whenResponseNonSuccessFinalThrow = whenResponseNonSuccessFinalThrow;
        this.customRetryExceptionPredicate = customRetryExceptionPredicate;
        this.customSubscriptionRegularConsumer = customSubscriptionRegularConsumer;
        this.customSubscriptionExceptionConsumer = customSubscriptionExceptionConsumer;
        this.flowable = createFlowable();
    }

    /*** The {@link BackpressureStrategy} backpressure selection system cache key value for {@link Flowable}.*/
    public static final String BACKPRESSURE_STRATEGY_PROPERTY = "io.reactivex.rxjava3.core.BackpressureStrategy.item";

    /**
     * Create a {@link Flowable} based on the existing conditions.
     *
     * <p>The suppressed selection can rely on {@link System#getProperty} to obtain the
     * value of key {@link #BACKPRESSURE_STRATEGY_PROPERTY}.
     *
     * <p>During each retry, the custom assertion exception type is prioritized for matching.
     * If the former does not match, the default response failure match
     * {@link #RESPONSE_NON_SUCCESS_RETRY_PREDICATE} is used. When no custom assertion match
     * is provided, all exceptions are retried by default.
     *
     * @return The {@code Flowable} class that implements the
     * <a href="https://github.com/reactive-streams/reactive-streams-jvm">Reactive Streams</a> {@link Publisher}
     * * Pattern and offers factory methods, intermediate operators and the ability to consume reactive dataflows.
     */
    protected Flowable<R> createFlowable() {

        BackpressureStrategy backpressureStrategy;
        String property = System.getProperty(BACKPRESSURE_STRATEGY_PROPERTY);
        if (StringUtils.isBlank(property)) {
            backpressureStrategy = BackpressureStrategy.LATEST;
        } else {
            try {
                backpressureStrategy = BackpressureStrategy.valueOf(property);
            } catch (Exception e) {
                backpressureStrategy = BackpressureStrategy.LATEST;
            }
        }
        Flowable<R> flowable0 = Flowable.create(s -> {
            s.onNext(new RetryHelpSupplier().get());
            s.onComplete();
        }, backpressureStrategy);

        return flowable0.retry(retryTimes, throwable -> {
            boolean customRetryPredicateResult;
            boolean responseNonSuccessRetryPredicateResult = false;

            if (customRetryExceptionPredicate != null) {
                customRetryPredicateResult = customRetryExceptionPredicate.test(throwable);
            } else customRetryPredicateResult = true;

            if (!customRetryPredicateResult) {
                if (whenResponseNonSuccessRetry) {
                    responseNonSuccessRetryPredicateResult = RESPONSE_NON_SUCCESS_RETRY_PREDICATE.test(throwable);
                }
            }

            return customRetryPredicateResult || responseNonSuccessRetryPredicateResult;
        });
    }

    @Override
    public void run() {
        this.disposable = flowable.subscribe(getOnNext(), getOnError());
        if (disposeSync()) dispose();
    }

    /**
     * Return Boolean markers indicating whether to synchronize resource
     * release related operations.
     *
     * @return Boolean markers indicating.
     */
    protected boolean disposeSync() {
        return true;
    }

    /**
     * @return {@link OnNext}.
     * @see top.osjf.sdk.core.util.FlowableCaller.OnNext
     */
    protected OnNext getOnNext() {
        return new OnNext();
    }

    /**
     * @return {@link OnError}.
     * @see top.osjf.sdk.core.util.FlowableCaller.OnError
     */
    protected OnError getOnError() {
        return new OnError();
    }

    /* FlowableCaller builder static method */

    /**
     * A static method for creating a new auxiliary construct for {@link FlowableCaller}.
     * @param <R> Generic R represents the type returned by an operation, which must
     *            inherit from the {@link Response} class.
     * @return a new auxiliary construct.
     */
    public static <R extends Response> Builder<R> newBuilder() {
        return new Builder<>();
    }

    /* FlowableCaller void static method */

    /**
     * A static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody {@link #runBody}.
     * @param <R>     Generic R represents the type returned by an operation, which must
     *                inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody) {
        call(runBody, 0);
    }

    /**
     * A static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody    {@link #runBody}.
     * @param retryTimes {@link #retryTimes}.
     * @param <R>        Generic R represents the type returned by an operation, which must
     *                   inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes) {
        call(runBody, retryTimes, false);
    }

    /**
     * A static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody                     {@link #runBody}.
     * @param retryTimes                  {@link #retryTimes}.
     * @param whenResponseNonSuccessRetry {@link #whenResponseNonSuccessRetry}.
     * @param <R>                         Generic R represents the type returned by an operation, which must
     *                                    inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 boolean whenResponseNonSuccessRetry) {
        call(runBody, retryTimes, whenResponseNonSuccessRetry, false);
    }

    /**
     * A static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody                          {@link #runBody}.
     * @param retryTimes                       {@link #retryTimes}.
     * @param whenResponseNonSuccessRetry      {@link #whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow {@link #whenResponseNonSuccessFinalThrow}.
     * @param <R>                              Generic R represents the type returned by an operation, which must
     *                                         inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 boolean whenResponseNonSuccessRetry,
                                                 boolean whenResponseNonSuccessFinalThrow) {
        call(runBody, retryTimes, whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow,
                null);
    }

    /**
     * A static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody                          {@link #runBody}.
     * @param retryTimes                       {@link #retryTimes}.
     * @param whenResponseNonSuccessRetry      {@link #whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow {@link #whenResponseNonSuccessFinalThrow}.
     * @param customRetryExceptionPredicate    {@link #customRetryExceptionPredicate}.
     * @param <R>                              Generic R represents the type returned by an operation, which must
     *                                         inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 boolean whenResponseNonSuccessRetry,
                                                 boolean whenResponseNonSuccessFinalThrow,
                                                 Predicate<? super Throwable> customRetryExceptionPredicate) {
        call(runBody, retryTimes, whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow,
                customRetryExceptionPredicate, null,
                null);
    }

    /**
     * A static method for SDK calls using the API of {@code FlowableCaller}.
     *
     * @param runBody                             {@link #runBody}.
     * @param retryTimes                          {@link #retryTimes}.
     * @param whenResponseNonSuccessRetry         {@link #whenResponseNonSuccessRetry}.
     * @param whenResponseNonSuccessFinalThrow    {@link #whenResponseNonSuccessFinalThrow}.
     * @param customRetryExceptionPredicate       {@link #customRetryExceptionPredicate}.
     * @param customSubscriptionRegularConsumer   {@link #customSubscriptionRegularConsumer}.
     * @param customSubscriptionExceptionConsumer {@link #customSubscriptionExceptionConsumer}.
     * @param <R>                                 Generic R represents the type returned by an operation, which must
     *                                            inherit from the {@link Response} class.
     */
    public static <R extends Response> void call(Supplier<R> runBody,
                                                 int retryTimes,
                                                 boolean whenResponseNonSuccessRetry,
                                                 boolean whenResponseNonSuccessFinalThrow,
                                                 Predicate<? super Throwable> customRetryExceptionPredicate,
                                                 Consumer<R> customSubscriptionRegularConsumer,
                                                 Consumer<Throwable> customSubscriptionExceptionConsumer) {
        new FlowableCaller<>(runBody, retryTimes,
                whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow, customRetryExceptionPredicate,
                customSubscriptionRegularConsumer, customSubscriptionExceptionConsumer).run();
    }

    /*** It happened after {@link Flowable#subscribe(io.reactivex.rxjava3.functions.Consumer,
     *  io.reactivex.rxjava3.functions.Consumer)})} onNext.*/
    protected class OnNext implements io.reactivex.rxjava3.functions.Consumer<R> {

        @Override
        public void accept(R r) {
            if (customSubscriptionRegularConsumer != null) {
                customSubscriptionRegularConsumer.accept(r);
            }
        }
    }

    /* FlowableCaller help class */

    /**
     * The Builder class is used to build {@link FlowableCaller} instances.
     * Set various configuration parameters through chain calls, including execution body,
     * retry count, retry behavior in case of response failure, whether to ultimately throw
     * an exception in case of response failure Customize retry exception judgment logic,
     * as well as normal and abnormal subscription consumption logic.
     *
     * @param <R> Generic R represents the type returned by an operation, which must
     *            inherit from the {@link Response} class.
     */
    public static class Builder<R extends Response> {

        /*** {@link FlowableCaller#runBody}*/
        private Supplier<R> runBody;
        /*** {@link FlowableCaller#retryTimes}*/
        private int retryTimes;
        /*** {@link FlowableCaller#whenResponseNonSuccessRetry}*/
        private boolean whenResponseNonSuccessRetry;
        /*** {@link FlowableCaller#whenResponseNonSuccessFinalThrow}*/
        private boolean whenResponseNonSuccessFinalThrow;
        /*** {@link FlowableCaller#customRetryExceptionPredicate}*/
        private Predicate<? super Throwable> customRetryExceptionPredicate;
        /*** {@link FlowableCaller#customSubscriptionRegularConsumer}*/
        private Consumer<R> customSubscriptionRegularConsumer;
        /*** {@link FlowableCaller#customSubscriptionExceptionConsumer}*/
        private Consumer<Throwable> customSubscriptionExceptionConsumer;

        /**
         * Set a {@link #runBody} for {@link Builder}.
         *
         * @param runBody {@link FlowableCaller#runBody}
         * @return this.
         */
        public Builder<R> runBody(Supplier<R> runBody) {
            this.runBody = runBody;
            return this;
        }

        /**
         * Set a {@link #retryTimes} for {@link Builder}.
         *
         * @param retryTimes {@link FlowableCaller#retryTimes}
         * @return this.
         */
        public Builder<R> retryTimes(int retryTimes) {
            this.retryTimes = retryTimes;
            return this;
        }

        /**
         * Set {@code true} value to {@code whenResponseNonSuccessRetry} for {@link Builder}.
         *
         * @return this.
         */
        public Builder<R> whenResponseNonSuccessRetry() {
            this.whenResponseNonSuccessRetry = true;
            return this;
        }

        /**
         * Set {@code true} value to {@code whenResponseNonSuccessFinalThrow} for {@link Builder}.
         *
         * @return this.
         */
        public Builder<R> whenResponseNonSuccessFinalThrow() {
            this.whenResponseNonSuccessFinalThrow = true;
            return this;
        }

        /**
         * Set a {@link #customRetryExceptionPredicate} for {@link Builder}.
         *
         * @param customRetryExceptionPredicate {@link FlowableCaller#customRetryExceptionPredicate}
         * @return this.
         */
        public Builder<R> customRetryExceptionPredicate(Predicate<? super Throwable> customRetryExceptionPredicate) {
            this.customRetryExceptionPredicate = customRetryExceptionPredicate;
            return this;
        }

        /**
         * Set a {@link #customSubscriptionRegularConsumer} for {@link Builder}.
         *
         * @param customSubscriptionRegularConsumer {@link FlowableCaller#customSubscriptionRegularConsumer}
         * @return this.
         */
        public Builder<R> customSubscriptionRegularConsumer(Consumer<R> customSubscriptionRegularConsumer) {
            this.customSubscriptionRegularConsumer = customSubscriptionRegularConsumer;
            return this;
        }

        /**
         * Set a {@link #customSubscriptionExceptionConsumer} for {@link Builder}.
         *
         * @param customSubscriptionExceptionConsumer {@link FlowableCaller#customSubscriptionRegularConsumer}
         * @return this.
         */
        public Builder<R> customSubscriptionExceptionConsumer(Consumer<Throwable> customSubscriptionExceptionConsumer) {
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
                    (runBody, retryTimes, whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow,
                            customRetryExceptionPredicate, customSubscriptionRegularConsumer,
                            customSubscriptionExceptionConsumer);
        }
    }

    /*** It happened after {@link Flowable#subscribe(io.reactivex.rxjava3.functions.Consumer,
     *  io.reactivex.rxjava3.functions.Consumer)})} onError.*/
    protected class OnError implements io.reactivex.rxjava3.functions.Consumer<Throwable> {

        @Override
        public void accept(Throwable e) {
            if (customSubscriptionExceptionConsumer != null) {
                customSubscriptionExceptionConsumer.accept(e);
            }
        }
    }

    /**
     * Check if the disposable object has not been disposed of yet, and if so,
     * proceed with the disposal operation.
     * This is to ensure that resources can be released correctly even in the event of
     * an exception, avoiding resource leakage.
     */
    public void dispose() {

        if (!disposable.isDisposed()) {
            disposable.dispose();
            LOGGER.log(Level.INFO, "Resource release completed");
        } else {
            LOGGER.log(Level.INFO, "The resource has been automatically released");
        }
    }

    /*** The function of this class is to determine the success or failure of the response result,
     *  as well as to calculate the number of times exceptions are thrown when failures are allowed
     *  (exceptions are only thrown for retry).*/
    private class RetryHelpSupplier implements Supplier<R> {

        @Override
        public R get() {
            R response = runBody.get();
            if (!response.isSuccess()) {
                if (whenResponseNonSuccessRetry) {
                    if (retryTimes > 0) {
                        retryTimes--;
                        //Is throwing an exception here for exception retry.
                        throw new SdkResponseNonSuccessException();
                    } else {
                        finalResolve(response);
                    }
                } else {
                    finalResolve(response);
                }
            }
            return response;
        }

        /**
         * Perform final processing based on parameter {@link #whenResponseNonSuccessFinalThrow}.
         * @param response Sdk response.
         */
        void finalResolve(R response) {
            if (whenResponseNonSuccessFinalThrow) {
                throw new SdkResponseNonSuccessException(response.getMessage());
            }
        }
    }
}
