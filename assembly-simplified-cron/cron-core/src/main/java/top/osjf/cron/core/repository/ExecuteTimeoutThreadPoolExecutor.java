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

import top.osjf.cron.core.lang.NotNull;
import top.osjf.cron.core.lang.Nullable;
import top.osjf.cron.core.lifecycle.SuperiorProperties;

import java.util.concurrent.Future;

/**
 * A thread pool executor extended from {@link SuperiorProperties ParsedThreadPoolExecutor},
 * used to support timeout monitoring for specific tasks (tasks that implement the
 * {@link TimeoutMonitoringRunnable} interface).
 *
 * When the submitted task is of type {@link TimeoutMonitoringRunnable}, the executor wraps
 * it as a task that can monitor execution time and controls its timeout through the internal
 * {@link Future} mechanism. If the task execution time exceeds the preset threshold, the
 * corresponding monitoring behavior can be triggered according to the configuration.
 *
 * If method {@link #execute(Runnable)}'s parameter {@link Runnable command} not be
 * {@link TimeoutMonitoringRunnable} when using {@link RunTimeoutRegistrarRepository},
 * it maybe be wrapped by other cron framework, so, you can set a {@link TimeoutMonitoringRunnableExtractor}
 * to support extract a {@link TimeoutMonitoringRunnable} from {@link Runnable command}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class ExecuteTimeoutThreadPoolExecutor extends SuperiorPropertiesParsedThreadPoolExecutor {

    @Nullable private TimeoutMonitoringRunnableExtractor extractor;

    /**
     * Creates a new {@code ExecuteTimeoutThreadPoolExecutor} with the given initial
     * parameter.
     * @param properties the initial {@link SuperiorProperties}.
     */
    public ExecuteTimeoutThreadPoolExecutor(SuperiorProperties properties) {
        super(properties);
    }

    /**
     * Sets a {@link TimeoutMonitoringRunnableExtractor} used to extract a {@link TimeoutMonitoringRunnable}.
     * @param extractor the {@link TimeoutMonitoringRunnableExtractor} to set.
     */
    public void setExtractor(@Nullable TimeoutMonitoringRunnableExtractor extractor) {
        this.extractor = extractor;
    }

    /**
     * {@inheritDoc}
     * @throws RunningException if running process fails occur.
     */
    @Override
    public void execute(@NotNull Runnable command) throws RunningException {

        if (command instanceof TimeoutMonitoringRunnable) {
            ((TimeoutMonitoringRunnable) command).setMonitoringExecutorIfAbsent(this).run();
        }
        else {
            TimeoutMonitoringRunnable ti = null;
            if (extractor != null) {
                try {
                    ti = extractor.extract(command);
                }
                catch (Exception ex){
                    throw new RunningException("Extract Timeout Monitoring Runnable failed", ex);
                }
            }
            if (ti != null) {
                if (ti.isIdentityRun()) {
                    ti.setMonitoringExecutorIfAbsent(this).run();
                }
                else {
                    Future<?> future = submit(command);
                    ti.get(future);
                }
            }
            else {
                super.execute(command);
            }
        }
    }

    /**
     * The {@code TimeoutMonitoringRunnableExtractor} is a {@link TimeoutMonitoringRunnable}
     * extractor used to extract a {@link TimeoutMonitoringRunnable} from specify {@link Runnable command}.
     */
    @FunctionalInterface
    public interface TimeoutMonitoringRunnableExtractor {

        /**
         * Extract a {@link TimeoutMonitoringRunnable} from specify {@link Runnable}.
         * @param command the input {@link Runnable} to extract.
         * @return the Extracted {@link TimeoutMonitoringRunnable}.
         * @throws Exception if extract processing fails is occur.
         */
        @Nullable
        TimeoutMonitoringRunnable extract(Runnable command) throws Exception;
    }

}
