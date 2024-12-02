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

package top.osjf.sdk.core.enums;

import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;

/**
 * SDK Enumeration Interface, defining common methods and properties related
 * to the SDK.
 * <p>This interface includes two main methods: one for obtaining the true
 * request address of the SDK, and another for obtaining the name of the
 * SDK request.
 *
 * <p>By implementing this interface, a unified access method and identifier
 * can be provided for different SDKs.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface SdkEnum {

    /**
     * Return the real server hostname when the current SDK executes
     * the request, which can be a domain name or access address.
     * <p>
     * The method parameter {@code host} can be empty, depending on
     * whether you dynamically change the actual host address value.
     *
     * @param host the real server hostname.
     * @return The request address for current SDK.
     */
    @NotNull
    String getUrl(@Nullable String host);

    /**
     * The name of the SDK request, which is a unique identifier name
     * to distinguish between successful analysis or failure in the
     * future, is not recommended to be {@literal null}.
     *
     * @return If it is an enumeration, simply rewrite {@link Enum#name()},
     * and the rest can be customized.
     */
    String name();
}
