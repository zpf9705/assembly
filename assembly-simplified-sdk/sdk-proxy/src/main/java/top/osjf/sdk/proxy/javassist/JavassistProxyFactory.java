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


package top.osjf.sdk.proxy.javassist;

import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import top.osjf.sdk.proxy.AbstractProxyFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code JavassistProxyFactory} is a factory class used to create dynamic proxy
 * classes.It extends the {@code AbstractProxyFactory} class and uses the Javassist
 * library to generate proxy classes.This factory class caches the generated proxy
 * classes to improve performance.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class JavassistProxyFactory extends AbstractProxyFactory<JavassistDelegationCallback> {

    private final Map<Class<?>, Class<?>> proxyCache = new ConcurrentHashMap<>(16);

    /**
     * {@inheritDoc}
     *
     * @param type     {@inheritDoc}
     * @param callback Javassist synthesizes the interface between
     *                 {@link javassist.util.proxy.MethodHandler}
     *                 and {@link top.osjf.sdk.proxy.DelegationCallback}.
     * @return {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T newProxyInternal(Class<T> type, JavassistDelegationCallback callback) throws Throwable {

        Class<?> proxyClass = proxyCache.computeIfAbsent(type, input -> {

            ProxyFactory pf = new ProxyFactory();
            if (input.isInterface()) {

                /*
                 * If the type passed in is an interface, set a proxy class to
                 * implement that interface. This is a common way to create
                 * interface proxies.
                 */
                pf.setInterfaces(new Class[]{input});
            }
            else {

                /*
                 * If the type passed in is a concrete class, set the proxy class
                 * to inherit that class. This is typically used to create class
                 * proxies, where the proxy class needs to inherit existing concrete
                 *  classes.
                 */
                pf.setSuperclass(input);
            }

            return pf.createClass();
        });

        Proxy proxy = (Proxy) proxyClass.getDeclaredConstructor().newInstance();
        proxy.setHandler(callback);
        return (T) proxy;
    }
}
