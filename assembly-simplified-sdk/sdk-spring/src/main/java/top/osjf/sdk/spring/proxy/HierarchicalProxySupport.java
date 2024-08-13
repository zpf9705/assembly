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

package top.osjf.sdk.spring.proxy;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * The functionality of this abstract class is based on a fusion version
 * of {@link AbstractJdkProxySupport} and {@link AbstractCglibProxySupport}.
 *
 * <p>For the convenience of encapsulation, a unified function is written based
 * on the input {@link ProxyModel} model for unified routing creation. If you
 * need to understand the proxy model, you can go to the abstract class above
 * for specific understanding.
 *
 * <p>The callbacks for cglib and jdk dynamic proxies have all been integrated
 * into {@link ProxyHandler}, allowing both proxies to use the same callback,
 * facilitating subsequent proxy execution calls.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see AbstractJdkProxySupport
 * @see AbstractCglibProxySupport
 * @since 1.0.0
 */
public abstract class HierarchicalProxySupport<T> extends FactoryProxyBeanSupport<T> implements MethodInterceptor,
        InvocationHandler, ProxyHandler {

    /**
     * The default proxy mode, JDK dynamic proxy.
     */
    private static final ProxyModel DEFAULT_PROXY_MODEL = ProxyModel.JDK;

    /**
     * Dynamic proxy model.
     */
    private ProxyModel proxyModel = DEFAULT_PROXY_MODEL;

    /**
     * Set the model enumeration for this proxy.
     *
     * @param proxyModel the model enumeration for this proxy.
     */
    public void setProxyModel(ProxyModel proxyModel) {
        this.proxyModel = proxyModel;
    }

    /**
     * Return the model enumeration for this proxy.
     *
     * @return the model enumeration for this proxy.
     */
    public ProxyModel getProxyModel() {
        return proxyModel;
    }

    @Override
    public T getObject0() {
        return null;
    }

    /*** {@inheritDoc}*/
    @Override
    public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) {
        return handle(proxy, method, args);
    }

    /*** {@inheritDoc}*/
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        return handle(proxy, method, args);
    }

    /*** {@inheritDoc}*/
    @Override
    public abstract Object handle(Object obj, Method method, Object[] args);
}
