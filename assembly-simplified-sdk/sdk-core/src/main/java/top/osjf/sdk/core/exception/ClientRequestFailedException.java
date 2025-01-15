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

import top.osjf.sdk.core.client.Client;

/**
 * When {@link Client#request()} is failed,throw it within a real cause.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ClientRequestFailedException extends SdkException {

    private static final long serialVersionUID = 4730988924158862453L;

    /**
     * Creates a {@code ClientRequestFailedException} by given case {@code Throwable}.
     * @param cause the case {@code Throwable} of take client failed.
     */
    public ClientRequestFailedException(Throwable cause) {
        super(cause);
    }
}
