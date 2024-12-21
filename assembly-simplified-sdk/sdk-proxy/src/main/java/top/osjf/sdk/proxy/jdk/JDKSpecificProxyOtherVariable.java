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


package top.osjf.sdk.proxy.jdk;

import top.osjf.sdk.proxy.DelegationCallback;

/**
 * The {@code JDKSpecificProxyOtherVariable} class used to provide
 * additional information related to a specific proxy instance in
 * the context of JDK dynamic proxy.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class JDKSpecificProxyOtherVariable implements DelegationCallback.SpecificProxyOtherVariable {
    /**
     * the proxy instance that the method was invoked on.
     */
    private final Object proxy;

    public JDKSpecificProxyOtherVariable(Object proxy) {
        this.proxy = proxy;
    }

    public Object getProxy() {
        return proxy;
    }

    @Override
    public String toString() {
        return "JDKSpecificProxyOtherVariable{" +
                "proxy=" + proxy +
                '}';
    }
}
