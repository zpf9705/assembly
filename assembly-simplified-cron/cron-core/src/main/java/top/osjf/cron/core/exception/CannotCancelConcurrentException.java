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


package top.osjf.cron.core.exception;

import top.osjf.cron.core.repository.DisallowConcurrentExecution;

/**
 * Thrown when failing to cancel the disallow-concurrent execution constraint of a scheduled task.
 * <p>Common scenarios that trigger this exception include:
 * <ul>
 * <li>The concurrency constraint is declared statically via {@link DisallowConcurrentExecution}
 * annotation, which is immutable at runtime;</li>
 * <li>The target task does not exist or has no disallow-concurrent configuration bound;</li>
 * <li>The current task executor does not support dynamic modification of concurrency constraints.</li>
 * </ul>
 * </p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class CannotCancelConcurrentException extends CronFrameworkException {

    private static final long serialVersionUID = 2526570052771350989L;

    /**
     * Constructs a new {@code CannotCancelConcurrentException} with the specified detail message.
     *
     * @param message the internal detail message.
     */
    public CannotCancelConcurrentException(String message) {
        super(message);
    }
}
