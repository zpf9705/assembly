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

import org.springframework.beans.factory.DisposableBean;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import top.osjf.optimize.service_bean.annotation.ServiceCollection;

import java.io.Closeable;
import java.io.IOException;

/**
 * {@code ConfigurableServiceContext} extends from the {@code ListableServiceContext}
 * interface and adds options for adding and deleting service classes based
 * on the parent class interface, making its management more flexible.
 *
 * <p>The constant {@link #SUPPORT_SCOPE} in the interface defines a custom scope
 * called {@code service}, which is used to specify the lifecycle and visibility
 * of specific beans in Spring configuration. By implementing the custom scope
 * {@link ServiceScope}, the behavior of this scope is further defined.
 *
 * <p>The configurable {@code ServiceContext} indicates the writable nature of
 * the service class, and this interface allows for custom registration and
 * deletion of service classes; And the {} interface has been implemented to
 * clear the names and types of related beans that have already been recorded
 * when necessary, so that necessary operations can be performed when the Spring
 * container restarts.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface ConfigurableServiceContext extends ListableServiceContext, Closeable {

    /**
     * Add a service instance based on the provided name and type, where the service
     * name is not required to be provided and defaults to the encoded qualified name of the
     * type.
     *
     * <p>The setting of the name is passed like the value provided by the annotation
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
     * @param name        the name of the service to add,can be empty, when empty,
     *                    use a type qualified name instead.
     * @param serviceType the type of service class added must be instantiated and the
     *                    parent class or interface must meet the annotation {@link ServiceCollection}.
     * @param <S>         the type of service to add.
     * @return If {@code true} is returned, it indicates successful addition; otherwise,
     * it indicates failed addition.
     * @throws NullPointerException if input serviceType is {@literal null}.
     * @since 1.0.2
     */
    <S> boolean addService(@Nullable String name, Class<S> serviceType);

    /**
     * Return a boolean tag representing the result of deleting the corresponding
     * service instance using the provided name.
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
     * <p>The transmission of this service name requires the use of the original core
     * processing class {@link ServiceDefinitionUtils} method {@code ServiceCore#enhancement}
     * and it must be a service name ending with {@code ServiceCore#BEAN_NAME_CLOSE_TAG}
     * , which has been specially processed.
     *
     * @param serviceName the name of the service to remove.
     * @return If {@code true} is returned, the service has been successfully removed,
     * otherwise it does not exist or not end with {@code ServiceCore#BEAN_NAME_CLOSE_TAG}.
     * @throws NullPointerException if input serviceName is {@literal null}.
     */
    boolean removeService(String serviceName);

    /**
     * Return a boolean type result representing the result of service removal,
     * based on the re encoding of the provided service name and type.
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
     * <p>Due to the limitations of the Spring framework for removing beans from
     * specific scopes, the {@code requiredType} of this method must be the native
     * type of the service class used to construct the name of the bean for removing
     * beans from specific scopes.
     *
     * @param serviceName  the name of the service to remove.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param <S>          the type of service to remove.
     * @return If {@code true} is returned, the service has been successfully removed,
     * otherwise it does not exist or not end with {@code ServiceCore#BEAN_NAME_CLOSE_TAG}.
     * @throws NullPointerException if input serviceName or requiredType is {@literal null}.
     * @since 1.0.2
     */
    <S> boolean removeService(String serviceName, Class<S> requiredType);


    /**
     * Close this service context, releasing all record service resources.
     */
    @Override
    void close() throws IOException;
}
