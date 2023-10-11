package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.context.annotation.Import;
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
 * <p>At the same time, the docking {@link EnableCronTaskRegister} also lacks
 * default startup. When changing the annotation configuration to default task
 * less registration, it will also start the timed thread.
 *
 * @author zpf
 * @see top.osjf.assembly.simplified.cron.CronTaskRegisterPostProcessor
 * @since 2.0.6
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CronTaskRegisterConfiguration.class})
public @interface EnableCronTaskRegister2 {
}
