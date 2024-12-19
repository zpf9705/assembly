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

import top.osjf.sdk.core.support.SdkSupport;

import java.lang.annotation.*;

/**
 * The {@code RequestType} annotation is used to mark methods
 * and specify the type of {@code Request} parameter.
 *
 * <p>This annotation defines the class type that request parameters
 * should follow through the {@link #value()} attribute, which must
 * be an instance of the {@code Request} class or its subclass.
 * It is mainly used in this framework to identify and process specific
 * types of {@code Request} parameters through reflection mechanisms.
 *
 * <p>Developers can use this annotation on the method of handling
 * HTTP requests to indicate the type of request parameters that
 * the framework accepts for that method.
 *
 * <p>The order priority is greater than the {@code Request} type
 * indicated by the specific interface {@link RequestTypeSupplier}
 * at the parameter level.
 *
 * <p>Here is a simple code example:
 * <pre>
 *     {@code
 *     interface ExampleInterface {
 *          RequestType(ExampleRequest.class)
 *          ExampleResponse test(@RequestSetter String email);
 *     }
 *     }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see SdkSupport#createRequest
 * @see RequestTypeSupplier
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestType {

    /**
     * Return the type of the request parameter.
     *
     * @return type of the request parameter.
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Request> value();
}
