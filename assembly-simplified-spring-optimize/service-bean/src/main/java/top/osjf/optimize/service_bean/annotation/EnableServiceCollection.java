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

import org.springframework.context.annotation.Import;
import top.osjf.optimize.service_bean.context.ServiceContext;
import top.osjf.optimize.service_bean.context.ServiceContextBeanNameGenerator;
import top.osjf.optimize.service_bean.context.ServiceScopeBeanPostProcessor;

import java.lang.annotation.*;

/**
 * The {@code EnableServiceCollection} annotation is the key to enabling
 * service collection functionality.
 *
 * <p>Annotation {@code EnableServiceCollection} is intended as a switch
 * to activate the core configuration and functionality of the service
 * collection framework. It provides a set of service components in the
 * application by importing specific configuration classes and registers
 * a unified mechanism for collection, management, and scope
 * {@link ServiceContext#SUPPORT_SCOPE} control.
 *
 * <p>Overview of core functions:</p>
 * <ul>
 * <li><strong>import {@code ServiceContextConfiguration}</strong>
 * <p>this configuration class is responsible for initializing the service
 * context instance {@link ServiceContext}.</li>
 * <li><strong>import {@code ServiceScopeBeanPostProcessorRegistrar}</strong>
 * <p>Register a {@link ServiceScopeBeanPostProcessor} to optimize the beans
 * corresponding to the list of eligible bean names collected by
 * {@link ServiceContextBeanNameGenerator}.
 * </li>
 * </ul>
 *
 * <p>When developers need to introduce a set of support components with
 * different types of bean names that can be defined repeatedly in Spring
 * applications, they can quickly enable the framework by adding the
 * {@code EnableServiceCollection} annotation on the configuration class.
 *
 * <p>This will automatically import necessary configurations and components,
 * allowing developers to focus on the implementation of business logic
 * without having to pay too much attention to the underlying details of
 * service management.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({ServiceContextConfiguration.class,
        ServiceScopeBeanPostProcessorRegistrar.class})
public @interface EnableServiceCollection {
}
