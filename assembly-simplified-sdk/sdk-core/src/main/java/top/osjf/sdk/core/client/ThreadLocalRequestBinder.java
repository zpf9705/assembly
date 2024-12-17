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
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;

import java.util.function.Supplier;

/**
 * The {@code ThreadLocalRequestBinder} class implements the {@code RequestBinder}
 * interface and uses {@link ThreadLocal} to bind and store request objects and url.
 * <p>
 * It allows setting and getting request objects and url wrapper in {@code LocalData}
 * in the current thread and provides a close method {@link #close()} to clear the
 * bound {@code LocalData}.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class ThreadLocalRequestBinder<R extends Response> implements RequestBinder<R> {

    /**
     * Initialize a {@code ThreadLocal} to store the thread ->{@code LocalData} variable.
     */
    private final ThreadLocal<LocalData> REQUEST_LOCAL = ThreadLocal.withInitial(() -> new LocalData());


    /**
     * {@inheritDoc}
     * <p>
     * Binds the specified request object to the {@code ThreadLocal} variable
     * of the current thread.
     *
     * @param request {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    @Override
    public ThreadLocalRequestBinder<R> bindRequest(@NotNull Request<R> request)
            throws IllegalStateException {
        stateRun(() -> {
            REQUEST_LOCAL.get().setRequest(request);
            return null;
        }, "bindRequest");
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Binds the specified url to the {@code ThreadLocal} variable
     * of the current thread.
     *
     * @param url {@inheritDoc}
     * @return {@inheritDoc}
     * @throws IllegalStateException {@inheritDoc}
     * @throws NullPointerException  {@inheritDoc}
     */
    @Override
    public RequestBinder<R> bindUrl(@NotNull String url)
            throws IllegalStateException {
        stateRun(() -> {
            REQUEST_LOCAL.get().setUrl(url);
            return null;
        }, "bindUrl");
        return this;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets the request object bound to the current thread.
     * <p>
     * If no request object is bound to the current thread,
     * it throw {@code IllegalStateException}.
     *
     * @return Returns the request object bound to the current thread.
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    @Nullable
    public Request<R> getBindRequest() throws IllegalStateException {
        return stateRun(() -> REQUEST_LOCAL.get().getRequest(), "getBindRequest");
    }

    /**
     * {@inheritDoc}
     * <p>
     * Gets the url bound to the current thread.
     * <p>
     * If no url is bound to the current thread,
     * it throw {@code IllegalStateException}.
     *
     * @return Returns the request object bound to the current thread.
     * @throws IllegalStateException {@inheritDoc}
     */
    @Override
    public String getBindUrl() throws IllegalStateException {
        return stateRun(() -> REQUEST_LOCAL.get().getUrl(), "getBindRequest");
    }

    /**
     * Running status, error thrown {@code IllegalStateException}.
     *
     * @param supplier state run Supplier.
     * @param state    state name.
     * @param <T>      state run return type.
     * @return state run return object.
     */
    private <T> T stateRun(Supplier<T> supplier, String state) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new IllegalStateException(state, e);
        }
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
        REQUEST_LOCAL.remove();
    }

    /**
     * The data variables temporarily stored in {@code ThreadLocal}.
     */
    class LocalData {
        private Request<R> request;
        private String url;

        public Request<R> getRequest() {
            return request;
        }

        public void setRequest(Request<R> request) {
            this.request = request;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
