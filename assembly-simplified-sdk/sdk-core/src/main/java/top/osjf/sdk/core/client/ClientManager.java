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

import top.osjf.sdk.core.process.Response;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;

import java.util.function.Supplier;

/**
 * {@code ClientManager} defines a generic client manager for managing
 * {@code Client} objects of a specific type.
 * <p>
 * By implementing this interface, developers can create custom {@code Client}
 * managers to handle operations such as creating, retrieving, and maintaining
 * {@code Client} objects.
 * <p>
 * Classes implementing {@code ClientManager} should ensure thread safety,
 * especially when operating on client objects in a multithreaded environment.
 *
 * @param <R> Implement a unified response class data type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface ClientManager<R extends Response> {

    /**
     * Creates a new {@code client} object under maintenance based on a
     * unique identifier.
     *
     * @param unique The unique identifier for the client.
     * @param client The {@code client} object that needs to be maintained.
     * @return A new {@code client} object under maintenance, which may be
     * a wrapper or modified version of the input client.
     */
    Client<R> maintenanceNewClient(@NotNull String unique, @NotNull Client<R> client);

    /**
     * Retrieves a {@code client} object under maintenance based on a unique
     * identifier.
     * <p>
     * If the client {@code client} corresponding to the unique identifier
     * does not exist, a new {@code client} object is created using the
     * provided Supplier.
     *
     * @param unique                    The unique identifier for the {@code client}.
     * @param ifAbsentNewClientSupplier A Supplier used to create a new client when the
     *                                  client does not exist.
     * @return The {@code client} object under maintenance, which may be existing or
     * newly created.
     */
    Client<R> getMaintainedClient(@NotNull String unique, @Nullable Supplier<Client<R>> ifAbsentNewClientSupplier);
}
