package top.osjf.assembly.simplified;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.service.ServiceContextUtils;
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

    //———————————————————————————————————————— auto config for serviceContext setting

    @Bean(ServiceContextUtils.SC_AWARE_BPP_NANE)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    @ConditionalOnMissingBean(ServiceContextAwareBeanPostProcessor.class)
    public ServiceContextAwareBeanPostProcessor serviceContextAwareBeanPostProcessor(
            //Since 2.2.3
            @Lazy //Here, lazy loading is used to prevent dependent beans from losing the function of AOP weaving.
            ServiceContext serviceContext) {
        return new ServiceContextAwareBeanPostProcessor(serviceContext);
    }
}
