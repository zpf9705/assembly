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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.process.URL;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.support.SdkSupport;
import top.osjf.sdk.core.util.StringUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

/**
 * The abstract auxiliary implementation class for {@link Client}
 * holds public methods.
 *
 * <p>At the level of public method assistance, the main solution is to cache
 * single instances {@link Client} and periodic processing of parameters.
 * <p>Therefore, static {@link ConcurrentHashMap} and {@link ThreadLocal}
 * are introduced to achieve the above requirements, as well as some publicly
 * available methods for obtaining the above parameters, while ensuring thread
 * safety.
 * <p>You can directly inherit this class to achieve the above purpose.
 *
 * <p>If you do not need the above purpose, you can directly implement the
 * {@link Client} interface to rewrite the necessary methods.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractClient<R extends Response> implements Client<R>, JSONResponseConvert<R> {

    private static final long serialVersionUID = -6931093876869566743L;

    /*** Default slf4j logger with current {@link Client} impl */
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /***
     * The manager instance of {@code Client}.
     * @since 1.0.2
     */
    private static final ClientManager CLIENT_MANAGER = SdkSupport.loadInstance(ClientManager.class,
            "top.osjf.sdk.core.client.DefaultClientManager");

    /***
     * The binding instance of {@code Request} for {@code Client}.
     * @since 1.0.2
     */
    private static final RequestBinder REQUEST_BINDER = SdkSupport.loadInstance(RequestBinder.class,
            "top.osjf.sdk.core.client.ThreadLocalRequestBinder");

    /*** The unique cache tag for the current {@code Client}.*/
    private final String unique;

    /*** Constructing for {@link Client} objects using unique identifier.
     * @param url   {@code URL} Object of packaging tags and URL addresses
     *                         and updated on version 1.0.2.
     * */
    public AbstractClient(@NotNull URL url) {
        this.unique = url.getUnique();
        cache(url.getUnique(), this);
    }

    /**
     * Use the unique sign as the key, {@link Client} object
     * as the value, and cache it in the current {@link #CLIENT_MANAGER}
     * to prepare for continuous access in the future.
     *
     * @param unique The unique identifier string for this
     *               client's cache.
     * @param client Real impl in {@link Client}.
     * @throws IllegalArgumentException unique or client {@literal null}
     *                                  error.
     */
    protected void cache(String unique, Client client) {
        if (StringUtils.isBlank(unique) || client == null)
            throw new IllegalArgumentException("unique or client");
        getClientManager().maintenanceNewClient(unique, client);
    }

    /**
     * Return a {@code Client} manager instance for
     * {@code ClientManager}.
     *
     * @return {@code Client} global static manager instance.
     */
    protected static ClientManager getClientManager() {
        return CLIENT_MANAGER;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Use {@code REQUEST_BINDER} to bind this {@code Request}.
     *
     * @param request {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Client<R> bindRequest(@Nullable Request<R> request) {
        REQUEST_BINDER.bindRequest(request);
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get the current bound {@code Request} parameter from the
     * initialized {@link #REQUEST_BINDER}, and give an exception
     * prompt when it is {@literal null}.
     *
     * @return {@inheritDoc}
     * @throws IllegalStateException The state exception thrown by binding parameter
     *                               {@link Request} was not obtained.
     */
    @Override
    public Request<R> getBindRequest() throws IllegalStateException {
        Request<R> bindRequest = REQUEST_BINDER.getBindRequest();
        if (bindRequest == null)
            throw new IllegalStateException("No available Request, consider whether to bind Request.");
        return bindRequest;
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
    public R request() {
        return (R) getClientManager().getMaintainedClient(unique, null).request();
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
    public String preResponseStrHandler(Request<R> request, String responseStr) {
        return responseStr;
    }

    /**
     * {@inheritDoc}
     * Default use {@link Logger#info}.
     *
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public BiConsumer<String, Object[]> normal() {
        return LOGGER::info;
    }

    /**
     * {@inheritDoc}
     * Default use {@link Logger#error}.
     *
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public BiConsumer<String, Object[]> sdkError() {
        return LOGGER::error;
    }

    /**
     * {@inheritDoc}
     * Default use {@link Logger#error}.
     *
     * @return {@inheritDoc}
     */
    @Override
    @NotNull
    public BiConsumer<String, Object[]> unKnowError() {
        return LOGGER::error;
    }

    /**
     * Release the temporarily stored request parameter information.
     */
    @Override
    public void close() throws Exception {
        REQUEST_BINDER.close();
    }
}
