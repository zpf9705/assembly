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


package top.osjf.optimize.service_bean.annotation;

/**
 * Configuration constants for internal.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.3
 */
public abstract class ServiceBeanManagementConfigUtils {

    /**
     * The bean name for internal registration {@code ServiceTypeRegistry}.
     */
    public static final String INTERNAL_SERVICE_TYPE_REGISTER_BEAN_NAME =
            "top.osjf.optimize.service_bean.context.internalServiceTypeRegistry";

    /**
     * The name of the internal bean {@code ServiceScope}.
     */
    public static final String INTERNAL_SERVICE_SCOPE_BEAN_NAME
            = "top.osjf.optimize.service_bean.context.internalServiceScope";

    /**
     * The {@code ServiceScope} class field name in {@code ServiceScopeBeanPostProcessor} class.
     */
    public static String SERVICE_SCOPE_FIELD_NAME = "serviceScope";

    /**
     * The {@code ServiceTypeRegistry} class field name in {@code ServiceScopeBeanPostProcessor} class.
     */
    public static String SERVICE_TYPE_REGISTER_FIELD_NAME = "serviceTypeRegistry";
}
