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
import top.osjf.assembly.util.lang.StringUtils;

import java.util.Objects;

/**
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class RegisterScopeConfiguration implements ImportAware, BeanFactoryPostProcessor {

    private String scopeName;

    private Class<? extends Scope> type;

    @Override
    public void setImportMetadata(@NotNull AnnotationMetadata importMetadata) {
        MappedAnnotationAttributes attributes = MappedAnnotationAttributes.of(
                importMetadata.getAnnotationAttributes(EnableRegisterScope.class.getCanonicalName()));
        type = attributes.getClass("value");
        scopeName = attributes.getString("scopeName");
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (StringUtils.isNotBlank(scopeName) && !Objects.equals(type, Scope.class)) {
            beanFactory.registerScope(scopeName, ReflectUtils.newInstance(type));
        }
    }
}
