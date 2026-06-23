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

import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.*;
import top.osjf.cron.core.repository.RepositoryContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * The enumeration class is used to describe the execution lifecycle of {@code CronListener},
 * where each cycle has its own consumption function {@link ListenerConsumer}, and the methods of this
 * declaration cycle stage are executed based on the given parameters.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public enum ListenerLifecycle {

    /**
     * When the {@link CronListener#start} stage is executed.
     */
    START((cronListener, listenerContext, e) -> cronListener.start(listenerContext)),

    /**
     * When the {@link CronListener#success} stage is executed.
     */
    SUCCESS((cronListener, listenerContext, e) -> cronListener.success(listenerContext)),

    /**
     * When the {@link CronListener#failed} stage is executed.
     */
    FAILED(CronListener::failed);

    final ListenerConsumer consumer;

    /**
     * At the beginning stage, a {@link ListenerContext} instance will be generated based
     * on the provided {@code ListenerContext} type and related parameters. This instance
     * will be retained in {@link ThreadLocal} and deleted after the {@link #SUCCESS} or
     * {@link #FAILED} stage.
     */
    private static final ThreadLocal<ListenerContext> CONTEXT_LOCAL = new ThreadLocal<>();

    ListenerLifecycle(ListenerConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Entry method for consuming all cron listeners, responsible for initializing task global context,
     * binding context to current thread via {@code ThreadLocal}, and safely invoking the listener consumption
     * logic.
     * <p>
     * Execution process:
     * <ol>
     * <li>Create lifecycle wrapper to record current listener execution lifecycle state;</li>
     * <li>Initialize {@link ListenerContext} only when the lifecycle is {@code START}, bind it to thread local
     * storage;</li>
     * <li>Obtain context from thread local and execute listener consumption logic;</li>
     * <li>Clear thread local context in finally block to avoid thread pool context pollution and memory leaks,
     * only executed when the lifecycle reaches the final stage.</li>
     * </ol>
     *
     * @param sourceContext     Custom business source context passed by the caller
     * @param repositoryContext Data repository operation context for cron task persistence
     * @param e                 Exception thrown during task execution, {@code null} if the task runs successfully
     * @param collector         Cron listener manager collector, used to filter and group registered listeners
     */
    void consumerListeners(Object sourceContext, RepositoryContext repositoryContext, @Nullable Throwable e,
                           CronListenerCollector collector) {
        ListenerLifecycleWrapper lifecycleWrapper = new ListenerLifecycleWrapper(this);
        if (lifecycleWrapper.matchLifecycle(START)) {
            ListenerContext listenerContext
                    = createListenerContext(collector, sourceContext, repositoryContext);
            CONTEXT_LOCAL.set(listenerContext);
        }
        ListenerContext listenerContext = CONTEXT_LOCAL.get();
        if (listenerContext != null) {
            try {
                consumerListeners(lifecycleWrapper, listenerContext, e, collector);
            }
            finally {
                if (lifecycleWrapper.matchFinally()) {
                    CONTEXT_LOCAL.remove();
                }
            }
        }
    }

    /**
     * Consume and execute all registered cron listeners according to different context types and lifecycle rules.
     * <p>
     * Execution branch description:
     * <ol>
     * <li>If the incoming context is NOT {@link ListenerErrorContext}:
     *     <ul>
     *         <li>First submit all {@link AsyncCronListener} to their own thread pool for asynchronous execution;
     *         </li>
     *         <li>Then execute all registered cron listeners synchronously, capture runtime exceptions,
     *         wrap the exception scene into {@link ListenerErrorContext} and rethrow the exception;</li>
     *     </ul>
     * </li>
     * <li>If the incoming context IS {@link ListenerErrorContext} (single listener local execution exception):
     *     <ul>
     *         <li>For ISOLATE strategy listener: only execute its own {@code failed} callback, trigger
     *         {@code failedFallback} when exception occurs;</li>
     *         <li>For PROPAGATE strategy listener: broadcast the failed event to all synchronous propagate-type
     *         listeners;</li>
     *     </ul>
     * </li>
     * </ol>
     *
     * @param lifecycleWrapper  the packaging auxiliary object for execution cycle, used to mark and restrict current
     *                         lifecycle execution status
     * @param listenerContext   the given listening context object, can be global {@link ListenerContext} or single
     *                          listener error {@link ListenerErrorContext}
     * @param e                 the exception object that occurred in the task or listener step, nullable if executed
     *                         successfully
     * @param collector         the manage instance objects for listeners, used to filter and obtain different groups
     *                         of cron listeners
     */
    void consumerListeners(ListenerLifecycleWrapper lifecycleWrapper,
                           ListenerContext listenerContext, @Nullable Throwable e, CronListenerCollector collector) {

        if (!(listenerContext instanceof ListenerErrorContext)) {
            for (CronListener cronListener : collector.newQueryBuilder().async().build()) {
                ((AsyncCronListener) cronListener).get()
                        .execute(() -> consumer.accept(cronListener, listenerContext, e));
            }
        }

        if (!(listenerContext instanceof ListenerErrorContext)) {
            for (CronListener cronListener : collector.getCronListeners()) {
                try {
                    consumer.accept(cronListener, listenerContext, e);
                }
                catch (Throwable ex) {
                    if (lifecycleWrapper.matchLifecycle(SUCCESS)) {
                        lifecycleWrapper.notAllow();
                    }
                    CONTEXT_LOCAL.set(new DefaultListenerErrorContext(listenerContext, this,
                            cronListener));
                    throw ex;
                }
            }
        }
        else {
            CronListener errorCronListener = ((ListenerErrorContext) listenerContext).getErrorCronListener();
            if (errorCronListener.getListenerErrorPropagateStrategy() == ListenerErrorPropagateStrategy.ISOLATE)
            {
                try {
                    errorCronListener.failed(listenerContext, e);
                }
                catch (Throwable ex) {
                    errorCronListener.failedFallback(ex);
                }
            }
            else {
                for (CronListener propagateCronListener : collector.newQueryBuilder().sync().propagate().build())
                {
                    try {
                        propagateCronListener.failed(listenerContext, e);
                    }
                    catch (Throwable ex) {
                        errorCronListener.failedFallback(ex);
                    }
                }
            }
        }
    }

    /**
     * The help {@link ListenerLifecycle} wrapper class.
     */
    protected static class ListenerLifecycleWrapper {

        ListenerLifecycle lifecycle;

        boolean allow = true;

        public ListenerLifecycleWrapper(ListenerLifecycle lifecycle) {
            this.lifecycle = lifecycle;
        }

        public boolean matchFinally() {
            return (matchLifecycle(SUCCESS) || matchLifecycle(FAILED)) && allow;
        }

        private void notAllow() {
            this.allow = false;
        }

        public boolean matchLifecycle(ListenerLifecycle matchLifecycle) {
            return this.lifecycle == matchLifecycle;
        }
    }

    /**
     * Create {@link ListenerContext} instance from {@link CronListenerCollector} configuration.
     * <p>
     * Priority rules for obtaining target {@code ListenerContext} implementation class:
     * <ol>
     * <li>Use the listener context type configured inside the collector first;</li>
     * <li>If the collector does not carry a type, resolve the type from the {@link ListenerContextTypeProvider}
     * annotation on the collector class;</li>
     * <li>Throw {@link IllegalArgumentException} if no valid implementation type can be resolved.</li>
     * </ol>
     * <p>
     * Two instantiation strategies are supported, controlled by {@code sourceContextBuildMode} of the annotation:
     * <ul>
     * <li>{@code SET}: Instantiate via no-arg constructor, then inject parameters through compatible single-argument
     * setter methods;</li>
     * <li>Other modes: Instantiate directly via the compatible two-parameter constructor.</li>
     * </ul>
     * </p>
     *
     * @param collector         cron listener collector carrying listener context configuration
     * @param sourceContext     source business context object to be injected
     * @param repositoryContext repository context object to be injected
     * @return instantiated {@link ListenerContext} instance
     * @throws IllegalArgumentException when no {@link ListenerContext} implementation type can be resolved
     * @since 3.0.2
     */
    private static ListenerContext createListenerContext(CronListenerCollector collector, Object sourceContext,
                                                 RepositoryContext repositoryContext) {
        Class<? extends ListenerContext> listenerContextClass = collector.getListenerContextClass();
        ListenerContextTypeProvider provider = collector.getClass().getAnnotation(ListenerContextTypeProvider.class);
        if (listenerContextClass == null) {
            if (provider != null) listenerContextClass = provider.value();
        }
        Assert.notNull(listenerContextClass, "No available " + ListenerContext.class.getName() + " type provided.");
        ListenerContext listenerContext;
        // Select the construction method based on the model, if one exists ...
        if (provider != null) {
            ListenerContextTypeProvider.BuildMode buildMode = provider.sourceContextBuildMode();
            if (buildMode == ListenerContextTypeProvider.BuildMode.SET) {
                listenerContext = createListenerContextBySetMethod(listenerContextClass, sourceContext, repositoryContext);
            }
            else {
                listenerContext = createListenerContextByConstructor(listenerContextClass, sourceContext, repositoryContext);
            }
        }
        else {
            // If there is no optional model, the constructor is directly used for creation ...
            listenerContext = createListenerContextByConstructor(listenerContextClass, sourceContext, repositoryContext);
        }

        return listenerContext;
    }

    /**
     * Instantiate {@link ListenerContext} via no-arg constructor, then inject dependencies through compatible setter methods.
     * <p>
     * Execution process:
     * <ol>
     * <li>Obtain the no-argument constructor of the target class to create an empty instance;</li>
     * <li>Find compatible single-parameter assignment methods for {@code sourceContext} and {@code repositoryContext}
     * (parameter type supports upward type compatibility matching, no restriction on method name);</li>
     * <li>Make the assignment methods accessible and use reflection to inject the two context objects into the instance.</li>
     * </ol>
     *
     * For example :
     * <pre>
     *     {@code
     *     public class DefaultListenerContext implements ListenerContext {
     *
     *         private final Object sourceContext;
     *         private final RepositoryContext repositoryContext;
     *
     *         public void setSourceContext(Object sourceContext) {
     *              this.sourceContext = sourceContext;
     *         }
     *
     *         public void setRepositoryContext(RepositoryContext repositoryContext) {
     *              this.repositoryContext = repositoryContext;
     *         }
     *     }
     *     }
     * </pre>
     *
     * @param listenerContextClass target implementation class of {@link ListenerContext}
     * @param sourceContext        source business context to be injected
     * @param repositoryContext    repository context to be injected
     * @return fully initialized {@link ListenerContext} instance
     * @throws IllegalArgumentException if no no-arg constructor or compatible setter method can be found
     * @since 3.0.2
     */
    private static ListenerContext createListenerContextBySetMethod(Class<? extends ListenerContext> listenerContextClass,
                                                              Object sourceContext, RepositoryContext repositoryContext) {

        Constructor<? extends ListenerContext> constructor = ClassUtils.getConstructorIfAvailable(listenerContextClass);
        Assert.notNull(constructor, "No available " + listenerContextClass + " empty constructor");
        ListenerContext listenerContext = BeanUtils.instantiateClass(constructor);
        Method sourceContextMd = ClassUtils.getCompatibleSetterMethod(listenerContextClass, sourceContext.getClass());
        Assert.notNull(sourceContextMd, "No available compatible " + sourceContext.getClass() + " setter method");
        Method repositoryContextMd = ClassUtils.getCompatibleSetterMethod(listenerContextClass, repositoryContext.getClass());
        Assert.notNull(repositoryContextMd, "No available compatible " + repositoryContext.getClass() + " setter method");
        ReflectionUtils.makeAccessible(sourceContextMd);
        ReflectionUtils.invokeMethod(sourceContextMd, listenerContext, sourceContext);
        ReflectionUtils.makeAccessible(repositoryContextMd);
        ReflectionUtils.invokeMethod(repositoryContextMd, listenerContext, repositoryContext);
        return listenerContext;
    }

    /**
     * Instantiate {@link ListenerContext} directly through a compatible two-parameter constructor.
     * <p>
     * Matching priority:
     * <ol>
     * <li>First try to match the constructor inherited from {@link AbstractListenerContext} with parameter types
     * {@code Object, RepositoryContext};</li>
     * <li>If not matched, scan all constructors to find one whose parameter types are assignable from the types
     * of the two incoming context objects.</li>
     * </ol>
     *
     * For example :
     * <pre>
     *     {@code
     *
     *     pubic class DefaultSourceContext {}
     *
     *     public class DefaultListenerContext extend AbstractListenerContext<DefaultSourceContext> {
     *
     *         private final DefaultSourceContext sourceContext;
     *         private final RepositoryContext repositoryContext;
     *
     *         public void DefaultListenerContext(DefaultSourceContext sourceContext, RepositoryContext repositoryContext) {
     *              this.sourceContext = sourceContext;
     *              this.repositoryContext = repositoryContext;
     *         }
     *     }
     *     }
     * </pre>
     *
     * @param listenerContextClass target implementation class of {@link ListenerContext}
     * @param sourceContext        source business context passed as constructor argument
     * @param repositoryContext    repository context passed as constructor argument
     * @return instantiated {@link ListenerContext} instance
     * @throws IllegalArgumentException if no compatible two-parameter constructor can be located
     * @since 3.0.2
     */
    private static ListenerContext createListenerContextByConstructor(Class<? extends ListenerContext> listenerContextClass,
                                                              Object sourceContext, RepositoryContext repositoryContext) {
        if (AbstractListenerContext.class.isAssignableFrom(listenerContextClass)) {
            Class<?> rawClass = ResolvableType.forClass(listenerContextClass).getSuperType().getGeneric(0).getRawClass();
            if (rawClass == null) rawClass = Object.class;
            Constructor<? extends ListenerContext> constructor
                    = ClassUtils.getConstructorIfAvailable(listenerContextClass, rawClass, RepositoryContext.class);
            if (constructor != null) {
                return BeanUtils.instantiateClass(constructor, sourceContext, repositoryContext);
            }
        }
        Constructor<? extends ListenerContext> constructor =
                ClassUtils.getCompatibleConstructorIfAvailable(listenerContextClass, sourceContext.getClass(),
                        repositoryContext.getClass());
        Assert.notNull(constructor,
                "No available Constructor <" + sourceContext.getClass() + "," + repositoryContext.getClass()
                        + "> provided.");
        return BeanUtils.instantiateClass(constructor, sourceContext, repositoryContext);
    }

    /**
     * Implement the callback logic of the scheduled task listener, automatically distinguishing
     * between synchronous and asynchronous execution modes
     *
     * <p>If the current listener is an asynchronous type listener of {@link AsyncCronListener},
     * the callback will be executed asynchronously using the thread pool provided by the listener
     * itself;
     * The ordinary {@link CronListener} listener directly executes the callback logic synchronously
     * in the current scheduling thread.
     * @param listener          the instance of scheduled task listener.
     * @param listenerCallback  the listener callback task to be executed includes callback methods
     *                          enclosed in {@link CronListener},such as {@link CronListener#start}.
     * @since 3.0.2
     */
    public static void doListenerCallback(CronListener listener, Runnable listenerCallback) {
        if (listener instanceof AsyncCronListener) {
            ((AsyncCronListener) listener).get().execute(listenerCallback);
        }
        else {
            listenerCallback.run();
        }
    }
}
