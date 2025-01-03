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

package top.osjf.cron.spring.scheduler.task;

import top.osjf.cron.spring.scheduler.SchedulingContext;
import top.osjf.cron.spring.scheduler.SchedulingContextSupplier;
import top.osjf.cron.spring.scheduler.SchedulingRunnable;

/**
 * The enhanced version class of  {@link org.springframework.scheduling.config.FixedRateTask}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class FixedRateTask extends org.springframework.scheduling.config.FixedRateTask implements SchedulingContextSupplier {

    private final SchedulingRunnable schedulingRunnable;

    /**
     * Create a new {@code IntervalTask}.
     *
     * @param runnable     the underlying task to execute
     * @param interval     how often in milliseconds the task should be executed
     * @param initialDelay the initial delay before first execution of the task
     */
    public FixedRateTask(SchedulingRunnable runnable, long interval, long initialDelay) {
        super(runnable, interval, initialDelay);
        this.schedulingRunnable = runnable;
    }

    @Override
    public SchedulingContext get() {
        return schedulingRunnable.get();
    }
}
