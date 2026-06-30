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
import top.osjf.commons.lang.OrderComparator;
import top.osjf.commons.lang.Ordered;
import top.osjf.commons.lang.Wrapper;
import top.osjf.commons.util.Assert;
import top.osjf.commons.util.CollectionUtils;
import top.osjf.cron.core.repository.Repository;
import top.osjf.cron.core.repository.RepositoryContext;
import top.osjf.cron.core.repository.TypedRepositoryContext;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * The {@code CronListenerCollector} abstract class is used to manage a set of {@code CronListener}
 * listeners and provide callback methods for startup, success, and failure.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class CronListenerCollector implements Wrapper {

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final LinkedList<CronListener> cronListeners = new LinkedList<>();

    private final RepositoryContext repositoryContext;

    @Nullable private RunningThreadHolder runningThreadHolder;

    /**
     * @param repository The resource class used for listening to callbacks
     *                   in {@link ListenerContext}.
     * @since 3.0.2
     */
    public CronListenerCollector(Repository repository) {
        this.repositoryContext = new TypedRepositoryContext(repository);
    }

    /**
     * Add a {@code CronListener} to the listener list if it does not already exist.
     * @param cronListener The {@code CronListener}  instance to be added.
     */
    public void addCronListener(CronListener cronListener) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (!cronListeners.contains(cronListener)) {
                cronListeners.add(cronListener);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Add a {@code CronListener} instance to the beginning of the listener list
     * if it does not already exist.
     * @param cronListener The {@code CronListener}  instance to be added.
     */
    public void addFirstCronListener(CronListener cronListener){
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (!cronListeners.contains(cronListener)) {
                cronListeners.addFirst(cronListener);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Add a {@code CronListener} instance to the end of the listener list
     * if it does not already exist.
     * @param cronListener The {@code CronListener}  instance to be added.
     */
    public void addLastCronListener(CronListener cronListener){
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            if (!cronListeners.contains(cronListener)) {
                cronListeners.addLast(cronListener);
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Remove the specified {@code CronListener} from the listener list.
     * @param cronListener {@code CronListener} instance to be removed.
     * @return {@code true} if the listener existed and was successfully removed;
     *         {@code false} if the listener was not found in the {@link #cronListeners}.
     */
    public boolean removeCronListener(CronListener cronListener) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            return cronListeners.remove(cronListener);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Remove the specified {@code CronListener} by the input listener name.
     * @param listenerName the {@code CronListener} name to be removed.
     * @return {@code true} if the listener existed and was successfully removed;
     *         {@code false} if the listener was not found in the {@link #cronListeners}.
     * @since 3.0.2
     */
    public boolean removeCronListener(String listenerName) {
        final Lock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            CronListener specified = null;
            for (CronListener cronListener : cronListeners) {
                if (Objects.equals(listenerName, cronListener.getName())) {
                    specified = cronListener;
                    break;
                }
            }
            return specified != null && cronListeners.remove(specified);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Return the specified {@code CronListener} by the input listener name.
     * @param listenerName the {@code CronListener} name to query.
     * @since 3.0.2
     */
    @Nullable
    public CronListener getListener(String listenerName) {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            for (CronListener cronListener : cronListeners) {
                if (Objects.equals(listenerName, cronListener.getName())) {
                    return cronListener;
                }
            }
            return null;
        } finally {
            readLock.unlock();
        }
    }

    /**
     * @return Return the number of registered listeners.
     * @since 3.0.2
     */
    public long getListenerSize() {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return cronListeners.size();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Return the unmodifiable list of {@code CronListener} instances saved by this collection
     * management instance.
     * @return the list of {@code CronListener} instances.
     */
    public List<CronListener> getCronListeners() {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return Collections.unmodifiableList(cronListeners);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Return a {@code Boolean} flag that the input {@code CronListener} already registered
     * in {@link #cronListeners}.
     * @param cronListener {@code CronListener} instance for determining registration or not.
     * @return a {@code Boolean} flag that the input {@code CronListener} already registered
     *         in {@link #cronListeners}.
     * @since 1.0.4
     */
    public boolean hasCronListener(CronListener cronListener) {
        final Lock readLock = lock.readLock();
        readLock.lock();
        try {
            return cronListeners.contains(cronListener);
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Create a listener conditional query builder based on all registered cron listeners.
     * Support filtering by synchronous/asynchronous type and exception propagation strategy.
     *
     * @return listener query builder instance
     * @since 3.0.2
     */
    public ListenerQueryBuilder newQueryBuilder() {
        return new ListenerQueryBuilder(getCronListeners());
    }

    /**
     * Builder for conditional filtering of registered {@link CronListener}.
     * <p>
     * Filter rules:
     * <ul>
     * <li>Filter by execution mode: synchronous / asynchronous</li>
     * <li>Filter by listener exception propagation strategy (only available for synchronous listeners)</li>
     * <li>Attempting to specify a propagation strategy when querying asynchronous listeners will trigger an
     * assertion error.</li>
     * </ul>
     * @since 3.0.2
     */
    public static class ListenerQueryBuilder {

        private final List<CronListener> cronListeners;

        private boolean sync = true;

        @Nullable private ListenerErrorPropagateStrategy propagateStrategy;

        private boolean sort = false;

        private boolean buildFlag = false;

        /**
         * Private constructor, create builder via {@link #newQueryBuilder()}
         * @param cronListeners original full list of cron listeners
         */
        private ListenerQueryBuilder(List<CronListener> cronListeners) {
            this.cronListeners = cronListeners;
        }

        /**
         * Set filter rule: only query synchronous {@link CronListener}
         * @return current builder instance for chain call
         */
        public ListenerQueryBuilder sync() {
            checkBuildFlag();
            this.sync = true;
            return this;
        }

        /**
         * Set filter rule: only query asynchronous {@link AsyncCronListener}
         *
         * After calling this method, {@link #isolate()} or {@link #propagate()} cannot be invoked,
         * otherwise an assertion exception will be thrown during build.
         *
         * @return current builder instance for chain call
         */
        public ListenerQueryBuilder async() {
            checkBuildFlag();
            this.sync = false;
            return this;
        }

        /**
         * Set strategy filter: only match listeners with ISOLATE exception propagation strategy
         * can only be used with {@link #sync()}.
         *
         * @return current builder instance for chain call
         */
        public ListenerQueryBuilder isolate() {
            checkBuildFlag();
            this.propagateStrategy = ListenerErrorPropagateStrategy.ISOLATE;
            return this;
        }

        /**
         * Set strategy filter: only match listeners with PROPAGATE exception propagation strategy
         * can only be used with {@link #sync()}.
         *
         * @return current builder instance for chain call
         */
        public ListenerQueryBuilder propagate() {
            checkBuildFlag();
            this.propagateStrategy = ListenerErrorPropagateStrategy.PROPAGATE;
            return this;
        }

        /**
         * Enable sorting of the result set for the listener.
         * <p>
         * After invoking this method, the final set of selected timer listeners will be sorted using
         * {@link OrderComparator} based on the return value of the {@link Ordered} interface implemented
         * by the listeners. The listener with a smaller sorting value will have a higher execution priority
         * and will be scheduled for execution earlier
         *
         * @return current builder instance for chain call
         */
        public ListenerQueryBuilder sort() {
            checkBuildFlag();
            this.sort = true;
            return this;
        }

        /**
         * Execute filtering rules and return the matched listener list.
         *
         * @return filtered listener list, empty list if original collection is empty
         * @throws IllegalArgumentException if asynchronous query specifies propagation strategy
         */
        public List<CronListener> build() {
            checkBuildFlag();
            if (CollectionUtils.isEmpty(cronListeners)) {
                buildFlag = true;
                return Collections.emptyList();
            }
            Assert.isTrue(!(!sync && propagateStrategy != null),
                    "Asynchronous listener does not support propagation strategies");
            List<CronListener> result = cronListeners.stream().filter(cronListener ->
                            (sync != (cronListener instanceof AsyncCronListener)) &&
                    (propagateStrategy == null || propagateStrategy == cronListener.getListenerErrorPropagateStrategy()))
                    .collect(Collectors.toList());
            if (sort) OrderComparator.sort(result);
            buildFlag = true;
            return result;
        }

        /**
         * Check whether the builder has completed construction.
         * If built, forbid modifying builder conditions.
         * @throws IllegalStateException if already built
         */
        private void checkBuildFlag() {
            Assert.state(!buildFlag, "The build() method can only be invoked once.");
        }
    }

    /**
     * Return the type of {@code ListenerContext}, usually an instantiated subclass
     * of {@code ListenerContext}.
     *
     * <p>This method can be used in conjunction with annotation {@link ListenerContextTypeProvider}.
     * The non-empty types returned by this method are used first, and the original context can be
     * specified in annotation {@link ListenerContextTypeProvider} to participate in the construction
     * of listening context objects {@link ListenerContextTypeProvider#sourceContextBuildMode()}.
     * <p>
     * The code case can be seen as follows:
     * <pre>
     *
     *     &#064;ListenerContextTypeProvider(sourceContextBuildMode = ListenerContextTypeProvider.BuildMode.SET)
     *     public CronListenerCollectorImpl extends CronListenerCollector {
     *
     *          &#064;Override
     *          protected Class&lt;? extends ListenerContext&gt; getListenerContextClass() {
     *               return ExampleListenerContext.class;
     *          }
     *     }
     *
     * </pre>
     *
     * @return The type of {@code ListenerContext}
     */
    @Nullable
    protected Class<? extends ListenerContext> getListenerContextClass() {
        return null;
    }

    /**
     * The listening cycle at the beginning of task execution, providing an original
     * context object.
     *
     * @param sourceContext the original context object provided by the framework used
     *                      for executing scheduled tasks.
     */
    protected void doStartListener(Object sourceContext) {
        doListeners(ListenerLifecycle.START, sourceContext, null);
    }

    /**
     * The listening period when the task is successfully executed, providing an original
     * context object.
     *
     * @param sourceContext the original context object provided by the framework used
     *                      for executing scheduled tasks.
     */
    protected void doSuccessListener(Object sourceContext) {
        doListeners(ListenerLifecycle.SUCCESS, sourceContext, null);
    }

    /**
     * The listening period when the task fails, providing an original context object.
     *
     * @param sourceContext the original context object provided by the framework used
     *                      for executing scheduled tasks.
     * @param e             error type object thrown during task execution.
     */
    protected void doFailedListener(Object sourceContext, Throwable e) {
        doListeners(ListenerLifecycle.FAILED, sourceContext,  e);
    }

    /**
     * Based on the provided execution cycle, enumerate the classes and execute the
     * corresponding listening methods.
     *
     * @param listenerLifecycle the lifecycle enumeration class for task execution.
     * @param sourceContext     the original context object provided by the framework used
     *                          for executing scheduled tasks.
     * @param e                 error type object thrown during task execution only when failed.
     */
    private void doListeners(ListenerLifecycle listenerLifecycle, Object sourceContext, @Nullable Throwable e) {
        listenerLifecycle.consumerListeners(sourceContext, repositoryContext, e, this);
    }

    /**
     * Lazy initialize the {@link RunningThreadHolder} instance to implement the interruption function
     * of the task thread, and subclasses can call the initialization as needed.
     * @return self
     * @since 3.0.2
     */
    public CronListenerCollector initRunningHolder() {
        if (runningThreadHolder == null)
            runningThreadHolder = new RunningThreadHolder();
        return this;
    }

    /**
     * @return Return the record instance of the task running thread, and
     * obtain it after initializing it by calling {@link #initRunningHolder()}
     * as needed.
     * @throws IllegalArgumentException if {@link #runningThreadHolder} uninitialized.
     * @since 3.0.2
     */
    public RunningThreadHolder getRunningThreadHolder() {
        Assert.notNull(runningThreadHolder, "Uninitialized RunningThreadHolder");
        return runningThreadHolder;
    }

    /**
     * Delegation method of {@link RunningThreadHolder#addCurrentRunningThread(String)}.
     * @param id the unique identifier of the registered cron task.
     * @since 3.0.2
     */
    public void addCurrentRunningThread(String id) {
        getRunningThreadHolder().addCurrentRunningThread(id);
    }

    /**
     * Delegation method of {@link RunningThreadHolder#removeCurrentRunningThread(String)}.
     * @param id the unique identifier of the registered cron task.
     * @since 3.0.2
     */
    public void removeCurrentRunningThread(String id) {
        getRunningThreadHolder().removeCurrentRunningThread(id);
    }

    /**
     * Delegation method of {@link RunningThreadHolder#removeRunningThreads(String)}.
     * @param id the unique identifier of the registered cron task.
     * @since 3.0.2
     */
    public void removeRunningThreads(String id) {
        getRunningThreadHolder().removeRunningThreads(id);
    }

    /**
     * Delegation method of {@link RunningThreadHolder#removeAllRunningThreads()}.
     * @since 3.0.2
     */
    public void removeAllRunningThreads() {
        getRunningThreadHolder().removeAllRunningThreads();
    }
}
