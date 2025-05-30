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


package top.osjf.spring.autoconfigure;

import org.springframework.context.annotation.Conditional;
import org.springframework.core.env.Environment;

import java.lang.annotation.*;

/**
 * Conditional annotation that determines if a configuration should be applied based on the presence of a property
 * and the active profile in the Spring environment.
 * <p>
 * This annotation allows you to conditionally enable or disable a configuration class or method based on
 * whether a specified property is set and if the application is running with a specific profile.
 * <p>
 * Example usage:
 * <pre class="code">
 * &#064;Configuration
 * public class MyAutoConfiguration {
 *
 *     &#064;ConditionalOnPropertyProfiles(propertyName = 'example.property')
 *     &#064;Bean
 *     public MyService myService() {
 *         // Bean implementation
 *         return new MyService();
 *     }
 *
 * }</pre>
 * In this example, the {@code MyService} bean will only be created if the {@code example.property}
 * property is set and the application is running with the required profile.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnPropertyProfilesConditional.class)
public @interface ConditionalOnPropertyProfiles {

    /**
     * The default property name used to determine the active profiles.
     */
    String DEFAULT_PROPERTY_NAME = "spring.condition.profiles";

    /**
     * Specify the name of the property to be checked, which should be configured in the
     * file with a value of the specified activation environment, when matched with
     * {@link Environment#getActiveProfiles()}.
     *
     * @return the name of the property to check
     */
    String propertyName() default DEFAULT_PROPERTY_NAME;
}
