package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.assembly.simplified.cron.CronTaskRegistrant;

import java.lang.annotation.*;

/**
 * Configure a solution that can dynamically register scheduled tasks
 * at runtime, and the solution depends on the beans of the Spring framework.
 *
 * <p>In the {@link CronRuntimeRegistrantConfiguration} configuration, configure
 * the default {@link CronTaskRegistrant} to register scheduled tasks at runtime.
 *
 * @see EnableCronTaskRegister2
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.8
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CronRuntimeRegistrantConfiguration.class})
public @interface EnableCronRuntimeRegistrant {
}
