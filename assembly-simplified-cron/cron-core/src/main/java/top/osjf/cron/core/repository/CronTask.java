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


package top.osjf.cron.core.repository;

/**
 * The {@code CronTask} class represents a timed task execution information wrapper
 * object, which contains a cron expression and a task to be executed (implemented
 * through {@code CronMethodRunnable}).
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public final class CronTask {

    private final String expression;

    private final CronMethodRunnable runnable;

    /**
     * Construct a {@code CronTask} instance by given cron expression and {@code CronMethodRunnable}.
     *
     * @param expression the cron expression defines the execution time of the task.
     * @param runnable   the task to be executed is implemented through {@code CronMethodRunnable}.
     */
    public CronTask(String expression, CronMethodRunnable runnable) {
        this.expression = expression;
        this.runnable = runnable;
    }

    /**
     * Get a cron expression that represents the frequency of task execution.
     *
     * @return the cron expression defines the execution time of the task.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Get the task to be executed {@code CronMethodRunnable}.
     *
     * @return the task to be executed is implemented through {@code CronMethodRunnable}.
     */
    public CronMethodRunnable getRunnable() {
        return runnable;
    }

    @Override
    public String toString() {
        return expression + "@" + runnable.toString();
    }
}
