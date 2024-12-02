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

package top.osjf.sdk.core.process;

import top.osjf.sdk.core.support.Nullable;

/**
 * Define {@code Request} as an executable interface aimed at facilitating
 * SDK calls and returning specific response types.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface Executable<R extends Response> {

    /**
     * A direct execution method without parameters that returns
     * a specific response class object.
     *
     * @return Specific response.
     */
    default R execute() {
        return execute(null);
    }

    /**
     * Carry the request host address parameter (can be empty, depending
     * on method {@link SdkEnum#getUrl}) to obtain
     * a specific response type object.
     *
     * @param host the real server hostname.
     * @return Specific response.
     */
    R execute(@Nullable String host);
}
