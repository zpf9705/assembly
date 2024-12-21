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


package top.osjf.sdk.proxy.jdk;

import top.osjf.sdk.proxy.AbstractProxyFactory;

import java.lang.reflect.Proxy;

/**
 * The {@code JDKProxyFactory} class that extends {@code AbstractProxyFactory}
 * and is specifically used to create JDK dynamic proxy objects.
 *
 * <p>This class implements the newProxyInternal method to create proxy
 * objects using Java reflection and the JDK dynamic proxy API.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SuppressWarnings("unchecked")
public class JDKProxyFactory extends AbstractProxyFactory<JDKDelegationCallback> {

    /**
     * {@inheritDoc}
     *
     * @param type     {@inheritDoc}
     * @param callback JDK synthesizes the interface between
     *                 {@link java.lang.reflect.InvocationHandler}
     *                 and {@link top.osjf.sdk.proxy.DelegationCallback}.
     * @throws IllegalArgumentException if input type is not an interface.
     */
    @Override
    public <T> T newProxyInternal(Class<T> type, JDKDelegationCallback callback) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException(type.getName() + " not an interface.");
        }
        return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, callback);
    }
}
