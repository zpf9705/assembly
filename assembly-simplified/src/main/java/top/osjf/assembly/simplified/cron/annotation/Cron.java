package top.osjf.assembly.simplified.cron.annotation;

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
 * @author zpf
 * @see EnableCronTaskRegister2
 * @see CronTaskRegisterPostProcessor
 * @see top.osjf.assembly.simplified.cron.CronRegister#register(Object, String[])
 * @see CronAnnotationAttributes
 * @since 1.1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cron {

    /**
     * Provide a default cron expression. When the value of {@link #value()}
     * or {@link #expression()} is not filled in, the default execution cycle
     * for the current task is once every 1 second.
     * @since 2.2.5
     */
    String DEFAULT_CRON_EXPRESSION = "0/1 * * * * ?";

    /**
     * After annotation mapping the map structure, map the key value of
     * the annotation attribute {@link #expression()}.
     * @since 2.2.5
     */
    String SELECT_OF_EXPRESSION_NAME = "expression";


    /**
     * After annotation mapping the map structure, map the key value of
     * the annotation attribute {@link #profiles()}.
     * @since 2.2.5
     */
    String SELECT_OF_PROFILES_NAME = "profiles";

    /**
     * Alias for {@link #expression}.
     * <p>Intended to be used when no other attributes are needed, for example:
     * {@code @Cron("0/1 * * * * ?")}.
     * @return an expression that can be parsed to a cron schedule.
     * @see #expression
     * @since 2.2.5
     */
    @AliasFor("expression")
    String value() default "";

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
     * <p>For clearer meaning, it was renamed 'expression' since 2.2.5.
     *
     * @return an expression that can be parsed to a cron schedule.
     */
    @AliasFor("value")
    String expression() default "";

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
