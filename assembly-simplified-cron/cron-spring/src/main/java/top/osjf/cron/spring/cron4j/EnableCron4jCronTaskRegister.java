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

package top.osjf.cron.spring.cron4j;

import org.springframework.context.annotation.Import;
import top.osjf.cron.cron4j.lifestyle.Cron4jCronLifeStyle;
import top.osjf.cron.spring.CronTaskRegisterPostProcessor;
import top.osjf.cron.spring.annotation.Cron;

import java.lang.annotation.*;

/**
 * Enable the scheduled task registration annotation based on Cron4j cron,
 * which will register the lifecycle beans of Cron4j cron and the registration
 * beans at runtime for scheduled task registration of methods carrying
 * {@link Cron} in {@link CronTaskRegisterPostProcessor}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({Cron4jCronTaskConfiguration.class,
        CronTaskRegisterPostProcessor.class})
public @interface EnableCron4jCronTaskRegister {

    /**
     * Whether to start as a daemon thread.
     * <p>If true, the scheduled task executed immediately after calling the
     * {@link Cron4jCronLifeStyle#stop()} method will end.
     * Otherwise, it will wait for the execution to complete before ending.
     *
     * @return Whether to start as a daemon thread.
     */
    boolean isDaemon() default false;

    /**
     * A time zone for which the cron expression will be resolved. By default, this
     * attribute is the empty String (i.e. the server's local time zone will be used).
     *
     * @return a zone id accepted by {@link java.util.TimeZone#getTimeZone(String)},
     * or an empty String to indicate the server's default time zone
     */
    String zone() default "";
}
