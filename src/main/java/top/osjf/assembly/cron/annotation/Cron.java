package top.osjf.assembly.cron.annotation;

import top.osjf.assembly.cron.CronTaskRegisterImport;

import java.lang.annotation.*;

/**
 * Flag annotation for timed task method registration, only supports starting with cron expression,
 * supports spring specified environment {@link org.springframework.core.env.Environment}registration,
 * and provides fixed initialization parameters.
 * <p>
 * Relying on {@link CronTaskRegisterImport} to scan, obtain, and register corresponding annotation methods.
 *
 * @author zpf
 * @since 1.1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cron {

    /**
     * Fill in a planned cron expression that represents the frequency of task execution
     *
     * @return corn express , must no be {@literal null}
     */
    String express();

    /**
     * Limited spring startup environment parameters,
     * only when registering the task in the specified environment
     *
     * @return If left blank, register directly
     */
    String[] profiles() default {};
}
