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


package top.osjf.cron.spring.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.cron.spring.datasource.driven.scheduled.DataSource;

import java.lang.annotation.*;

/**
 * Enabler annotation for datasource-driven scheduled task management in Spring applications.
 *
 * <p>This annotation serves as the activation switch for the datasource-driven scheduled
 * task system. When applied to a Spring configuration class, it imports the necessary
 * infrastructure beans through {@link DatabaseDrivenScheduledConfiguration} to enable:
 *
 * <ul>
 *   <li>MyBatis-Plus integrated task persistence</li>
 *   <li>Spring-managed task lifecycle</li>
 *   <li>Dynamic SpEL-based task execution</li>
 *   <li>Environment-aware task activation</li>
 * </ul>
 *
 * <h2>Example:</h2>
 * <pre>{@code
 * SpringBootApplication
 * EnableDataSourceDrivenScheduled(DataSource.MY_BATIS_PLUS_ORM_DATABASE)
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * }</pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DatabaseDrivenScheduledConfiguration.class)
public @interface EnableDataSourceDrivenScheduled {

    DataSource value();
}
