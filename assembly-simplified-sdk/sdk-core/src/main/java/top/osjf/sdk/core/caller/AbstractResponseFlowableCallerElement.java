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

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Abstract {@link ResponseFlowableCallerElement}, storing common methods and related properties.
 *
 * @param <R> The type of response result must be the Response class or its subclass.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class AbstractResponseFlowableCallerElement<R extends Response> implements
        ResponseFlowableCallerElement<R> {

    /*** JDK logger.*/
    protected final Logger LOGGER = Logger.getLogger(getClass().getName());

    /*** The provider of the running entity, the subject used to generate or execute tasks. */
    @NotNull
    private final Supplier<R> runBody;

    /*** The maximum number of retries to attempt to re-execute a task after it has failed. */
    private final int retryTimes;

    /*** The millisecond value of the retry trigger delay interval. */
    private final long retryIntervalMilliseconds;

    /*** The flag indicating whether to retry when the response is unsuccessful. If true, attempt
     *  to retry when the response does not meet the success criteria. */
    private final boolean whenResponseNonSuccessRetry;

    /*** When the response retry is still unsuccessful, whether to throw an exception
     * {@link SdkResponseNonSuccessException} flag. */
    private final boolean whenResponseNonSuccessFinalThrow;

    /*** Custom retry exception predicate used to determine which exception types should trigger
     * the retry mechanism. */
    @Nullable
    private final Predicate<? super Throwable> customRetryExceptionPredicate;

    /**
     * A construction method for collecting relevant information on response to weight tests and unsuccessful states.
     *
     * @param runBody                          The asynchronous operation body, executed when the Flowable is
     *                                         subscribed to,and returns the operation result.
     * @param retryTimes                       The number of retries upon failure, 0 indicates no automatic
     *                                         retries,negative values will be treated as 1 retry.
     * @param retryIntervalMilliseconds        The millisecond value of the retry interval time.
     * @param whenResponseNonSuccessRetry      Do we need to retry when the response to the request is unsuccessful
     *                                         {@code Response#isSuccess() == false}.
     * @param whenResponseNonSuccessFinalThrow When the response is ultimately unsuccessful, should an exception be
     *                                         thrown {@code Response#isSuccess() == false}..
     * @param customRetryExceptionPredicate    A custom predicate used to determine if an exception should trigger
     *                                         a retry. If null, all exceptions will trigger a retry (if retries are
     *                                         configured).
     * @throws NullPointerException if input runBody is {@literal null}.
     */
    public AbstractResponseFlowableCallerElement(@NotNull Supplier<R> runBody,
                                                 int retryTimes,
                                                 long retryIntervalMilliseconds,
                                                 boolean whenResponseNonSuccessRetry,
                                                 boolean whenResponseNonSuccessFinalThrow,
                                                 @Nullable Predicate<? super Throwable> customRetryExceptionPredicate) {
        Objects.requireNonNull(runBody, "runBody == null");
        this.runBody = runBody;
        this.retryTimes = Math.max(retryTimes, 0);
        this.retryIntervalMilliseconds = Math.max(retryIntervalMilliseconds, 0);
        if (this.retryTimes == 0 && this.retryIntervalMilliseconds > 0) {
            LOGGER.warning
                    ("When there is no retry, providing the retry interval parameter will be meaningless.");
        }
        this.whenResponseNonSuccessRetry = whenResponseNonSuccessRetry;
        this.whenResponseNonSuccessFinalThrow = whenResponseNonSuccessFinalThrow;
        this.customRetryExceptionPredicate = customRetryExceptionPredicate;
    }

    @Override
    @NotNull
    public Supplier<R> getRunBody() {
        return runBody;
    }

    @Override
    public int getRetryTimes() {
        return retryTimes;
    }

    @Override
    public long getRetryIntervalMilliseconds() {
        return retryIntervalMilliseconds;
    }

    @Override
    public boolean isWhenResponseNonSuccessRetry() {
        return whenResponseNonSuccessRetry;
    }

    @Override
    public boolean isWhenResponseNonSuccessFinalThrow() {
        return whenResponseNonSuccessFinalThrow;
    }

    @Override
    @Nullable
    public Predicate<? super Throwable> getCustomRetryExceptionPredicate() {
        return customRetryExceptionPredicate;
    }
}
