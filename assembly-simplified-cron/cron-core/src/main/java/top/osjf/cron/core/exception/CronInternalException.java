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


package top.osjf.cron.core.exception;

/**
 * The internal exception thrown by the framework during scheduled task
 * execution has its real cause in {@link CronInternalException#getCause()}.
 *
 * <p>To convert it into a runtime exception, developers need to obtain the
 * cause of the exception for analysis.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class CronInternalException extends RuntimeException {

    private static final long serialVersionUID = 1460657936213652266L;

    /**
     * Constructs a new {@code CronInternalException} with {@code null} as its
     * detail message.
     * @since 3.0.1
     */
    public CronInternalException() {
        super();
    }

    /**
     * Constructs a new {@code CronInternalException} with the specified detail message.
     *
     * @param message the internal detail message.
     */
    public CronInternalException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code CronInternalException} with the specified internal cause.
     *
     * @param cause the internal cause (which is saved for later retrieval by the
     *              {@link #getCause()} method).
     */
    public CronInternalException(Throwable cause) {
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
    public CronInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}
