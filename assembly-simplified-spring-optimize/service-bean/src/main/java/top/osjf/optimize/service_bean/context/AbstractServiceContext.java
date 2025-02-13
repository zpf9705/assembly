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
public abstract class AbstractServiceContext implements ConfigurableServiceContext, ApplicationContextAware {

    private ApplicationContext context;

    private ServiceContextBeanNameGenerator serviceContextBeanNameGenerator;

    private Map<String, Class<?>> recordServiceBeanMap;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    /**
     * Inject {@code ServiceContextBeanNameGenerator} beans for internal use.
     *
     * <p>Retrieve the cached record type and use it as a subsequent API to verify
     * whether it is a bean type supported by the record.
     *
     * @param serviceContextBeanNameGenerator an internal {@link ServiceContextBeanNameGenerator} instance.
     */
    @Autowired
    public void setServiceContextBeanNameGenerator(
            @Qualifier(ServiceDefinitionUtils.INTERNAL_BEAN_NAME_GENERATOR_BEAN_NAME)
            ServiceContextBeanNameGenerator serviceContextBeanNameGenerator) {
        this.serviceContextBeanNameGenerator = serviceContextBeanNameGenerator;
        this.recordServiceBeanMap = serviceContextBeanNameGenerator.getRecordServiceBeanMap();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get serviceBean count by {@link #serviceContextBeanNameGenerator}.
     */
    @Override
    public int getServiceBeanCount() {
        return recordServiceBeanMap.size();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get serviceBean names by {@link #serviceContextBeanNameGenerator}.
     */
    @Override
    public String[] getServiceBeanNames() {
        return StringUtils.toStringArray(recordServiceBeanMap.keySet());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get serviceBean names for type by {@link #serviceContextBeanNameGenerator}.
     */
    @Override
    public String[] getServiceBeanNamesForType(Class<?> type) throws NoAvailableServiceException {
        if (!isRecordType(type)) {
            throw new NoAvailableServiceException(type);
        }
        List<String> names = new ArrayList<>();
        recordServiceBeanMap.forEach((n, c) -> {
            if (c == type) names.add(n);
        });
        return StringUtils.toStringArray(names);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Get type for serviceBean name by {@link #serviceContextBeanNameGenerator}.
     */
    @Override
    public Class<?> getTypeForServiceBeanName(String serviceName) throws NoAvailableServiceException {
        if (!ServiceDefinitionUtils.isEnhancementServiceName(serviceName)
                || !recordServiceBeanMap.containsKey(serviceName)) {
            throw new NoAvailableServiceException(serviceName);
        }
        return recordServiceBeanMap.get(serviceName);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Close the current Spring configurable context object.
     * <p>
     * If the context instance is an instance of {@code ConfigurableApplicationContext},
     * call its close method to close the context.
     */
    @Override
    public void close() {
        serviceContextBeanNameGenerator.close();
    }

    /**
     * Return a {@code Boolean} type indicating whether the input type is a record type.
     *
     * @param type the given type.
     * @return If {@code true} is returned, it represents the type of record, otherwise
     * it is not.
     */
    protected boolean isRecordType(Class<?> type) {
        return recordServiceBeanMap.containsValue(type);
    }

    /**
     * Return the context object of Spring.
     *
     * @return the context object of Spring.
     */
    protected ApplicationContext getContext() {
        return context;
    }

    /**
     * Return the Spring context converted object based on the specified type.
     *
     * @param clazz the specified type.
     * @param <T>   the specified generic.
     * @return the Spring context converted object based on the specified type.
     */
    protected <T> T unwrapApplicationContext(Class<T> clazz) {
        if (!clazz.isInstance(context)) {
            throw new ClassCastException(context.getClass().getName() + " cannot be cast " + clazz.getName());
        }
        return clazz.cast(context);
    }
}
