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

import org.springframework.core.annotation.AliasFor;
import top.osjf.cron.spring.CronAnnotationPostProcessor;
import top.osjf.cron.spring.cron4j.EnableCron4jCronTaskRegister;
import top.osjf.cron.spring.hutool.EnableHutoolCronTaskRegister;
import top.osjf.cron.spring.quartz.EnableQuartzCronTaskRegister;

import java.lang.annotation.*;

/**
 * <p>This annotation is used to define the execution plan of scheduled tasks
 * and control the execution cycle of tasks through specified cron expressions.
 * It can be applied to methods or annotation class to mark tasks or configurations
 * that' need to be executed at scheduled intervals according to cron expressions.
 *
 * <p>Cron expression is a powerful way of describing timing rules, which extends
 * traditional UNIX cron expressions by adding second level trigger support (except
 * for cron4j), allowing for finer time control.
 *
 * <p>This annotation is processed in {@link CronAnnotationPostProcessor} and can be
 * triggered for parsing based on the tags of the following annotations:
 * <ul>
 * <li>{@link EnableHutoolCronTaskRegister @EnableHutoolCronTaskRegister}</li>
 * <li>{@link EnableQuartzCronTaskRegister @EnableQuartzCronTaskRegister}</li>
 * <li>{@link EnableCron4jCronTaskRegister @EnableCron4jCronTaskRegister}</li>
 * </ul>
 *
 * <p>The attributes of {@link #value()} and {@link #expression()} are considered
 * equivalent to {@link AliasFor}, but in reality, it declares that these two attributes
 * are semantically identical, meaning they should have the same value. If the values
 * of these two attributes are different, it usually means there is inconsistency in
 * the configuration, which may lead to runtime errors or unpredictable behavior.
 * <p>
 * <h2>Example usage</h2>
 * <pre>
 * &#64;Cron(expression = "0 0/5 * * * ?", profiles = {"dev"})
 * public void myCronTask() {
 *     // Implementation logic of timed tasks
 *     System.out.println("Hello, Cron framework!");
 * }
 * </pre>
 * <p>In the above example {@code myCronTask} method is marked as a scheduled task
 * with a cron expression of {@code 0 0/5 * * * ?}, which means it is executed every
 * 5 minutes. Meanwhile, this task is only executed when the 'dev' configuration file
 * is activated.
 *
 * <p>In summary, {@code Cron} annotations provide a flexible and powerful way to define
 * timed tasks that can be executed according to specified cron expressions and environment
 * configurations.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(Crones.class)
public @interface Cron {

    /**
     * Provide a default cron expression. When the value of {@link #value()}
     * or {@link #expression()} is not filled in, the default execution cycle
     * for the current task is once every 1 second.
     */
    String DEFAULT_CRON_EXPRESSION = "0/1 * * * * ?";

    /**
     * Alias for {@link #expression}.
     * <p>Intended to be used when no other attributes are needed, for example:
     * {@code @Cron("0/1 * * * * ?")}.
     *
     * @return an expression that can be parsed to a cron schedule.
     * @see #expression
     */
    @AliasFor("expression") String value() default "";

    /**
     * Provide a cron expression that conforms to the rules.
     * <p>This parameter will determine the execution interval of the
     * registered scheduled task.
     * <p>Please understand the writing conventions of cron expressions
     * before use to avoid unnecessary errors.
     *
     * <p> A cron-like expression, extending the usual UN*X definition to include triggers
     * on the second, minute, hour, day of month, month, and day of week.
     * <p>For example, {@code "0 * * * * MON-FRI"} means once per minute on weekdays
     * (at the top of the minute - the 0th second).
     * <p>The fields read from left to right are interpreted as follows.
     * <ul>
     * <li>second</li>
     * <li>minute</li>
     * <li>hour</li>
     * <li>day of month</li>
     * <li>month</li>
     * <li>day of week</li>
     * </ul>
     *
     * @return an expression that can be parsed to a cron schedule.
     */
    @AliasFor("value") String expression() default "";

    /**
     * Specify the environment configuration for activating tasks in Spring.
     *
     * <p>This is an annotation attribute used to define under which Spring
     * profiles the currently annotated task or configuration should be activated
     * and executed. By listing different profile names in an array, the task
     * can be precisely controlled in which environments it should run.
     *
     * <p>Provides a method for configuring applications for different environments,
     * such as development, testing, and production.By using this property, developers
     * can ensure that tasks are executed only in specific environment configurations,
     * thereby avoiding potentially harmful or unsuitable code running in inappropriate
     * environments.
     *
     * <p>For example, a task may only run in a development environment for debugging
     * and testing purposes, but not in a production environment. By setting the {@code profiles}
     * property of this task to {@code dev}, it can be ensured that it only executes when
     * the development configuration file is activated.
     * <pre>
     *     {@code
     *     class CronExample {
     *      Cron(profiles={"dev"})
     *      public void test() {
     *          System.out.println("Hello , Cron framework !")
     *      }
     *     }
     *    }
     * </pre>
     *
     * <p>If no execution environment is specified (i.e. default to an empty array), the
     * task will not be restricted by the configuration file and can be executed under
     * any activated environment configuration.
     *
     * @return A string array containing the name of a configuration file, used
     * to specify the environment configuration for task activation.
     */
    String[] profiles() default {};
}
