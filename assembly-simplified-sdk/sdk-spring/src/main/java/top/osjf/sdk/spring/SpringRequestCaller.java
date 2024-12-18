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

package top.osjf.sdk.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.CollectionUtils;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.caller.CallOptions;
import top.osjf.sdk.core.caller.Callback;
import top.osjf.sdk.core.caller.RequestCaller;
import top.osjf.sdk.core.caller.ThrowablePredicate;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The {@code SpringRequestCaller} class is a subclass of {@code RequestCaller}
 * specifically designed for use in Spring frameworks.
 *
 * <p>By extending {@code RequestCaller}  and passing in Spring's {@code ApplicationContext},
 * this class leverages Spring's IoC (Inversion of Control) container to obtain and
 * manage beans, simplifying the logic for dependency injection and invoking other
 * beans in a Spring environment.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class SpringRequestCaller extends RequestCaller implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * Resolves the {@code Request} and executes it, configuring the call options based
     * on the {@code CallOptions} annotation on the method or class.
     * <p>
     * Regarding the search for the existence of {@code CallOptions} annotations,
     * follow the following rules:
     * <ul>
     *     <li>Prioritize searching from the method, existing and ready to use.</li>
     *     <li>The method did not search from the definition class, it exists and is
     *     ready to use.</li>
     *     <li>There are no methods or defined classes, execute {@code Request#execute}
     *     directly and return.</li>
     * </ul>
     *
     * @param request   input {@code Request} obj.
     * @param host      the real server hostname.
     * @param method    the method object to be executed.
     * @param callbacks the provider {@code Callback} instances.
     * @return The {@code Response} object obtained from the response
     * returns empty when {@link CallOptions#callbackClass()} exists.
     * @throws NullPointerException if input args is {@literal null}.
     */
    @Nullable
    public Response resolveRequestExecuteWithTypeOrMethodOptions(@NotNull Request<?> request,
                                                                 String host,
                                                                 @NotNull Method method,
                                                                 @Nullable List<Callback> callbacks) {
        CallOptions callOptions = method.getAnnotation(CallOptions.class);
        if (callOptions == null) {
            callOptions = method.getDeclaringClass().getAnnotation(CallOptions.class);
            if (callOptions == null) {
                return noCallOptionsToExecute(request, host, callbacks);
            }
        }
        return super.resolveRequestExecuteWithOptions(request, host, callOptions, callbacks);
    }

    /**
     * The proxy method did not find the execution method for annotation {@code CallOptions}.
     *
     * @param request   input {@code Request} obj.
     * @param host      the real server hostname.
     * @param callbacks the provider {@code Callback} instances.
     * @return Response result {@code Response} object, returns {@literal null}
     * in case of exception.
     */
    @Nullable
    private Response noCallOptionsToExecute(@NotNull Request<?> request,
                                            String host,
                                            @Nullable List<Callback> callbacks) {
        boolean hasCallbacks = CollectionUtils.isNotEmpty(callbacks);
        try {
            Response response = request.execute(host);
            if (response.isSuccess())
                if (hasCallbacks)
                    callbacks.forEach(c -> c.success(response));
            return response;
        } catch (Throwable e) {
            if (hasCallbacks) {
                callbacks.forEach(c -> c.exception(request.matchSdkEnum().name(), e));
            } else
                throw e;
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sort by {@link AnnotationAwareOrderComparator} use annotation
     * {@link org.springframework.core.annotation.Order}.
     *
     * @param callbacks {@inheritDoc}
     */
    @Override
    protected void sortCallbacks(List<Callback> callbacks) {
        AnnotationAwareOrderComparator.sort(callbacks);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Execute the rewrite method {@link #getClassedInstance} of this class.
     *
     * @param name        {@inheritDoc}
     * @param callOptions {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    protected ThrowablePredicate getThrowablePredicateByOptions(String name, CallOptions callOptions) {
        return getClassedInstance(name, callOptions.retryThrowablePredicateClass());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Execute the rewrite method {@link #getClassedInstance} of this class.
     *
     * @param name        {@inheritDoc}
     * @param callOptions {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    protected Callback getCallbackByOptions(String name, CallOptions callOptions) {
        return getClassedInstance(name, callOptions.callbackClass());
    }

    /**
     * Hand over object management to the Spring framework and search for
     * all {@code Class} type option objects related to annotations from
     * the Spring container.
     *
     * @param name  {@inheritDoc}
     * @param clazz {@inheritDoc}
     * @param <T>   {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    @Nullable
    @SuppressWarnings("unchecked")
    protected <T> T getClassedInstance(String name, Class<T> clazz) {
        Map<String, ?> beanMap = applicationContext.getBeansOfType(clazz);
        if (MapUtils.isNotEmpty(beanMap)) {
            List<?> beans = Arrays.asList(beanMap.values().toArray());
            AnnotationAwareOrderComparator.sort(beans);
            return (T) beans.get(0);
        }
        return null;
    }
}
