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
 * If no suitable service instance is found for the known service
 * name and type, this exception will be thrown as a reminder.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public class NoAvailableServiceException extends IllegalStateException {

    private static final long serialVersionUID = 922435911357767431L;

    public NoAvailableServiceException(String serviceName) {
        super("No available service named " + serviceName + ".");
    }

    public NoAvailableServiceException(String serviceName, Class<?> requiredType) {
        super("No available " + requiredType.getName() + " type for service name " + serviceName + ".");
    }
}
