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

/**
 * Throwing this type of exception indicates that the run has exceeded
 * the specified time limit.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
public class RunningTimeoutException extends RunningException {

    private static final long serialVersionUID = -4395166082604906410L;

    /**
     * Constructs a new {@code RunningTimeoutException} with {@code null} as its
     * detail message.
     */
    public RunningTimeoutException() {
        super();
    }

    /**
     * Constructs a new {@code RunningTimeoutException} with the specified detail message.
     *
     * @param message the internal detail message.
     */
    public RunningTimeoutException(String message) {
        super(message);
    }
}
