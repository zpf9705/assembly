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
 * Thrown when the current Cron task repository does not support the requested
 * operation or event.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class UnsupportedException extends CronInternalException {

    private static final long serialVersionUID = 1842038613933398163L;

    /**
     * Constructs a new {@code UnsupportedException} with {@code null} as its
     * detail message.
     */
    public UnsupportedException() {
        super();
    }

    /**
     * Constructs a new {@code UnsupportedException} with the specified detail message.
     *
     * @param message the internal detail message.
     */
    public UnsupportedException(String message) {
        super(message);
    }
}
