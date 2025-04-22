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

import top.osjf.sdk.core.caller.RequestCaller;

/**
 * The {@code RequestAttributes} interface defines a set of auxiliary
 * request attributes that are optional for SDK request functionality,
 * but their presence can greatly enhance the functionality and flexibility
 * of requests.
 *
 * <p>This interface provides a series of methods to set and retrieve
 * these auxiliary properties, including hostname and request caller.
 * If a property is not set, the related method will return null and
 * will not affect the basic request function of the SDK.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface RequestAttributes {

    /**
     * Sets the host name.
     *
     * <p>This method is used to set the host name for the
     * current object. If the passed host parameter is null,
     * it means no host name is set.
     *
     * <p>The recommended setting for this value is to enable
     * dynamic changes to the server requested by the SDK,
     * with better scalability. However, this is only a
     * suggestion and depends on individual needs.
     *
     * @param host The host name, which can be null.
     */
    void setHost(String host);


    /**
     * Gets the host name.
     *
     * <p>This method is used to retrieve the host name set
     * for the current object. If no host name has been set
     * before, it will return null.
     *
     * @return The host name, which may be null.
     */
    String getHost();

    /**
     * Sets the request caller {@code RequestCaller}.
     *
     * <p>This method is used to set a request caller {@code RequestCaller}
     * for the current object,if the passed {@code RequestCaller} parameter
     * is null, it means no request caller is set.
     *
     * <p>The recommended setting for this value is to implement special
     * auxiliary functions such as retry for SDK call failures. Please
     * refer to class {@code RequestCaller} for details. Of course, this
     * is just a suggestion and depends on individual needs.
     *
     * @param requestCaller The request caller, which can be null.
     * @since 1.0.2
     */
    void setRequestCaller(RequestCaller requestCaller);

    /**
     * Gets the request caller {@code RequestCaller}.
     *
     * <p>This method is used to retrieve the request caller
     * {@code RequestCaller} set for the current object. If no
     * request caller has been set before, it will return null.
     *
     * @return The request caller, which may be null.
     * @since 1.0.2
     */
    RequestCaller getRequestCaller();
}
