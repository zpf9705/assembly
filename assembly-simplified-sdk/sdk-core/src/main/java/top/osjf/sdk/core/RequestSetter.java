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
 * The {@code RequestSetter} annotation is used to mark request
 * parameters and indicate how to set certain properties of the
 * {@code Request} object.
 *
 * <p>This annotation is marked on the method parameter. If no
 * {@link #name()} method value is provided, this parameter name
 * will be used. If it is not possible or unchanged to use the
 * set method for assignment, reflection can be used for assignment
 * when <pre>{@code useReflect == true }</pre>.
 *
 * <p>Here is a simple code example:
 * <p><h3>use set method</h3></p>
 * <pre>
 *     {@code
 *     class ExampleRequest implements Request<ExampleResponse>{
 *         private String email;
 *         public void setEmail(String email){
 *             this.email = email;
 *         }
 *     }
 *
 *     interface ExampleInterface {
 *
 *          RequestType(ExampleRequest.class)
 *          ExampleResponse test(@RequestSetter String email);
 *     }
 *     }
 * </pre>
 * <p><h3>use reflect</h3></p>
 * <pre>
 *     {@code
 *     class ExampleRequest implements Request<ExampleResponse>{
 *         private String email;
 *     }
 *
 *     interface ExampleInterface {
 *
 *          RequestType(ExampleRequest.class)
 *          ExampleResponse test(@RequestSetter(useReflect=true) String email);
 *     }
 *     }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see RequestConstructor
 * @see top.osjf.sdk.core.support.SdkSupport#createRequest
 * @since 1.0.2
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestSetter {

    /**
     * Returns the field name that needs to be assigned using the set
     * method during the construction of the {@code Request} object,
     * defaulting to the name of the method tag parameter.
     *
     * @return The field name, defaulting to the name of the method tag
     * parameter.
     */
    String name() default "";

    /**
     * Returns whether to use reflection for attribute field assignment.
     *
     * <p>If {@literal true} is returned, the set method for that field
     * can be omitted and applied according to specific scenarios. For
     * example, this option can be enabled when the setter method is
     * inaccessible directly or when attributes need to be set dynamically
     * through reflection.
     *
     * @return Whether to use reflection for attribute field assignment.
     */
    boolean useReflect() default false;
}
