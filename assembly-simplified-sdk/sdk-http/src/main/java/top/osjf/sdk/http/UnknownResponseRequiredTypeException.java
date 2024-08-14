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

package top.osjf.sdk.http;

import top.osjf.sdk.core.exception.SdkException;

/**
 * Unknown response required type exception class.
 *
 * <p>This class is a custom exception class that inherits from
 * {@link SdkException} and is used to represent exceptions thrown when
 * encountering unknown response types in the SDK,It is mainly
 * used to encapsulate and handle error situations caused by
 * response types that do not meet expectations.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class UnknownResponseRequiredTypeException extends SdkException {

    private static final long serialVersionUID = 8387664892991324499L;

    /**
     * <p>Creates a new instance of {@code UnknownResponseRequiredTypeException} to
     * encapsulate the given cause.
     *
     * @param cause the cause (which is being wrapped by this exception, enabling
     *              it to be passed along to the caller).
     *              It can be <code>null</code> if the cause is nonexistent or unknown.
     */
    public UnknownResponseRequiredTypeException(Throwable cause) {
        super(cause);
    }
}
