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


package top.osjf.sdk.proxy.cglib;

import net.sf.cglib.proxy.MethodProxy;
import top.osjf.sdk.proxy.DelegationCallback;

import java.util.Objects;

/**
 * The {@code CglibPeculiarProxyVariable} class used to provide
 * additional information related to a specific proxy instance
 * and {@code MethodProxy} instance in the context of Cglib dynamic
 * proxy.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class CglibPeculiarProxyVariable implements DelegationCallback.PeculiarProxyVariable {
    /**
     * "this", the enhanced object.
     */
    private final Object obj;
    /**
     * used to invoke super (non-intercepted method); may be called
     */
    private final MethodProxy methodProxy;

    public CglibPeculiarProxyVariable(Object obj, MethodProxy methodProxy) {
        this.obj = obj;
        this.methodProxy = methodProxy;
    }

    public Object getObj() {
        return obj;
    }

    public MethodProxy getMethodProxy() {
        return methodProxy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(obj, methodProxy);
    }
}
