/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.optimize.aware;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Enables BeanAware support in Spring applications.
 *
 * <p>This annotation imports {@link BeanAwareConfiguration} which registers
 * {@link BeanAwareSupportBeanPostProcessor} to handle injection of beans
 * implementing {@link BeanAware} interface.
 *
 * <p><b>Usage Example:</b>
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableBeanAware
 * public class AppConfig {
 *     // Configuration classes...
 * }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 * @see BeanAware
 * @see BeanAwareConfiguration
 * @see BeanAwareSupportBeanPostProcessor
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(BeanAwareConfiguration.class)
@Documented
public @interface EnableBeanAware {
}
