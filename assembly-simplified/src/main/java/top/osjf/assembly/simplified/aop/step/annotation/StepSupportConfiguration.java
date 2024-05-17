package top.osjf.assembly.simplified.aop.step.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.aop.step.AspectJStepSupport;

/**
 * Enable loading configuration for {@link EnableStepSupport} on
 * annotation classes.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class StepSupportConfiguration {

    @Bean
    public AspectJStepSupport aspectJStepSupport() {
        return new AspectJStepSupport();
    }
}
