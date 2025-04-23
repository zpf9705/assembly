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

import javassist.util.proxy.MethodHandler;
import top.osjf.sdk.proxy.DelegationCallback;

import java.lang.reflect.Method;

/**
 * {@code JavassistDelegationCallback} extends the {@code MethodHandler}
 * and {@code DelegationCallback} interfaces.
 *
 * <p>It provides a mechanism for Javassist dynamic proxies, through which
 * the method invocation behavior of proxy objects can be customized.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface JavassistDelegationCallback extends MethodHandler, DelegationCallback {

    @Override
    default Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        return callback(thisMethod, args, new JavassistPeculiarProxyVariable(self, proceed));
    }
}
