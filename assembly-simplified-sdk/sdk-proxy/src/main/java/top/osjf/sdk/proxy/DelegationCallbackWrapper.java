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


package top.osjf.sdk.proxy;

import top.osjf.sdk.proxy.bytebuddy.InvocationHandlerAdapterDelegationCallback;
import top.osjf.sdk.proxy.cglib.CglibDelegationCallback;
import top.osjf.sdk.proxy.javassist.JavassistDelegationCallback;
import top.osjf.sdk.proxy.jdk.JDKDelegationCallback;
import top.osjf.sdk.proxy.springcglib.SpringCglibDelegationCallback;

import java.lang.reflect.Method;

import static java.util.Objects.requireNonNull;

/**
 * The {@code DelegationCallbackWrapper} class is a wrapper that implements multiple
 * delegation callback interfaces, allowing a single {@code DelegationCallback} instance
 * to be used across different proxying frameworks and libraries. This class provides
 * a unified way to handle callback logic by delegating calls to an underlying
 * {@code DelegationCallback} instance.
 *
 * <p>This wrapper is particularly useful in scenarios where you need to support
 * multiple proxying mechanisms (e.g., JDK dynamic proxies, CGLIB proxies, Spring CGLIB proxies,
 * Javassist proxies) with a single callback implementation. By wrapping a
 * {@code DelegationCallback} instance, this class ensures that the same callback logic
 * can be reused across different proxying technologies.</p>
 *
 * <h3>Implemented Interfaces</h3>
 * <ul>
 *     <li>{@link JDKDelegationCallback}</li>
 *     <li>{@link CglibDelegationCallback}</li>
 *     <li>{@link SpringCglibDelegationCallback}</li>
 *     <li>{@link JavassistDelegationCallback}</li>
 *     <li>{@link InvocationHandlerAdapterDelegationCallback}</li>
 * </ul>
 *
 * <h3>Example Usage</h3>
 *
 * <pre>{@code
 * DelegationCallback originalCallback = new MyDelegationCallback();
 * DelegationCallbackWrapper wrapper = new DelegationCallbackWrapper(originalCallback);
 *
 * // Use the wrapper where any of the implemented interfaces are required
 * JDKDelegationCallback jdkCallback = wrapper;
 * CglibDelegationCallback cglibCallback = wrapper;
 * // ... and so on for other interfaces
 * }</pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class DelegationCallbackWrapper
        implements JDKDelegationCallback, CglibDelegationCallback, SpringCglibDelegationCallback,
        JavassistDelegationCallback, InvocationHandlerAdapterDelegationCallback {

    /**
     * The underlying {@code DelegationCallback} instance that this wrapper delegates to.
     */
    private final DelegationCallback delegationCallback;

    /**
     * Constructs a new {@code DelegationCallbackWrapper} with the specified
     * {@code DelegationCallback} instance.
     *
     * @param delegationCallback the {@code DelegationCallback} instance to delegate to;
     *                           must not be null
     * @throws NullPointerException if {@code delegationCallback} is null
     */
    public DelegationCallbackWrapper(DelegationCallback delegationCallback) {
        requireNonNull(delegationCallback, "delegationCallback");
        this.delegationCallback = delegationCallback;
    }

    @Override
    public Object callback(Method method, Object[] args, PeculiarProxyVariable variable) throws Throwable {
        return delegationCallback.callback(method, args, variable);
    }
}
