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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The {@code CronListenerCollector} abstract class is used to manage a set of {@code CronListener}
 * listeners and provide callback methods for startup, success, and failure.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class CronListenerCollector {

    private final Lock lock = new ReentrantLock();

    private final LinkedList<CronListener> cronListeners = new LinkedList<>();

    /**
     * Add a {@code CronListener} to the listener list if it does not already exist.
     *
     * @param cronListener The {@code CronListener}  instance to be added.
     */
    public void addCronListener(@NotNull CronListener cronListener) {
        lock.lock();
        try {
            if (!cronListeners.contains(cronListener)) {
                cronListeners.add(cronListener);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Add a {@code CronListener} instance to the beginning of the listener list
     * if it does not already exist.
     *
     * @param cronListener The {@code CronListener}  instance to be added.
     */
    public void addFirstCronListener(@NotNull CronListener cronListener){
        lock.lock();
        try {
            if (!cronListeners.contains(cronListener)) {
                cronListeners.addFirst(cronListener);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Add a {@code CronListener} instance to the end of the listener list
     * if it does not already exist.
     *
     * @param cronListener The {@code CronListener}  instance to be added.
     */
    public void addLastCronListener(@NotNull CronListener cronListener){
        lock.lock();
        try {
            if (!cronListeners.contains(cronListener)) {
                cronListeners.addLast(cronListener);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Remove the specified {@code CronListener} from the listener list.
     *
     * @param cronListener {@code CronListener} instance to be removed.
     */
    public void removeCronListener(@NotNull CronListener cronListener) {
        lock.lock();
        try {
            cronListeners.remove(cronListener);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Return the unmodifiable list of {@code CronListener} instances saved by this collection
     * management instance.
     *
     * @return the list of {@code CronListener} instances.
     */
    public List<CronListener> getCronListeners() {
        lock.lock();
        try {
            return Collections.unmodifiableList(cronListeners);
        } finally {
            lock.unlock();
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
     * @see ListenerContextSupport#createListenerContext
     */
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
        doListeners(ListenerLifecycle.FAILED, sourceContext, e);
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
    private void doListeners(ListenerLifecycle listenerLifecycle, Object sourceContext, Throwable e) {
        listenerLifecycle.consumerListeners(sourceContext, e, this);
    }
}
