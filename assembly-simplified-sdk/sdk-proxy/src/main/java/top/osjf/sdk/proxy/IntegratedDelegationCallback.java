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


package top.osjf.sdk.proxy;

import top.osjf.sdk.proxy.bytebuddy.InvocationHandlerAdapterDelegationCallback;
import top.osjf.sdk.proxy.cglib.CglibDelegationCallback;
import top.osjf.sdk.proxy.javassist.JavassistDelegationCallback;
import top.osjf.sdk.proxy.jdk.JDKDelegationCallback;
import top.osjf.sdk.proxy.springcglib.SpringCglibDelegationCallback;

/**
 * The {@code IntegratedDelegationCallback} interface is a marker interface that
 * aggregates multiple delegation callback interfaces. By implementing this interface,
 * a class signifies that it can serve as a unified callback mechanism across various
 * proxying frameworks and libraries, including JDK dynamic proxies, CGLIB proxies,
 * Spring CGLIB proxies, Javassist proxies, and invocation handler adapters.
 *
 * <p>This interface is particularly useful in scenarios where a single callback
 * implementation needs to be compatible with multiple proxying technologies. It
 * provides a contract that ensures the implementing class can handle callback
 * invocations in a consistent manner across different proxying mechanisms.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface IntegratedDelegationCallback
        extends JDKDelegationCallback,
        CglibDelegationCallback,
        SpringCglibDelegationCallback,
        JavassistDelegationCallback,
        InvocationHandlerAdapterDelegationCallback {
}
