package top.osjf.assembly.simplified;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
    @ConditionalOnBean(ServiceContext.class) //Configure during service context loading
    @ConditionalOnMissingBean(ServiceContextAwareBeanPostProcessor.class) //Only configure when this configuration is not available
    public ServiceContextAwareBeanPostProcessor serviceContextAwareBeanPostProcessor(/*Since 2.2.2*/
            ServiceContext serviceContext) {
        return new ServiceContextAwareBeanPostProcessor(serviceContext);
    }
}
