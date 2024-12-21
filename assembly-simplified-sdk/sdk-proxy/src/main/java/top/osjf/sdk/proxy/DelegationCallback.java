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

import top.osjf.sdk.core.IsInstanceWrapper;

import java.lang.reflect.Method;

/**
 * Proxy callback interface, used to handle method calls for
 * proxy objects.
 *
 * <p>In proxy mode, the implementation class of the {@code DelegationCallback}
 * interface is responsible for handling method calls to proxy objects,
 * typically involving forwarding calls to another object (referred to
 * as the "target object" or "actual object"), or executing some custom
 * logic (such as logging, permission checking, etc.).
 *
 * <p>This interface extends the {@code IsInstanceWrapper} interface
 * and can perform directed conversion of callback types based on the
 * underlying implementation, but attention should be paid to the
 * original type.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface DelegationCallback extends IsInstanceWrapper {

    /**
     * This method proxy execution passes the proxy method object
     * {@code Method}, proxy method parameters, and other proxy
     * information {@code SpecificProxyOtherVariable} (determined
     * by the underlying framework), passes them to the processing
     * class for processing, and returns the result.
     *
     * <p>When the proxy class is called, this method will be called.
     *
     * @param method   proxy method for callback.
     * @param args     the parameters of the proxy callback method.
     * @param variable proxy information other than proxy method objects
     *                 and parameters (provided by the underlying framework).
     * @return the result object of the proxy method callback processing.
     * @throws Throwable it can be any error that can be thrown.
     */
    Object callback(Method method, Object[] args, SpecificProxyOtherVariable variable) throws Throwable;

    /**
     * An internal interface that extends the {@code IsInstanceWrapper}
     * interface for subclass wrapper conversion.
     *
     * <p>It is a markup interface or container interface used to represent
     * other variables or states related to a specific agent.
     *
     * <p>The specific implementation of a subclass can contain any additional
     * information related to the proxy object or proxy logic.
     */
    interface SpecificProxyOtherVariable extends IsInstanceWrapper {
        /**
         * Return the string information of the remaining proxy information.
         *
         * @return string information of the remaining proxy information.
         */
        String toString();
    }
}
