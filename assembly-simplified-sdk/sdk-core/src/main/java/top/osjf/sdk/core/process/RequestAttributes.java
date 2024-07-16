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

/**
 * Regarding the definition property of {@link Request},
 * the aware interface needs to be implemented to set and
 * obtain the corresponding request properties.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface RequestAttributes {

    /**
     * Set the host address required for the SDK request
     * domain, which can be a domain name or an IP address
     * containing a good port.
     *
     * @param host Given request a host address.
     */
    void setHost(String host);

    /**
     * @return A request builder within a host.
     */
    String getHost();
}
