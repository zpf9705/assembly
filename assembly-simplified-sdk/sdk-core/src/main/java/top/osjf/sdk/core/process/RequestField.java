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

package top.osjf.sdk.core.process;

import java.lang.annotation.*;

/**
 * When assigning values to request class fields, if the compiled
 * parameters no longer have the original parameter names, use this
 * annotation to mark the true information of the current request
 * class field.
 *
 * <p>For specific information on related properties, you can query
 * the relevant properties of this annotation.
 *
 * <p>For specific usage, please refer to {@link RequestParam}.
 *
 * <p>Regarding the proxy analysis of SDK to implement the parsing of
 * this annotation, please refer to {@code top.osjf.assembly.simplified.sdk.
 * SdkUtils#invokeCreateRequestUseSet(Class, Object...)}
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestField {

    /**
     * The real name of the marked field.
     *
     * <p>When using reflection assignment, this
     * property is not mandatory.
     *
     * @return real name of the marked field.
     */
    String value() default "";

    /**
     * The true ranking of this parameter in the proxy method.
     *
     * <p>If not specified, the selection will be made in the
     * default order.
     *
     * @return ranking of this parameter in the proxy method.
     */
    int order() default -1;

    /**
     * Whether to directly use reflection to assign values to fields.
     * <p>If {@code true}, then the real name of the field {@link #value()}
     * is no longer required.
     *
     * @return result of use reflection to assign values to fields.
     */
    boolean useReflect() default false;
}
