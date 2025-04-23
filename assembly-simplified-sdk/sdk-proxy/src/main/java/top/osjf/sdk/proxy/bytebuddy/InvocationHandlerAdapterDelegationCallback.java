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


package top.osjf.sdk.proxy.bytebuddy;

import top.osjf.sdk.proxy.jdk.JDKDelegationCallback;

/**
 * The byteBuddy framework is designed to adapt to the callback information
 * interface intercepted by {@link java.lang.reflect.InvocationHandler} methods.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface InvocationHandlerAdapterDelegationCallback extends JDKDelegationCallback {
}
