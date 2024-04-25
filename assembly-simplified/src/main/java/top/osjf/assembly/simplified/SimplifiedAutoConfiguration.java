package top.osjf.assembly.simplified;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.service.context.ServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContextAwareBeanPostProcessor;

/**
 * Auto Configuration for assembly-simplified.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.1
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SimplifiedAutoConfiguration {

    @Bean
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnBean(ServiceContext.class)
    public ServiceContextAwareBeanPostProcessor serviceContextAwareBeanPostProcessor() {
        return new ServiceContextAwareBeanPostProcessor();
    }
}
