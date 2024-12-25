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

import top.osjf.optimize.service_bean.context.ServiceContext;

import java.lang.annotation.*;

/**
 * {@code ServiceCollection} tag annotation is used to mark the parent
 * class or interface of a service class, and the tagged class  will
 * participate in the renaming of the service class in the Spring framework.
 *
 * <p>The unique attribute method {@link #value()} is used for distinguishing
 * when the bean name is not unique. If this value is empty, the fully qualified
 * name of the current tag class is used as the unique identifier.
 *
 * <p>The tagged class can be converted between the defined name of the service
 * class and the type of the current tagged person in the context object
 * {@link ServiceContext}. Without a tagged class, it can be an interface or
 * parent class, but it does not participate in renaming and cannot be uniquely
 * converted in the context object {@link ServiceContext}.
 *
 * <p>If the service class you want to add is not known to Spring, you can use
 * method {@link ServiceContext#addService} to manually add it, provided that
 * the parent class and interface have this annotation tag.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 * @see top.osjf.optimize.service_bean.context.ServiceCore#getTargetServiceTypes
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceCollection {
    /**
     * The unique distinguishing string of the name, which defaults
     * to the fully qualified name of the class.
     * @return the unique distinguishing string of the name.
     */
    String value() default "";
}
