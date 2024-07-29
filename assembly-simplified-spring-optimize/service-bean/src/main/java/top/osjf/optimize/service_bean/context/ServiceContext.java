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

package top.osjf.optimize.service_bean.context;

import org.springframework.context.ApplicationContext;

import java.io.Closeable;

/**
 * The central interface for obtaining service context configuration.
 *
 * <p>The underlying implementation utilizes the bean query function method of spring
 * context central interface {@link ApplicationContext}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface ServiceContext extends Closeable {

    /**
     * Returns a service with a specified name, which can be shared or independent.
     * <p>The operation is equivalent to {@link ApplicationContext#getBean(String, Class)}},
     * and on this basis,Add the class name {@link Class#getName()}} of the routing service
     * in the name to prevent potential bean duplication issues, in order to better adapt to single
     * interface multi implementation class routing scenarios in the Spring framework.
     * <p>According to the specifications of Spring's beans, multiple alias queries are supported,
     * but methods in the context will be called to support {@link ApplicationContext#getBean(String, Class)}.
     *
     * @param serviceName  the name of the service to retrieve.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param <S>          types of required.
     * @return an instance of the service.
     * @throws NoSuchServiceException if there is no such service.
     */
    <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException;

    /**
     * Returns the context assist class for the spring framework.
     * <p>When the current service context cannot meet the scenario, spring's context
     * object can be obtained to operate the corresponding API to meet the current
     * service acquisition requirements.
     *
     * @return Return a helper of Spring context.
     */
    ApplicationContext getApplicationContext();

    @Override
    void close();
}
