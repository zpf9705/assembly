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

package top.osjf.sdk.core.support;

import java.lang.annotation.*;

/**
 * This annotation is used to mark the classes that need to be loaded in a specific order.
 *
 * <p>It is typically used to control the loading order of service providers or any components
 * that need to be initialized in a specific order at program startup.
 *
 * <p>By defining an integer type {@code value} attribute, the loading priority of each class
 * can be explicitly specified, where a smaller value indicates a higher loading priority.
 *
 * <p>If {@code value} is not explicitly specified, 'Integer' is used by default MAX_VALUE`，
 * This indicates that the loading priority of this class is the lowest.
 *
 * <p> <b>Purpose:</b>
 * When loading service providers using {@link java.util.ServiceLoader}, the loading order
 * can be determined based on the value of this annotation by combining reflection and custom
 * class loading logic.
 *
 * <p>In custom class loaders or initialization frameworks, such annotations can be read to
 * control the loading and initialization order of classes.
 *
 * <p>In complex systems that require managing the lifecycle order of multiple components,
 * {@link LoadOrder} provides a declarative way to specify the loading order.
 *
 * <p><b>Attention:</b>
 * When using this annotation, please ensure that your application or framework can recognize
 * and process it so that it can correctly apply its specified loading order when loading classes.
 *
 * <p> If there is no custom class loading logic or initialization framework to support this annotation,
 * it will have no effect.
 *
 * <p>In complex systems, in addition to the loading order of classes, other factors such as dependencies
 * and concurrent loading need to be considered to ensure system stability and performance.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LoadOrder {

    /**
     * The order value.
     * <p>Default is {@link Integer#MAX_VALUE}.
     */
    int value() default Integer.MAX_VALUE;
}
