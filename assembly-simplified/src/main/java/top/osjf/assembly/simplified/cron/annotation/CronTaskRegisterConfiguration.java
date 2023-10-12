package top.osjf.assembly.simplified.cron.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

/**
 * The configuration class of the post processor for timed task registration.
 *
 * <p>Only for temporary configuration, not for user use.
 *
 * @author zpf
 * @since 2.0.6
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class CronTaskRegisterConfiguration {

    @Bean(CronTaskRegisterPostProcessor.NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public CronTaskRegisterPostProcessor cronTaskRegisterPostProcessor() {
        return new CronTaskRegisterPostProcessor();
    }
}
