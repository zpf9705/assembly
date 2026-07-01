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

import io.micrometer.core.instrument.LongTaskTimer;
import top.osjf.cron.core.micrometer.MeterRegistryDelegation;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import static top.osjf.cron.core.micrometer.RepositoryTagConstants.*;

/**
 * Abstract template support class for orchestrating cron task listener lifecycle execution.
 *
 * <p>This abstract class implements {@link Runnable} and adopts the Template Method Pattern,
 * which encapsulates the standard execution flow of scheduled tasks and uniformly manages the
 * three core lifecycle callbacks: <b>task startup</b>, <b>task execution success</b>, <b>task
 * execution failure</b>.
 *
 * <p><strong>Core Execution Mechanism</strong>
 * <ul>
 * <li>1. The complete execution flow: trigger the {@code START} listener callback before task
 * execution → run the original scheduled business logic → trigger the {@code SUCCESS} callback
 * if executed normally, otherwise capture any {@link Throwable} and trigger the {@code FAILED}
 * callback;</li>
 * <li>2. Delegate listener event distribution to {@link ListenerLifecycle}, which automatically
 * distinguishes {@link AsyncCronListener}(executed asynchronously via self-bound thread pool)
 * and ordinary {@link CronListener}(executed synchronously on the current scheduling thread);
 * </li>
 * <li>3. The global try-catch captures all runtime exceptions of business tasks to ensure that
 * task failure events can be completely notified to all registered listeners without loss;
 * </li>
 * </ul>
 *
 * <p>Subclasses must implement three abstract methods to provide the original scheduled task,the
 * global listener collector and the runtime task context, so as to reuse the unified lifecycle
 * scheduling capability defined by the parent class.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class ListenerExecuteSupport implements Runnable {

    private final AtomicBoolean runningFlag = new AtomicBoolean(false);

    /**
     * @see #doStart()
     * @see #doSuccess()
     * @see #doFailed(Throwable)
     */
    @Override
    public void run() {
        // Basic information for registering long tasks...
        Optional<LongTaskTimer> timer = MeterRegistryDelegation.longTaskTimer(TASK_BODY_EXECUTION_TIMER_KEY,
                null, MODULE_TAG_KEY, getListenerContext().getRepositoryContext().getRepository().getName());
        // Set the task start marker...
        runningFlag.set(true);
        try {
            // Notify all cron listeners that the task is about to start
            doStart();
            // Start collecting long task metrics...
            Optional<LongTaskTimer.Sample> sample
                    = MeterRegistryDelegation.startLongTaskTimer(timer.orElse(null));
            // Execute the main logic of the runnable
            try {
                getRaw().run();
            }
            finally {
                // End collecting long task metrics...
                sample.ifPresent(MeterRegistryDelegation::stopSample);
            }
            // Notify all cron listeners that the task has completed successfully
            doSuccess();
        }
        catch (Throwable ex) {
            // If an error occurs during task execution, notify all cron listeners
            // of the failure, passing the exception context for further handling
            doFailed(ex);
        }
        finally {
            // Set the task end marker...
            runningFlag.set(false);
        }
    }

    /**
     * Trigger task [Start] lifecycle callback: = Distribute task start events to all synchronous
     * and asynchronous timing listeners.
     */
    private void doStart() {
        ListenerLifecycle.START.consumerListeners(this::getListenerContext, null, getCronListenerCollector());
    }

    /**
     * Trigger task [Success] lifecycle callback: Distribute task execution success events to all
     * registered scheduled listeners.
     */
    private void doSuccess() {
        ListenerLifecycle.SUCCESS.consumerListeners(this::getListenerContext, null, getCronListenerCollector());
    }

    /**
     * Trigger task [failed] lifecycle callback: Distribute task execution failure events to all
     * listeners, carrying exception stack information.
     * @param ex the exception that occur during task execution or during the monitoring of execution.
     */
    private void doFailed(Throwable ex) {
        ListenerLifecycle.FAILED.consumerListeners(this::getListenerContext, ex, getCronListenerCollector());
    }

    /**
     * Check whether the current cron task is being executed.
     * @return {@code true} if the task is running, otherwise {@code false}
     */
    public boolean isRunning() {
        return runningFlag.get();
    }

    /**
     * Returns the internal original scheduled business tasks to be executed
     * @return the original business scheduled task.
     */
    protected abstract Runnable getRaw();

    /**
     * Returns the instance of scheduled task listener collector.
     * @return the instance of scheduled task listener collector.
     */
    protected abstract CronListenerCollector getCronListenerCollector();

    /**
     * Returns the current scheduled task lifecycle context for transparent transmission to
     * various listener callback methods.
     * @return the {@code ListenerContext} instance.
     */
    protected abstract ListenerContext getListenerContext();
}
