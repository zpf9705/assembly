package top.osjf.assembly.simplified.service.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.context.annotation.Role;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.simplified.scope.EnableRegisterScope;
import top.osjf.assembly.simplified.service.ServiceContextUtils;
import top.osjf.assembly.simplified.service.context.RecordServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceContext;
import top.osjf.assembly.simplified.service.context.ServiceScope;
import top.osjf.assembly.util.annotation.NotNull;

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
@EnableRegisterScope(scopeName = ServiceContextUtils.SERVICE_SCOPE, scopeType = ServiceScope.class)
public class ServiceContextRecordConfiguration extends AbstractServiceCollectionConfiguration {

    @Bean(ServiceContextUtils.RECORD_BEAN_NAME)
    public ServiceContext serviceContext() {
        return new RecordServiceContext();
    }

    /**
     * Register a {@link RecordServiceContext.ChangeScopePostProcessor} to support scope modification.
     * @since 2.2.5
     */
    public static class ServiceContextRecordImportConfiguration implements ImportBeanDefinitionRegistrar {
        @Override
        public void registerBeanDefinitions(@NotNull AnnotationMetadata importingClassMetadata,
                                            @NotNull BeanDefinitionRegistry registry,
                                            @NotNull BeanNameGenerator importBeanNameGenerator) {
            BeanDefinition beanDefinition =
                    BeanDefinitionBuilder.genericBeanDefinition(RecordServiceContext.ChangeScopePostProcessor.class)
                            .getBeanDefinition();
            registry.registerBeanDefinition(importBeanNameGenerator.generateBeanName(beanDefinition, registry),
                    beanDefinition);
        }
    }
}
