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

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import top.osjf.cron.core.listener.CronListener;
import top.osjf.cron.core.util.CollectionUtils;

import java.util.List;

/**
 * Task execution running body, including callbacks implemented for
 * each stage of the execution cycle.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class SchedulingRunnable implements Runnable, SchedulingContextSupplier {

    private final SchedulingContext context;

    private final Runnable runnable;

    private final List<CronListener> cronListeners;

    /**
     * Is {@link #cronListeners} empty.
     */
    private final boolean hasSchedulingListener;

    /**
     * Create a new {@code SchedulingRunnable} within any task info.
     *
     * @param id            the unique ID of the task.
     * @param runnable      the executor of the task.
     * @param cronListeners the collection of listeners for the task.
     */
    public SchedulingRunnable(@NonNull String id, @NonNull Runnable runnable,
                              @Nullable List<CronListener> cronListeners) {
        this.runnable = runnable;
        this.context = new DefaultSchedulingContext(id, runnable, cronListeners);
        this.cronListeners = cronListeners;
        this.hasSchedulingListener = CollectionUtils.isNotEmpty(cronListeners);
    }

    /**
     * Execute the callback that is ready to start.
     */
    void onStart() {
        if (hasSchedulingListener) {
            cronListeners.forEach(c -> c.start(newSchedulingListenerContext()));
        }
    }

    /**
     * Successful callback execution.
     */
    void onSucceeded() {
        if (hasSchedulingListener) {
            cronListeners.forEach(c -> c.success(newSchedulingListenerContext()));
        }
    }

    /**
     * The callback that failed to execute.
     */
    void onFailed(Throwable e) {
        if (hasSchedulingListener) {
            cronListeners.forEach(c -> c.failed(newSchedulingListenerContext(), e));
        }
    }

    private SchedulingListenerContext newSchedulingListenerContext() {
        return new SchedulingListenerContext(context.getId(), context);
    }

    @Override
    public void run() {
        onStart();
        try {
            runnable.run();
            onSucceeded();
        } catch (Throwable e) {
            onFailed(e);
        }
    }

    @Override
    public SchedulingContext get() {
        return context;
    }
}
