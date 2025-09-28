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

package top.osjf.cron.core.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simple utility class for {@link java.util.concurrent.Executor}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public abstract class ExecutorUtils {

    /**
     * Shutdown the {@link ExecutorService} by given config property.
     *
     * @param executorService               the {@link ExecutorService} instance.
     * @param awaitTermination              the flag that whether await termination.
     * @param awaitTerminationTimeout       the maximum time to wait
     * @param awaitTerminationTimeoutUnit   the time unit of the timeout argument.
     * @throws NullPointerException if input {@link ExecutorService} or {@link TimeUnit} is {@literal null}.
     */
    public static void shutdownExecutor(ExecutorService executorService,
                                        boolean awaitTermination, long awaitTerminationTimeout,
                                        TimeUnit awaitTerminationTimeoutUnit) {
        if (awaitTermination) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(awaitTerminationTimeout, awaitTerminationTimeoutUnit)) {
                    // If the timeout is not completed, force termination.
                    executorService.shutdownNow();
                }
            } catch (InterruptedException ex) {
                // Restore interrupted state.
                Thread.currentThread().interrupt();
            } finally {
                // Ensure that the service has been completely terminated.
                if (!executorService.isTerminated()) {
                    executorService.shutdownNow();
                }
            }
        } else {
            // Directly force termination.
            executorService.shutdownNow();
        }
    }
}
