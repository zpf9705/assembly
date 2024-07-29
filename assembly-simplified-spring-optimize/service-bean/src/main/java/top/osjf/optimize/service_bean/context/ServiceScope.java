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

package top.osjf.optimize.service_bean.context;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The scope processing class of the corresponding bean for
 * service collection. The services to be collected will
 * not be initialized at project startup, but will be
 * separately initialized when the context is actually
 * used for retrieval, and saved to the storage structure
 * of the current class.
 *
 * <p>The corresponding service cache lifecycle is equivalent
 * to that of an application, and a unified {@link DisposableBean}
 * destruction callback is performed when the application is closed.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ServiceScope implements Scope, DisposableBean {

    /*** There is a map for service mapping.*/
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>(16);

    /*** Destroy the cache collection of logic {@link DisposableBean}.*/
    private final List<Runnable> destructionCallbacks = new LinkedList<>();

    /*** placeholder for {@link #getConversationId()}*/
    private final String uniqueId = UUID.randomUUID().toString();

    /**
     * An empty construct carries a hook function that performs a destroy callback.
     */
    public ServiceScope() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy));
    }

    @Override
    @NonNull
    public Object get(@NonNull String name, @NonNull ObjectFactory<?> objectFactory) {
        Object bean = serviceMap.get(name);
        if (bean == null) {
            bean = objectFactory.getObject();
            serviceMap.put(name, bean);
        }
        return bean;
    }

    @Nullable
    @Override
    public Object remove(@NonNull String name) {
        return serviceMap.remove(name);
    }

    @Override
    public void registerDestructionCallback(@NonNull String name, @NonNull Runnable callback) {
        destructionCallbacks.add(callback);
    }

    @Nullable
    @Override
    public Object resolveContextualObject(@NonNull String key) {
        return null;
    }

    @Nullable
    @Override
    public String getConversationId() {
        return uniqueId;
    }

    @Override
    public void destroy() {
        for (Runnable runnable : this.destructionCallbacks) {
            runnable.run();
        }
        this.destructionCallbacks.clear();
    }
}
