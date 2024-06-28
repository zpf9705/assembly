/*
 * Copyright 2023-2024 the original author or authors.
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

package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * An exception thrown when the service is not queried.
 *
 * @author zpf
 * @since 2.0.4
 */
public class NoSuchServiceException extends NoSuchBeanDefinitionException {

    private static final long serialVersionUID = 922435911357767431L;

    private final String serviceName;

    private final Class<?> requiredType;

    public NoSuchServiceException(String serviceName, Class<?> requiredType) {
        super("Required type " + requiredType.getName() + " no service named " + serviceName +
                " was found from the service context");
        this.serviceName = serviceName;
        this.requiredType = requiredType;
    }

    /**
     * Return the service name of the parameter that did not find the service bean.
     *
     * @return the service name of the parameter that did not find the service bean.
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Return the conversion type of parameters for service beans that were not found.
     *
     * @return the conversion type of parameters for service beans that were not found.
     */
    public Class<?> getRequiredType() {
        return requiredType;
    }
}
