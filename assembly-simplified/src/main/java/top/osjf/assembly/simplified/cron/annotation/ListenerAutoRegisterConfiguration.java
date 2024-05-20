package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.cron.CronListener;
import top.osjf.assembly.simplified.cron.CronRegister;

import java.util.List;

/**
 * Use annotation {@link EnableListenerAutoRegister} to enable this
 * configuration, perform {@link CronListener} bean level collection,
 * and set listeners.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2024.05.20
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ListenerAutoRegisterConfiguration {

    /**
     * Automatically set the listener for scheduled task execution.
     * @param cronListeners Implement {@link CronListener} using configured Spring components.
     */
    @Autowired(required = false)
    public void setCronListeners(List<CronListener> cronListeners) {
        CronRegister.addListeners(cronListeners);
    }
}
