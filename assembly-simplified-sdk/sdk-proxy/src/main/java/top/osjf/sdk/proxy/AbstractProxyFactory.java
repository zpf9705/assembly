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

import top.osjf.sdk.core.util.ReflectUtil;

/**
 * An abstract base class for proxy factories responsible for generating proxy instances
 * of specified types. This class serves as a template and defines the core logic for
 * proxy creation, delegating the specific implementation details to concrete subclasses.
 *
 * <p>The class employs a template method pattern, where the {@link #newProxy} method
 * provides a standardized interface for proxy creation, while the actual proxy generation
 * logic is deferred to the {@link #newProxyInternal} method, which must be implemented
 * by subclasses.
 *
 * <p>Subclasses are required to provide the specific implementation for creating proxy
 * instances, including handling the conversion of the {@link DelegationCallback} to the
 * appropriate type expected by the subclass. This design promotes flexibility and reusability,
 * allowing different proxy mechanisms to be integrated seamlessly.
 *
 * @param <C> Subclass needs to convert the type of {@code DelegationCallback}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class AbstractProxyFactory<C> implements ProxyFactory {

    private Class<C> callbackType;

    /**
     * {@inheritDoc}
     * <p>
     * Creates a proxy instance of the specified type using the provided callback.
     * This method acts as a facade, handling the conversion of the callback to
     * the appropriate type and delegating the actual proxy creation to the
     * subclass-specific implementation.
     *
     * @param type     {@inheritDoc}
     * @param callback {@inheritDoc}
     * @param <T>      {@inheritDoc}
     * @return {@inheritDoc}
     * @throws Throwable {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public final <T> T newProxy(Class<T> type, DelegationCallback callback) throws Throwable {
        Class<C> callbackType = getCallbackType();
        //Directly call the internal method to create a
        // proxy object corresponding to the type.
        if (callback.isWrapperFor(callbackType)) {
            return newProxyInternal(type, (C) callback);
        }
        //The callback types required by non-proxy factories are packaged using wrapper classes.
        return newProxyInternal(type, (C) new DelegationCallbackWrapper(callback));
    }

    /**
     * Determines the actual type of the callback expected by the subclass.
     *
     * <p>This method uses reflection to retrieve the first generic type parameter
     * declared by the current class, which is assumed to be the type of the callback
     * expected by the subclass. This approach ensures type safety and reduces the
     * need for explicit type casting.
     *
     * @return the actual type of {@code DelegationCallback}.
     */
    private Class<C> getCallbackType() {
        if (callbackType == null) {
            callbackType = ReflectUtil.getSuperGenericClass(getClass(), 0);
        }
        return callbackType;
    }

    /**
     * After converting the callback interface {@code DelegationCallback}
     * to the type required by the subclass, the subclass needs to implement
     * this method to provide the logic for creating proxy objects.
     *
     * @param <T>      the type of the proxy object.
     * @param type     the type of proxy object to be created (the
     *                 specific type depends on the underlying framework).
     * @param callback the {@code DelegationCallback} callback executed
     *                 when the method of the proxy object is called.
     * @return specify {@code T} the type of proxy object.
     * @throws Throwable it can be any error that can be thrown.
     */
    protected abstract <T> T newProxyInternal(Class<T> type, C callback) throws Throwable;
}
