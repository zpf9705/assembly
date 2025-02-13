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

/**
 * The {@code ListableServiceContext} interface, which extends the basic {@code ServiceContext}
 * interface, provides advanced capabilities for managing and retrieving service beans within a
 * {@code ServiceContext}.
 *
 * <p>Through this interface, users can easily obtain the total count of service beans, retrieve
 * lists of service bean names, search for service beans by type,and retrieve the type of  service
 * bean by its name.
 *
 * <p>It should be noted that the following listed related queries are established after
 * {@link ServiceContextBeanNameGenerator} has been recorded. If a query is performed before its
 * record or does not comply with its rules, an unexpected value will be returned.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public interface ListableServiceContext extends ServiceContext {

    /**
     * Retrieves the total count of service beans registered in the current service context.
     *
     * <p>This is a useful method for understanding how many service beans are contained
     * within the current service context, for further statistics or management purposes.
     *
     * @return Returns an integer representing the total count of service beans.
     */
    int getServiceBeanCount();

    /**
     * Retrieves and returns an array of names for all service beans in the current service
     * context.
     *
     * <p>These names can be used for further retrieval or management of specific service
     * beans.
     *
     * @return Returns a string array containing the names of all service beans.
     */
    String[] getServiceBeanNames();

    /**
     * Searches for and returns an array of service bean names matching the specified type.
     *
     * <p>This is a powerful method that allows users to find service beans based on their
     * type,enabling easy access and use of specific types of service beans.
     *
     * @param type the type of service bean to search for.
     * @return Returns a string array containing the names of matching service beans.
     * @throws NoAvailableServiceException Thrown if no matching service beans are found.
     */
    String[] getServiceBeanNamesForType(Class<?> type) throws NoAvailableServiceException;

    /**
     * Retrieves and returns the type of service bean based on its name.
     * <p>
     * This is a useful method that allows users to obtain type information for a service bean
     * by its name, enabling dynamic handling and use of these service beans at runtime.
     *
     * @param serviceName the name of the service bean to retrieve the type for.
     * @return Returns a {@code Class} object representing the type of the service bean.
     * @throws NoAvailableServiceException Thrown if the specified service bean is not found.
     */
    Class<?> getTypeForServiceBeanName(String serviceName) throws NoAvailableServiceException;
}
