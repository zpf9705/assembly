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


import top.osjf.sdk.proxy.bytebuddy.ByteBuddyProxyFactory;
import top.osjf.sdk.proxy.cglib.CglibProxyFactory;
import top.osjf.sdk.proxy.javassist.JavassistProxyFactory;
import top.osjf.sdk.proxy.jdk.JDKProxyFactory;
import top.osjf.sdk.proxy.springcglib.SpringCglibProxyFactory;

/**
 * The {@code ProxyModel} enumeration encapsulates various proxy models, each of which
 * is associated with a specific proxy factory responsible for generating proxy instances
 * of the corresponding type. This design allows for a modular and extensible approach to
 * proxy creation, enabling developers to select the most appropriate proxy mechanism based
 * on their requirements.
 *
 * <p>Each enum constant represents a distinct proxy model, leveraging different underlying
 * technologies or libraries to achieve dynamic proxy generation. The enumeration implements
 * the {@link ProxyFactory} interface, thereby providing a unified API for proxy creation
 * across diverse proxy mechanisms.</p>
 *
 * <p>
 * <h2>The supported proxy models include:</h2>
 * <ul>
 *     <li>{@link #JDK}: Utilizes the JDK dynamic proxy mechanism, which is based on Java's
 *     built-in reflection and is suitable for interface-based proxying.</li>
 *     <li>{@link #CGLIB}: Employs the CGLIB library, which generates proxies by subclassing
 *     target classes, thus supporting class-based proxying.</li>
 *     <li>{@link #SPRING_CGLIB}: Leverages the CGLIB proxy mechanism as integrated with the
 *     Spring Framework, offering enhanced compatibility and features within Spring-based
 *     applications.</li>
 *     <li>{@link #BYTE_BUDDY}: Utilizes the Byte Buddy library, a powerful and flexible
 *     bytecode manipulation library that provides advanced proxying capabilities.</li>
 *     <li>{@link #JAVASSIST}: Relies on the Javassist library, which manipulates Java
 *     bytecode to generate dynamic proxies with fine-grained control.</li>
 * </ul>
 *
 * <p>By abstracting the proxy creation logic into distinct factory implementations, this enumeration
 * promotes separation of concerns and adheres to the principle of single responsibility. It also
 * facilitates easy extension, allowing new proxy models to be added without modifying existing code.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public enum ProxyModel implements ProxyFactory {

    /**
     * Represents a proxy model that employs the JDK dynamic proxy factory for
     * generating proxy instances.This model is suitable for scenarios where the
     * target class implements one or more interfaces.
     */
    JDK(new JDKProxyFactory()),

    /**
     * Represents a proxy model that utilizes the CGLIB proxy factory for generating
     * proxy instances.This model is suitable for scenarios where the target class does
     * not implement any interfaces,as it relies on subclassing the target class.
     */
    CGLIB(new CglibProxyFactory()),

    /**
     * Represents a proxy model that leverages the Spring CGLIB proxy factory for
     * generating proxy instances.This model is optimized for use within Spring-based
     * applications and offers seamless integration with Spring's dependency injection
     * and AOP frameworks.
     */
    SPRING_CGLIB(new SpringCglibProxyFactory()),

    /**
     * Represents a proxy model that utilizes the Byte Buddy proxy factory for generating
     * proxy instances.This model provides advanced features and flexibility, making it
     * suitable for complex proxying scenarios.
     */
    BYTE_BUDDY(new ByteBuddyProxyFactory()),

    /**
     * Represents a proxy model that employs the Javassist proxy factory for generating
     * proxy instances.This model offers fine-grained control over bytecode manipulation,
     * making it suitable for scenarios requiring precise customization of proxy behavior.
     */
    JAVASSIST(new JavassistProxyFactory());

    /**
     * The proxy factory associated with this proxy model,
     * responsible for generating proxy instances.
     */
    final ProxyFactory proxyFactory;

    /**
     * Constructs a new proxy model with the specified proxy factory.
     * @param proxyFactory the proxy factory to be associated with this proxy model
     */
    ProxyModel(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Delegates the proxy creation to the associated proxy factory, ensuring that the
     * proxy instance is generated according to the specific mechanism defined by the factory.
     */
    @Override
    public <T> T newProxy(Class<T> type, DelegationCallback callback) throws Throwable {
        return proxyFactory.newProxy(type, callback);
    }
}
