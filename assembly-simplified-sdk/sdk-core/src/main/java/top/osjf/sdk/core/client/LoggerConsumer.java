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

import top.osjf.sdk.core.support.NotNull;

import java.util.function.BiConsumer;

/**
 * The LoggerConsumer interface defines consumer interfaces for handling different
 * types of log recording.It contains three methods for logging normal entries, SDK
 * exception logs, and unknown exception logs.Each method returns a {@code BiConsumer<String, Object[]>}
 * that allows logging in the form of a message template and an array of arguments.
 *
 * <p>This design makes log recording more flexible and easier to integrate into different
 * logging frameworks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface LoggerConsumer {

    /**
     * Regular log entries, used for recording normal log output.
     * This method returns a {@code BiConsumer<String, Object[]>} that allows logging
     * in the form of a message template and an array of arguments.
     *
     * <p>It is similar to the `Logger#info(String, Object...)` method in SLF4J but
     * encapsulated in a functional programming style using `BiConsumer`.
     *
     * @return A `BiConsumer` that accepts a log message template and an array of
     * arguments for recording normal logs.
     */
    @NotNull
    BiConsumer<String, Object[]> normal();

    /**
     * SDK exception-exclusive log item, used for logging exceptions that occur
     * within the user's SDK.
     *
     * <p>This method also returns a {@code BiConsumer<String, Object[]>} that allows
     * logging SDK-related exception logs in the form of an error message template
     * and an array of arguments.
     * It is similar to the `Logger#error(String, Object...)` method in SLF4J but
     * focused on SDK exception logging.
     *
     * @return A `BiConsumer` that accepts an error message template and an array
     * of arguments for recording SDK exception logs.
     */
    @NotNull
    BiConsumer<String, Object[]> sdkError();

    /**
     * Unknown exception log output, used for logging all other exceptions except
     * for SDK exceptions.
     *
     * <p>This method returns a {@code BiConsumer<String, Object[]>} that allows logging unknown
     * exception logs in the form of an error message template and an array of arguments.
     * It is similar to the `Logger#error(String, Object...)` method in SLF4J but focused
     * on non-SDK exception logging.
     *
     * @return A `BiConsumer` that accepts an error message template and an array of
     * arguments for recording unknown exception logs.
     */
    @NotNull
    BiConsumer<String, Object[]> unKnowError();
}
