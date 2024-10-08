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

import top.osjf.sdk.core.process.Response;

import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * Abstract {@link ResponseFlowableCallerElement}, storing common methods and related properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class AbstractResponseFlowableCallerElement<R extends Response> implements
        ResponseFlowableCallerElement<R> {

    protected final Logger LOGGER = Logger.getLogger(getClass().getName());

    /*** {@link ResponseFlowableCallerElement#getRunBody()} */
    private final Supplier<R> runBody;

    /*** {@link ResponseFlowableCallerElement#getRetryTimes()} */
    private final int retryTimes;

    /*** {@link ResponseFlowableCallerElement#getRetryIntervalMilliseconds()} */
    private final long retryIntervalMilliseconds;

    /*** {@link ResponseFlowableCallerElement#isWhenResponseNonSuccessRetry()} */
    private final boolean whenResponseNonSuccessRetry;

    /*** {@link ResponseFlowableCallerElement#isWhenResponseNonSuccessFinalThrow()} */
    private final boolean whenResponseNonSuccessFinalThrow;

    /*** {@link ResponseFlowableCallerElement#getCustomRetryExceptionPredicate()} */
    private final Predicate<? super Throwable> customRetryExceptionPredicate;

    public AbstractResponseFlowableCallerElement(Supplier<R> runBody,
                                                 int retryTimes,
                                                 long retryIntervalMilliseconds,
                                                 boolean whenResponseNonSuccessRetry,
                                                 boolean whenResponseNonSuccessFinalThrow,
                                                 Predicate<? super Throwable> customRetryExceptionPredicate) {
        Objects.requireNonNull(runBody, "runBody");
        Objects.requireNonNull(runBody, "sdk runBody");
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
    public Predicate<? super Throwable> getCustomRetryExceptionPredicate() {
        return customRetryExceptionPredicate;
    }
}
