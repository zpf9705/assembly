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


package top.osjf.sdk.core.util.internal.logging.spi;

import top.osjf.sdk.core.spi.SpiLoader;
import top.osjf.sdk.core.spi.SpiLoaderException;
import top.osjf.sdk.core.util.internal.logging.InternalLogger;
import top.osjf.sdk.core.util.internal.logging.InternalLoggerAccessor;

/**
 * The {@code InternalLoggerSpi} class serves as an abstract base class that provides a
 * Service Provider Interface (SPI) mechanism for accessing an instance of {@code InternalLogger}.
 * By implementing the {@code InternalLoggerAccessor} interface, this class ensures adherence
 * to a specific contract for logger access, facilitating dynamic and flexible logging
 * configurations within an application.
 *
 * <p>The primary functionality of this class is to lazily initialize and provide access
 * to an {@code InternalLogger} instance. The logger instance is dynamically loaded using
 * an SPI loader, which selects the highest priority implementation of {@code InternalLogger}
 * available at runtime. This design promotes extensibility, allowing different logging
 * implementations to be easily integrated into the application without modifying its core code.
 *
 * <p>Key Features:</p>
 * <ul>
 *     <li><strong>Lazy Initialization:</strong> The {@code InternalLogger} instance is
 *     initialized only when it is first requested, optimizing resource usage.</li>
 *     <li><strong>Dynamic SPI Loading:</strong> Utilizes an SPI loader to dynamically
 *     load the highest priority {@code InternalLogger} implementation, enabling runtime
 *     flexibility and configurability.</li>
 *     <li><strong>Error Handling:</strong> Throws an {@code IllegalStateException} if
 *     no suitable logger implementation is found, indicating a potential configuration issue.</li>
 * </ul>
 *
 * <p>Subclasses of {@code InternalLoggerSpi} can extend this class to provide additional
 * functionality or to customize the logger initialization process. They can leverage
 * the common SPI-based logger access mechanism provided by this abstract class, ensuring
 * consistency and maintainability across different logging implementations.</p>
 *
 * <p>The SPI mechanism employed by this class allows applications to support multiple
 * logging backends (e.g., file-based, console-based, network-based) by simply adding
 * or removing the appropriate SPI configuration files. This approach decouples the
 * logging mechanism from the application's business logic, enhancing modularity and
 * testability.</p>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class InternalLoggerSpi implements InternalLoggerAccessor {

    /**
     * The internal logger instance used for logging within the application.
     * This instance is lazily initialized and dynamically loaded through an SPI mechanism.
     */
    private InternalLogger logger;

    /**
     * {@inheritDoc}
     * <p>
     * This method provides access to an {@code InternalLogger} instance. If the logger
     * instance has not been initialized, it is lazily loaded using an SPI loader. If no
     * suitable logger implementation is found, an {@code IllegalStateException} is thrown,
     * indicating a configuration issue.
     *
     * @return an instance of {@code InternalLogger} for logging purposes.
     * @throws IllegalStateException if no {@code InternalLogger} implementation is available.
     */
    @Override
    public InternalLogger getLogger() throws IllegalStateException {
        if (logger == null) {
            logger = SpiLoader.of(InternalLogger.class).loadHighestPriorityInstance();
            if (logger == null) {
                throw new IllegalStateException(new SpiLoaderException(InternalLogger.class.getName() +
                        " Provider class not found, please check if it is in the SPI configuration file?"));
            }
        }
        return logger;
    }
}
