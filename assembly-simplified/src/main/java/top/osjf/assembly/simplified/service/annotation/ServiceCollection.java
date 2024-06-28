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

package top.osjf.assembly.simplified.service.annotation;

import java.lang.annotation.*;

/**
 * The annotation that needs to be annotated for service interfaces/abstract classes
 * is mainly used to tell you that once the class is annotated, its implementation
 * class or subclass can be automatically collected, provided that it has been added
 * to the Spring container and can be collected.
 *
 * <p>For specific collection methods, please refer to the implementation class
 * {@link top.osjf.assembly.simplified.service.context.ClassesServiceContext}.</p>
 *
 * @author zpf
 * @since 2.0.4
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceCollection {

    /**
     * @return Service name prefix,the default is {@link Class#getName()}.
     * @since 2.0.6
     */
    String prefix() default "";
}
