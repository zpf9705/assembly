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


package top.osjf.sdk.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import top.osjf.sdk.proxy.AbstractProxyFactory;

/**
 * The {@code CglibProxyFactory} class extends the {@code AbstractProxyFactory}
 * abstract class and is specifically designed for creating proxy objects based
 * on CGLIB.
 *
 * <p>It generates subclasses of the target class through the {@code Enhancer} class
 * provided by the CGLIB framework and inserts proxy logic into these subclasses.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@SuppressWarnings("unchecked")
public class CglibProxyFactory extends AbstractProxyFactory<CglibDelegationCallback> {

    /**
     * {@inheritDoc}
     *
     * @param type     {@inheritDoc}
     * @param callback Cglib synthesizes the interface between
     *                 {@link net.sf.cglib.proxy.MethodInterceptor}
     *                 and {@link top.osjf.sdk.proxy.DelegationCallback}.
     * @return {@inheritDoc}
     */
    @Override
    protected <T> T newProxyInternal(Class<T> type, CglibDelegationCallback callback) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(callback);
        return (T) enhancer.create();
    }
}
