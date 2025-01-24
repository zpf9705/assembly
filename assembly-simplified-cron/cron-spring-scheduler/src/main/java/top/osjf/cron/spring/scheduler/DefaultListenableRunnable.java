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

package top.osjf.cron.spring.scheduler;

import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.listener.ListenerContext;
import top.osjf.cron.core.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code DefaultListenerRunnable} class is a class that implements the
 * {@code ListenableRunnable} interface.
 * <p>
 * It is used to proxy the execution of the real running body {@link Runnable},
 * and adds listening to the running of this running body, allowing developers
 * to more accurately feel the execution status of this task.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class DefaultListenableRunnable implements ListenableRunnable {
    /**
     * The unique ID for this task.
     */
    private final String id;
    /**
     * The execution function for this task.
     */
    private final Runnable runnable;
    /**
     * The triggering method for the execution of this task.
     */
    private final Trigger trigger;
    /**
     * The list of eavesdroppers for the execution of this task.
     */
    private final List<CronListener> cronListeners = new ArrayList<>();
    /**
     * The listening context information for this task.
     */
    private final ListenerContext listenerContext;

    /**
     * Constructs a new {@code DefaultListenableRunnable} with the unique ID,
     * execution function ,triggering method,list of eavesdroppers.
     *
     * @param id            the unique ID for this task.
     * @param runnable      the execution function for this task.
     * @param trigger       the triggering method for the execution of this task.
     * @param cronListeners the list of eavesdroppers for the execution of this task.
     */
    public DefaultListenableRunnable(String id, Runnable runnable, @Nullable Trigger trigger,
                                     @Nullable List<CronListener> cronListeners) {
        this.id = id;
        this.runnable = runnable;
        this.trigger = trigger;
        if (CollectionUtils.isNotEmpty(cronListeners)) {
            this.cronListeners.addAll(cronListeners);
        }
        this.listenerContext = new ListenerContextImpl(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Runnable getRunnable() {
        return runnable;
    }

    @Nullable
    @Override
    public Trigger getTrigger() {
        return trigger;
    }

    @Override
    @Nullable
    public List<CronListener> getCronListeners() {
        return cronListeners;
    }

    /**
     * This method represents the execution body of a runnable task.
     * It first notifies all registered cron listeners of the start of the task,
     * then executes the main runnable logic, and finally notifies the listeners
     * of either the success or failure of the task execution.
     */
    @Override
    public void run() {
        try {
            // Notify all cron listeners that the task is about to start
            cronListeners.forEach(c -> c.start(listenerContext));
            // Execute the main logic of the runnable
            runnable.run();
            // Notify all cron listeners that the task has completed successfully
            cronListeners.forEach(c -> c.success(listenerContext));
        } catch (Throwable e) {
            // If an error occurs during task execution, notify all cron listeners
            // of the failure, passing the exception context for further handling
            cronListeners.forEach(c -> c.failed(listenerContext, e));
        }
    }
}
