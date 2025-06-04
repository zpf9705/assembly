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

package top.osjf.sdk.core.caller;

import top.osjf.sdk.core.exception.SdkException;

/**
 * Sdk caller exception indicate the error thrown when using the {@link CallOptions}
 * mapping {@link RequestCaller} scheme.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class SdkCallerException extends SdkException {

    private static final long serialVersionUID = -8031736713390106538L;

    /**
     * Creates a {@code SdkCallerException} without args.
     */
    public SdkCallerException() {
        super();
    }

    /**
     * Creates a {@code SdkCallerException} by given message.
     *
     * @param s the detail error message.
     */
    public SdkCallerException(String s) {
        super(s);
    }

    /**
     * Creates a {@code SdkCallerException} by given message and
     * the cause {@code Throwable}.
     *
     * @param message the error message.
     * @param cause   the cause {@code Throwable}.
     */
    public SdkCallerException(String message, Throwable cause) {
        super(message, cause);
    }
}
