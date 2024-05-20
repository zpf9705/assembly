package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.context.annotation.Import;
import top.osjf.assembly.simplified.cron.CronListener;

import java.lang.annotation.*;

/**
 * Use this configuration to enable the configuration item for
 * {@link ListenerAutoRegisterConfiguration}, to automatically
 * collect implementation beans related to {@link CronListener}
 * from the spring container, and add them to the scheduled task
 * execution cycle.
 *
 * <p>At present, it has been integrated into {@link EnableCronTaskRegister2}
 * annotations, automatically scanning scheduled tasks while registering
 * listeners.
 *
 * @see EnableCronTaskRegister2
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({ListenerAutoRegisterConfiguration.class})
@Documented
public @interface EnableListenerAutoRegister {
}
