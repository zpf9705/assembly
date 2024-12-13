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
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.MapUtils;
import top.osjf.sdk.core.util.caller.CallOptions;
import top.osjf.sdk.core.util.caller.Callback;
import top.osjf.sdk.core.util.caller.RequestCaller;
import top.osjf.sdk.core.util.caller.ThrowablePredicate;

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
     * @param request input {@code Request} obj.
     * @param host    the real server hostname.
     * @param method  The method object to be executed.
     * @return The {@code Response} object obtained from the response
     * returns empty when {@link CallOptions#callbackClass()} exists.
     * @throws NullPointerException if input args is {@literal null}.
     */
    @Nullable
    public Response resolveRequestExecuteWithTypeOrMethodOptions(@NotNull Request<?> request,
                                                                 String host,
                                                                 @NotNull Method method) {
        CallOptions callOptions = method.getAnnotation(CallOptions.class);
        if (callOptions == null) {
            callOptions = method.getDeclaringClass().getAnnotation(CallOptions.class);
            if (callOptions == null) {
                return request.execute(host);
            }
        }
        return super.resolveRequestExecuteWithOptions(request, host, callOptions);
    }

    @Nullable
    @Override
    protected ThrowablePredicate getThrowablePredicateByOptions(String name, CallOptions callOptions) {
        return getClassedInstance(name, callOptions.retryThrowablePredicateClass());
    }

    @Nullable
    @Override
    protected Callback getCallbackByOptions(String name, CallOptions callOptions) {
        return getClassedInstance(name, callOptions.callbackClass());
    }

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
