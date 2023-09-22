package top.osjf.assembly.simplified.cron.annotation;

import top.osjf.assembly.simplified.cron.CronTaskRegisterImport;

import java.lang.annotation.*;

/**
 * Flag annotation for timed task method registration, only supports starting with cron expression,
 * supports spring specified environment {@link org.springframework.core.env.Environment}registration,
 * and provides fixed initialization parameters.
 *
 * <p>Relying on {@link CronTaskRegisterImport} to scan, obtain, and register corresponding annotation methods.
 *
 * @author zpf
 * @since 1.1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cron {

    /**
     * Provide the correct formatted cron expression for timing.
     *
     * @return corn express , must no be {@literal null}.
     */
    String express();

    /**
     * Specify the set of environment names for the spring framework to start and run for scheduled tasks.
     *
     * @return If left blank, register directly.
     */
    String[] profiles() default {};
}
