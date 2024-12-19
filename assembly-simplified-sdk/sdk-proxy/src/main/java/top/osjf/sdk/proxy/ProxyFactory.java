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

/**
 * Factory interface for proxy object creation.
 *
 * <p>This interface defines a generic proxy factory method for creating
 * proxy objects of any type and the method accepts a variable number of
 * arguments,which can be used to provide necessary configurations or
 * initialization data when creating the proxy object.
 *
 * <p>With the generic method {@link #newProxy}, it allows flexible creation
 * of proxy instances of any type. This is very useful when dynamic proxy objects
 * need to be generated, such as in AOP (Aspect-Oriented Programming), or when
 * proxy logic (e.g., caching, transaction management, permission checking)
 * needs to be provided for certain objects.
 *
 * <p>Classes implementing this interface need to define how to create and
 * return proxy objects of a specific type based on the passed arguments.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface ProxyFactory {

    /**
     * Creates a new proxy object.
     *
     * <p>This method accepts a generic type parameter {@code T} to specify
     * the type of the proxy object. It also accepts a variable number of
     * {@code Object} type parameters {@code args}, which will be used when
     * creating the proxy object.
     *
     * <p>Callers can specify the type of proxy object they want to create
     * and pass necessary configuration information, dependencies, or other
     * initialization data through the {@code args} parameters. The proxy
     * factory will dynamically generate a proxy object based on this
     * information.
     *
     * <p>The generated proxy object will have the same interface or parent
     * class as the original object (type support depends on the architecture)
     * , and additional behavior can be added when calling its methods, such
     * as JDK's {@link java.lang.reflect.InvocationHandler}.
     *
     * @param <T>  the type of the proxy object.
     * @param args a variable number of arguments used to provide necessary
     *             configurations or initialization data when creating the
     *             proxy object.
     * @return specify {@code T} the type of proxy object.
     * @throws Exception it can be any error that can be thrown.
     */
    <T> T newProxy(Object... args) throws Exception;
}
