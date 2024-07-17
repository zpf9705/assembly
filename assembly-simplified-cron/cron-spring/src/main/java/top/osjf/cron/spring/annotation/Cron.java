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

import java.lang.annotation.*;

/**
 * Flag annotation for timed task method registration, only supports
 * starting with cron expression, supports spring specified environment
 * {@link org.springframework.core.env.Environment} registration,
 * and provides fixed initialization parameters.
 *
 * <p>Relying on {@link CronTaskRegisterPostProcessor} to scan, obtain,
 * and register corresponding annotation methods，filter the non-static,
 * publicly county-wide, and annotated methods defined in the current
 * class as a timed runtime.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cron {

    /**
     * Provide a default cron expression. When the value of {@link #value()}
     * or {@link #expression()} is not filled in, the default execution cycle
     * for the current task is once every 1 second.
     */
    String DEFAULT_CRON_EXPRESSION = "0/1 * * * * ?";

    /**
     * After annotation mapping the map structure, map the key value of
     * the annotation attribute {@link #expression()}.
     */
    String SELECT_OF_EXPRESSION_NAME = "expression";


    /**
     * After annotation mapping the map structure, map the key value of
     * the annotation attribute {@link #profiles()}.
     */
    String SELECT_OF_PROFILES_NAME = "profiles";

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
     * Since the implementation of this annotation needs to be in the
     * springboot environment, the purpose of this parameter is to restrict
     * the environment for registering scheduled tasks.
     * <p>Referring to the configuration parameters of
     * <pre>{@code Spring.profiles.active}</pre>, this parameter can be
     * registered to the scheduled thread pool after being configured as above.
     *
     * @return The array of registered environment parameters needs to be restricted.
     * If it is empty, it will be directly registered to the task thread pool.
     */
    String[] profiles() default {};
}
