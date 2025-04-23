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
import top.osjf.sdk.core.URL;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.support.SdkSupport;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.core.util.internal.logging.InternalLogger;
import top.osjf.sdk.core.util.internal.logging.spi.InternalLoggerSpi;

/**
 * The {@code AbstractClient} class is an abstract base class designed to provide a common
 * framework for client implementations that interact with external services or APIs. This
 * class extends {@code InternalLoggerSpi} and implements both {@code Client} and
 * {@code JSONResponseConvert} interfaces, ensuring that subclasses adhere to a specific
 * contract for request binding, execution, and response handling.
 *
 * <p>This class introduces a caching mechanism for client instances, managed by an
 * {@code InstanceHolder} inner class, which provides static access to client and request
 * management components. The caching mechanism is based on a unique identifier associated
 * with each client instance, ensuring that clients can be retrieved and managed efficiently.
 *
 * <p>Key functionalities include:</p>
 * <ul>
 *     <li>Binding requests and URLs to client instances.</li>
 *     <li>Executing requests and retrieving responses.</li>
 *     <li>Caching and managing client instances.</li>
 *     <li>Logging through an internal logger instance.</li>
 * </ul>
 *
 * <p>Subclasses of {@code AbstractClient} are expected to provide specific implementations
 * for request execution and response handling, while leveraging the common infrastructure
 * provided by this abstract class.</p>
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractClient<R extends Response>
        extends InternalLoggerSpi implements Client<R>, JSONResponseConvert<R> {

    private static final long serialVersionUID = -6931093876869566743L;

    /**
     * The unique cache tag for the current {@code Client}.
     */
    private final String unique;

    /**
     * Constructing for {@code AbstractClient} objects using unique identifier.
     *
     * @param url {@code URL} Object of packaging tags and URL addresses
     *            and updated on version 1.0.2.
     * @throws NullPointerException If the input url is {@literal null}.
     */
    public AbstractClient(@NotNull URL url) {
        this.unique = url.getUnique();
        cache(url.getUnique(), this);
    }

    /**
     * Cache the client into the management class {@code InstanceHolder.ClientManager}.
     * <p>
     * This method receives a unique identifier and a {@code Client} object, and
     * passes them to an {@code InstanceHolder} who manages the client for caching.
     * If the provided unique identifier is empty or the {@code Client} object is
     * null, an {@code IllegalArgumentException} exception is thrown.
     *
     * @param unique is a unique string used to identify the {@code Client}.
     * @param client The {@code Client} object that client needs to be cached.
     * @throws IllegalArgumentException thrown if unique is empty or contains
     *                                  only whitespace characters, or if
     *                                  {@code Client} is {@literal null}.
     */
    protected void cache(String unique, Client client) {
        if (StringUtils.isBlank(unique) || client == null)
            throw new IllegalArgumentException("unique or client not be null");
        InstanceHolder.getClientManager().maintenanceNewClient(unique, client);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Use {@code REQUEST_BINDER} to bind this {@code Request}.
     *
     * @param request {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    @Override
    public Client<R> bindRequest(@NotNull Request<R> request) throws IllegalStateException {
        InstanceHolder.getRequestBinder().bindRequest(request);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Use {@code REQUEST_BINDER} to bind this {@code url}.
     *
     * @param url {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    @Override
    public Client<R> bindUrl(@NotNull String url) throws IllegalStateException {
        InstanceHolder.getRequestBinder().bindUrl(url);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get the current bound {@code Request} parameter from the
     * initialized {@link InstanceHolder#getRequestBinder()}.
     *
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public Request<R> getBindRequest() throws IllegalStateException {
        return InstanceHolder.getRequestBinder().getBindRequest();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get the current bound {@code Url} parameter from the
     * initialized {@link InstanceHolder#getRequestBinder()}.
     *
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public String getBindUrl() throws IllegalStateException {
        return InstanceHolder.getRequestBinder().getBindUrl();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieve {@link Client} from the cache and execute it, provided
     * that {@code Request} parameter binding is also performed.
     *
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public R request() {
        return (R) InstanceHolder.getClientManager().getMaintainedClient(unique, null).request();
    }

    /**
     * {@inheritDoc}
     * The default return is the request string itself.
     *
     * @param request     {@inheritDoc}
     * @param responseStr {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public String preResponseStrHandler(@NotNull Request<R> request, @NotNull String responseStr) {
        return responseStr;
    }

    /**
     * Release the temporarily stored request parameter information.
     */
    @Override
    public void close() throws Exception {
        InstanceHolder.getRequestBinder().close();
    }

    /**
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public InternalLogger getLogger() throws IllegalStateException {
        return super.getLogger();
    }

    /**
     * Any instance holder.
     *
     * @since 1.0.2
     */
    protected static class InstanceHolder {

        /***
         * The manager instance of {@code Client}.
         */
        private static final ClientManager CLIENT_MANAGER = SdkSupport.loadInstance(ClientManager.class,
                "top.osjf.sdk.core.client.DefaultClientManager");

        /***
         * The binding instance of {@code Request} for {@code Client}.
         */
        private static final RequestBinder REQUEST_BINDER = SdkSupport.loadInstance(RequestBinder.class,
                "top.osjf.sdk.core.client.ThreadLocalRequestBinder");

        /**
         * Return a {@code Client} manager instance for
         * {@code ClientManager}.
         *
         * @return {@code Client} global static manager instance.
         */
        public static ClientManager getClientManager() {
            return CLIENT_MANAGER;
        }

        /**
         * Return a {@code Request} binder instance for
         * {@code RequestBinder}.
         *
         * @return {@code Request} global static binder instance.
         */
        public static RequestBinder getRequestBinder() {
            return REQUEST_BINDER;
        }
    }
}
