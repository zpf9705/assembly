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

import java.io.IOException;


/**
 * Response failed exception class.
 *
 * <p>This class is a subclass of IOException, specifically designed to be
 * thrown when a response fails to return successfully as expected during
 * network requests or communication processes.
 *
 * <p>It provides a mechanism to encapsulate and pass specific information
 * about response failures, helping developers diagnose the problem.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ResponseFailedException extends IOException {

    private static final long serialVersionUID = 2367808447118437245L;

    /**
     * <p>Creates a new instance of {@code ResponseFailedException} to
     * represent a failed response scenario,accompanied by a detailed message
     * describing the cause of the failure.
     *
     * @param message the error message that describes the reason for the response
     *                failure. This message will be included with the exception and
     *                can be used in error logs or user interfaces.
     */
    public ResponseFailedException(String message) {
        super(message);
    }
}
