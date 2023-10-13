package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.service.annotation.EnableServiceCollection2;

/**
 * Service context configuration class for {@link SimpleServiceContext}.
 *
 * <p>This configuration class works inside the container and is not related to usage.
 * <p>The annotation {@link Role} is {@link BeanDefinition#ROLE_INFRASTRUCTURE}.
 * <p>For details, please refer to the specific annotations mentioned earlier.
 *
 * <p>Configure a {@link ServiceContext} container singleton object for {@link SimpleServiceContext},
 * which can be used by users.</p>
 *
 * <p>Apply to {@link EnableServiceCollection2#type()}.
 *
 * @author zpf
 * @since 2.0.6
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class SimpleServiceContextConfiguration {

    @Bean(SimpleServiceContext.CONFIG_BEAN_NAME)
    @Role(BeanDefinition.ROLE_INFRASTRUCTURE)
    public ServiceContext serviceContext() {
        return new SimpleServiceContext();
    }
}
