/*
 * Copyright 2026-? the original author or authors.
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


package top.osjf.filewatch.spring.config.refresh;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a Bean as supporting {@link org.springframework.beans.factory.annotation.Value @Value}
 * configuration dynamic automatic refresh capability.
 *
 * <p>Beans annotated with {@code @Refreshable} will automatically sense configuration file changes,
 * and the framework will automatically re-inject the latest configuration values into fields
 * marked with {@link org.springframework.beans.factory.annotation.Value @Value}.
 *
 * <p>It only takes effect on Spring Bean classes, and is usually used with configuration classes
 * such as XXXProperties to realize hot update of parameters without restart.
 *
 * <h2>Usage Example</h2>
 * <pre class="code">
 * &#064;Refreshable
 * &#064;Component
 * public class AppConfig {
 *
 *     // Automatically refresh after configuration changes
 *     &#064;Value("${app.name}")
 *     private String appName;
 *
 *     &#064;Value("${app.version}")
 *     private String appVersion;
 * }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Refreshable {

}
