/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.optimize.service_bean.context;

import java.io.Serializable;

/**
 * The storage object of service entity metadata.
 *
 * @param <S> The generic name of the service.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public final class ServicePair<S> implements Serializable {

    private static final long serialVersionUID = -7475939440268231784L;

    @SuppressWarnings("rawtypes")
    private static final ServicePair EMPTY = new ServicePair<>(null, null);

    /**
     * Encode service Name
     */
    public final String encodeServiceName;

    /**
     * Service object
     */
    public final S service;

    /**
     * Create a new ServicePair instance.
     *
     * @param encodeServiceName the encode service name, may be null
     * @param service           the service object, may be null
     */
    public ServicePair(final String encodeServiceName, final S service) {
        this.encodeServiceName = encodeServiceName;
        this.service = service;
    }

    /**
     * Static method for create a new {@code ServicePair} instance.
     *
     * @param <S>               The generic name of the service.
     * @param encodeServiceName the encode service name, may be null.
     * @param service           the service object, may be null.
     * @return An {@code ServicePair} instance.
     */
    public static <S> ServicePair<S> of(final String encodeServiceName, final S service) {
        return new ServicePair<>(encodeServiceName, service);
    }

    /**
     * Static method for return an empty ServicePair.
     *
     * @param <S> The generic name of the service.
     * @return An empty {@link #EMPTY} instance.
     */
    @SuppressWarnings("unchecked")
    public static <S> ServicePair<S> empty() {
        return (ServicePair<S>) EMPTY;
    }

    /**
     * Return the encoded service name.
     *
     * @return encoded service name.
     */
    public String getEncodeServiceName() {
        return encodeServiceName;
    }

    /**
     * Return the stored service entity.
     *
     * @return stored service entity.
     */
    public S getService() {
        return service;
    }

    /**
     * Return the result of whether the corresponding entity exists.
     *
     * @return If {@code true} represents existence, otherwise it
     * does not exist.
     */
    public boolean exist() {
        return getService() != null;
    }
}
