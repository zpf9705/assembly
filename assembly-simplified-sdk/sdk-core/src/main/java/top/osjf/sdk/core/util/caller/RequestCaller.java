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

import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.ReflectUtil;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The {@code RequestCaller} class is designed to handle request execution. It supports
 * configuring request execution options through the {@code CallOptions} annotation, such
 * as retry times, retry intervals, custom exception predicate logic, retry strategies when
 * the response is not successful, and whether to finally throw exceptions.
 *
 * <p>This class provides two constructors: a no-argument constructor that uses a default
 * instantiation function (to instantiate objects via reflection) and a constructor with a
 * {@code Function<Class<?>, Object>} parameter that allows users to customize instantiation logic.
 *
 * <p>The core method in this class is {@link #resolveRequestExecuteWithOptions(Supplier, CallOptions)},
 * which accepts a request object (provided through a {@code Supplier}) and a {@code CallOptions}
 * annotation as parameters and final call method {@code resolveRequestExecuteWithOptions(Supplier,
 * int, long, ThrowablePredicate, boolean, boolean, Callback)} (this method is not elaborated here
 * as a commonly used combination method in this package tool).It executes the request according
 * to the execution options configured in the annotation and returns the response object. If a {@code Callback}
 * class (callbackClass) is specified in the {@code CallOptions} annotation, the acquisition of the response
 * and exception handling will be completed within the callback class, and the method will return null at
 * this time; otherwise, the method will directly return the response object and throw exceptions directly
 * when they occur.
 *
 * <p>The class also provides some auxiliary methods for obtaining configured execution option values
 * from the {@code CallOptions} annotation, such as retry times, retry intervals, and so on.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class RequestCaller {

    /**
     * {@code Function} function that uses a class object to create an instance object.
     */
    private final Function<Class<?>, Object> receiveInstanceFunc;

    /**
     * Initialize an empty constructor of {@code RequestCaller} using reflection
     * to call the class object {@link Function}.
     */
    public RequestCaller() {
        receiveInstanceFunc = ReflectUtil::instantiates;
    }

    /**
     * The construction method of a {@code Function} function that uses a class
     * object to create an instance object.
     *
     * @param receiveInstanceFunc {@code Function} function that uses a class
     *                            object to create an instance object.
     */
    public RequestCaller(@NotNull Function<Class<?>, Object> receiveInstanceFunc) {
        this.receiveInstanceFunc = receiveInstanceFunc;
    }

    /**
     * Refer to {@link #resolveRequestExecuteWithOptions(Supplier, CallOptions)}
     *
     * @param request     input {@code Request} obj.
     * @param host        the real server hostname.
     * @param callOptions {@code CallOptions} annotation.
     * @return The {@code Response} object obtained from the response
     * returns empty when {@link CallOptions#callbackClass()} exists.
     */
    @Nullable
    public Response resolveRequestExecuteWithOptions(Request<?> request, String host, CallOptions callOptions) {
        return resolveRequestExecuteWithOptions(() -> request.execute(host), callOptions);
    }

    /**
     * Processing the execution of request {@code Request} results in the option
     * of {@code Response} object ,annotated with {@code CallOptions}.
     *
     * @param supplier    The provider function of the {@code Response} object.
     * @param callOptions {@code CallOptions} annotation.
     * @return The {@code Response} object obtained from the response
     * returns empty when {@link CallOptions#callbackClass()} exists.
     */
    @Nullable
    public Response resolveRequestExecuteWithOptions(Supplier<Response> supplier, CallOptions callOptions) {
        int retryTimes = getRetryTimesByOptions(callOptions);
        long retryIntervalMilliseconds = getRetryIntervalMillisecondsByOptions(callOptions);
        ThrowablePredicate throwablePredicate = getThrowablePredicateByOptions(callOptions);
        boolean whenResponseNonSuccessRetry = getWhenResponseNonSuccessRetryOptions(callOptions);
        boolean whenResponseNonSuccessFinalThrow = getWhenResponseNonSuccessFinalThrowByOptions(callOptions);
        Callback callback = getCallbackByOptions(callOptions);
        return resolveRequestExecuteWithOptions(supplier, retryTimes, retryIntervalMilliseconds,
                throwablePredicate, whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow, callback);
    }

    /**
     * The processing logic adopts some functional classes related to {@code io.reactivex.rxjava3}
     * encapsulated in this package.
     * <p>
     * There are the following return situations for the returned
     * {@code Response} object:
     * <ul>
     *     <li>When providing {@link Callback}, the retrieval and exception handling
     *     of {@code Response} are performed in the callback class.</li>
     *     <li>When {@link Callback}, is not provided, the retrieval
     *     of {@code Response} will be returned directly through this method, and an
     *     exception will be thrown directly.</li>
     * </ul>
     *
     * @param supplier                         The provider function of the {@code Response} object.
     * @param retryTimes                       The retry times.
     * @param retryIntervalMilliseconds        The retry interval milliseconds.
     * @param throwablePredicate               The Instance {@code ThrowablePredicate}.
     * @param whenResponseNonSuccessRetry      When response nonSuccess retry boolean mark.
     * @param whenResponseNonSuccessFinalThrow When response nonSuccess final throw exception mark.
     * @param callback                         The Instance {@code Callback}.
     * @return The {@code Response} object obtained from the response
     * returns empty when {@link CallOptions#callbackClass()} exists.
     */
    @Nullable
    public Response resolveRequestExecuteWithOptions(Supplier<Response> supplier, int retryTimes,
                                                     long retryIntervalMilliseconds,
                                                     @Nullable ThrowablePredicate throwablePredicate,
                                                     boolean whenResponseNonSuccessRetry,
                                                     boolean whenResponseNonSuccessFinalThrow,
                                                     @Nullable Callback callback) {
        FlowableCallerBuilder<Response> builder = FlowableCallerBuilder.newBuilder()
                .runBody(supplier)
                .retryTimes(retryTimes)
                .retryIntervalMilliseconds(retryIntervalMilliseconds)
                .customRetryExceptionPredicate(throwablePredicate);
        if (whenResponseNonSuccessRetry) builder.whenResponseNonSuccessRetry();
        if (whenResponseNonSuccessFinalThrow) builder.whenResponseNonSuccessFinalThrow();
        if (callback != null) {
            builder.customSubscriptionRegularConsumer(callback::success);
            builder.customSubscriptionExceptionConsumer(callback::exception);
            builder.build().run();
            return null;
        }
        return builder.buildBlock().get();
    }

    /**
     * Get the retry times by annotation {@code CallOptions}
     *
     * @param callOptions {@code CallOptions} annotation.
     * @return The retry times.
     */
    protected int getRetryTimesByOptions(CallOptions callOptions) {
        return callOptions.retryTimes();
    }

    /**
     * Get the retry interval milliseconds by annotation {@code CallOptions}
     *
     * @param callOptions {@code CallOptions} annotation.
     * @return The retry interval milliseconds.
     */
    protected long getRetryIntervalMillisecondsByOptions(CallOptions callOptions) {
        return callOptions.retryIntervalMilliseconds();
    }

    /**
     * Get an Instance {@code ThrowablePredicate} by annotation {@code CallOptions}
     *
     * @param callOptions {@code CallOptions} annotation.
     * @return The Instance {@code ThrowablePredicate}.
     */
    @Nullable
    protected ThrowablePredicate getThrowablePredicateByOptions(CallOptions callOptions) {
        Class<? extends ThrowablePredicate> throwablePredicateClass = callOptions.retryThrowablePredicateClass();
        if (throwablePredicateClass == ThrowablePredicate.class) {
            return null;
        }
        return (ThrowablePredicate) receiveInstanceFunc.apply(callOptions.retryThrowablePredicateClass());
    }

    /**
     * Get when response nonSuccess retry boolean mark by annotation {@code CallOptions}
     *
     * @param callOptions {@code CallOptions} annotation.
     * @return When response nonSuccess retry boolean mark.
     */
    protected boolean getWhenResponseNonSuccessRetryOptions(CallOptions callOptions) {
        return callOptions.whenResponseNonSuccessRetry();
    }

    /**
     * Get when response nonSuccess final throw exception mark by annotation {@code CallOptions}
     *
     * @param callOptions {@code CallOptions} annotation.
     * @return When response nonSuccess final throw exception mark.
     */
    protected boolean getWhenResponseNonSuccessFinalThrowByOptions(CallOptions callOptions) {
        return callOptions.whenResponseNonSuccessFinalThrow();
    }

    /**
     * Get an Instance {@code Callback} by annotation {@code CallOptions}
     *
     * @param callOptions {@code CallOptions} annotation.
     * @return The Instance {@code Callback}.
     */
    @Nullable
    protected Callback getCallbackByOptions(CallOptions callOptions) {
        Class<? extends Callback> callbackClass = callOptions.callbackClass();
        if (callbackClass == Callback.class) {
            return null;
        }
        return (Callback) receiveInstanceFunc.apply(callbackClass);
    }
}
