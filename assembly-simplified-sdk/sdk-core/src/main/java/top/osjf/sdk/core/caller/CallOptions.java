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

import top.osjf.sdk.core.support.NotNull;

import java.lang.annotation.*;
import java.util.concurrent.Executor;

/**
 * Call option annotation, used to configure the calling behavior of methods
 * or classes.
 *
 * <p>This annotation can be used on classes or methods to provide detailed
 * configuration on how to perform specific calls, such as network requests,
 * database operations, etc.
 * It allows developers to specify options such as retry times, retry intervals,
 * retry conditions, and callback handling
 *
 * <p>When this annotation is applied to a method, it will override any values
 * of the same annotation attribute applied to the class containing the method.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see RequestCaller
 * @since 1.0.2
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CallOptions {

    /**
     * Get the number of retries.
     *
     * <p>The number of times the call will be attempted to be re-executed when
     * it fails. The default value is 1, and if the conditions are met, retry again.
     *
     * @return The number of retries must be a non-negative integer.
     */
    int retryTimes() default 1;

    /**
     * Get retry interval (in milliseconds).
     *
     * <p>The waiting time between each retry.
     * The default value is 1000 milliseconds (i.e. 1 second).
     *
     * @return retry interval, in milliseconds.
     */
    long retryIntervalMilliseconds() default 1000;

    /**
     * Retrieve the exception condition class used to determine whether a
     * retry is necessary.
     *
     * <p>This is a class that implements the {@code ThrowablePredicate} interface to
     * determine which types of exceptions should trigger retries.
     * <p>
     * Developers can define more specific retry conditions by providing custom
     * {@code ThrowablePredicate} implementations.
     *
     * <p>The default type is {@link DefaultThrowablePredicate}, and marking
     * this type indicates that {@code ThrowablePredicate} is not provided.
     *
     * @return is an exception condition class used to determine whether
     * a retry is necessary.
     */
    Class<? extends ThrowablePredicate> retryThrowablePredicateClass() default DefaultThrowablePredicate.class;

    /**
     * Whether to retry when the response is unsuccessful.
     * <p>
     * If the status code or content of the response indicates that the call
     * was unsuccessful (even if no exception was thrown), should we try to retry.
     * The default value is {@literal true}, which means to retry when the response
     * is unsuccessful.
     *
     * @return The flag indicating whether to retry when the response is unsuccessful.
     */
    boolean whenResponseNonSuccessRetry() default true;

    /**
     * Whether to throw an exception when the response is unsuccessful and
     * the retry attempts are exhausted.
     *
     * <p>If the response is unsuccessful and all configured retry attempts
     * have been made, should an exception be thrown.
     * The default value is {@literal true}, indicating that an exception is
     * thrown in this situation.
     *
     * @return is a flag indicating whether an exception is thrown when the
     * response is unsuccessful and the retry attempts have been exhausted.
     */
    boolean whenResponseNonSuccessFinalThrow() default true;

    /**
     * Get {@code Callback} processing class.
     *
     * <p>This is a class that implements the {@code Callback}  interface,
     * used to handle callback logic when calls succeed or fail.
     *
     * <p>Developers can define more specific callback behaviors by
     * providing custom {@code Callback} implementations.
     *
     * <p>The default type is {@link DefaultCallback}, and marking
     * this type indicates that {@code Callback} is not provided.
     *
     * @return is a class used for callback processing.
     */
    Class<? extends Callback> callbackClass() default DefaultCallback.class;

    /**
     * Only use the {@code Boolean} tag of the provided {@code Callback}.
     *
     * <p>The default is {@code false}, which means that the {@code Callback}
     * type of this annotation and the other provided {@code Callback} are used
     * together as the execution class of the callback; If set to {@code true},
     * only the provided {@code Callback} will be used as the class for executing
     * callback functions, and the fixed {@link #callbackClass()} type set in
     * the annotation will no longer be valid, provided that the provided callback
     * class exists. If the provided callback class is empty, the fixed
     * {@link #callbackClass()} type set in this annotation will still be valid.
     * This setting is usually used in scenarios where {@code Callback} classes are
     * dynamically set.
     *
     * <p>The implementation logic can be viewed as {@code RequestCaller#fusionCallbacks}.
     *
     * @return only use {@code Boolean} tags that provide callbacks.
     */
    boolean onlyUseProvidedCallback() default false;

    /**
     * Specify the thread pool type for asynchronous execution.
     *
     * <p>If the type of thread pool is specified, asynchronous
     * methods will be executed, and if not specified, synchronous
     * execution will still occur.
     *
     * <p>This type of setting can be applied according to the scenario.
     * When there is no asynchronous support for external calls, this
     * type can be set, and internal calls will be executed asynchronously.
     * Of course, developers should also be prepared for asynchronous execution.
     *
     * <p>In the parsing of method {@link RequestCaller#getExecutorByOptions}
     * , the {@code Executor} type you provide needs to have the configuration
     * information about the thread pool set in advance. This method only
     * initializes through empty constructs.
     *
     * <p>The default type is {@link DefaultExecutor}, and marking
     * this type indicates that {@code Executor} is not provided.
     *
     * @return The type of {@code Executor} provided for asynchronous
     * execution.
     */
    Class<? extends Executor> executorClass() default DefaultExecutor.class;

    /**
     * Default annotation placeholder, default {@code Callback} for no operation.
     */
    final class DefaultCallback implements Callback {
    }

    /**
     * Default annotation placeholder, default {@code ThrowablePredicate} for no operation.
     */
    final class DefaultThrowablePredicate implements ThrowablePredicate {
        @Override
        public boolean test(Throwable throwable) {
            return false;
        }
    }

    /**
     * Default annotation placeholder, default {@code Executor} for no operation.
     */
    final class DefaultExecutor implements Executor {
        @Override
        public void execute(@NotNull Runnable command) {
        }
    }
}
