/*
 * Copyright 2026-? the original author or authors.
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

import java.util.List;

import static top.osjf.cron.core.listener.ListenerLifecycle.doListenerCallback;

/**
 * Implementation support class for scheduled task listener.
 *
 * <p>Internally used template support class, implementing {@link Runnable}, encapsulates
 * the complete lifecycle execution logic of Cron scheduled tasks in a unified manner:
 * Pre-task startup listening callback -> Execute original scheduled business task ->
 * Task success callback / Task exception failure callback.
 *
 * <p><strong>Internal execution rules</strong>
 * <ul>
 * <li>1.Traverse all {@link CronListener}s, and automatically distinguish synchronous/asynchronous
 * listeners through {@link ListenerLifecycle#doListenerCallback(CronListener, Runnable)};</li>
 * </li>
 * <li>2.{@link AsyncCronListener} will use its own bound thread pool to execute callbacks asynchronously,
 * while ordinary listeners execute synchronously on the current scheduling thread;</li>
 * <li>Globally catch {@link Throwable}, whether it's a business exception or a system error, and trigger
 * a failure listening callback to ensure that exception events are not lost.</li>
 *
 * <p>Adopt the template method pattern, where subclasses provide the original task, listener collection,
 * and task context, reusing a unified listener orchestration logic.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class ListenerExecuteSupport implements Runnable {

    /**
     * A unified execution entry for scheduled tasks, orchestrating the complete task
     * lifecycle and listener event callbacks.
     *
     * <strong>Execution process:</strong>
     * <ul>
     * <li>1.Traverse all scheduled listeners and dispatch the 'task start' event callback;</li>
     * <li>2.Execute the user's original scheduled task business logic;</li>
     * <li>3.No exception in task: Distribute the callback event for [task execution success];</li>
     * <li>4.Any exception occurs in the task: catch Throwable, dispatch a 'task execution failure'
     * event callback, and pass the exception object.</li>
     * </ul>
     *
     * <p>The listener will automatically select the current scheduling thread for synchronous
     * execution based on its type, or use a custom thread pool for asynchronous execution of callbacks.
     */
    @Override
    public void run() {
        List<CronListener> cronListeners = getCronListeners();
        ListenerContext listenerContext = getListenerContext();
        try {
            // Notify all cron listeners that the task is about to start
            cronListeners.forEach(c -> doListenerCallback(c, ()-> c.start(listenerContext)));
            // Execute the main logic of the runnable
            getRaw().run();
            // Notify all cron listeners that the task has completed successfully
            cronListeners.forEach(c -> doListenerCallback(c, ()-> c.success(listenerContext)));
        } catch (Throwable e) {
            // If an error occurs during task execution, notify all cron listeners
            // of the failure, passing the exception context for further handling
            cronListeners.forEach(c -> doListenerCallback(c, ()-> c.failed(listenerContext, e)));
        }
    }
    /**
     * Returns the internal original scheduled business tasks to be executed
     * @return the original business scheduled task.
     */
    protected abstract Runnable getRaw();

    /**
     * Returns all the scheduled listeners bound to the current scheduled task
     * @return the list of Cron listeners associated with the task.
     */
    protected abstract List<CronListener> getCronListeners();

    /**
     * Obtain the current scheduled task lifecycle context for transparent transmission to
     * various listener callback methods.
     * @return the {@code ListenerContext} instance.
     */
    protected abstract ListenerContext getListenerContext();
}
