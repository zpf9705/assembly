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

package top.osjf.sdk.spring.annotation;

import top.osjf.sdk.core.exception.SdkIllegalArgumentException;

/**
 * The {@code IncorrectHostException} class defines a custom {@code SdkIllegalArgumentException}
 * indicating that the provided host address is invalid.
 * <p>
 * During network communication or data requests, it is often necessary to validate the host
 * address. If the passed-in host address does not meet the expected format or is unreachable,
 * this exception is thrown. This class inherits from {@code SdkIllegalArgumentException} in the
 * Java standard library, indicating that it is an unchecked exception, meaning the compiler does
 * not enforce the capturing or declaration of throwing this exception.
 * <p>
 * The constructor receives a string parameter {@code host}, representing the problematic
 * host address that was passed in, and appends this address to the exception message,
 * generating a detailed error message in the form of "[host] not a valid host address".
 * <p>
 * With this custom exception, it becomes easier to identify and handle cases of invalid
 * host addresses in the code, enhancing the robustness and readability of the code.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class IncorrectHostException extends SdkIllegalArgumentException {

    private static final long serialVersionUID = -1221839322641243165L;

    public IncorrectHostException(String host) {
        super("[" + host + "] not a valid host address");
    }
}
