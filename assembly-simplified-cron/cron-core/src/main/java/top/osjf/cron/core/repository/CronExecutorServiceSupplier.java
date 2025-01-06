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

import top.osjf.cron.core.lang.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

/**
 * The {@code CronExecutorServiceSupplier} interface defines a specific {@link Supplier}
 * specifically designed to provide ExecutorService instances, this interface is primarily
 * used in scenarios where scheduled task execution services are required (such as tasks
 * scheduled using Cron expressions), allowing users to customize how to create and configure
 * {@link ExecutorService} instances.
 *
 * <p>By implementing this interface, developers can provide custom {@link ExecutorService}
 * instances that may have specific thread pool configurations, thread name policies, or
 * other thread management features to meet specific application or performance requirements.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
@FunctionalInterface
public interface CronExecutorServiceSupplier extends Supplier<ExecutorService> {
    /**
     * Get a non-empty {@code ExecutorService} instance.
     *
     * <p>This method must return a configured ExecutorService instance that will be used
     * to execute scheduled tasks.Implementers should customize the configuration of thread
     * pools based on application requirements, such as the number of threads, thread names,
     * thread priorities, etc.
     *
     * @return A pre configured, non-empty {@code ExecutorService} instance.
     */
    @Override
    @NotNull
    ExecutorService get();
}
