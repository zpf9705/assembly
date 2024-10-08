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

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import org.reactivestreams.Publisher;
import top.osjf.sdk.core.exception.SdkResponseNonSuccessException;
import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.util.StringUtils;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The {@code AbstractFlowableCaller} class is an abstract class that extends the
 * {@code AbstractResponseFlowableCallerElement} class and uses the generic R,
 * Among them, R is a subclass or implementation class of Response.
 * This class is mainly used to handle Flowable objects, representing an observable
 * data flow,Zero or multiple data items can be sent asynchronously.
 *
 * <p>This class defines some key attributes and behaviors, including policy keys for handling
 * backpressure, default retry predicates,And a Flowable object. The backpressure strategy
 * key is used to configure the backpressure processing mechanism of Flowable, while the default ret
 * ry predicate is used to determine whether a retry is triggered due to an unsuccessful response.
 * When the exception is an instance of {@code SdkResponseNonSuccessException}, it is considered
 * necessary to retry.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class AbstractFlowableCaller<R extends Response> extends AbstractResponseFlowableCallerElement<R> {

    /*** The {@link BackpressureStrategy} backpressure selection system cache key value for {@link Flowable}.*/
    public static final String BACKPRESSURE_STRATEGY_PROPERTY = "io.reactivex.rxjava3.core.BackpressureStrategy.item";

    /*** The default response unsuccessful retry predicate is used to determine whether an exception
     *  is caused by a response unsuccessful and trigger a retry.When the exception is an instance of
     *  {@code SdkResponseNonSuccessException}, it is considered necessary to retry. */
    private static final Predicate<Throwable> RESPONSE_NON_SUCCESS_RETRY_PREDICATE
            = (e) -> e instanceof SdkResponseNonSuccessException;

    /*** Flowable object, representing an observable data flow, can asynchronously emit zero or
     *  more data items. */
    private Flowable<R> flowable;

    /* AbstractResponseFlowableCallerElement */
    public AbstractFlowableCaller(Supplier<R> runBody, int retryTimes,
                                  long retryIntervalMilliseconds,
                                  boolean whenResponseNonSuccessRetry,
                                  boolean whenResponseNonSuccessFinalThrow,
                                  Predicate<? super Throwable> customRetryExceptionPredicate) {
        super(runBody, retryTimes, retryIntervalMilliseconds, whenResponseNonSuccessRetry,
                whenResponseNonSuccessFinalThrow, customRetryExceptionPredicate);
    }

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
        int retryTimes = getRetryTimes();
        Flowable<R> flowable0 = Flowable.create(s -> {
            s.onNext(new RetryHelpSupplier(retryTimes).get());
            s.onComplete();
        }, backpressureStrategy);

        return flowable0.retry(retryTimes, buildRetryPredicate());
    }

    /**
     * Build a retry exception checker.
     *
     * @return a retry exception checker form {@link io.reactivex.rxjava3.functions.Predicate}.
     */
    protected io.reactivex.rxjava3.functions.Predicate<Throwable> buildRetryPredicate() {
        return e -> {
            boolean customRetryPredicateResult;
            boolean responseNonSuccessRetryPredicateResult = false;

            Predicate<? super Throwable> customRetryExceptionPredicate = getCustomRetryExceptionPredicate();
            if (getCustomRetryExceptionPredicate() != null) {
                customRetryPredicateResult = customRetryExceptionPredicate.test(e);
            } else customRetryPredicateResult = true;

            if (!customRetryPredicateResult) {
                if (isWhenResponseNonSuccessRetry()) {
                    responseNonSuccessRetryPredicateResult = RESPONSE_NON_SUCCESS_RETRY_PREDICATE.test(e);
                }
            }

            boolean finalResult = customRetryPredicateResult || responseNonSuccessRetryPredicateResult;
            if (finalResult) {
                long retryIntervalMilliseconds = getRetryIntervalMilliseconds();
                if (retryIntervalMilliseconds > 0) {
                    try {
                        Thread.sleep(retryIntervalMilliseconds);
                    } catch (InterruptedException ignored) {
                        //When the thread sleep is interrupted, a retry is triggered directly.
                    }
                }
            }
            return finalResult;
        };
    }

    /**
     * Retrieve the operation object of the stream and create it when it does not exist.
     *
     * @return operation object of the stream.
     */
    public Flowable<R> getFlowable() {
        if (flowable == null) {
            this.flowable = createFlowable();
        }
        return flowable;
    }

    /*** The function of this class is to determine the success or failure of the response result,
     *  as well as to calculate the number of times exceptions are thrown when failures are allowed
     *  (exceptions are only thrown for retry).*/
    private class RetryHelpSupplier implements Supplier<R> {
        private int retryTimes;

        public RetryHelpSupplier(int retryTimes) {
            this.retryTimes = retryTimes;
        }

        @Override
        public R get() {
            R response = getRunBody().get();
            if (!response.isSuccess()) {
                if (isWhenResponseNonSuccessRetry()) {
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
         * Perform final processing based on parameter {@link #isWhenResponseNonSuccessFinalThrow()}.
         *
         * @param response Sdk response.
         */
        void finalResolve(R response) {
            if (isWhenResponseNonSuccessFinalThrow()) {
                throw new SdkResponseNonSuccessException(response.getMessage());
            }
        }
    }
}
