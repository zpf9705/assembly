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


package top.osjf.cron.core.listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Provide some methodological support for {@link ListenerContext}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ListenerContextSupport {

    private static final Map<String, Constructor<?>> CONSTRUCTOR_CACHE = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * By collecting the {@code ListenerContext} type provided by the given listener
     * class, creating a {@code ListenerContext} instance object using {@code sourceContext},
     * the provided {@code ListenerContext} type needs to satisfy the construction method of
     * having a {@code sourceContext} type.
     *
     * @param collector     the collection instance of the listener.
     * @param sourceContext the original context object provided by the framework used
     *                      for executing scheduled tasks.
     * @return The {@code ListenerContext} instances created using a specific type.
     * @throws NullPointerException         if input {@code CronListenerCollector} or {@code sourceContext}
     *                                      is {@literal null}.
     * @throws RuntimeException             create a runtime exception for {@code ListenerContext} using reflection.
     * @throws UndeclaredThrowableException create a {@code ListenerContext} object of the inspected abnormal
     *                                      package using reflection.
     */
    public static ListenerContext createListenerContext(CronListenerCollector collector, Object sourceContext) {
        Class<? extends ListenerContext> listenerContextClass = collector.getListenerContextClass();
        if (listenerContextClass == null) {
            Class<? extends CronListenerCollector> clazz = collector.getClass();
            ListenerContextTypeProvider provider = clazz.getAnnotation(ListenerContextTypeProvider.class);
            listenerContextClass = provider.value();
        }

        if (listenerContextClass == null) {
            throw new IllegalStateException("No available " + ListenerContext.class.getName() + " type provided.");
        }

        ListenerContext listenerContext;
        try {
            Constructor<?> constructor = CONSTRUCTOR_CACHE.get(listenerContextClass.getName());
            if (constructor == null){
                constructor
                        = listenerContextClass.getConstructor(sourceContext.getClass());
                CONSTRUCTOR_CACHE.putIfAbsent(listenerContextClass.getName(), constructor);
            }
            listenerContext = (ListenerContext) constructor.newInstance(sourceContext);
        }
        catch (Exception ex) {
            if (ex instanceof NoSuchMethodException) {
                for (Constructor<?> constructor : listenerContextClass.getConstructors()) {
                    if (constructor.getParameterTypes().length == 1 &&
                            constructor.getParameterTypes()[0].isAssignableFrom(sourceContext.getClass())) {
                        try {
                            listenerContext = (ListenerContext) constructor.newInstance(sourceContext);
                            CONSTRUCTOR_CACHE.putIfAbsent(listenerContextClass.getName(), constructor);
                            return listenerContext;
                        }
                        catch (Exception e) {
                            throw new UndeclaredThrowableException(ex);
                        }
                    }
                }
            }
            if (ex instanceof RuntimeException) {
                throw (RuntimeException) ex;
            }
            throw new UndeclaredThrowableException(ex);
        }

        return listenerContext;
    }
}
