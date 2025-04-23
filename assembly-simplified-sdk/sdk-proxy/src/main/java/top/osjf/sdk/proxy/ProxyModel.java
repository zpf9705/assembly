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


import top.osjf.sdk.proxy.bytebuddy.ByteBuddyProxyFactory;
import top.osjf.sdk.proxy.cglib.CglibProxyFactory;
import top.osjf.sdk.proxy.javassist.JavassistProxyFactory;
import top.osjf.sdk.proxy.jdk.JDKProxyFactory;
import top.osjf.sdk.proxy.springcglib.SpringCglibProxyFactory;

/**
 * The {@code ProxyModel} enumeration defines different proxy
 * models, each corresponding to a specific proxy factory
 * responsible for creating proxy objects of the corresponding type.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public enum ProxyModel implements ProxyFactory {

    /**
     * A proxy model created using JDK dynamic proxy factory.
     */
    JDK(new JDKProxyFactory()),

    /**
     * A proxy model created using CGLIB proxy factory.
     */
    CGLIB(new CglibProxyFactory()),

    /**
     * A proxy model created using the Spring CGLIB proxy factory.
     */
    SPRING_CGLIB(new SpringCglibProxyFactory()),

    /**
     * A proxy model created using the byteBuddy proxy factory.
     */
    BYTE_BUDDY(new ByteBuddyProxyFactory()),

    /**
     * A proxy model created using the javassist proxy factory.
     */
    JAVASSIST(new JavassistProxyFactory());

    final ProxyFactory proxyFactory;

    ProxyModel(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Create a proxy object using the factory method of the current model.
     */
    @Override
    public <T> T newProxy(Class<T> type, DelegationCallback callback) throws Throwable {
        return proxyFactory.newProxy(type, callback);
    }
}
