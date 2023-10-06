package top.osjf.assembly.simplified.cron.annotation;

import top.osjf.assembly.simplified.cron.CronTaskRegister;

import java.lang.annotation.*;

/**
 * Flag annotation for timed task method registration, only supports
 * starting with cron expression, supports spring specified environment
 * {@link org.springframework.core.env.Environment} registration,
 * and provides fixed initialization parameters.
 *
 * <p>Relying on {@link CronTaskRegister} to scan, obtain, and register
 * corresponding annotation methods.
 *
 * @author zpf
 * @since 1.1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cron {

    /**
     * Provide a cron expression that conforms to the rules.
     * <p>This parameter will determine the execution interval of the
     * registered scheduled task.
     * <p>Please understand the writing conventions of cron expressions
     * before use to avoid unnecessary errors.
     *
     * @return Must be provided and cannot be {@literal null}.
     */
    String express();

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
