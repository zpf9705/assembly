package top.osjf.assembly.simplified.sdk.annotation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.osjf.assembly.simplified.sdk.SdkProxyBeanDefinition;
import top.osjf.assembly.simplified.support.AbstractProxyBeanInjectSupport;
import top.osjf.assembly.util.annotation.NotNull;

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
public class SdkProxyBeanRegister extends AbstractProxyBeanInjectSupport<EnableSdkProxyRegister, Sdk> {

    @Override
    @NotNull
    public Class<EnableSdkProxyRegister> getOpenClazz() {
        return EnableSdkProxyRegister.class;
    }

    @Override
    @NotNull
    public Class<Sdk> getFindClazz() {
        return Sdk.class;
    }

    @Override
    public void beanRegister(AnnotationAttributes attributes, BeanDefinitionRegistry registry, AnnotationMetadata meta) {
        String className = meta.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(SdkProxyBeanDefinition.class);
        definition.addPropertyValue("host", getRequestHost(attributes.getString("hostProperty")));
        definition.addPropertyValue("clazz", className);
        AnnotationAttributes attributes0 = attributes.getAnnotation("attributes");
        String beanName = attributes0.getString("beanName");
        String[] alisa = attributes0.getStringArray("alisa");
        String scope = attributes0.getString("scope");
        Number autowireMode = attributes0.getNumber("autowireMode");
        if (StringUtils.isBlank(beanName)) beanName = className;
        definition.setScope(scope);
        definition.setAutowireMode(autowireMode.intValue());
        BeanDefinitionHolder holder = new BeanDefinitionHolder(definition.getBeanDefinition(), beanName, alisa);
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
    }

    @Override
    @NotNull
    public String getPackagesSign() {
        return "basePackages";
    }

    /**
     * Obtain host configuration parameters,according to the startup
     * environment of spring.
     *
     * @param hostProperty Host configuration key.
     * @return Real host address.
     */
    private String getRequestHost(String hostProperty) {
        Assert.hasText(hostProperty, "HostProperty no be null");
        String host = getEnvironment().resolvePlaceholders(hostProperty);
        if (StringUtils.isBlank(host)) {
            host = getEnvironment().getProperty(hostProperty);
        }
        Assert.hasText(host,
                "Provided by the configuration keys [" + hostProperty + "] , Didn't find the corresponding " +
                        "configuration items , Please check");
        return host;
    }
}
