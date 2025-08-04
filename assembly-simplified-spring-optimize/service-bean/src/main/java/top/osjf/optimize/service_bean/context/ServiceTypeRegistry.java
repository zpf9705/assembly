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

import org.springframework.lang.Nullable;

import java.io.Closeable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@code ServiceTypeRegistry} is a service type registry class that stores and manages
 * mappings of service names to their corresponding types.
 * It implements the Closeable interface to allow resource cleanup when needed.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ServiceTypeRegistry implements Closeable {

    /**
     * This is a thread-safe map that stores mappings of service names to their types.
     */
    private final Map<String, Class<?>> serviceTypeMap = new ConcurrentHashMap<>(64);

    /**
     * A unmodifiable {@link #serviceTypeMap}.
     * @since 3.0.1
     */
    private final Map<String, Class<?>> unmodifiableServiceTypeMap;

    /**
     * Constructs an empty {@code ServiceTypeRegistry}.
     * @since 3.0.1
     */
    public ServiceTypeRegistry() {
        this.unmodifiableServiceTypeMap = Collections.unmodifiableMap(serviceTypeMap);
    }

    /**
     * Registers the specified service name and type in the service type map.
     * If the service name already exists, the existing type mapping will not be overwritten.
     *
     * @param serviceName the name of the service
     * @param type        the type of the service
     */
    public void registerServiceType(String serviceName, Class<?> type) {
        serviceTypeMap.putIfAbsent(serviceName, type);
    }

    /**
     * Removes the specified service name and its type mapping from the service type map.
     * If the service name exists, it returns the corresponding type; otherwise, it returns
     * {@code null}.
     *
     * @param serviceName the name of the service to remove
     * @return the removed service type, or {@code null} if the service name does not exist.
     */
    @Nullable
    public Class<?> removeServiceType(String serviceName) {
        return serviceTypeMap.remove(serviceName);
    }

    /**
     * Gets an unmodifiable view of the service type map.
     * This allows external code to read the map but not modify it.
     *
     * @return an unmodifiable view of the service type map.
     */
    public Map<String, Class<?>> getServiceTypeMap() {
        return unmodifiableServiceTypeMap;
    }

    /**
     * Clears the service type map and releases resources.
     * This is typically called when this registry is no longer needed.
     */
    @Override
    public void close() {
        serviceTypeMap.clear();
    }
}
