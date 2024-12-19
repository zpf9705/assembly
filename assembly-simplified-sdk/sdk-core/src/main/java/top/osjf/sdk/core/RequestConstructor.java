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


package top.osjf.sdk.core;

import java.lang.annotation.*;

/**
 * The {@code RequestConstructor} annotation is used to mark constructor
 * parameters.
 *
 * <p>This annotation indicates whether the annotated parameter should
 * participate in object creation as a constructor parameter.
 * By setting the {@link #required()} property, you can control whether
 * this parameter must be used as a constructor parameter and the default
 * value is {@literal true}, which means it participates in the construction
 * of constructor parameters by default, otherwise it does not participate.
 *
 * <p>This annotation is mainly used in this framework to support the dynamic
 * creation of {@code Request} instances through reflection mechanisms, and
 * allow developers to clearly specify which parameters should be used as
 * constructor parameters.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestConstructor {

    /**
     * Return the option to participate as a constructor
     * parameter, default to {@literal true}, that is,
     * participate as a constructor parameter, otherwise
     * do not participate.
     *
     * @return the option to participate as a constructor
     * parameter.
     */
    boolean required() default true;
}
