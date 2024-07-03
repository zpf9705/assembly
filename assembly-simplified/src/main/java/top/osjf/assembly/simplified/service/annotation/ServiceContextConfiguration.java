package top.osjf.assembly.simplified.service.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.service.context.ClassesServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContext;
import top.osjf.assembly.simplified.service.ServiceContextUtils;

/**
 * Service context configuration class.
 *
 * <p>This configuration class works inside the container and is not related to usage.
 * <p>The annotation {@link Role} is {@link BeanDefinition#ROLE_INFRASTRUCTURE}.
 * <p>For details, please refer to the specific annotations mentioned earlier.
 *
 * <p>Configure a {@link ServiceContext} container singleton object for {@link ClassesServiceContext},
 * which can be used by users.</p>
 *
 * <p>Apply to {@link top.osjf.assembly.simplified.service.annotation.EnableServiceCollection}.
 *
 * @author zpf
 * @since 2.0.4
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class ServiceContextConfiguration extends AbstractServiceCollectionConfiguration {

    @Bean(ServiceContextUtils.CONFIG_BEAN_NAME)
    public ServiceContext serviceContext() {
        return new ClassesServiceContext();
    }
}
