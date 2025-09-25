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

import top.osjf.cron.core.exception.CronInternalException;

/**
 * Throwing this exception indicates a runtime exception during
 * task scheduling execution.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class RunningException extends CronInternalException {

    private static final long serialVersionUID = -6478314724385183507L;

    /**
     * Constructs a new {@code RunningException} with {@code null} as its
     * detail message.
     */
    public RunningException() {
        super();
    }

    /**
     * Constructs a new {@code RunningException} with the specified detail message.
     *
     * @param message the internal detail message.
     */
    public RunningException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code RunningException} with the specified internal cause.
     *
     * @param cause the internal cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).
     */
    public RunningException(Throwable cause) {
        super(cause);
    }
}
