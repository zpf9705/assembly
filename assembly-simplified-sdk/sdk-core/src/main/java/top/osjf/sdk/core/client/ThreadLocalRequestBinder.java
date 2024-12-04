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
import top.osjf.sdk.core.support.Nullable;

/**
 * The {@code ThreadLocalRequestBinder} class implements the {@code RequestBinder}
 * interface and uses {@link ThreadLocal} to bind and store request objects.
 * <p>
 * It allows setting and getting request objects in the current thread and
 * provides a close method to clear the bound request object.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class ThreadLocalRequestBinder<R extends Response> implements RequestBinder<R> {

    /**
     * Initialize a {@code ThreadLocal} to store the thread ->{@code Request<R>} variable.
     */
    private final ThreadLocal<Request<R>> REQUEST_LOCAL = new ThreadLocal<>();

    /**
     * {@inheritDoc}
     * <p>
     * Binds the specified request object to the {@code ThreadLocal} variable
     * of the current thread.
     * <p>
     * If the request object is null, it removes the bound request object
     * from the current thread.
     *
     * @param request {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public ThreadLocalRequestBinder<R> bindRequest(@Nullable Request<R> request) {
        if (request == null) {
            REQUEST_LOCAL.remove();
        } else {
            REQUEST_LOCAL.set(request);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets the request object bound to the current thread.
     * <p>
     * If no request object is bound to the current thread,
     * it returns {@literal null}.
     *
     * @return Returns the request object bound to the current thread,
     * or null if no request is bound.
     */
    @Override
    @Nullable
    public Request<R> getBindRequest() {
        return REQUEST_LOCAL.get();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Closes the binder and clears the request object bound to the current thread.
     * This is actually done by passing null to the bindRequest method.
     *
     * @throws Exception {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        bindRequest(null);
    }
}
