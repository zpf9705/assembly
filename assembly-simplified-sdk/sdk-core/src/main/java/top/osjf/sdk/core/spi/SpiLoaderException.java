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

/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.osjf.sdk.core.spi;

/**
 * NOTE: This file has been copied and slightly modified from {com.alibaba.csp.sentinel.spi}.
 * <p>
 * Error thrown when something goes wrong while loading Provider via {@link SpiLoader}.
 */
public class SpiLoaderException extends RuntimeException {
    private static final long serialVersionUID = -2879324709866758433L;
    public SpiLoaderException() {
        super();
    }

    public SpiLoaderException(String message) {
        super(message);
    }

    public SpiLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
