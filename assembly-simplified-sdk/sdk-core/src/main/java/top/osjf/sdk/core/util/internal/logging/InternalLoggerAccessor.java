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


package top.osjf.sdk.core.util.internal.logging;

/**
 * The {@code InternalLoggerAccessor} interface provides a contract for accessing an instance of
 * the {@code InternalLogger}. This interface is designed to abstract the mechanism of obtaining
 * a logger instance, allowing for flexibility in how the logger is provided, such as through
 * dependency injection, service loader, or other configuration mechanisms.
 *
 * <p>Implementations of this interface are responsible for returning a valid {@code InternalLogger}
 * instance that can be used for logging purposes within an application. The specific logger
 * implementation returned may vary depending on the application's configuration or runtime
 * environment.</p>
 *
 * <p>In scenarios where the logger instance is obtained dynamically, such as through a service
 * loader, the implementation should handle cases where no suitable logger implementation is
 * available. In such cases, it is recommended to throw an {@code IllegalStateException} to
 * indicate that the logging functionality is not properly configured.</p>
 *
 * <p>This interface is particularly useful in applications where the logging mechanism needs to
 * be decoupled from the business logic, allowing for easier testing, maintenance, and
 * extensibility.</p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface InternalLoggerAccessor {

    /**
     * Retrieves an instance of the {@code InternalLogger}.
     *
     * <p>This method is responsible for providing access to an {@code InternalLogger} instance,
     * which is used for logging purposes within the application. The specific implementation
     * of InternalLogger returned by this method may vary depending on the configuration
     * or the runtime environment.</p>
     *
     * <p>In some cases, the {@code InternalLogger} instance may be obtained through a service
     * loader mechanism, which allows for dynamic loading of implementations based on availability
     * and priority. If no suitable implementation is found, this method may throw an
     * IllegalStateException to indicate that logging functionality is not properly configured.
     *
     * @return an instance of {@code InternalLogger} that can be used for logging within the application.
     * @throws IllegalStateException if no {@code InternalLogger} implementation is available,
     *                               which may indicate a configuration issue or a missing logging provider.
     * @see InternalLogger for details on the logging interface and its methods.
     */
    InternalLogger getLogger() throws IllegalStateException;
}
