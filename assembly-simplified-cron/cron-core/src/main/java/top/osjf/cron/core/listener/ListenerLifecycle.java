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

import org.slf4j.LoggerFactory;
import top.osjf.commons.lang.Nullable;
import top.osjf.commons.util.*;
import top.osjf.cron.core.repository.RepositoryContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

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
     * Thread-local storage, isolate listener context data exclusive to each thread.
     */
    private static final ThreadLocal<ListenerContextLocalData> CONTEXT_LOCAL = new ThreadLocal<>();

    ListenerLifecycle(ListenerConsumer consumer) {
        this.consumer = consumer;
    }

    /**
     * Construct the consumer listener method entry for {@link ListenerContext} based on the original context
     * object and {@link RepositoryContext resource context object}.
     *
     * @param sourceContext     the custom business source context passed by the task runtime
     * @param repositoryContext the repository context for cron task data operations
     * @param e                 the Exception thrown during task or listener execution.
     * @param collector         the cron listener collector for filtering and grouping registered listeners
     * @see #createListenerContext
     */
    void consumerListeners(Object sourceContext, RepositoryContext repositoryContext,
                           @Nullable Throwable e, CronListenerCollector collector) {
        consumerListeners(() -> createListenerContext(collector, sourceContext, repositoryContext), e, collector);
    }

    /**
     * Core method to consume and execute all registered cron listeners according to current lifecycle
     * and context type.
     * <p>
     * Execution process description:
     * <ol>
     * <li>Create lifecycle wrapper to record current lifecycle execution state;</li>
     * <li>When the lifecycle is {@code START}, obtain {@link ListenerContext} via the provided supplier
     * and bind it to thread-local storage;</li>
     * <li>If the context is a normal {@link ListenerContext} (global task lifecycle trigger):
     *     <ul>
     *         <li>Execute all {@link AsyncCronListener} asynchronously using their own bound thread pools
     *         first;</li>
     *         <li>Then synchronously execute all registered synchronous cron listeners; if any listener
     *         throws an exception, wrap the exception scene into {@link ListenerErrorContext}, store it
     *         in thread-local and rethrow the exception;</li>
     *     </ul>
     * </li>
     * <li>If the context is {@link ListenerErrorContext} (single listener execution exception):
     *     <ul>
     *         <li>For listeners adopting {@code ISOLATE} error propagation strategy: only execute the
     *         {@code failed} callback of the abnormal listener itself,
     *         trigger {@code failedFallback} if the callback throws an exception;</li>
     *         <li>For listeners adopting {@code PROPAGATE} error propagation strategy: broadcast the failure
     *         event to all synchronous propagate-type listeners, and execute the fallback method when any callback
     *         fails.</li>
     *     </ul>
     * </li>
     * </ol>
     *
     * @param listenerContextSupplier the functional supplier used to lazily create the global task listener context
     * @param e                       the exception thrown during task or listener execution, {@code null} if no error
     *                                occurs
     * @param collector               the cron listener collector used to filter asynchronous, synchronous and
     *                                error-propagate listeners
     * @since 3.0.2
     */
    void consumerListeners(Supplier<ListenerContext> listenerContextSupplier, @Nullable Throwable e,
                           CronListenerCollector collector) {

        ListenerLifecycleWrapper lifecycleWrapper = new ListenerLifecycleWrapper(this);
        if (lifecycleWrapper.matchLifecycle(START)) {
            ListenerContext listenerContext = listenerContextSupplier.get();
            CONTEXT_LOCAL.set(new ListenerContextLocalData(listenerContext));
        }

        ListenerContextLocalData localData = CONTEXT_LOCAL.get();
        if (localData != null) {
            ListenerContext listenerContext = localData.listenerContext;

            try {
                // When the isolation strategy is in effect, the event is directly terminated and not
                // propagated outward. Only the current listener consumes this abnormal event
                if (!(listenerContext instanceof ListenerErrorContext)) {
                    List<AsyncCronListener> asyncCronListeners = collector.newQueryBuilder().async().sort().build();
                    for (AsyncCronListener asyncCronListener : asyncCronListeners) {

                        // When executing the current asynchronous listener, it is necessary to determine whether
                        // the listener has been interrupted abnormally. Only non interrupt asynchronous listeners
                        // are supported to perform listening tasks.
                        AtomicBoolean interruptionFlag = localData.getInterruptionFlag(asyncCronListener);
                        if (!interruptionFlag.get()) {
                            asyncCronListener.get()
                                    .execute(() -> {
                                        try {
                                            consumer.accept(asyncCronListener, listenerContext, e);
                                        }
                                        catch (Throwable ex) {
                                            // If an error occurs, mark the interrupt status directly.
                                            interruptionFlag.set(true);

                                            failed(asyncCronListener, listenerContext, ex);
                                        }
                                    });
                        }
                    }
                }

                if (!(listenerContext instanceof ListenerErrorContext)) {
                    for (CronListener cronListener : collector.newQueryBuilder().sync().sort().build()) {
                        try {
                            consumer.accept(cronListener, listenerContext, e);
                        }
                        catch (Throwable ex) {
                            // When an error occurs in the monitoring step, if it is a successful
                            // notification, do not clear the local cache immediately. Clear it
                            // after the next failed notification.
                            if (lifecycleWrapper.matchLifecycle(SUCCESS)) {
                                lifecycleWrapper.notAllow();
                            }
                            // Create a new error listening context for listening and passing.
                            localData.listenerContext
                                    = new DefaultListenerErrorContext(listenerContext, this, cronListener);
                            throw ex;
                        }
                    }
                }
                else {
                    CronListener errorCronListener = ((ListenerErrorContext) listenerContext).getErrorCronListener();
                    try {
                        // Regardless of whether it is an independent propagation mechanism or not,
                        // the event source listener needs to be executed first.
                        errorCronListener.failed(listenerContext, e);
                    }
                    catch (Throwable ex) {
                        failedFallback(errorCronListener, listenerContext, ex);
                    }
                    // If ISOLATE strategy is adopted, terminate the event propagation directly
                    if (errorCronListener.getListenerErrorPropagateStrategy() == ListenerErrorPropagateStrategy.ISOLATE)
                    {
                        return;
                    }
                    // PROPAGATE strategy: execute synchronous propagate listeners in order sorted.
                    for (CronListener propagateCronListener : collector.newQueryBuilder().sync().propagate().sort()
                            .build())
                    {
                        if (propagateCronListener == errorCronListener) {
                            continue;
                        }
                        try {
                            propagateCronListener.failed(listenerContext, e);
                        }
                        catch (Throwable ex) {
                            failedFallback(propagateCronListener, listenerContext, ex);
                        }
                    }
                }
            }
            finally {
                if (lifecycleWrapper.matchFinally()) {
                    CONTEXT_LOCAL.remove();
                }
            }
        }
    }

    private static void failed(AsyncCronListener ac, ListenerContext listenerContext, Throwable ex) {
        try {
            ac.failed(listenerContext, ex);
        }
        catch (Throwable exf) {

            failedFallback(ac, listenerContext, ex);
        }
    }

    private static void failedFallback(CronListener c, ListenerContext listenerContext, Throwable ex) {
        try {
            c.failedFallback(ex);
        }
        catch (Throwable e) {
            LoggerFactory.getLogger(ListenerLifecycle.class).error(
                    "[{}] An exception occurred when executing listener fallback method [failedFallback]." +
                            " Task ID: {}, Listener Name: {}, Exception Message: {}",
                    c instanceof AsyncCronListener ? "AsyncCronListener" : "CronListener",
                    listenerContext.getID(),
                    c.getName(),
                    ex.getMessage(), ex
            );
        }
    }

    /**
     * Listener context thread-local data wrapper.
     * @since 3.0.2
     */
    private static class ListenerContextLocalData {

        /** Bound listener context instance. */
        private ListenerContext listenerContext;

        /** A mapping used to mark the asynchronous listener name and whether it is in an interrupt state. */
        private final Map<String, AtomicBoolean> asyncListenerInterruptionFlags = new HashMap<>();

        public ListenerContextLocalData(ListenerContext listenerContext) {
            this.listenerContext = listenerContext;
        }

        public AtomicBoolean getInterruptionFlag(AsyncCronListener asyncCronListener) {
            return asyncListenerInterruptionFlags
                    .computeIfAbsent(asyncCronListener.getName(), s -> new AtomicBoolean(false));
        }
    }

    /**
     * The help {@link ListenerLifecycle} wrapper class.
     * @since 3.0.2
     */
    private static class ListenerLifecycleWrapper {

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
     * @throws RuntimeException If the reflection execution method fails, the exception is converted.
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
     *     public class DefaultSourceContext {}
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
     * @throws BeanInstantiationException if the bean cannot be instantiated
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
}
