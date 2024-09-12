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
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

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
     * Add a service instance based on the provided service name and type, where the service
     * name is not required to be provided and defaults to the encoded qualified name of the
     * type.
     *
     * <p>The setting of the service name is passed like the value provided by the annotation
     * {@link Component#value()}, which is dynamically passed here. The initialization of the
     * instance is carried out by the implementation to ensure that the type has the necessary
     * elements such as public construction methods.
     *
     * <p>Only beans that have not been marked by Spring to be added to the container are supported,
     * which means only dynamic bean registration is supported. Container objects that have been
     * recognized by Spring will be automatically added to the current context, so this must be noted.
     *
     * @param serviceName the name of the service to retrieve,can be empty, when empty,
     *                    use a type qualified name instead.
     * @param serviceType real and instantiated service types.
     * @param <S>         types of required.
     * @return If {@code true} is returned, it indicates successful addition; otherwise,
     * it indicates failed addition.
     * @since 1.0.2
     */
    <S> boolean addService(@Nullable String serviceName, Class<S> serviceType);

    /**
     * Returns a Boolean value representing the result of the containing operation, which re
     * encodes the final service name using the provided service name and type to match the
     * bean of the specific type provided.This method does not throw undiscovered exceptions
     * and is represented by a Boolean result.
     *
     * <p>Essentially call the factory method of {@link ApplicationContext#containsBean} and
     * make intake adjustments in the name stage.
     *
     * @param serviceName  the name of the service to retrieve.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param <S>          types of required.
     * @return If {@code true} is returned, this service exists, otherwise it does not exist.
     * @since 1.0.2
     */
    <S> boolean containsService(String serviceName, Class<S> requiredType);

    /**
     * Return a boolean type result representing the result of service removal,
     * based on the re encoding of the provided name and type.
     *
     * <p>The removed service will be destroyed in the Spring container at the
     * same time as it is removed in the context of this service, with a callback
     * {@link org.springframework.beans.factory.DisposableBean}.
     *
     * @param serviceName  the name of the service to retrieve.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param <S>          types of required.
     * @return If {@code true} is returned, the service has been successfully removed, otherwise
     * it does not exist or the removal has failed.
     * @since 1.0.2
     */
    <S> boolean removeService(String serviceName, Class<S> requiredType);

    /**
     * Returns the context assist class for the spring framework.
     * <p>When the current service context cannot meet the scenario, spring's context
     * object can be obtained to operate the corresponding API to meet the current
     * service acquisition requirements.
     *
     * @return Return a helper of Spring context.
     */
    ApplicationContext getApplicationContext();

    /**
     * {@inheritDoc}
     */
    @Override
    void close();
}
