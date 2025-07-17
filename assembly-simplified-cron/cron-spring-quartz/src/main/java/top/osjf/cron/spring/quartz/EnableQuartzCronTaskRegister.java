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

package top.osjf.cron.spring.quartz;

import org.intellij.lang.annotations.Language;
import org.springframework.context.annotation.Import;
import top.osjf.cron.core.lifecycle.SuperiorProperties;
import top.osjf.cron.quartz.repository.QuartzCronTaskRepository;
import top.osjf.cron.spring.CronAnnotationPostProcessor;
import top.osjf.cron.spring.CronTaskConfiguration;
import top.osjf.cron.spring.annotation.Cron;

import java.lang.annotation.*;

/**
 * Enable Quartz scheduled task registration annotation.
 *
 * <p>This annotation aims to enable Quartz scheduled tasks in the Spring framework,
 * allowing developers to define and manage scheduled tasks through simple annotations.
 * By importing Quartz related configuration classes and registering corresponding beans
 * at runtime, timed task registration and scheduling of methods with Cron expression
 * annotations can be achieved.
 *
 * <p>When this annotation is marked on the Spring configuration class or startup class,
 * the Spring container will automatically configure the environment required for Quartz
 * scheduled tasks, including loading the core configuration of Quartz and registering
 * the scheduled task processor.
 *
 * <p>This annotation will import two configurations, {@link QuartzCronTaskConfiguration}
 * and {@link CronTaskConfiguration}, respectively configuring the core repository of
 * {@link QuartzCronTaskRepository} and the post processor of {@link CronAnnotationPostProcessor}
 * that scans the relevant beans wearing the annotation {@link Cron} and registers the task.
 *
 * <p>There are too many core attribute configurations for quartz, which can be encapsulated
 * in {@link SuperiorProperties} and passed through using method
 * {@link QuartzCronTaskRepository#setSuperiorProperties}for construction.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.0
 * @see QuartzCronTaskRepository
 * @see Cron
 * @see CronAnnotationPostProcessor
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({QuartzCronTaskConfiguration.class,
        CronTaskConfiguration.class})
public @interface EnableQuartzCronTaskRegister {

    /**
     *
     * @return
     */
    @Language("SpEL") String propertiesReferTo() default "";
}
