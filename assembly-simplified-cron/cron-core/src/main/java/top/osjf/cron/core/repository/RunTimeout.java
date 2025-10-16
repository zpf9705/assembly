/*
 * Copyright 2025-? the original author or authors.
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


package top.osjf.cron.core.repository;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.*;

/**
 * This annotation {@code RunTimeout} is used to specify the timeout time and timeout policy
 * of the annotated method during execution. The timeout threshold can be defined through the
 * timeout and timeUnit properties, with a default of 1 hour.
 *
 * <p> The behavior after timeout is controlled by the {@link RunningTimeoutPolicy} policy,
 * such as interrupting threads or ignoring results, which is suitable for preventing methods
 * from blocking for a long time.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 3.0.1
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(METHOD)
public @interface RunTimeout {
    /**
     * Specifies the maximum duration value allowed for method execution.
     * Default is 1, used in conjunction with {@link #timeUnit} to express the complete timeout (e.g., 1 hour).
     * For example: timeout = 5, timeUnit = {@code TimeUnit.SECONDS} means a 5-second timeout.
     * The value must be non-negative.
     * @return Timeout value (non negative).
     */
    long timeout() default 1;

    /**
     * Defines the unit of time for the timeout value, such as seconds, milliseconds, minutes, or hours.
     * The default unit is {@code TimeUnit.HOURS}.
     * <p>
     * Users can customize the time unit for more flexible timeout settings, e.g., {@code TimeUnit.SECONDS}
     * or {@code TimeUnit.MILLISECONDS}.
     *
     * @return The timeout unit is set to hours by default.
     */
    TimeUnit timeUnit() default TimeUnit.HOURS;

    /**
     * Defines the action to take when the method execution exceeds the timeout.
     * The default policy is {@code RunningTimeoutPolicy.INTERRUPT}, which interrupts the executing thread.
     * @return The timeout handling strategy defaults to interrupting the thread.
     */
    RunningTimeoutPolicy policy() default RunningTimeoutPolicy.INTERRUPT;

}
