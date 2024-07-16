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

package top.osjf.sdk.core.client;

import top.osjf.sdk.core.exception.SdkException;

import java.util.function.BiConsumer;

/**
 * Log consumers can easily specify log output for each {@link Client}
 * using the function {@link BiConsumer}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface LoggerConsumer {

    /**
     * Regular log entries, user normal log output.
     *
     * @return For example: {@code org.slf4j.Logger#info(String, Object...)}
     */
    BiConsumer<String, Object[]> normal();

    /**
     * {@link SdkException} Exclusive log item, log output for user SDK exceptions.
     *
     * @return For example: {@code org.slf4j.Logger#error(String, Object...)}
     */
    BiConsumer<String, Object[]> sdkError();

    /**
     * Unknown exception log output except for SDK.
     *
     * @return For example: {@code org.slf4j.Logger#error(String, Object...)}
     */
    BiConsumer<String, Object[]> unKnowError();
}
