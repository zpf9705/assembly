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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

import java.io.Closeable;

/**
 * The {@code ServiceContext} interface defines the interaction specifications
 * with service beans in the Spring framework, including the retrieval, addition,
 * existence checking, and removal of services.
 *
 * <p>It provides a mechanism that allows fine-grained management of service beans
 * outside the Spring container or within a specific scope, thereby enhancing the
 * flexibility and scalability of the Spring framework and extend Java's {@link Closeable}
 * interface to elegantly close Spring's {@link ApplicationContext} by calling the
 * {@link #close()} method of this interface.
 *
 * <p>The constant {@link #SUPPORT_SCOPE} in the interface defines a custom scope
 * called {@code service}, which is used to specify the lifecycle and visibility
 * of specific beans in Spring configuration. By implementing the custom scope
 * {@link ServiceScope}, the behavior of this scope is further defined.
 *
 * <p>The methods in the interface cover the full lifecycle management of services,
 * including service acquisition, addition, existence checking, and removal. These
 * methods are based on generics to support multiple types of services.
 *
 * <p>The underlying implementation utilizes the bean query function method of spring
 * context central interface {@link ApplicationContext}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface ServiceContext extends Closeable {

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
     * <p>The transmission of this service name requires the use of the original core processing
     * class {@link ServiceCore} method {@code ServiceCore#enhancement}, which has been specially
     * processed.
     *
     * @param serviceName the name of the service to retrieve.
     * @param <S>         the type of service to retrieve.
     * @return The service instance obtained by parsing the service name.
     * @throws NoAvailableServiceException if there is no available service.
     * @throws BeansException              if the bean could not be obtained
     * @throws ClassCastException          If the bean obtained by name is not the
     *                                     required generic type.
     * @throws NullPointerException        if input serviceName is {@literal null}.
     */
    <S> S getService(String serviceName) throws NoAvailableServiceException, ClassCastException;

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
     * @param serviceName  the name of the service to retrieve.
     * @param requiredType type the bean must match,can be an interface or superclass.
     * @param <S>          the type of service to retrieve.
     * @return The service instance obtained by parsing the service name and type.
     * @throws NoAvailableServiceException    if there is no available service.
     * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
     * @throws BeansException                 if the bean could not be created
     * @throws NullPointerException           if input serviceName or requiredType is {@literal null}.
     */
    <S> S getService(String serviceName, Class<S> requiredType) throws NoAvailableServiceException;

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
     * recognized by Spring will be automatically added to the current context, so this must be
     * noted.
     *
     * <p>Service classes that meet the scanning conditions of the Spring framework will be
     * executed, while other service classes that have not been scanned can be dynamically
     * registered using this method. This method will dynamically create Spring beans and
     * participate in renaming operations based on the parent class or interface of annotation
     * {@link ServiceCollection} marked on this bean.
     *
     * @param serviceName the name of the service to add,can be empty, when empty,
     *                    use a type qualified name instead.
     * @param serviceType the type of service class added must be instantiated and the
     *                    parent class or interface must meet the annotation {@link ServiceCollection}.
     * @param <S>         the type of service to add.
     * @return If {@code true} is returned, it indicates successful addition; otherwise,
     * it indicates failed addition.
     * @throws BeanDefinitionStoreException if registration failed.
     * @throws NullPointerException         if input serviceType is {@literal null}.
     * @since 1.0.2
     */
    <S> boolean addService(@Nullable String serviceName, Class<S> serviceType) throws IllegalStateException;

    /**
     * Return a boolean tag indicating whether the current input service name contains
     * the corresponding service instance.
     *
     * <p>Essentially call the factory method of {@link ApplicationContext#containsBean} and
     * make intake adjustments in the name stage.
     *
     * <p>
     * <strong>Note:</strong>
     * <p>The transmission of this service name requires the use of the original core processing
     * class {@link ServiceCore} method {@code ServiceCore#enhancement}, which has been specially
     * processed.
     *
     * @param serviceName the name of the service to retrieve.
     * @return If {@code true} is returned, this service exists, otherwise it does not exist.
     */
    boolean containsService(String serviceName);

    /**
     * Returns a boolean value representing the result of the containing operation, which re
     * encodes the final service name using the provided service name and type to match the
     * bean of the specific type provided.This method does not throw undiscovered exceptions
     * and is represented by a boolean result.
     *
     * <p>Essentially call the factory method of {@link ApplicationContext#containsBean} and
     * make intake adjustments in the name stage.
     *
     * @param serviceName  the name of the service to contains.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param <S>          the type of service to contains.
     * @return If {@code true} is returned, this service exists, otherwise it does not exist.
     * @throws NullPointerException if input serviceName or requiredType is {@literal null}.
     * @since 1.0.2
     */
    <S> boolean containsService(String serviceName, Class<S> requiredType);

    /**
     * Return a boolean tag representing the result of deleting the corresponding
     * service instance using the provided service name.
     *
     * <p>The removed service will be destroyed in the Spring container at the
     * same time as it is removed in the context of this service, with a callback
     * {@link DisposableBean}.
     *
     * <p>Only applicable for deleting service classes that have specific scope
     * {@link org.springframework.beans.factory.config.Scope} storage, such as
     * the currently supported {@link ServiceScope}, developers can push this type.
     *
     * <p>
     * <strong>Note:</strong>
     * <p>The transmission of this service name requires the use of the original core processing
     * class {@link ServiceCore} method {@code ServiceCore#enhancement}, which has been specially
     * processed.
     *
     * @param serviceName the name of the service to remove.
     * @return If {@code true} is returned, the service has been successfully removed,
     * otherwise it does not exist or the removal has failed.
     */
    boolean removeService(String serviceName) throws NoAvailableServiceException;

    /**
     * Return a boolean type result representing the result of service removal,
     * based on the re encoding of the provided name and type.
     *
     * <p>The removed service will be destroyed in the Spring container at the
     * same time as it is removed in the context of this service, with a callback
     * {@link DisposableBean}.
     *
     * <p>Only applicable for deleting service classes that have specific scope
     * {@link org.springframework.beans.factory.config.Scope} storage, such as
     * the currently supported {@link ServiceScope}, developers can push this type.
     *
     * @param serviceName  the name of the service to remove.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param <S>          the type of service to remove.
     * @return If {@code true} is returned, the service has been successfully removed,
     * otherwise it does not exist or the removal has failed.
     * @throws IllegalArgumentException if beanName does not correspond to an object in a mutable scope.
     * @throws IllegalStateException    if no Scope SPI registered for certain scope name.
     * @throws NullPointerException     if input serviceName or requiredType is {@literal null}.
     * @since 1.0.2
     */
    <S> boolean removeService(String serviceName, Class<S> requiredType);
}
