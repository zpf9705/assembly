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

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;

/**
 * This abstract class is used to create proxy objects using Spring's cglib
 * pattern. In addition to interfaces, it can also create objects for regular
 * classes, which compensates for this disadvantage compared to {@link AbstractJdkProxySupport}.
 *
 * <p>The process is to first rely on the dynamic proxy {@link MethodInterceptor} of
 * spring cglib to create a proxy object,hand it over to Spring, and when Spring calls
 * the proxy object, execute the {@link #intercept(Object, Method, Object[], MethodProxy)}
 * method,which performs unified logical processing and distribution within this method,
 *
 * <p>The method entrusted to Spring to create bean objects is to implement the
 * {@link FactoryBean} interface and dynamically register beans through
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
 *
 * <p>If you want to standardize processing, you can access {@link ConcentrateProxySupport}.
 *
 * @param <T> The data type of the proxy class.
 * @see Enhancer
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractCglibProxySupport<T> implements FactoryBean<T>, MethodInterceptor {

    /**
     * The proxy object created.
     */
    private T proxy;

    /**
     * The target type of dynamic proxy.
     */
    private Class<T> type;

    /**
     * Is the object managed by this factory a singleton.
     */
    public boolean isSingleton = true;

    public void setType(Class<T> type) {
        this.type = type;
    }

    public Class<T> getType() {
        return type;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return isSingleton;
    }

    @Override
    public T getObject() {
        if (proxy != null) {
            return proxy;
        }
        proxy = createProxy(type, this);
        return proxy;
    }

    @SuppressWarnings("unchecked")
    static <T> T createProxy(Class<T> type, Callback callback) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(callback);
        return (T) enhancer.create();
    }

    @Override
    public abstract Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy);
}
