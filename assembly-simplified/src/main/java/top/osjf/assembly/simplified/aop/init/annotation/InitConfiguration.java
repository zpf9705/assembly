package top.osjf.assembly.simplified.aop.init.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.aop.init.AspectJInitSupport;

/**
 * The relevant configuration of the initialization mechanism
 * relies on the annotation {@link EnableInit} to enable loading.
 *
 * @see EnableInit
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class InitConfiguration {

    @Bean
    public AspectJInitSupport aspectJInitSupport() {
        return new AspectJInitSupport();
    }
}
