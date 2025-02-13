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

import java.io.Closeable;
import java.io.IOException;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface ConfigurableServiceContext extends ServiceContext, Closeable {

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

    @Override
    void close() throws IOException;
}
