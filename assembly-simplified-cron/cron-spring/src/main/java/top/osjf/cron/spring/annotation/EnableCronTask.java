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


package top.osjf.cron.spring.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.cron.core.repository.CronTaskRepository;

import java.lang.annotation.*;

/**
 * Enables support for scanning {@link Cron} and {@link Crones} annotations
 * on Spring components to register custom cron task definitions.
 *
 * <p>To be used together with {@link CronTaskRepository} for task lifecycle management.
 * When present on any {@code @Configuration} class, the framework automatically registers
 * a {@link CronAnnotationPostProcessor} to detect annotated methods and load cron tasks
 * after the application context has been fully refreshed.
 *
 * <p>This annotation imports {@link CronTaskConfiguration} to register the internal
 * annotation post processor bean infrastructure.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.2
 * @see Cron
 * @see Crones
 * @see CronTaskConfiguration
 * @see CronAnnotationPostProcessor
 * @see CronTaskRepository
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(CronTaskConfiguration.class)
@Documented
public @interface EnableCronTask {
}
