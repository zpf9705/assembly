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
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;

import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The {@code ResponseFlowableCallerElement} interface defines a specification for configuring
 * and executing responsive flow call elements.
 *
 * <p>This interface contains a series of methods for obtaining execution entities (such as
 * task generators or executors), retry policies (such as maximum retry times, retry intervals, etc.)
 * And the behavior when the response is unsuccessful (such as whether to retry, whether to
 * throw an exception, etc.). In addition, a custom retry exception predicate is provided,
 * Used to determine which types of exceptions should trigger a retry mechanism.
 *
 * @param <R> The type of response result must be the Response class or its subclass.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface ResponseFlowableCallerElement<R extends Response> {

    /**
     * Return the provider of the running entity, the subject used to generate or execute tasks.
     *
     * @return running entity.
     */
    @NotNull
    Supplier<R> getRunBody();

    /**
     * Return the maximum number of retries to attempt to re execute a task after it has failed.
     *
     * @return number of retries.
     */
    int getRetryTimes();

    /**
     * Return the value of the millisecond interval between retries.
     *
     * @return millisecond interval between retries.
     */
    long getRetryIntervalMilliseconds();

    /**
     * Return the flag indicating whether to retry when the response is unsuccessful.
     * If true, attempt to retry when the response does not meet the success criteria.
     *
     * @return the flag indicating whether to retry when the response is unsuccessful.
     */
    boolean isWhenResponseNonSuccessRetry();

    /**
     * Return do we need to throw an exception boolean symbol when the latest response
     * result is unsuccessful.
     *
     * @return throw an exception boolean symbol when the latest response
     * result is unsuccessful.
     */
    boolean isWhenResponseNonSuccessFinalThrow();

    /**
     * Return custom retry exception predicate used to determine which exception types should
     * trigger the retry mechanism.
     *
     * @return custom retry exception predicate.
     */
    @Nullable
    Predicate<? super Throwable> getCustomRetryExceptionPredicate();
}
