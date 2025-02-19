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
 * Throw an unknown parameter "exception" of the specified type.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class UnknownParameterException extends SdkException {

    private static final long serialVersionUID = -7867163222873978334L;

    /**
     * Creates a {@code UnknownParameterException} by given parameter name.
     *
     * @param name the parameter name.
     */
    public UnknownParameterException(String name) {
        super("Can't find or have multiple " + name + " parameters, how can I find the appropriate parameters?");
    }

    /**
     * Creates a {@code UnknownParameterException} by given cause {@code Throwable}.
     *
     * @param cause the cause {@code Throwable}.
     */
    public UnknownParameterException(Throwable cause) {
        super(cause);
    }
}
