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

import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.SdkEnum;
import top.osjf.sdk.core.support.LoadOrder;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.CollectionUtils;
import top.osjf.sdk.core.util.ReflectUtil;
import top.osjf.sdk.core.util.SynchronizedWeakHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
 * <p>The core method in this class is {@link #resolveRequestExecuteWithOptions(Supplier, String, CallOptions, List)},
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
 * <p>The following methods only provide a synchronous running mechanism. If asynchronous correlation
 * is required, please implement it yourself.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class RequestCaller {

    /**
     * This class involves caching maps for class instantiation.
     */
    private static final Map<String, Object> OBJECT_CACHE = new SynchronizedWeakHashMap<>();

    /**
     * Please refer to {@code resolveRequestExecuteWithOptions(Supplier, CallOptions)}
     * for the execution logic.
     *
     * @param request     input {@code Request} obj.
     * @param host        the real server hostname.
     * @param callOptions {@code CallOptions} annotation.
     * @param callbacks   the {@code Callback} instances.
     * @return The {@code Response} object obtained from the response
     * returns empty when {@link CallOptions#callbackClass()} exists.
     * @throws NullPointerException if input request or {@code CallOptions} is {@literal null}.
     */
    @Nullable
    public Response resolveRequestExecuteWithOptions(@NotNull Request<?> request, String host,
                                                     @NotNull CallOptions callOptions,
                                                     @Nullable List<Callback> callbacks) {

        return resolveRequestExecuteWithOptions
                (() -> request.execute(host), request.matchSdkEnum().name(), callOptions, callbacks);
    }

    /**
     * Processing the execution of request {@code Request} results in the option
     * of {@code Response} object ,annotated with {@code CallOptions}.
     * <p>
     * Please refer to {@code resolveRequestExecuteWithOptions
     * (Supplier, int, long, ThrowablePredicate, boolean, boolean, Callback)} for the
     * execution logic.
     *
     * @param supplier    the provider function of the {@code Response} object.
     * @param name        the sdk name,as see {@link SdkEnum#name()}.
     * @param callOptions {@code CallOptions} annotation.
     * @param callbacks   the {@code Callback} instances.
     * @return The {@code Response} object obtained from the response
     * returns empty when {@link CallOptions#callbackClass()} exists.
     * @throws NullPointerException if input request or {@code CallOptions} is {@literal null}.
     */
    @Nullable
    public Response resolveRequestExecuteWithOptions(@NotNull Supplier<Response> supplier,
                                                     @NotNull String name,
                                                     @NotNull CallOptions callOptions,
                                                     @Nullable List<Callback> callbacks) {
        int retryTimes = getRetryTimesByOptions(callOptions);
        long retryIntervalMilliseconds = getRetryIntervalMillisecondsByOptions(callOptions);
        ThrowablePredicate throwablePredicate = getThrowablePredicateByOptions(name, callOptions);
        boolean whenResponseNonSuccessRetry = getWhenResponseNonSuccessRetryOptions(callOptions);
        boolean whenResponseNonSuccessFinalThrow = getWhenResponseNonSuccessFinalThrowByOptions(callOptions);
        Callback callback = getCallbackByOptions(name, callOptions);
        return resolveRequestExecuteWithOptions(supplier, retryTimes, retryIntervalMilliseconds,
                throwablePredicate, whenResponseNonSuccessRetry, whenResponseNonSuccessFinalThrow, name,
                fusionCallbacks(callback, callbacks));
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
     * <p>
     * Regarding callback {@code Callback}, this method does not refer to asynchronous
     * callbacks, but rather a synchronous mechanism that is suitable for synchronously
     * calling callback processing that does not focus on the return value.
     *
     * @param supplier                         the provider function of the {@code Response} object.
     * @param retryTimes                       the retry times.
     * @param retryIntervalMilliseconds        the retry interval milliseconds.
     * @param throwablePredicate               the Instance {@code ThrowablePredicate}.
     * @param whenResponseNonSuccessRetry      when response nonSuccess retry boolean mark.
     * @param whenResponseNonSuccessFinalThrow when response nonSuccess final throw exception mark.
     * @param name                             the sdk name,as see {@link SdkEnum#name()}.
     * @param callbacks                        the {@code Callback} instances.
     * @return The {@code Response} object obtained from the response
     * returns empty when {@link CallOptions#callbackClass()} exists.
     * @throws NullPointerException if input args is {@literal null}.
     */
    @Nullable
    public Response resolveRequestExecuteWithOptions(@NotNull Supplier<Response> supplier,
                                                     int retryTimes,
                                                     long retryIntervalMilliseconds,
                                                     @Nullable ThrowablePredicate throwablePredicate,
                                                     boolean whenResponseNonSuccessRetry,
                                                     boolean whenResponseNonSuccessFinalThrow,
                                                     @NotNull String name,
                                                     @Nullable List<Callback> callbacks) {
        FlowableCallerBuilder<Response> builder = FlowableCallerBuilder.newBuilder()
                .runBody(supplier)
                .retryTimes(retryTimes)
                .retryIntervalMilliseconds(retryIntervalMilliseconds)
                .customRetryExceptionPredicate(throwablePredicate);
        if (whenResponseNonSuccessRetry) builder.whenResponseNonSuccessRetry();
        if (whenResponseNonSuccessFinalThrow) builder.whenResponseNonSuccessFinalThrow();
        if (CollectionUtils.isNotEmpty(callbacks)) {
            sortCallbacks(callbacks);
            builder.customSubscriptionRegularConsumer(r -> callbacks.forEach(c -> c.success(r)));
            builder.customSubscriptionExceptionConsumer(e -> callbacks.forEach(c -> c.exception(name, e)));
            builder.build().run();
            return null;
        }
        return builder.buildBlock().get();
    }

    /**
     * Fuse annotation gets {@code Callback} and provider
     * {@code Callback} list to a new {@code Callback} list.
     *
     * @param callback  annotation gets {@code Callback}.
     * @param callbacks provider {@code Callback} list.
     * @return fusion {@code Callback} list.
     */
    @Nullable
    private List<Callback> fusionCallbacks(@Nullable Callback callback, @Nullable List<Callback> callbacks) {
        List<Callback> fusion = null;
        if (callback != null) {
            fusion = new ArrayList<>();
            fusion.add(callback);
        }
        if (CollectionUtils.isNotEmpty(callbacks)) {
            if (fusion == null) fusion = new ArrayList<>();
            fusion.addAll(callbacks);
        }
        return fusion;
    }

    /**
     * Sort the {@code Callback} collection according to the specified rules.
     *
     * @param callbacks the {@code Callback} collection.
     */
    protected void sortCallbacks(List<Callback> callbacks) {
        callbacks.sort(LoadOrder.SortRegulation.COMPARATOR);
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
     * @param name        current sdk name.
     * @param callOptions {@code CallOptions} annotation.
     * @return The Instance {@code ThrowablePredicate}.
     */
    @Nullable
    protected ThrowablePredicate getThrowablePredicateByOptions(String name, CallOptions callOptions) {
        Class<? extends ThrowablePredicate> throwablePredicateClass = callOptions.retryThrowablePredicateClass();
        if (throwablePredicateClass == ThrowablePredicate.class) {
            return null;
        }
        return getClassedInstance(name, throwablePredicateClass);
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
     * @param name        current sdk name.
     * @param callOptions {@code CallOptions} annotation.
     * @return The Instance {@code Callback}.
     */
    @Nullable
    protected Callback getCallbackByOptions(String name, CallOptions callOptions) {
        Class<? extends Callback> callbackClass = callOptions.callbackClass();
        if (callbackClass == Callback.class) {
            return null;
        }
        return getClassedInstance(name, callbackClass);
    }

    /**
     * Return some method options in annotation {@code CallOptions}
     * that return {@code Class}(for example {@link CallOptions#callbackClass()}),
     * use them for instantiation with {@link ReflectUtil#instantiates}, and cache
     * them for use in {@link #OBJECT_CACHE}(key is current sdk name plus object
     * class name).
     *
     * @param name  current sdk name.
     * @param clazz Annotation related {@code CallOptions} options
     *              that need to be instantiated.
     * @param <T>   Get the type of the object.
     * @return Return the instantiated {@code CallOptions} class option.
     */
    @SuppressWarnings("unchecked")
    protected <T> T getClassedInstance(String name, Class<T> clazz) {
        return (T) OBJECT_CACHE
                .computeIfAbsent(name + ":" + clazz.getName(), s -> ReflectUtil.instantiates(clazz));
    }
}
