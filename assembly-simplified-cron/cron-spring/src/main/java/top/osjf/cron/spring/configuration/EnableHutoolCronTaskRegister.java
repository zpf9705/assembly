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

package top.osjf.cron.spring.configuration;

import org.springframework.context.annotation.Import;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.annotation.CronTaskRegisterPostProcessor;

import java.lang.annotation.*;

/**
 * Enable the scheduled task registration annotation based on hutool cron,
 * which will register the lifecycle beans of hutool cron and the registration
 * beans at runtime for scheduled task registration of methods carrying
 * {@link Cron} in {@link CronTaskRegisterPostProcessor}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({HutoolCronTaskConfiguration.class, CronTaskRegisterPostProcessor.class})
public @interface EnableHutoolCronTaskRegister {
}
