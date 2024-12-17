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

import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.util.org.hibernate.validator.internal.util.v6_2_0_final.ConcurrentReferenceHashMap;

import java.util.function.Supplier;

/**
 * Default implementation of the client manager for managing {@code Client} objects
 * of a specific type.
 * <p>
 * Uses {@code ConcurrentReferenceHashMap} as a cache to store client objects, supporting
 * thread safety and soft references.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class DefaultClientManager<R extends Response> implements ClientManager<R> {

    /**
     * Cache for client objects, implemented using {@code ConcurrentReferenceHashMap},
     * supporting thread safety and {@link java.lang.ref.SoftReference} references.
     * <p>
     * The key is the unique identifier of the {@code Client}, and the value is the
     * corresponding {@code Client} object.
     */
    private final ConcurrentReferenceHashMap<String, Client<R>> CLIENT_CACHE = new ConcurrentReferenceHashMap<>
            (16, ConcurrentReferenceHashMap.ReferenceType.SOFT,
                    ConcurrentReferenceHashMap.ReferenceType.SOFT);

    /**
     * {@inheritDoc}
     * Use {@link ConcurrentReferenceHashMap#putIfAbsent}
     *
     * @param unique {@inheritDoc}
     * @param client {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public void maintenanceNewClient(@NotNull String unique, @NotNull Client<R> client) {
        CLIENT_CACHE.putIfAbsent(unique, client);
    }

    /**
     * {@inheritDoc}
     * Use {@link ConcurrentReferenceHashMap#computeIfAbsent}
     *
     * @param unique                    {@inheritDoc}
     * @param ifAbsentNewClientSupplier {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    @Override
    public Client<R> getMaintainedClient(@NotNull String unique, Supplier<Client<R>> ifAbsentNewClientSupplier) {
        return CLIENT_CACHE.computeIfAbsent(unique, s -> ifAbsentNewClientSupplier.get());
    }
}
