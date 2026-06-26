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

/**
 * Base runtime exception for all exceptions thrown within the cron scheduling framework.
 *
 * <p>All custom business exceptions defined in this scheduled task component must inherit
 * from this top-level exception. It unifies the exception entry for upper applications to
 * implement centralized exception capturing, log aggregation, exception monitoring and
 * global exception handling.
 *
 * <p>Typical exception scenarios include but are not limited to: cron expression parsing error,
 * task duplicate registration, illegal task operation, concurrent execution policy violation,
 * task startup/shutdown failure, task resource access exception and other framework constraint
 * verification failures.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class CronFrameworkException extends RuntimeException {

    private static final long serialVersionUID = -5811542520082807241L;

    /**
     * Constructs a new {@code CronException} with {@code null} as its detail message.
     */
    public CronFrameworkException() {
        super();
    }

    /**
     * Constructs a new {@code CronException} with the specified detail message.
     *
     * @param message the internal detail message.
     */
    public CronFrameworkException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code CronException} with the specified internal cause.
     *
     * @param cause the internal cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).
     */
    public CronFrameworkException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new {@code CronException} with the specified detail message and
     * the specified internal cause
     *
     * @param message the internal detail message.
     * @param cause   the internal cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).
     */
    public CronFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }
}
