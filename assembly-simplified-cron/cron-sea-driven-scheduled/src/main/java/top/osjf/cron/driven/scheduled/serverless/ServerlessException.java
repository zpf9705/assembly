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


package top.osjf.cron.driven.scheduled.serverless;

import top.osjf.cron.core.exception.CronFrameworkException;

/**
 * Indicate errors that occur during the startup process of the serverless function.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class ServerlessException extends CronFrameworkException {
    private static final long serialVersionUID = -4058750405000310384L;

    /**
     * Constructs a new {@code CronInternalException} with the specified detail message.
     *
     * @param message the internal detail message.
     */
    public ServerlessException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code CronInternalException} with the specified internal cause.
     *
     * @param cause the internal cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).
     */
    public ServerlessException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code CronInternalException} with the specified detail message and
     * the specified internal cause
     *
     * @param message the internal detail message.
     * @param cause   the internal cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).
     */
    public ServerlessException(String message, Throwable cause) {
        super(message, cause);
    }
}
