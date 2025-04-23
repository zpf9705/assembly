/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.sdk.http.client;

import top.osjf.sdk.core.URL;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.spi.SpiLoader;
import top.osjf.sdk.core.spi.SpiLoaderException;
import top.osjf.sdk.core.util.internal.logging.InternalLogger;
import top.osjf.sdk.http.HttpResponse;

/**
 * A {@code Client} implementation class that can utilize Java's
 * {@link java.util.ServiceLoader} mechanism to obtain the highest
 * priority {@code InternalLogger} for use.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 * @deprecated {@link top.osjf.sdk.core.util.internal.logging.spi.InternalLoggerSpi}
 */
public class ServiceLoaderLoggerHttpClient<R extends HttpResponse> extends DefaultHttpClient<R> {

    private static final long serialVersionUID = 4669129398883621311L;

    private InternalLogger logger;

    /**
     * Constructing for {@code ServiceLoaderLoggerHttpClient} objects using access URLs.
     *
     * @param url {@code URL} Object of packaging tags and URL addresses
     *            and updated on version 1.0.2.
     * @throws NullPointerException If the input url is {@literal null}.
     */
    public ServiceLoaderLoggerHttpClient(@NotNull URL url) {
        super(url);
        loadHighPriorityLogger();
    }

    /**
     * Load a {@code InternalLogger},if null use {@link SpiLoader}
     * load a high priority {@code InternalLogger} to use in this {@code Client}.
     *
     * @throws SpiLoaderException if spi configuration file not found ?
     */
    private void loadHighPriorityLogger() {
        if (logger == null) {
            logger = SpiLoader.of(InternalLogger.class).loadHighestPriorityInstance();
            if (logger == null)
                throw new SpiLoaderException(InternalLogger.class.getName() +
                        " Provider class not found, please check if it is in the SPI configuration file?");
        }
    }

    @Override
    public InternalLogger getLogger() {
        return logger;
    }
}
