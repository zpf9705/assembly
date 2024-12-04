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
import top.osjf.sdk.core.support.ServiceLoadManager;

/**
 * The use of binding interface for {@link Request} provides a method
 * to bind a {@code Request} to the current target object, i.e. {@link Client}.
 * <p>
 * It should be noted that you need to ensure that the binding scheme
 * of the implementation class is consistent with the scheme of {@code Client}
 * executing the request to obtain the correct corresponding {@code Request}.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface RequestBinder<R extends Response> extends AutoCloseable {

    /**
     * Bind a {@code Request} resource and return itself.
     *
     * @param bindRequest Waiting for bound {@code Request}
     *                    resources.
     * @return itself {@code RequestBinder}.
     */
    RequestBinder<R> bindRequest(Request<R> bindRequest);

    /**
     * Retrieve the current {@code RequestBinder} bound
     * {@code Request} resource.
     *
     * @return the current {@code RequestBinder} bound
     * {@code Request} resource.
     */
    Request<R> getBindRequest();

    /**
     * {@inheritDoc}
     * <p>
     * Release the currently bound {@code Request}.
     *
     * @throws Exception {@inheritDoc}
     */
    @Override
    void close() throws Exception;

    /**
     * The Holder class is a utility class for providing instances
     * of {@code RequestBinder}.
     */
    @SuppressWarnings("rawtypes")
    class Holder {

        /**
         * Gets an instance of {@code RequestBinder}.
         * <p>
         * This method first attempts to load a high-priority implementation
         * of {@code RequestBinder} through {@code ServiceLoadManager}.
         * <p>
         * If the load fails (i.e., returns null), it creates a new
         * {@code ThreadLocalRequestBinder} instance and returns it.
         *
         * @return Returns an instance of {@code RequestBinder}.
         */
        static RequestBinder getInstance() {
            RequestBinder requestBinder
                    = ServiceLoadManager.loadHighPriority(RequestBinder.class);
            if (requestBinder == null) {
                requestBinder = new ThreadLocalRequestBinder();
            }
            return requestBinder;
        }
    }
}
