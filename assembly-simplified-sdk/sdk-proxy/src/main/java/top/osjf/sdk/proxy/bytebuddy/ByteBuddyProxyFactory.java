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

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;
import top.osjf.sdk.proxy.AbstractProxyFactory;

/**
 * {@code ByteBuddyProxyFactory} is a class that extends {@code AbstractProxyFactory} and is
 * used to dynamically create proxy classes.It utilizes the ByteBuddy library to generate and
 * manipulate Java classes at runtime,supporting the creation of dynamic proxies for interfaces
 * and regular classes (non-enumeration,non-final).
 *
 * <p>The main function of this class is to dynamically generate subclasses that implement
 * specified interfaces or inherit specified classes through {@link ByteBuddy}, And use the
 * provided {@code InvocationHandlerAdapterDelegationCallback} to intercept method calls.
 * This method allows for flexible modification or extension of class behavior at runtime
 * without the need to modify the class's source code.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class ByteBuddyProxyFactory extends AbstractProxyFactory<InvocationHandlerAdapterDelegationCallback> {


    /**
     * {@inheritDoc}
     *
     * @param type     {@inheritDoc}
     * @param callback ByteBuddy's configuration callback
     *                 interface {@link java.lang.reflect.InvocationHandler}.
     * @return {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    protected <T> T newProxyInternal(Class<T> type, InvocationHandlerAdapterDelegationCallback callback)
            throws Throwable {
        Class<?> subclass;
        ByteBuddy byteBuddy = new ByteBuddy();
        if (type.isInterface()) {

            /*
             * If the type passed in is an interface, create a subclass that inherits from
             * java.lang.Object and implements the interface.
             */
            subclass = byteBuddy
                    .subclass(Object.class)
                    .implement(type)
                    .method(ElementMatchers.any())
                    .intercept(InvocationHandlerAdapter.of(callback))
                    .make()
                    .load(type.getClassLoader())
                    .getLoaded();

        }
        else {

            /*
             * If the passed type is a class (non enumeration, non-final class), create a subclass
             * that inherits from that class.
             * Use Invocation Handler Adapter. of (callback) to intercept calls to class methods.
             *
             * Note: Due to the specificity of enumeration types and final classes, this branch does
             *  not support creating dynamic proxies for them.
             */

            subclass = byteBuddy
                    .subclass(type)
                    .method(ElementMatchers.any())
                    .intercept(InvocationHandlerAdapter.of(callback))
                    .make()
                    .load(type.getClassLoader())
                    .getLoaded();
        }

        //Return an instance of the generated dynamic proxy class
        return (T) subclass.getDeclaredConstructor().newInstance();
    }
}
