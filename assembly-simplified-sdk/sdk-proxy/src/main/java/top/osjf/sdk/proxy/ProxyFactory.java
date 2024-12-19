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
 * <p>This interface is a factory interface for creating proxy objects,
 * which allows you to create an object that can perform operations on
 * behalf of another object (the original object) and the proxy object
 * can add additional behavior before or after executing the methods of
 * the original object, or completely replace the method implementation
 * of the original object.
 *
 * <p>Proxy objects are typically dynamically created through reflection
 * mechanisms and implement the same interface as the original object
 * (such as the dynamic proxy {@link java.lang.reflect.Proxy} provided
 * by JDK). Proxy objects can intercept method calls and forward them
 * to the original object, or perform other logic such as logging,
 * transaction management, security checks, etc.
 *
 * <p>This interface will summarize most popular proxy object construction
 * frameworks. {@code DelegationCallback} is the general proxy interface
 * for proxy callbacks, and its implementation {@link top.osjf.sdk.core.Wrapper}
 * can be freely switched within subclasses.
 *
 * <p>Please note that methods in this interface may throw exceptions,
 * and the caller needs to handle these exceptions appropriately.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public interface ProxyFactory {

    /**
     * Creates a new proxy object.
     *
     * <p>This method accepts a generic type parameter {@code T} to
     * specify the type of the proxy object based on the specified
     * type (interface or class) {@code Class} and callback
     * {@code DelegationCallback}.
     *
     * <p>Callers can specify the type of proxy object they want
     * to create and pass necessary configuration information,
     * dependencies, or other initialization data through method
     * parameters and proxy factory will dynamically generate a
     * proxy object based on this information.
     *
     * <p>The generated proxy object will have the same interface
     * or parent class as the original object (depending on the
     * underlying framework), but additional behavior (such as
     * pre-processing and post-processing) can be added when calling
     * its methods.
     *
     * @param <T>      the type of the proxy object.
     * @param type     the type of proxy object to be created (the
     *                 specific type depends on the underlying framework).
     * @param callback the {@code DelegationCallback} callback executed
     *                 when the method of the proxy object is called.
     * @return specify {@code T} the type of proxy object.
     * @throws Throwable it can be any error that can be thrown.
     */
    <T> T newProxy(Class<T> type, DelegationCallback callback) throws Throwable;
}
