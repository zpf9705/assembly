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

package top.osjf.sdk.core.caller;

import top.osjf.sdk.core.Response;
import top.osjf.sdk.core.SdkEnum;
import top.osjf.sdk.core.support.NotNull;

/**
 * Callback interface, used to handle the results of asynchronous operations.
 *
 * <p>This interface defines two methods {@link #success} and {@link #exception}
 * are used to handle the success and failure of synchronous or asynchronous
 * operations, respectively.
 *
 * <p>When synchronous or asynchronous operations are completed, the caller will
 * use the implementation of this interface to notify the client of the result
 * of the operation.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface Callback {

    /**
     * The result of successfully handling asynchronous operations.
     *
     * <p>When synchronous or asynchronous operations are successfully completed,
     * call this method. It receives a {@code Response} object as a parameter, which
     * contains the result of the operation or related data.
     *
     * @param response The object contains the asynchronous operation result.
     */
    void success(@NotNull Response response);

    /**
     * Exception thrown when handling asynchronous operation failure.
     *
     * <p>When a synchronous or asynchronous operation fails and throws an exception,
     * call this method. It takes a {@code Throwable} object as a parameter, which
     * represents the exception that occurred.
     *
     * @param name the sdk name,as see {@link SdkEnum#name()}.
     * @param e    represents the 'Throwable' object where asynchronous operations fail.
     */
    void exception(@NotNull String name, @NotNull Throwable e);
}
