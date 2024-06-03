package top.osjf.assembly.simplified.scope;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.context.annotation.Role;
import org.springframework.core.type.AnnotationMetadata;
import top.osjf.assembly.simplified.support.MappedAnnotationAttributes;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ReflectUtils;

/**
 * Annotate the registration configuration class for custom scope
 * {@code 'support'} loaded by {@link EnableRegisterSupportScope}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RegisterSupportScopeConfiguration implements ImportAware, BeanFactoryPostProcessor {

    private Scope scope;

    @Override
    public void setImportMetadata(@NotNull AnnotationMetadata importMetadata) {
        Class<? extends Scope> type = MappedAnnotationAttributes.of(importMetadata
                        .getAnnotationAttributes(EnableRegisterSupportScope.class.getCanonicalName()))
                .getClass("value");
        scope = ReflectUtils.newInstance(type);
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.registerScope("support", scope);
    }
}
