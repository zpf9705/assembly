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

import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.util.ReflectUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * Provide some methodological support for {@link ListenerContext}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ListenerContextSupport {

    private static final Map<String, Function<Object, ListenerContext>> LISTENER_CONTEXT_BUILD_FUC_CACHE
            = Collections.synchronizedMap(new WeakHashMap<>());

    /**
     * Create a specific type of {@code ListenerContext} instance.
     *
     * <p>This method creates using reflection or direct instantiation based on the provided
     * {@code CronListenerCollector} and {@code sourceContext} parameters
     * A {@code ListenerContext} object. The creation process depends on the annotations
     * and configuration of { code CronListenerCollector}.
     *
     * <p>Firstly, the method attempts to retrieve a function from the cache that can directly
     * convert {@code sourceContext} to {@code ListenerContext}.
     * If such a function exists in the cache, apply it directly and return the result.
     *
     * <p>If the corresponding function is not found in the cache, the method will check if
     * {@code CronListenerCollector} specifies it The class of {@code ListenerContext} (set
     * directly or annotated). If not specified and there is a {@code ListenerContextTypeProvider}
     * annotation,Then use the class specified in the annotation. If neither the class is
     * directly specified nor the annotation provides class information, throw {@code IllegalStateException}
     * exception.
     *
     * <p>After determining the class of {@code ListenerContext}, the method will the use of the
     * constructor is determined by the {@code sourceContextBuildMode} property (if any) in the
     * {@code ListenerContextTypeProvider} annotation
     * Still use setter methods to create and initialize {@code ListenerContext} objects.
     *
     * <p>If the build mode is not specified in the annotation or there is no annotation, the
     * constructor method is used by default to create the object.
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
     * @throws IllegalStateException        if no available {@code ListenerContext} type provided.
     * @throws IllegalArgumentException     if there are errors in the execution parameters.
     */
    public static ListenerContext createListenerContext(CronListenerCollector collector, Object sourceContext) {
        Function<Object, ListenerContext> func = LISTENER_CONTEXT_BUILD_FUC_CACHE.get(collector.getClass().getName());
        if (func != null) {
            return func.apply(sourceContext);
        }

        Class<? extends ListenerContext> listenerContextClass = collector.getListenerContextClass();
        ListenerContextTypeProvider provider = collector.getClass().getAnnotation(ListenerContextTypeProvider.class);
        if (listenerContextClass == null) {
            if (provider != null) {
                listenerContextClass = provider.value();
            } else {
                throw new IllegalStateException("No available " + ListenerContext.class.getName() + " type provided.");
            }
        }

        if (provider != null) {
            switch (provider.sourceContextBuildMode()) {
                case CONSTRUCTOR:
                    return createListenerContextByConstructor(collector, listenerContextClass, sourceContext);
                case SET:
                    return createListenerContextBySet(collector, listenerContextClass, sourceContext);
            }
        }

        return createListenerContextByConstructor(collector, listenerContextClass, sourceContext);
    }

    private static ListenerContext createListenerContextByConstructor(CronListenerCollector collector,
                                                                      Class<? extends ListenerContext> listenerContextClass,
                                                                      Object sourceContext) {
        ConstructorCreatedListenerContextFunction func = new ConstructorCreatedListenerContextFunction(listenerContextClass);
        ListenerContext listenerContext = func.apply(sourceContext);
        LISTENER_CONTEXT_BUILD_FUC_CACHE.putIfAbsent(collector.getClass().getName(), func);
        return listenerContext;
    }

    private static ListenerContext createListenerContextBySet(CronListenerCollector collector,
                                                              Class<? extends ListenerContext> listenerContextClass,
                                                              Object sourceContext) {
        SetCreatedListenerContextFunction func = new SetCreatedListenerContextFunction(listenerContextClass);
        ListenerContext listenerContext = func.apply(sourceContext);
        LISTENER_CONTEXT_BUILD_FUC_CACHE.putIfAbsent(collector.getClass().getName(), func);
        return listenerContext;
    }

    private static class ConstructorCreatedListenerContextFunction implements Function<Object, ListenerContext> {
        /**
         * Constructor instance.
         */
        private Constructor<? extends ListenerContext> constructor;
        /**
         * The source context class.
         */
        private Class<?> sourceContextClass;
        /**
         * The input {@code ListenerContext} class.
         */
        private final Class<? extends ListenerContext> listenerContextClass;

        /**
         * Creates a {@code ConstructorCreatedListenerContextFunction} by given {@link ListenerContext} class.
         *
         * @param listenerContextClass the input {@code ListenerContext} class.
         */
        public ConstructorCreatedListenerContextFunction(Class<? extends ListenerContext> listenerContextClass) {
            this.listenerContextClass = listenerContextClass;
        }

        /**
         * Returns a constructor object with only one parameter and the type is the type or parent class
         * of the original context object.
         * <p>
         * <strong>Note:</strong>
         * After the first input of the original context type, it will be recorded, and any
         * changes in the provided original context type will be considered as incorrect input.
         *
         * @param o the original context object.
         * @return A set method with only one parameter and the parameter type.
         * @throws IllegalArgumentException if the original context type provided has changed.
         * @throws IllegalStateException    If a constructor that meets the standards cannot be found.
         */
        @SuppressWarnings("unchecked")
        public Constructor<? extends ListenerContext> getConstructor(Object o) {
            if (sourceContextClass == null) {
                sourceContextClass = o.getClass();
            } else {
                if (!Objects.equals(o.getClass(), sourceContextClass)) {
                    throw new IllegalArgumentException("The original context type matching is inconsistent.");
                }
            }
            if (constructor == null) {
                try {
                    constructor = listenerContextClass.getConstructor(sourceContextClass);
                } catch (NoSuchMethodException e) {
                    for (Constructor<?> constructor0 : listenerContextClass.getDeclaredConstructors()) {
                        if (constructor0.getParameterTypes().length == 1 &&
                                constructor0.getParameterTypes()[0].isAssignableFrom(sourceContextClass)) {
                            this.constructor = (Constructor<? extends ListenerContext>) constructor0;
                        }
                    }
                }
                if (constructor == null) {
                    throw new IllegalStateException("There is no available constructor.");
                }
            }

            return constructor;
        }

        @Override
        public ListenerContext apply(Object o) {
            return ReflectUtils.newInstance(getConstructor(o), o);
        }
    }

    private static class SetCreatedListenerContextFunction implements Function<Object, ListenerContext> {
        /**
         * Set method instance.
         */
        private Method setMethod;
        /**
         * The source context class.
         */
        private Class<?> sourceContextClass;
        /**
         * The input {@code ListenerContext} class.
         */
        private final Class<? extends ListenerContext> listenerContextClass;

        /**
         * Creates a {@code SetCreatedListenerContextFunction} by given {@link ListenerContext} class.
         *
         * @param listenerContextClass the input {@code ListenerContext} class.
         */
        public SetCreatedListenerContextFunction(@NotNull Class<? extends ListenerContext> listenerContextClass) {
            this.listenerContextClass = listenerContextClass;
        }

        /**
         * Return a set method with only one parameter and the parameter type is input to the
         * original context object type.
         * <p>
         * <strong>Note:</strong>
         * After the first input of the original context type, it will be recorded, and any
         * changes in the provided original context type will be considered as incorrect input.
         *
         * @param o the original context object.
         * @return A set method with only one parameter and the parameter type.
         * @throws IllegalArgumentException if the original context type provided has changed.
         * @throws IllegalStateException    If a method that meets the standards cannot be found.
         */
        public Method getSetMethod(Object o) {
            if (sourceContextClass == null) {
                sourceContextClass = o.getClass();
            } else {
                if (!Objects.equals(o.getClass(), sourceContextClass)) {
                    throw new IllegalArgumentException("The original context type matching is inconsistent.");
                }
            }
            if (setMethod == null) {
                for (Method method : listenerContextClass.getDeclaredMethods()) {
                    if (method.getName().startsWith("set")
                            && method.getParameterTypes().length == 1
                            && method.getParameterTypes()[0].isAssignableFrom(sourceContextClass)) {
                        setMethod = method;
                    }
                }
                if (setMethod == null) {
                    throw new IllegalStateException("There is no available method.");
                }
            }

            return setMethod;
        }

        @Override
        public ListenerContext apply(Object o) {
            ListenerContext listenerContext = ReflectUtils.newInstance(listenerContextClass);
            Object result = ReflectUtils.invokeMethod(listenerContext, getSetMethod(o));
            if (!(result instanceof ListenerContext) || result == listenerContext) {
                return listenerContext;
            }
            return (ListenerContext) result;
        }
    }
}
