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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The abstract {@link ServiceContext} service class mainly adapts to
 * the following functions:
 * <ul>
 * <li>Provide its context object through the mechanism of Spring
 * {@link ApplicationContext}.</li>
 * <li>Specify the notification event for container bean refresh
 * completion, and clear the name collection of optimized beans
 * marked by {@link ServiceContextBeanNameGenerator} in the event
 * content.</li>
 * <li>Calling the {@link #close()} method can elegantly close
 * the Spring context {@link ApplicationContext}.</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public abstract class AbstractServiceContext implements ConfigurableServiceContext,
        ApplicationContextAware, DisposableBean {

    private UnwrapApplicationContext unwrapApplicationContext;

    private ServiceTypeRegistry serviceTypeRegistry;

    private Map<String, Class<?>> serviceTypeMap;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.unwrapApplicationContext = new UnwrapApplicationContext(context);
    }

    /**
     * Inject {@code ServiceTypeRegistry} beans for internal use.
     *
     * <p>Inject this instance to dynamically manage the records of injected service types.
     *
     * @param serviceTypeRegistry an internal {@link ServiceTypeRegistry} instance.
     */
    @Autowired
    public void setServiceTypeRegistry(
            @Qualifier(ServiceDefinitionUtils.INTERNAL_SERVICE_TYPE_REGISTER_BEAN_NAME)
            ServiceTypeRegistry serviceTypeRegistry) {
        this.serviceTypeRegistry = serviceTypeRegistry;
        this.serviceTypeMap = serviceTypeRegistry.getServiceTypeMap();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get serviceBean count by {@link #serviceTypeRegistry}.
     *
     * @since 1.0.3
     */
    @Override
    public int getServiceBeanCount() {
        return serviceTypeMap.size();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get serviceBean names by {@link #serviceTypeRegistry}.
     *
     * @since 1.0.3
     */
    @Override
    public String[] getServiceBeanNames() {
        return StringUtils.toStringArray(serviceTypeMap.keySet());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get serviceBean names for type by {@link #serviceTypeRegistry}.
     *
     * @since 1.0.3
     */
    @Override
    public String[] getServiceBeanNamesForType(Class<?> type) throws NoAvailableServiceException {
        if (!isRecordType(type)) {
            throw new NoAvailableServiceException(type);
        }
        List<String> names = new ArrayList<>();
        serviceTypeMap.forEach((n, c) -> {
            if (c == type) names.add(n);
        });
        return StringUtils.toStringArray(names);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get type for serviceBean name by {@link #serviceTypeRegistry}.
     *
     * @since 1.0.3
     */
    @Override
    public Class<?> getTypeForServiceBeanName(String serviceName) throws NoAvailableServiceException {
        if (!ServiceDefinitionUtils.isEnhancementServiceName(serviceName)
                || !serviceTypeMap.containsKey(serviceName)) {
            throw new NoAvailableServiceException(serviceName);
        }
        return serviceTypeMap.get(serviceName);
    }

    @Override
    public void destroy() {
        close();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Clear the relevant service class information that has already been loaded
     * in {@link #serviceTypeRegistry}.
     */
    @Override
    public void close() {
        serviceTypeRegistry.close();
    }

    /**
     * @return A service type registry class that stores and manages.
     */
    protected ServiceTypeRegistry getServiceTypeRegistry() {
        return serviceTypeRegistry;
    }

    /**
     * @return The unwrap function object of spring context {@link ApplicationContext}.
     */
    protected UnwrapApplicationContext unwrapContext() {
        return unwrapApplicationContext;
    }

    /**
     * Return a {@code Boolean} type indicating whether the input type is a record type.
     *
     * @param type the given type.
     * @return If {@code true} is returned, it represents the type of record, otherwise
     * it is not.
     */
    protected boolean isRecordType(Class<?> type) {
        return serviceTypeMap.containsValue(type);
    }
}
