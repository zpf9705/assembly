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

import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.util.StringUtils;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

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
public abstract class AbstractClient<R extends Response> implements Client<R> {

    private static final long serialVersionUID = -6931093876869566743L;

    /*** The use of object locks for client retrieval and caching.*/
    private static final Object lock = new Object();

    /*** Cache request clients for each request object to prevent memory waste caused
     * by multiple new requests*/
    private static final Map<String, Client> cache = new ConcurrentHashMap<>(16);

    /*** Save each request parameter and use it for subsequent requests*/
    private static final ThreadLocal<Request> local = new ThreadLocal<>();

    /*** Constructing for {@link Client} objects using unique identifier.
     * @param unique The unique identifier string for this client's cache.
     * */
    public AbstractClient(String unique) {
        Objects.requireNonNull(unique, "Client unique");
        cache(unique, this);
    }

    /**
     * Use the unique sign as the key, {@link Client} object
     * as the value, and cache it in the current {@link #cache}
     * to prepare for continuous access in the future.
     *
     * @param unique The unique identifier string for this
     *               client's cache.
     * @param client Real impl in {@link Client}.
     */
    protected void cache(String unique, Client client) {
        if (StringUtils.isBlank(unique) || client == null) {
            return;
        }
        cache.putIfAbsent(unique, client);
    }

    /**
     * Regarding the placement of the {@link Request} parameter, it
     * was first placed in {@link #local} to facilitate the retrieval
     * of the current request parameter {@link Request} in the case of
     * global constraints in this request.
     *
     * @param <R>     Data Generics for {@link Response}.
     * @param request The parameter model of the current request is
     *                an implementation of {@link Request}.
     */
    protected static <R extends Response> void setCurrentParam(Request<R> request) {
        if (request == null) {
            local.remove();
        } else {
            local.set(request);
        }
    }

    /**
     * Return and cache a {@link Client}. When it does not exist based on the
     * unique sign, cache {@link Client}. Otherwise, retrieve it directly from
     * the cache to ensure uniqueness.
     *
     * @param newClientSupplier New client provider,if not found, add it directly.
     * @param request           {@link Request} class model parameters of API.
     * @param unique            The unique identifier string for this client's cache.
     * @param <R>               Data Generics for {@link Response}.
     * @return {@link Client} 's singleton object, persistently requesting.
     */
    protected static <R extends Response> Client<R> getCachedClient(Supplier<Client<R>> newClientSupplier,
                                                                    Request<R> request,
                                                                    String unique) {
        Objects.requireNonNull(unique, "Client unique");
        Objects.requireNonNull(request, "Client Request");
        setCurrentParam(request);
        Client<R> client = cache.get(unique);
        if (client == null) {
            synchronized (lock) {
                client = cache.get(unique);
                if (client == null) {
                    client = newClientSupplier.get();
                }
            }
        }
        return client;
    }

    /**
     * Return the current request parameters, which is the
     * implementation class of {@link Request}.
     *
     * @return Actual {@link Request} implementation.
     */
    protected Request<R> getCurrentRequest() {
        return local.get();
    }

    @Override
    public void close() {
        setCurrentParam(null);
    }
}
