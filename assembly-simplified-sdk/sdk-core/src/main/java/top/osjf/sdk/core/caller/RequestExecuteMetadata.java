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

import top.osjf.sdk.core.Request;
import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.lang.Nullable;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Interface for request execution metadata, which describes the relevant
 * information when a {@code Request} is executed.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface RequestExecuteMetadata {

    /**
     * Retrieves the {@code Request} object.
     *
     * @return {@code Request} object.
     */
    @NotNull
    Request<?> getRequest();

    /**
     * Retrieves the {@code Method} used for the request.
     *
     * @return {@code Method} used for the request.
     */
    @NotNull
    Method getMethod();

    /**
     * Retrieves the {@code OptionsMetadata} metadata for the request.
     *
     * @return {@code OptionsMetadata} metadata for the request.
     */
    @Nullable
    OptionsMetadata getOptionsMetadata();

    /**
     * Regarding the use of annotations {@link CallOptions} to provide options of the same type for
     * executing additional metadata interfaces.
     */
    interface OptionsMetadata {

        /**
         * Return a list of {@code Callback} instances that have successfully or
         * abnormally executed a request.
         *
         * @return List of {@code Callback} instances.
         */
        @Nullable
        List<Callback> getCallbacks();

        /**
         * Retrieves a request execution exception to determine whether to retry the
         * {@code ThrowablePredicate} instance.
         *
         * @return A nullable instance {@code ThrowablePredicate}.
         */
        @Nullable
        ThrowablePredicate getThrowablePredicate();

        /**
         * Retrieves an instance object {@code AsyncPubSubExecutorProvider} from the thread
         * pool that requests asynchronous subscription observation.
         *
         * @return A nullable instance {@code AsyncPubSubExecutorProvider}.
         */
        @Nullable
        AsyncPubSubExecutorProvider getSubscriptionExecutorProvider();
    }
}
