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


package top.osjf.sdk.spring.annotation;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.lang.NonNull;
import top.osjf.sdk.core.util.StringUtils;

import java.lang.annotation.*;
import java.util.Map;

/**
 * A {@link Conditional conditional} extend annotation used on Spring configuration
 * classes or methods to determine whether to load the configuration or method based
 * on the existence of specified properties.
 * <p>
 * If the property is not contained in the {@link Environment} at all, the
 * {@link #matchIfMissing()} attribute is consulted. By default missing attributes do not
 * match.
 * <p>
 * This condition cannot be reliably used for matching collection properties. For example,
 * in the following configuration, the condition matches if {@code spring.example.values}
 * is present in the {@link Environment} but does not match if
 * {@code spring.example.values[0]} is present.
 *
 * <pre class="code">
 * &#064;ConditionalOnPropertyExistence(prefix = "spring", name = "example.values")
 * class ExampleAutoConfiguration {
 * }
 * </pre>
 * <p>
 * It is better to use a custom condition for such cases.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(ConditionalOnPropertyExistence.OnPropertyExistenceCondition.class)
public @interface ConditionalOnPropertyExistence {

    /**
     * Alias for {@link #name()}.
     *
     * @return the names
     */
    @AliasFor("name")
    String[] value() default {};

    /**
     * A prefix that should be applied to each property. The prefix automatically ends
     * with a dot if not specified. A valid prefix is defined by one or more words
     * separated with dots (e.g. {@code "acme.system.feature"}).
     *
     * @return the prefix
     */
    String prefix() default "";

    /**
     * The name of the properties to test. If a prefix has been defined, it is applied to
     * compute the full key of each property. For instance if the prefix is
     * {@code app.config} and one value is {@code my-value}, the full key would be
     * {@code app.config.my-value}
     * <p>
     * Use the dashed notation to specify each property, that is all lower case with a "-"
     * to separate words (e.g. {@code my-long-property}).
     *
     * @return the names
     */
    @AliasFor("value")
    String[] name() default {};

    /**
     * Specify if the condition should match if the property is not set. Defaults to
     * {@code false}.
     *
     * @return if should match if the property is missing
     */
    boolean matchIfMissing() default false;

    /**
     * A class implementing the {@link Condition} interface, used to determine
     * if the properties specified by the {@code ConditionalOnPropertyExistence}
     * annotation exist.
     */
    class OnPropertyExistenceCondition implements Condition {
        @Override
        public boolean matches(@NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
            Map<String, Object> attrs = metadata.
                    getAnnotationAttributes(ConditionalOnPropertyExistence.class.getName());
            if (attrs != null) {
                String prefix = (String) attrs.get("prefix");
                for (String property : (String[]) attrs.get("name")) {
                    String property0 = StringUtils.isNotBlank(prefix) ? prefix + "." + property : property;
                    if (!context.getEnvironment().containsProperty(property0)) {
                        return (boolean) attrs.get("matchIfMissing");
                    }
                }
            }
            return true;
        }
    }
}
