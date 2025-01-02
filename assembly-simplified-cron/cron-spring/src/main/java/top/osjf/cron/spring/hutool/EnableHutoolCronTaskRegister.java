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

package top.osjf.cron.spring.hutool;

import org.springframework.context.annotation.Import;
import top.osjf.cron.spring.CronTaskConfiguration;
import top.osjf.cron.spring.annotation.Cron;
import top.osjf.cron.spring.CronAnnotationPostProcessor;

import java.lang.annotation.*;

/**
 * Enable the scheduled task registration annotation based on hutool cron,
 * which will register the lifecycle beans of hutool cron and the registration
 * beans at runtime for scheduled task registration of methods carrying
 * {@link Cron} in {@link CronAnnotationPostProcessor}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({HutoolCronTaskConfiguration.class,
        CronTaskConfiguration.class})
public @interface EnableHutoolCronTaskRegister {

    /**
     * Set whether to support second matching.
     * <p>This method is used to define whether to use the second matching mode.
     * If it is true, the first digit in the timed task expression is seconds,
     * otherwise it is minutes, and the default is minutes.
     *
     * @return Whether to support second matching.
     */
    boolean isMatchSecond() default true;

    /**
     * Whether to start as a daemon thread.
     * <p>If true, the scheduled task executed immediately after calling the
     * {@link top.osjf.cron.core.repository.CronTaskRepository#stop()} method will end.
     * Otherwise, it will wait for the execution to complete before ending.
     *
     * @return Whether to start as a daemon thread.
     */
    boolean isDaemon() default false;
}
