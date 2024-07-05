package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.cron.CronTaskRegistrant;
import top.osjf.assembly.simplified.cron.DefaultCronTaskRegistrant;

/**
 * The configuration item triggered by {@link EnableCronRuntimeRegistrant}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.8
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CronRuntimeRegistrantConfiguration {

    @Bean
    public CronTaskRegistrant cronTaskRegistrant() {
        return new DefaultCronTaskRegistrant();
    }
}
