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


package top.osjf.sdk.proxy.javassist;

import top.osjf.sdk.proxy.DelegationCallback;

import java.lang.reflect.Method;

/**
 * The {@code JavassistPeculiarProxyVariable} class used to provide
 * additional information related to a specific proxy instance
 * in the context of Javassist dynamic proxy.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class JavassistPeculiarProxyVariable implements DelegationCallback.PeculiarProxyVariable {

    /**
     * the proxy instance.
     */
    private final Object self;

    /**
     * the forwarder method for invoking the overridden
     * method.  It is null if the overridden method is
     * abstract or declared in the interface.
     */
    private final Method proceed;

    public JavassistPeculiarProxyVariable(Object self, Method proceed) {
        this.self = self;
        this.proceed = proceed;
    }

    public Object getSelf() {
        return self;
    }

    public Method getProceed() {
        return proceed;
    }
}
