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

import top.osjf.sdk.core.util.Nullable;

/**
 * Get request parameters of the specified type.
 *
 * @param <T> Param type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@FunctionalInterface
public interface RequestParamCapable<T> {

    /**
     * Return the actual request parameters, which can be the
     * request body of a post or the URL parameters of a get
     * request, depending on the nature of the request itself.
     *
     * @return the actual request parameters.
     */
    @Nullable
    T getRequestParam();
}
