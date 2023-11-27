package top.osjf.assembly.simplified.sdk.annotation;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.osjf.assembly.simplified.sdk.SdkProxyBeanDefinition;
import top.osjf.assembly.simplified.support.BeanDefinitionRegisterBeforeRefresh;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.assembly.util.logger.Console;

/**
 * The proxy registration class of SDK scans the relevant interface
 * classes carrying {@link EnableSdkProxyRegister}.
 *
 * <p>Annotations based on the switch {@link Sdk}, automatically equips
 * the proxy implementation class , and calls the method of unified direction
 * {@link SdkProxyBeanDefinition} to achieve unified deployment of SDK proxy
 * encapsulation.
 *
 * <p>It mainly defines beans through {@link BeanDefinitionBuilder},
 * with variables including obtaining host addresses through spring's
 * environment variables, class objects of proxy interfaces, class aliases,
 * and scanning path names (from value or basePackages).
 *
 * @author zpf
 * @since 1.1.0
 */
public class SdkProxyBeanRegister extends BeanDefinitionRegisterBeforeRefresh {

    static final String DEFAULT_HTTP_BROWSER_HOST = "127.0.0.1:80";

    @Override
    public BeanDefinitionHolder getBeanDefinitionHolder(AnnotationAttributes attributes, AnnotationMetadata meta) {
        String className = meta.getClassName();
        Class<?> beanDefinitionClass = attributes.getClass("beanDefinitionClass");
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(beanDefinitionClass);
        definition.addPropertyValue("host", getRequestHost(attributes.getString("hostProperty")));
        definition.addPropertyValue("clazz", className);
        //@since 2.0.7
        String beanName = attributes.getString("beanName");
        String[] alisa = attributes.getStringArray("alisa");
        String scope = attributes.getString("scope");
        Number autowireMode = attributes.getNumber("autowireMode");
        //@since 2.0.9
        String initMethod = attributes.getString("initMethod");
        String destroyMethod = attributes.getString("destroyMethod");
        int role = attributes.getNumber("role").intValue();
        boolean lazyInit = attributes.getBoolean("lazyInit");
        String description = attributes.getString("description");
        if (StringUtils.isBlank(beanName)) beanName = className;
        definition.setScope(scope);
        definition.setAutowireMode(autowireMode.intValue());
        if (StringUtils.isNotBlank(initMethod)) definition.setInitMethodName(initMethod);
        if (StringUtils.isNotBlank(destroyMethod)) definition.setDestroyMethodName(destroyMethod);
        definition.setRole(role);
        definition.setLazyInit(lazyInit);
        if (StringUtils.isNotBlank(description)) definition.getBeanDefinition().setDescription(description);
        return new BeanDefinitionHolder(definition.getBeanDefinition(), beanName, alisa);
    }

    @Override
    @NotNull
    public Class<EnableSdkProxyRegister> getImportAnnotationClass() {
        return EnableSdkProxyRegister.class;
    }

    @Override
    @NotNull
    public Class<Sdk> getFilterAnnotationClass() {
        return Sdk.class;
    }

    @Override
    @NotNull
    public String getScanPathAttributeName() {
        return "basePackages";
    }

    /**
     * Based on the provided configuration name, obtain the
     * corresponding host address.
     * <p>The supported formats are as described in {@link Sdk#hostProperty()}.
     * <p>If none of the supported formats are obtained, the
     * default value is {@link #DEFAULT_HTTP_BROWSER_HOST}, and any
     * subsequent exceptions will be handled on their own.
     * @param hostProperty Host configuration key.
     * @return Real host address,defaults to {@link #DEFAULT_HTTP_BROWSER_HOST}.
     */
    private String getRequestHost(String hostProperty) {
        Assert.hasText(hostProperty, "HostProperty no be null");
        String host = getEnvironment().resolvePlaceholders(hostProperty);
        if (StringUtils.isBlank(host)) {
            host = getEnvironment().getProperty(hostProperty);
        }
        if (StringUtils.isBlank(host)) {
            Console.warn("【WARNING】Provided by the configuration keys [{}] , didn't find the corresponding" +
                    " configuration items", hostProperty);
            host = DEFAULT_HTTP_BROWSER_HOST;
        }
        return host;
    }
}
