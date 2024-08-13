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
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * Proxy object integration is an abstract support class for
 * Spring custom creation object interface {@link FactoryBean}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class FactoryProxyBeanSupport<T> implements FactoryBean<T> {

    /**
     * The proxy object created.
     */
    private T proxy;

    /**
     * The target type of dynamic proxy.
     */
    private final Class<T> type;

    /**
     * Is the object managed by this factory a singleton.
     */
    public boolean isSingleton = true;

    /**
     * Constructor for a {@code Class} type.
     *
     * @param type a {@code Class} type
     */
    public FactoryProxyBeanSupport(@NonNull Class<T> type) {
        this.type = type;
    }

    /**
     * Return the target type created by this proxy.
     *
     * @return the target type created by this proxy.
     */
    public Class<T> getType() {
        return type;
    }

    /**
     * Set the object managed by this factory a singleton.
     *
     * @param singleton the object managed by this factory a singleton.
     */
    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    @Nullable
    @Override
    public T getObject() throws Exception {
        if (proxy != null) {
            return proxy;
        }
        proxy = getObject0();
        return proxy;
    }

    @Nullable
    protected abstract T getObject0() throws Exception;

    @Nullable
    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public boolean isSingleton() {
        return isSingleton;
    }
}
