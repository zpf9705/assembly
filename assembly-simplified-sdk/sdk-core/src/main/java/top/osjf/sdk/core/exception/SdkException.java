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

package top.osjf.sdk.core.exception;

/**
 * The top-level runtime exception class of the SDK framework,
 * differentiated into multiple word class exceptions based on
 * the scenario, can be thrown in specific scenarios.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class SdkException extends RuntimeException {

    private static final long serialVersionUID = -7204419580156052252L;

    /**
     * Creates a {@code SdkException} without args.
     */
    public SdkException() {
        super();
    }

    /**
     * Creates a {@code SdkException} by given message.
     *
     * @param s the detail error message.
     */
    public SdkException(String s) {
        super(s);
    }

    /**
     * Creates a {@code DataConvertException} by given message and
     * the cause {@code Throwable}.
     *
     * @param message the error message.
     * @param cause   the cause {@code Throwable}.
     */
    public SdkException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a {@code DataConvertException} by given cause {@code Throwable}.
     *
     * @param cause the cause {@code Throwable}.
     */
    public SdkException(Throwable cause) {
        super(cause);
    }
}