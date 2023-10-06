package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import top.osjf.assembly.simplified.cron.AbstractCronListener;
import top.osjf.assembly.simplified.cron.CronListener;
import top.osjf.assembly.simplified.cron.CronRegister;
import top.osjf.assembly.simplified.cron.CronTaskRegister;

import java.lang.annotation.*;

/**
 * Opening annotations for timed task registration.
 *
 * <p>This annotation will scan the methods that carry {@link CronTaskRegister} through
 * {@link Cron}, register them with the timed thread pool using {@link CronRegister},
 * and trigger these operations.
 *
 * <p>It only needs to be standardized in the class header of the spring
 * container to be implemented.</p>
 *
 * @author zpf
 * @since 1.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CronTaskRegister.class})
public @interface EnableCronTaskRegister {

    /**
     * Carrying the path where the {@link Cron} class is located.
     * <p>At the same time, task listeners can also be added to implement
     * {@link CronListener} or {@link AbstractCronListener}.
     * <p>If it is null, the default is to use springboot to start the
     * package path where the main class is located.
     *
     * @return Alias for {{@link #basePackages()}}.
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * His value shifts to {@link #value()}, consistent with it.
     * <p>At the same time, task listeners can also be added to implement
     * {@link CronListener} or {@link AbstractCronListener}.
     * <p>If it is null, the default is to use springboot to start the
     * package path where the main class is located.
     *
     * @return Alias for {{@link #value()}}.
     */
    @AliasFor("value")
    String[] basePackages() default {};

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
