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

import java.nio.charset.Charset;

/**
 * Request Parameter Capability getting Interface.
 * <p>
 * This interface defines the capability to obtain request parameters
 * and their character encoding.
 * <p>
 * Concrete implementations should determine whether to fetch parameters
 * from the request body or URL parameters based on the type of request
 * (e.g., GET or POST). Additionally, it provides a method to obtain the
 * character set encoding for the request and parameters.
 *
 * @param <T> Param type.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface RequestParamCapable<T> {

    /**
     * Return the actual request parameters, which can be the
     * request body of a post or the URL parameters of a get
     * request, depending on the nature of the request itself.
     *
     * @return the actual request parameters.
     */
    T getRequestParam();

    /**
     * Return the encoded character set object of the request and
     * parameter.
     *
     * @return The encoding character set for the request and parameter.
     * @since 1.0.2
     */
    Charset getCharset();
}
