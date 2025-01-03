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
import top.osjf.cron.cron4j.repository.Cron4jCronTaskRepository;
import top.osjf.cron.spring.CronAnnotationPostProcessor;
import top.osjf.cron.spring.CronTaskConfiguration;
import top.osjf.cron.spring.annotation.Cron;

import java.lang.annotation.*;

/**
 * Enable Cron4j scheduled task registration annotation.
 *
 * <p>This annotation aims to enable Cron4j scheduled tasks in the Spring framework,
 * allowing developers to define and manage scheduled tasks through simple annotations.
 * By importing Cron4j related configuration classes and registering corresponding beans
 * at runtime, timed task registration and scheduling of methods with Cron expression
 * annotations can be achieved.
 *
 * <p>When this annotation is marked on the Spring configuration class or startup class,
 * the Spring container will automatically configure the environment required for Cron4j
 * scheduled tasks, including loading the core configuration of Cron4j and registering
 * the scheduled task processor.
 *
 * <p>This annotation will import two configurations, {@link Cron4jCronTaskConfiguration}
 * and {@link CronTaskConfiguration}, respectively configuring the core repository of
 * {@link Cron4jCronTaskRepository} and the post processor of {@link CronAnnotationPostProcessor}
 * that scans the relevant beans wearing the annotation {@link Cron} and registers the task.
 *
 * <p>The registered attribute entries are all {@link it.sauronsoftware.cron4j.Scheduler}
 * core open attributes of the configuration scheduler.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 * @see Cron4jCronTaskRepository
 * @see Cron
 * @see CronAnnotationPostProcessor
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({Cron4jCronTaskConfiguration.class,
        CronTaskConfiguration.class})
public @interface EnableCron4jCronTaskRegister {

    /**
     * Whether to start as a daemon thread.
     * <p>If true, the scheduled task executed immediately after calling the
     * {@link top.osjf.cron.core.repository.CronTaskRepository#stop()} method will end.
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
    String timeZone() default "";
}
