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

package top.osjf.sdk.core.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ServiceLoadManager class is a utility class for managing and loading service instances.
 * It leverages Java's ServiceLoader mechanism to discover and load service providers that
 * implement a specific interface.
 *
 * <p>The class provides caching mechanisms to reduce the overhead of repeatedly loading the
 * same type of service instances.
 *
 * <p>It supports functionalities to load all instances, load the highest priority instance,
 * and load the lowest priority instance.
 *
 * <p>This class is designed as a variation of the Singleton pattern, with a private constructor
 * to prevent instantiation and static methods provided for external use.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public final class ServiceLoadManager {

    /*** Private constructor to prevent external instantiation.*/
    private ServiceLoadManager() {
        throw new AssertionError("No instance for you !");
    }

    /*** Full load cache.*/
    private final static Map<Class<?>, List<?>> loadAllCache = new ConcurrentHashMap<>(16);

    /*** Cache of the highest priority instance.*/
    private final static Map<Class<?>, Object> loadHighPriorityCache = new ConcurrentHashMap<>(16);

    /*** Cache of the lowest priority instance.*/
    private final static Map<Class<?>, Object> loadLowerPriorityCache = new ConcurrentHashMap<>(16);

    /**
     * Loads all instances of the specified type and caches the result.
     * If the instances for the specified type already exist in the cache, returns them directly;
     * otherwise, calls the {@link #loadInstances} method to load the instances, caches the result,
     * and then returns it.
     *
     * @param <T>  The generic type, specifying the type of instances to be loaded.
     * @param type The Class object of the type whose instances are to be loaded.
     * @return A list containing all instances of the specified type.
     */
    public static <T> List<T> loadAll(Class<T> type) {
        return (List<T>) loadAllCache.computeIfAbsent(type, type1 -> loadInstances(type1, false));
    }

    /**
     * Loads the high-priority instance of the specified type and caches the result.
     * If instances of the specified type already exist in the cache, returns the first instance
     * (considered high-priority);
     * otherwise, calls the {@link #loadInstances} method to load a list of instances, returns the first
     * instance from the list if it's not empty,and caches the result.
     *
     * @param <T>  The generic type, specifying the type of instance to be loaded.
     * @param type The Class object of the type whose instance is to be loaded.
     * @return The high-priority instance of the specified type, or null if no instance is found.
     */
    public static <T> T loadHighPriority(Class<T> type) {
        return (T) loadHighPriorityCache.computeIfAbsent(type, type1 -> {
            List<?> instances = loadInstances(type1, true);
            return !instances.isEmpty() ? instances.get(0) : null;
        });
    }

    /**
     * Loads the low-priority instance (typically the last instance in the list) of the specified type and
     * caches the result.
     * If an instance of the type is already present in the cache, it returns the cached instance directly.
     * Otherwise, it loads the instances by calling the {@link #loadInstances} method, and returns the last
     * instance in the list if available.If no instances are found, it returns null.
     *
     * @param <T>  The generic type, indicating the type of the instance to be loaded.
     * @param type The Class object of the type of instance to be loaded.
     * @return The low-priority instance of the specified type, or null if no instance is found.
     */
    public static <T> T loadLowerPriority(Class<T> type) {
        return (T) loadLowerPriorityCache.computeIfAbsent(type, type1 -> {
            List<?> instances = loadInstances(type1, true);
            return !instances.isEmpty() ? instances.get(instances.size() - 1) : null;
        });
    }

    /**
     * Loads all available instances of the specified type, optionally sorting them.
     * It uses Java's ServiceLoader mechanism to find and load all service providers that implement the
     * specified interface.
     * If sorting is specified (via the sort parameter), it sorts the loaded instances based on the value
     * of the LoadOrder annotation present on the service provider class.
     *
     * @param <T>  The generic type, indicating the type of the instances to be loaded.
     * @param type The Class object of the type of instances to be loaded.
     * @param sort A boolean indicating whether to sort the loaded instances. If true, sorts the instances
     *             based on the value of the
     *             LoadOrder annotation.
     * @return A list containing all loaded instances, sorted by the value of the LoadOrder annotation if
     * sorting is specified and the instances have the annotation.
     */
    private static <T> List<T> loadInstances(Class<T> type, boolean sort) {
        List<T> instances = new ArrayList<>();
        ServiceLoader.load(type).forEach(instances::add);
        if (sort && !instances.isEmpty()) {
            instances.sort((o1, o2) -> {
                LoadOrder order1 = o1.getClass().getAnnotation(LoadOrder.class);
                LoadOrder order2 = o2.getClass().getAnnotation(LoadOrder.class);
                int value1 = order1.value();
                int value2 = order2.value();
                return Integer.compare(value1, value2);
            });
        }
        return instances;
    }
}
