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
import top.osjf.sdk.core.exception.SdkCallerException;
import top.osjf.sdk.core.exception.SdkResponseNonSuccessException;
import top.osjf.sdk.core.process.Response;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
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

    private static final Logger LOGGER = Logger.getLogger(FlowableCaller.class.getName());

    /*** The provider of the running entity, the subject used to generate or execute tasks. */
    private final Supplier<R> runBody;

    /*** The maximum number of retries to attempt to re execute a task after it has failed. */
    private int retryTimes;

    /*** The flag indicating whether to retry when the response is unsuccessful. If true, attempt
     *  to retry when the response does not meet the success criteria. */
    private final boolean whenResponseNonSuccessRetry;

    /*** Custom retry exception predicate used to determine which exception types should trigger
     * the retry mechanism. */
    private final Predicate<? super Throwable> customRetryExceptionPredicate;

    /*** Customized subscription for regular consumers, used to handle normal response results. */
    private final Consumer<R> customSubscriptionRegularConsumer;

    /*** Custom subscription exception consumers, used to handle exceptions that occur during the
     * subscription process. */
    private final Consumer<Throwable> customSubscriptionExceptionConsumer;

    /*** Flowable object, representing an observable data flow, can asynchronously emit zero or
     *  more data items. */
    private final Flowable<R> flowable;

    /*** Counter, used to control the synchronization of certain operations, may be used here to
     * wait for the completion of an event or condition. */
    private final CountDownLatch count = new CountDownLatch(1);

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
                          Predicate<? super Throwable> customRetryExceptionPredicate,
                          Consumer<R> customSubscriptionRegularConsumer,
                          Consumer<Throwable> customSubscriptionExceptionConsumer) {

        Objects.requireNonNull(runBody, "sdk runBody");
        this.runBody = runBody;
        this.retryTimes = Math.max(retryTimes, 0);
        this.whenResponseNonSuccessRetry = whenResponseNonSuccessRetry;
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

        //Execute custom consumers at a specified stage and subtract the current
        // thread count after execution to wait for resource cleanup in the future.

        Disposable disposable = flowable.subscribe(r -> {
            if (customSubscriptionRegularConsumer != null) {
                try {
                    customSubscriptionRegularConsumer.accept(r);
                } finally {
                    count.countDown();
                }
            } else count.countDown();
        }, throwable -> {
            if (customSubscriptionExceptionConsumer != null) {
                try {
                    customSubscriptionExceptionConsumer.accept(throwable);
                } finally {
                    count.countDown();
                }
            } else {
                count.countDown();
                throw new SdkCallerException(throwable);
            }
        });


        try {
            /*
             * Waiting for the designated consumer to complete the execution is to ensure
             * the smooth release of resources occupied by the subscription relationship in the future.
             * */
            count.await();
        } catch (InterruptedException e) {

            // Restoring the interrupted state is important!
            Thread.currentThread().interrupt();

            LOGGER.log(Level.INFO, "Interrupted while waiting for count, continuing execution...", e);
        } finally {

            /*
             * Check if the disposable object has not been disposed of yet, and if so,
             * proceed with the disposal operation
             * This is to ensure that resources can be released correctly even in the event of
             * an exception, avoiding resource leakage
             * */
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
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
                        throw new SdkResponseNonSuccessException();
                    }
                }
            }
            return response;
        }
    }
}
