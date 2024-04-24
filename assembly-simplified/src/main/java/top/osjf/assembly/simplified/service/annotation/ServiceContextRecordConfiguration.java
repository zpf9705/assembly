package top.osjf.assembly.simplified.service.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.service.ServiceContextUtils;
import top.osjf.assembly.simplified.service.context.RecordServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContext;

/**
 * The import configuration of {@link EnableServiceCollection3} annotations, based on the storage name of
 * {@link top.osjf.assembly.simplified.service.context.AbstractServiceContext.ServiceContextBeanNameGenerator},
 * manages the storage of beans after refreshing the context of the spring container.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see EnableServiceCollection3
 * @since 2.2.0
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ServiceContextRecordConfiguration {

    @Bean(ServiceContextUtils.RECORD_BEAN_NAME)
    public ServiceContext serviceContext() {
        return new RecordServiceContext();
    }
}
