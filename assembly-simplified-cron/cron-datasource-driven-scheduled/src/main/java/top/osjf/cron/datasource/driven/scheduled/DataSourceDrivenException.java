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


package top.osjf.cron.datasource.driven.scheduled;

import top.osjf.cron.core.exception.CronInternalException;

/**
 * Indicate errors thrown during the data source configuration task.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
public class DataSourceDrivenException extends CronInternalException {

    private static final long serialVersionUID = -642360262042974699L;

    /**
     * Constructs a new {@code DataSourceDrivenException} with the specified detail message.
     *
     * @param message the internal detail message.
     */
    public DataSourceDrivenException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code DataSourceDrivenException} with the specified detail message and
     * the specified internal cause
     *
     * @param message the internal detail message.
     * @param cause   the internal cause (which is saved for later retrieval by the
     *                {@link #getCause()} method).
     */
    public DataSourceDrivenException(String message, Throwable cause) {
        super(message, cause);
    }
}
