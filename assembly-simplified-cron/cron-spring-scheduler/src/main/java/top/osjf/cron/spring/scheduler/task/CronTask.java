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
 * The enhanced version class of {@link org.springframework.scheduling.config.CronTask}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class CronTask extends org.springframework.scheduling.config.CronTask implements SchedulingContextSupplier {

    private final SchedulingRunnable schedulingRunnable;

    /**
     * Create a new {@code CronTask}.
     *
     * @param runnable   the underlying task to execute
     * @param expression the cron expression defining when the task should be executed
     */
    public CronTask(SchedulingRunnable runnable, String expression) {
        super(runnable, expression);
        this.schedulingRunnable = runnable;
    }

    @Override
    public SchedulingContext get() {
        return schedulingRunnable.get();
    }
}
