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

import javax.annotation.concurrent.ThreadSafe;

/**
 * The use of binding interface for {@link Request} provides a method
 * to bind a {@code Request} to the current target object, i.e. {@link Client}.
 * <p>
 * It should be noted that you need to ensure that the binding scheme
 * of the implementation class is consistent with the scheme of {@code Client}
 * executing the request to obtain the correct corresponding {@code Request}.
 *
 * <p>The URL address bound to this interface must have undergone various
 * formats and be a truly accessible address.
 *
 * <p>This interface can be extended according to Java's SPI mechanism
 * {@link top.osjf.sdk.core.spi.SpiLoader}, with annotations {@link top.osjf.sdk.core.spi.Spi},
 * to achieve self defined extensions.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@ThreadSafe
public interface RequestBinder<R extends Response> extends AutoCloseable {

    /**
     * Bind a {@code Request} resource and return itself.
     *
     * @param bindRequest Waiting for bound {@code Request}
     *                    resources.
     * @return itself {@code RequestBinder}.
     * @throws NullPointerException  If the input bindRequest is {@literal null}.
     * @throws IllegalStateException If the bound resource is cleared
     *                               in advance, i.e. calling {@link #close()},
     *                               a bound resource status exception will be thrown.
     */
    RequestBinder<R> bindRequest(@NotNull Request<R> bindRequest) throws IllegalStateException;

    /**
     * Bind a {@code Url} resource and return itself.
     *
     * @param url The real address accessed, including
     *            formatting operations on the URL such
     *            as concatenating query parameters.
     * @return itself {@code RequestBinder}.
     * @throws NullPointerException  If the input url is {@literal null}.
     * @throws IllegalStateException If the bound resource is cleared
     *                               in advance, i.e. calling {@link #close()},
     *                               a bound resource status exception will be thrown.
     */
    RequestBinder<R> bindUrl(@NotNull String url) throws IllegalStateException;

    /**
     * Retrieve the current {@code RequestBinder} bound
     * {@code Request} resource.
     *
     * @return the current {@code RequestBinder} bound
     * {@code Request} resource.
     * @throws IllegalStateException If the bound {@code Request<R>}
     *                               is empty, throw the current binding
     *                               status exception.
     */
    Request<R> getBindRequest() throws IllegalStateException;

    /**
     * Retrieve the current {@code RequestBinder} bound
     * {@code Url} resource.
     *
     * @return the current {@code RequestBinder} bound
     * {@code Url} resource.
     * @throws IllegalStateException If the bound {@code Url} is empty,
     *                               throw the current binding status
     *                               exception.
     */
    String getBindUrl() throws IllegalStateException;

    /**
     * {@inheritDoc}
     * <p>
     * Release the currently bound {@code Request}.
     *
     * @throws Exception {@inheritDoc}
     */
    @Override
    void close() throws Exception;
}
