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

package top.osjf.proxy.core;

/**
 * {@code ProxyFactory} production factory interface for proxy classes.
 *
 * <p>This interface defines a standardized contract for creating dynamic proxy objects,
 * adhering to the Factory Pattern and Proxy Pattern design principles. It serves as
 * an abstraction layer that decouples client code from underlying proxy implementation
 * technologies, enabling seamless integration of various proxy mechanisms (JDK Dynamic
 * Proxies, CGLIB, ByteBuddy, etc.) while providing infrastructure for advanced features
 * like method interception and Aspect-Oriented Programming (AOP).
 * <p>
 * <h3>Core Responsibilities</h3>
 * <ul>
 * <li><strong>Dynamic proxy generation</strong>: Generate proxy objects based on the
 * target type at runtime, without the need to hard code proxy classes.</li>
 * <li><strong>Method Call Routing</strong>: Delegate method calls on proxy objects to
 * a specified processor ({@link MethodInvocationHandler}).
 * <ul>
 * <li>Implement call interception mechanism</li>
 * <li>Support custom logic before and after method execution</li>
 * <li>Allow modification of method parameters and return values</li>
 * </ul>
 * </li>
 * <li><strong>Technology independence</strong>: Block differences in proxy technology and
 * provide a unified proxy creation API.
 * <ul>
 * <li>The client does not need to be aware of the underlying proxy implementation</li>
 * <li>Support proxy technology hot switching</li>
 * <li>Provide a consistent exception handling mechanism</li>
 * </ul>
 * </li>
 * </ul>
 * <p>This interface embodies the structural solution of the proxy pattern in Design Patterns,
 * used to control object access. Implementing cross-cutting concerns (logs, security, transactions)
 * through non-invasive methods, Compliant with AOP principles. The factory abstraction conforms
 * to the Dependency Inversion Principle, promoting loose coupling and testability.
 * <h3>Typical application scenarios</h3>
 * <ul>
 * <li>Remote Proxy in Distributed Systems (RPC Framework)</li>
 * <li>Transaction proxy for database access layer</li>
 * <li>Security check proxy for service layer</li>
 * <li>Performance Monitoring and Statistical Agent</li>
 * </ul>
 *
 * <h3>Typical implementation class structure:</h3>
 * <pre>
 *     public class JdkProxyFactory implements ProxyFactory {
 *     public <T> T newProxy(Class<T> type, MethodInvocationHandler handler) throws Throwable {
 *         // Verify interface type
 *         if (!type.isInterface()) {
 *             throw new IllegalArgumentException("JDK proxy only supports interface types");
 *         }
 *         // Proxy Logic
 *         // ...
 *     }
 * }
 *      // usage
 *      ProxyFactory factory = new JdkProxyFactory();
 *      MyService proxy = factory.newProxy(MyService.class, new MyInvocationHandler());
 *      proxy.performOperation();
 * </pre>
 *
 * <h2>NOTE:</h2>
 *
 * <p>This {@code ProxyFactory} implementation follows a stateless proxy generation strategy,
 * and its design paradigm does not maintain a persistent caching mechanism for proxy instances.
 * Instead, it adopts an on the fly generation mode, where each call creates an independent proxy
 * object instance.
 * <ul>
 * <li>Need to handle target type validation (such as final classes/methods)</li>
 * <li>Cache mechanism should be implemented to improve performance</li>
 * <li>Need to support proxy chains (multiple processor combinations)</li>
 * <li>An exception conversion mechanism should be provided</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface ProxyFactory {

    /**
     * Create a dynamic proxy instance of the specified type and delegate method calls
     * to the provided processor.
     *
     * <p>This method dynamically generates a proxy object based on the incoming target type
     * (interface or class), and calls the method invocation handler ({@link MethodInvocationHandler})
     * for all methods on the proxy object. The proxy object behaves in accordance with the target type,
     * But in actual execution, it will trigger the processor's custom logic.
     *
     * @param type              interface or class type (Class object) that requires proxy.
     * @param invocationHandler method call processor, define proxy method execution strategy.
     * @param <T>               target type parameter of proxy object.
     * @return Dynamically generated proxy object instances.
     * @throws Throwable                Other possible exceptions that may be thrown (determined by the proxy
     *                                  implementation or processor).
     * @throws ProxyCreationException   Throwing when proxy creation fails (e.g. type does not support proxy).
     * @throws IllegalArgumentException When the input parameter is invalid, throw it (such as null type).
     */
    <T> T newProxy(Class<T> type, MethodInvocationHandler invocationHandler) throws Throwable;
}
