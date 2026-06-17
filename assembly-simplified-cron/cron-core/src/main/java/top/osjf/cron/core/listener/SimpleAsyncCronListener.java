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


package top.osjf.cron.core.listener;

import top.osjf.cron.core.lang.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple asynchronous timer task listener with a built-in single-thread pool ensures the orderly
 * execution of callback events, avoiding blocking the main thread of the timer task.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class SimpleAsyncCronListener extends SimpleCronListener implements AsyncCronListener {

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    /**
     * @return Return to the default single-thread thread pool for asynchronously and orderly executing
     * all the scheduled task lifecycle callbacks of the current listener.
     */
    @NotNull
    @Override
    public ExecutorService get() {
        return executorService;
    }
}
