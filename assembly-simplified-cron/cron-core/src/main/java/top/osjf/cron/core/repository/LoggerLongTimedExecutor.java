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


package top.osjf.cron.core.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.commons.util.compat.ArrayUtils;

/**
 * A {@link CronTaskRepository.LongTimedExecutor} implementation that logs task execution
 * start and end timestamps using SLF4J.
 *
 * <p>This executor wraps a given {@link Runnable} and records its execution boundary
 * (start and finish) via INFO-level logs. The log messages include the execution
 * timestamp and an optional tag string for contextual identification.
 *
 * <p>This is particularly useful for monitoring long-running cron tasks, enabling
 * operators to trace execution timelines and associate logs with specific task
 * types or instances.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class LoggerLongTimedExecutor implements CronTaskRepository.LongTimedExecutor {

    private static final Logger logger = LoggerFactory.getLogger(LoggerLongTimedExecutor.class);

    private final String tagCombine;

    /**
     * Constructs a new {@code LoggerLongTimedExecutor} with the specified tags.
     *
     * <p>The tags are concatenated into a single comma-separated string for
     * inclusion in log messages. If no tags are provided, the log will display
     * {@code "No tags"} instead.
     *
     * @param tags the contextual tags to be included in the log messages
     */
    public LoggerLongTimedExecutor(String... tags) {
        this.tagCombine = ArrayUtils.isNotEmpty(tags) ? String.join(",", tags) : "No tags";
    }

    /**
     * Logs the start of a task execution with the current system timestamp.
     * {@inheritDoc}
     */
    @Override
    public void start() {
        logger.info("Task execution started, start time [{}], tags [{}].", System.currentTimeMillis(), tagCombine);
    }

    /**
     * Logs the completion of a task execution with the current system timestamp.
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        logger.info("Task execution finished, end time [{}], tags [{}].", System.currentTimeMillis(), tagCombine);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void record(Runnable runnable) {
        start();
        try {
            runnable.run();
        }
        finally {
            stop();
        }
    }
}
