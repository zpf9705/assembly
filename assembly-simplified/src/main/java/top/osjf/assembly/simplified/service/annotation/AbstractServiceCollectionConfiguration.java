package top.osjf.assembly.simplified.service.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.service.ServiceContextUtils;
import top.osjf.assembly.simplified.service.context.ServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContextAware;
import top.osjf.assembly.simplified.service.context.ServiceContextAwareBeanPostProcessor;

/**
 * The abstract configuration inherited by the configuration class
 * of {@link ServiceContext} provides support for loading {@link ServiceContextAware}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.4
 */
public abstract class AbstractServiceCollectionConfiguration {

    //—————————————————————————— mv form SimplifiedAutoConfiguration
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
