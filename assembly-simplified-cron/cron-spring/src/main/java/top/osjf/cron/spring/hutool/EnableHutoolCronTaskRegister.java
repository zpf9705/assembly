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
import top.osjf.cron.hutool.repository.HutoolCronTaskRepository;
import top.osjf.cron.spring.CronAnnotationPostProcessor;
import top.osjf.cron.spring.CronTaskConfiguration;
import top.osjf.cron.spring.annotation.Cron;

import java.lang.annotation.*;

/**
 * Enable Hutool scheduled task registration annotation.
 *
 * <p>This annotation aims to enable Hutool scheduled tasks in the Spring framework,
 * allowing developers to define and manage scheduled tasks through simple annotations.
 * By importing Hutool related configuration classes and registering corresponding beans
 * at runtime, timed task registration and scheduling of methods with Cron expression
 * annotations can be achieved.
 *
 * <p>When this annotation is marked on the Spring configuration class or startup class,
 * the Spring container will automatically configure the environment required for Hutool
 * scheduled tasks, including loading the core configuration of Hutool and registering
 * the scheduled task processor.
 *
 * <p>This annotation will import two configurations, {@link HutoolCronTaskConfiguration}
 * and {@link CronTaskConfiguration}, respectively configuring the core repository of
 * {@link HutoolCronTaskRepository} and the post processor of {@link CronAnnotationPostProcessor}
 * that scans the relevant beans wearing the annotation {@link Cron} and registers the task.
 *
 * <p>The registered attribute entries are all {@link cn.hutool.cron.Scheduler} core open
 * attributes of the configuration scheduler.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see HutoolCronTaskRepository
 * @see Cron
 * @see CronAnnotationPostProcessor
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

    /**
     * A time zone for which the cron expression will be resolved. By default, this
     * attribute is the empty String (i.e. the server's local time zone will be used).
     *
     * @return a zone id accepted by {@link java.util.TimeZone#getTimeZone(String)},
     * or an empty String to indicate the server's default time zone.
     * @since 1.0.3
     */
    String timeZone() default "";

    /**
     * Indicate whether to clear all tasks when stopping the scheduler.
     *
     * <p>This attribute is a Boolean value used to control whether to clear
     * related tasks when performing a specific operation. If set to {@code true},
     * the task will be cleared when the operation is executed; If set to {@code false},
     * the task will not be cleared. By default, this attribute is set to {@code true}.
     *
     * <p>It should be noted that the setting of its value will become invalid when
     * {@link #isDaemon()} is set to true, because when setting the daemon thread,
     * the settings of its related tasks will be automatically cleared after the
     * main thread is closed.
     *
     * @return The boolean flag indicate whether to clear all tasks when stopping the scheduler.
     * @since 1.0.3
     */
    boolean isIfStopClearTasks() default true;
}
