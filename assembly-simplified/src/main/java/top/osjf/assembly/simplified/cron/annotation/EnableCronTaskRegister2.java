package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.assembly.simplified.cron.CronListener;
import top.osjf.assembly.simplified.cron.CronTaskRegisterConfiguration;

import java.lang.annotation.*;

/**
 * The second simplified annotation for automatic assembly registration
 * of scheduled tasks.
 *
 * <p>Compared to {@link EnableCronTaskRegister}, this annotation lacks the
 * formulation of a scanning path and instead chooses to wear annotations
 * {@link Cron} method on all registered beans, which is more convenient.
 *
 * @see top.osjf.assembly.simplified.cron.CronTaskRegisterPostProcessor
 * @author zpf
 * @since 2.0.6
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CronTaskRegisterConfiguration.class})
public @interface EnableCronTaskRegister2 {

    /**
     * Choose whether to start the timed thread pool even if no
     * registration method is found.
     * <p>But I won't actively add listeners for {@link CronListener},
     * unless a timing method with {@link Cron} annotation is added.
     *
     * @return If {@code true} , defaults to enable.
     */
    boolean noMethodDefaultStart() default false;
}
