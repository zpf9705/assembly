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

import top.osjf.sdk.core.exception.ClientRequestFailedException;
import top.osjf.sdk.core.process.Request;
import top.osjf.sdk.core.process.Response;

import java.util.function.Supplier;

/**
 * Regarding the request executor of {@link Client}, instantiation is not
 * allowed and only its static methods can be called.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ClientExecutors {

    private ClientExecutors() {
        throw new AssertionError("No instance for you !");
    }

    /**
     * The static method executed by SDK is functionally placed in
     * the host name {@link Supplier} and executed through the host
     * name and {@link Request} parameters.
     *
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK link to {@link Supplier}.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a defined {@link Response} class object.
     */
    public static <R extends Response> R executeRequestClient(Supplier<String> host, Request<R> request) {
        return executeRequestClient(host.get(), request);
    }

    /**
     * The static method executed by SDK is executed through the
     * host name and {@link Request} parameters.
     *
     * @param request {@link Request} class model parameters of API.
     * @param host    The host name of the SDK.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a defined {@link Response} class object.
     */
    public static <R extends Response> R executeRequestClient(String host, Request<R> request) {
        try (Client<R> client = getAndSetClient(request.getUrl(host), request)) {
            return client.request();
        } catch (Throwable e) {
            throw new ClientRequestFailedException(e);
        }
    }

    /**
     * Returns a {@link Client} client using a {@link java.net.URL},
     * If it does not exist, it will be added to the cache.
     *
     * @param request {@link Request} class model parameters of API.
     * @param url     The real URL address accessed by the SDK.
     * @param <R>     Data Generics for {@link Response}.
     * @return Returns a single instance {@link Client} distinguished by a key.
     */
    public static <R extends Response> Client<R> getAndSetClient(String url, Request<R> request) {
        return AbstractClient.getAndSetClient(() -> getNewClient(url, request), request, url);
    }

    /**
     * Instantiate a {@link Client} use reflect by {@link AbstractClient#AbstractClient(String)}.
     *
     * @param request {@link Request} class model parameters of API.
     * @param url     The real URL address accessed by the SDK.
     * @param <R>     Data Generics for {@link Response}.
     * @return {@link Client} instance.
     */
    @SuppressWarnings("unchecked")
    private static <R extends Response> Client<R> getNewClient(String url, Request<R> request) {
        try {
            //Building client objects through reflection based
            // on client type (provided that they are not cached)
            return request.getClientCls().getConstructor(String.class).newInstance(url);
        } catch (Exception e) {
            //all Exceptions throw RuntimeException.
            throw new RuntimeException(e);
        }
    }
}
