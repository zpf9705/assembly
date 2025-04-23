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

import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.util.ReflectUtil;

/**
 * An abstract proxy factory class used to create proxy objects of
 * specified types.
 *
 * <p>This class serves as a template, and concrete subclasses need
 * to provide the logic for creating proxy objects.
 *
 * @param <C> Subclass needs to convert the type of {@code DelegationCallback}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public abstract class AbstractProxyFactory<C> implements ProxyFactory {

    /**
     * {@inheritDoc}
     * <p>
     * Create a proxy object based on the specified type and callback.
     *
     * @param type     {@inheritDoc}
     * @param callback {@inheritDoc}
     * @param <T>      {@inheritDoc}
     * @return {@inheritDoc}
     * @throws Throwable          {@inheritDoc}
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
     * Get the actual type of {@code DelegationCallback}.
     *
     * <p>This method defaults to obtaining the first generic
     * parameter type declared by the current class through reflection.
     *
     * @return the actual type of {@code DelegationCallback}.
     */
    @NotNull
    private Class<C> getCallbackType() {
        return ReflectUtil.getSuperGenericClass(getClass(), 0);
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
