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
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

/**
 * The {@code ServiceContext} interface defines the interaction specifications
 * with service beans in the Spring framework, including the retrieval, addition,
 * existence checking.
 *
 * <p>It provides a mechanism that allows fine-grained management of service beans
 * outside the Spring container or within a specific scope, thereby enhancing the
 * flexibility and scalability of the Spring framework.
 *
 * <p>The methods in the interface cover the full lifecycle management of services,
 * including service acquisition, addition, existence checking. These methods are
 * based on generics to support multiple types of services.
 *
 * <p>The underlying implementation utilizes the bean query function method of spring
 * context central interface {@link ApplicationContext}.
 * <p>
 * <strong>Note:</strong>
 * <p>The parameter names of the following methods are {@code name}, which can
 * be used to encode the final service name using the parent class or interface
 * (for Spring, it is the name or alias of the available bean); the parameter
 * name is {@code serviceName}, which means that only the type of the service
 * class itself can participate in the encoding of the final service name (for
 * Spring, only the name of the bean can be used).
 *
 * <p>All service class operations of itself and its extended subclasses are
 * completed within scope {@link #SUPPORT_SCOPE}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface ServiceContext {

    /**
     * Define a constant to represent a custom scope name {@code service} for a
     * Bean in the Spring framework.
     *
     * <p>This constant means that it represents a custom scope named {@code service}
     * , and in Spring configuration, the behavior of this scope can be defined through
     * a specific class {@link ServiceScope}.
     *
     * @see org.springframework.beans.factory.config.Scope
     * @see org.springframework.context.annotation.AnnotationScopeMetadataResolver
     */
    String SUPPORT_SCOPE = "service";

    /**
     * Returns a service instance with a specified name, which can be shared or independent.
     *
     * <p>The operation is equivalent to {@link ApplicationContext#getBean(String)}},and on
     * this basis, tag the exploration annotation {@link ServiceCollection} of the routing
     * service interface or parent class to prevent potential bean duplication issues, in
     * order to better adapt to single interface multi implementation class routing scenarios
     * in the Spring framework.
     *
     * <p>According to the specifications of Spring's beans, multiple alias queries are supported,
     * but methods in the context will be called to support {@link ApplicationContext#getBean(String)}.
     * <p>
     * <strong>Note:</strong>
     * <p>The transmission of this name requires the use of the original core processing
     * class {@link ServiceDefinitionUtils} method {@code ServiceCore#enhancement}, which has been
     * specially processed.
     *
     * @param name the name of the service to retrieve.
     * @param <S>  the type of service to retrieve.
     * @return The service instance obtained by parsing the name.
     * @throws NoAvailableServiceException if there is no available service.
     * @throws NullPointerException        if input serviceName is {@literal null}.
     */
    <S> S getService(String name) throws NoAvailableServiceException;

    /**
     * Returns a service instance with the specified name and type, which can be shared or independent.
     *
     * <p>The operation is equivalent to {@link ApplicationContext#getBean(String, Class)}},
     * and on this basis, tag the exploration annotation {@link ServiceCollection} of the routing
     * service interface or parent class to prevent potential bean duplication issues, in order
     * to better adapt to single interface multi implementation class routing scenarios in the
     * Spring framework.
     *
     * <p>According to the specifications of Spring's beans, multiple alias queries are supported,
     * but methods in the context will be called to support {@link ApplicationContext#getBean(String, Class)}.
     *
     * @param name         the name of the service to retrieve.
     * @param requiredType type the bean must match,can be an interface or superclass.
     * @param <S>          the type of service to retrieve.
     * @return The service instance obtained by parsing the name and type.
     * @throws NoAvailableServiceException if there is no available service.
     * @throws NullPointerException        if input serviceName or requiredType is {@literal null}.
     */
    <S> S getService(String name, Class<S> requiredType) throws NoAvailableServiceException;

    /**
     * Return a boolean tag indicating whether the current input name contains
     * the corresponding service instance.
     *
     * <p>Essentially call the factory method of {@link ApplicationContext#containsBean} and
     * make intake adjustments in the name stage.
     *
     * <p>
     * <strong>Note:</strong>
     * <p>The transmission of this name requires the use of the original core processing
     * class {@link ServiceDefinitionUtils} method {@code ServiceCore#enhancement}, which has
     * been specially processed.
     *
     * @param name the name of the service to contains.
     * @return If {@code true} is returned, this service exists, otherwise it does not exist.
     * @throws NullPointerException if input serviceName is {@literal null}.
     */
    boolean containsService(String name);

    /**
     * Returns a boolean value representing the result of the containing operation, which re
     * encodes the final name using the provided name and type to match the
     * bean of the specific type provided.This method does not throw undiscovered exceptions
     * and is represented by a boolean result.
     *
     * <p>Essentially call the factory method of {@link ApplicationContext#containsBean} and
     * make intake adjustments in the name stage.
     *
     * @param name         the name of the service to contains.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param <S>          the type of service to contains.
     * @return If {@code true} is returned, this service exists, otherwise it does not exist.
     * @throws NullPointerException if input serviceName or requiredType is {@literal null}.
     * @since 1.0.2
     */
    <S> boolean containsService(String name, Class<S> requiredType);
}
