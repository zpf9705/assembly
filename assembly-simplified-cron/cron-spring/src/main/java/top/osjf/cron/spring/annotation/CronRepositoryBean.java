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

import org.springframework.context.annotation.Bean;
import top.osjf.cron.core.lifecycle.InitializeAble;
import top.osjf.cron.core.lifecycle.Lifecycle;
import top.osjf.cron.core.repository.CronTaskRepository;

import java.lang.annotation.*;

/**
 * {@code CronRepositoryBean} is a combination tag annotation with {@link Bean},
 * indicating the use of Spring's container features to implement {@link Lifecycle}
 * lifecycle management for {@link CronTaskRepository} after creating a
 * {@link CronTaskRepository} instance in the container, including specifying
 * initialization and destruction methods.
 * <p>
 * There is a comparative relationship as follows:
 * <ul>
 *     <li>{@link Bean#initMethod()} == 'initialize' refer to
 *     {@link InitializeAble#initialize()}</li>
 *     <li>{@link Bean#destroyMethod()} == 'stop' refer to
 *     {@link Lifecycle#stop()}</li>
 * </ul>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Bean(initMethod = "initialize", destroyMethod = "stop")
public @interface CronRepositoryBean {
}
