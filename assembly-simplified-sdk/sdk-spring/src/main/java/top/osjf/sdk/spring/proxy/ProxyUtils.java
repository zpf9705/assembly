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

package top.osjf.sdk.spring.proxy;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.lang.NonNull;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Proxy related tools.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@SuppressWarnings("unchecked")
public abstract class ProxyUtils {

    /**
     * Using a {@code class} and {@code InvocationHandler} build a JDK dynamic proxy object.
     *
     * @param clazz             Proxy type.
     * @param invocationHandler Callback interface.
     * @param <T>               type.
     * @return JDK dynamic proxy object
     */
    public static <T> T createJdkProxy(Class<T> clazz, InvocationHandler invocationHandler) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, invocationHandler);
    }

    /**
     * Using a {@code class} and {@code InvocationHandler} build a JDK dynamic proxy object.
     *
     * @param args The conditions for creating a proxy object.
     * @param <T>  type.
     * @return JDK dynamic proxy object
     */
    public static <T> T createJdkProxy(@NonNull Object... args) {
        Class<T> type = null;
        InvocationHandler invocationHandler = null;
        for (Object arg : args) {
            if (type != null && invocationHandler != null) {
                break;
            }
            if (arg instanceof Class) {
                type = (Class<T>) arg;
            }
            if (arg instanceof InvocationHandler) {
                invocationHandler = (InvocationHandler) arg;
            }
        }
        if (type == null || invocationHandler == null) throw new IllegalArgumentException();
        return createJdkProxy(type, invocationHandler);
    }

    /**
     * Using a {@code class} and {@code Callback} build a Cglib dynamic proxy object.
     *
     * @param type     Proxy type.
     * @param callback Callback interface.
     * @param <T>      type.
     * @return Cglib dynamic proxy object.
     */
    public static <T> T createCglibProxy(Class<T> type, Callback callback) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(type);
        enhancer.setCallback(callback);
        return (T) enhancer.create();
    }

    /**
     * Using a {@code class} and {@code Callback} build a Cglib dynamic proxy object.
     *
     * @param args The conditions for creating a proxy object.
     * @param <T>  type.
     * @return Cglib dynamic proxy object.
     */
    public static <T> T createCglibProxy(@NonNull Object... args) {
        Class<T> type = null;
        Callback callback = null;
        for (Object arg : args) {
            if (type != null && callback != null) {
                break;
            }
            if (arg instanceof Class) {
                type = (Class<T>) arg;
            }
            if (arg instanceof Callback) {
                callback = (Callback) arg;
            }
        }
        if (type == null || callback == null) throw new IllegalArgumentException();
        return createCglibProxy(type, callback);
    }
}
