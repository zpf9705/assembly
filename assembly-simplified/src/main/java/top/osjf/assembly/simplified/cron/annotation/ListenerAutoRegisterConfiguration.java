package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.cron.CronConfigurer;
import top.osjf.assembly.simplified.cron.CronListener;
import top.osjf.assembly.simplified.cron.CronTaskManager;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * Use annotation {@link EnableListenerAutoRegister} to enable this
 * configuration, perform {@link CronListener} bean level collection,
 * and set listeners.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
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
        CronTaskManager.addListeners(cronListeners);
    }

    /**
     * Automatically set the listener by {@link CronConfigurer} for scheduled task execution.
     * @param cronConfigurers Implement the interface configuration item for {@link CronConfigurer}.
     */
    @Autowired(required = false)
    public void setCronListenersByCronConfigurer(List<CronConfigurer> cronConfigurers) {
        if (CollectionUtils.isNotEmpty(cronConfigurers)) {
            cronConfigurers.stream()
                    .map(CronConfigurer::getWillRegisterCronListeners)
                    .filter(Objects::nonNull)
                    .distinct()
                    .forEach(CronTaskManager::addListeners);
        }
    }
}
