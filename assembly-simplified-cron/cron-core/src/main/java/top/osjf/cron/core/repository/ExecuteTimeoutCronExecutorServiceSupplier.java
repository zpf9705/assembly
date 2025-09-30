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

import top.osjf.cron.core.lifecycle.SuperiorProperties;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutorService;

/**
 * The implementation class of {@link CronExecutorServiceSupplier} is provided to
 * create a {@link ExecuteTimeoutThreadPoolExecutor} thread pool for task scheduling.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class ExecuteTimeoutCronExecutorServiceSupplier implements CronExecutorServiceSupplier {

    private final SuperiorProperties superiorProperties;

    /**
     * Constructs a {@link ExecuteTimeoutCronExecutorServiceSupplier} by given initial {@link SuperiorProperties}.
     * @param superiorProperties the given initial {@link SuperiorProperties}.
     */
    public ExecuteTimeoutCronExecutorServiceSupplier(SuperiorProperties superiorProperties) {
        this.superiorProperties = superiorProperties;
    }

    @Nonnull
    @Override
    public ExecutorService get() {
        return new ExecuteTimeoutThreadPoolExecutor(superiorProperties);
    }
}
