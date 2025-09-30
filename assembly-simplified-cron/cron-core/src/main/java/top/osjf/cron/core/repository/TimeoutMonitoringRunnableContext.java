/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.cron.core.repository;

import top.osjf.cron.core.lang.Nullable;

/**
 * {@code TimeoutMonitoringRunnableContext} is a management abstract class that
 * sets a {@link TimeoutMonitoringRunnable} in the thread lifecycle to ensure
 * its visibility in the thread context.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class TimeoutMonitoringRunnableContext {

    private static final ThreadLocal<TimeoutMonitoringRunnable> context = new ThreadLocal<>();


    /**
     * Sets the given {@code TimeoutMonitoringRunnableContext} into the current thread's context.
     *
     * @param runnable The {@code TimeoutMonitoringRunnableContext} instance to bind to the current thread.
     */
    public static void set(@Nullable TimeoutMonitoringRunnable runnable) {
        if (runnable == null) {
            context.remove();
        } else {
            context.set(runnable);
        }
    }

    /**
     * Retrieves the currently bound {@code TimeoutMonitoringRunnable} from the thread-local context.
     *
     * @return The instance associated with the current thread, or {@literal null} if none.
     */
    public static TimeoutMonitoringRunnable get() {
        return context.get();
    }
}
