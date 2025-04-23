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

import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.util.internal.logging.InternalLogger;
import top.osjf.sdk.core.util.internal.logging.InternalLoggerAccessor;

import java.io.Serializable;

/**
 * The client interface defines the core behaviors for processing requests and responses, as well as
 * functions for preprocessing responses, converting responses, logging, and automatically shutting
 * down resources.
 * <p>
 * This interface is a generic interface, and the generic parameter R must be {@link Response} or
 * its subclass.
 * <p>
 * The interface integrates the following functions:
 * <ul>
 * <li>{@link RequestCore}: defines the core method for sending requests</li>
 * <li>{@link PreProcessingResponseHandler}: Allow preprocessing before response processing</li>
 * <li>{@link ResponseConvert}: defines a method for converting responses to specific types</li>
 * <li>{@link AutoCloseable}: Defined a method for automatically closing resources, typically used
 * to release network connections and other resources</li>
 * <li>{@link Serializable}: Allow interface implementation classes to be serialized for use during
 * network transmission or persistent storage</li>
 * </ul>
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface Client<R extends Response> extends RequestCore<R>,
        PreProcessingResponseHandler<R>, ResponseConvert<R>, InternalLoggerAccessor, Serializable {
    /**
     * {@inheritDoc}
     * <p>
     * Bind a {@code Request} to the current {@link Client} and return itself.
     *
     * @param request {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    @Override
    Client<R> bindRequest(@NotNull Request<R> request) throws IllegalStateException;

    /**
     * {@inheritDoc}
     * <p>
     * Bind a {@code Url} to the current {@link Client} and return itself.
     *
     * @param url {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    @Override
    Client<R> bindUrl(@NotNull String url) throws IllegalStateException;

    /**
     * {@inheritDoc}
     * <p>
     * Timely release the {@link Request} resources bound this time.
     */
    @Override
    void close() throws Exception;

    /**
     * {@inheritDoc}
     */
    InternalLogger getLogger() throws IllegalStateException;
}
