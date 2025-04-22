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

package top.osjf.sdk.core;

import top.osjf.sdk.core.lang.NotNull;
import top.osjf.sdk.core.lang.Nullable;

/**
 * SDK related request metadata configuration interface.
 *
 * <p>It now includes the URL access address obtained by passing in the
 * host name and the name of the current SDK. The definition of the
 * URL obtained depends on your rewriting logic.
 *
 * <p>We recommend that you implement this interface in the enumeration class,
 * which initializes the original configuration of the SDK once globally for
 * use. However, this is not the only option, and you can choose to implement
 * it according to your actual situation.
 *
 * <p>In the decision of version 1.0.2, this interface is not limited to optional
 * enumeration. Of course, you can use dynamic analysis and creation to move the
 * interface to the current package and expand its definition to the greatest extent
 * possible.
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
