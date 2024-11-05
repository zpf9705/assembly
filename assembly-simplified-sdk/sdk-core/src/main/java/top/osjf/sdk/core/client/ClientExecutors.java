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
 * The {@code ClientExecutors} class provides static methods for executing client requests.
 * It does not allow instantiation and only provides static methods to execute requests
 * through host names and request parameters.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class ClientExecutors {

    /**
     * Private constructor to prevent external instantiation of this class.
     * <p>
     * Throwing an AttributeError exception to indicate that instantiation of this class is not allowed.
     */
    private ClientExecutors() {
        throw new AssertionError("No instance for you !");
    }

    /**
     * Execute client requests using the hostname supplier and request object.
     * <p>
     * This method first converts the host name vendor into an actual host name
     * string, and then calls the overloaded executeRequestClient method.
     *
     * @param host    The supplier of hostname, used to dynamically obtain the hostname.
     * @param request object, containing API parameters.
     * @param <R>     is a generic type that responds to data.
     * @return Returns a response object of the specified type.
     */
    public static <R extends Response> R executeRequestClient(Supplier<String> host, Request<R> request) {
        return executeRequestClient(host.get(), request);
    }

    /**
     * Execute client requests using the host name and request object.
     * <p>
     * This method retrieves or sets a client instance based on the given
     * host name and request object, and sends the request.
     *
     * @param host    The name of the link to the host SDK.
     * @param request object, containing API parameters.
     * @param <R>     is a generic type that responds to data.
     * @return Returns a response object of the specified type.
     * If the request execution fails, throw this exception.
     */
    public static <R extends Response> R executeRequestClient(String host, Request<R> request) {
        try (Client<R> client = getClient(request.getUrl(host), request)) {
            return client.request();
        } catch (Throwable e) {
            throw new ClientRequestFailedException(e);
        }
    }

    /**
     * Retrieve a {@code Client} instance using the given URL and request object.
     * <p>
     * If it does not exist in the cache, a new client is instantiated through
     * reflection and added to the cache.
     * <p>
     * Instantiate a new client based on the given URL and request object using
     * reflection mechanism.
     * <p>
     * This method calls the {@code Request#getClientCls()} method of the request
     * object to retrieve the client class and create its instance through reflection.
     *
     * @param url     The actual URL address accessed by the SDK.
     * @param request object, containing API parameters.
     * @param <R>     is a generic type that responds to data.
     * @return returns the client instance distinguished by a unique key.
     */
    @SuppressWarnings("unchecked")
    protected static <R extends Response> Client<R> getClient(String url, Request<R> request) throws Throwable {
        return AbstractClient.getCachedClient(() ->
                //Building client objects through reflection based
                // on client type (provided that they are not cached)
                request.getClientCls().getConstructor(String.class).newInstance(url), request, url);
    }
}
