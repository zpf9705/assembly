package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.assembly.simplified.cron.CronListener;

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
 * <p>At this point, support for automatic registration {@link CronListener}
 * has been added, relying on annotations {@link EnableListenerAutoRegister}.
 *
 * @see CronTaskRegisterPostProcessor
 * @author zpf
 * @since 2.0.6
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CronTaskRegisterPostProcessor.class})
@EnableListenerAutoRegister //Since 2.2.5
@EnableCronRuntimeRegistrant //since 2.2.8
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
