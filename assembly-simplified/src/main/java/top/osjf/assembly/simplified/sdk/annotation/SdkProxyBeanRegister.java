package top.osjf.assembly.simplified.sdk.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.osjf.assembly.simplified.sdk.proxy.SdkCglibProxyBean;
import top.osjf.assembly.simplified.sdk.proxy.SdkJDKProxyBean;
import top.osjf.assembly.simplified.sdk.proxy.SdkProxyBeanUtils;
import top.osjf.assembly.simplified.support.AnnotationTypeScanningCandidateImportBeanDefinitionRegistrar;
import top.osjf.assembly.simplified.support.BeanPropertyUtils;
import top.osjf.assembly.simplified.support.ProxyModel;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.StringUtils;
import top.osjf.assembly.util.logger.Console;

/**
 * The proxy registration class of SDK scans the relevant interface
 * classes carrying {@link EnableSdkProxyRegister}.
 *
 * <p>Annotations based on the switch {@link Sdk}, automatically equips
 * the proxy implementation class , and calls the method of unified direction
 * {@link SdkJDKProxyBean} or since 2.2.5 {@link SdkCglibProxyBean}to achieve
 * unified deployment of SDK proxy encapsulation.
 *
 * <p>It mainly defines beans through {@link BeanDefinitionBuilder},
 * with variables including obtaining host addresses through spring's
 * environment variables, class objects of proxy interfaces, class aliases,
 * and scanning path names (from value or basePackages).
 *
 * @author zpf
 * @since 1.1.0
 */
public class SdkProxyBeanRegister extends AnnotationTypeScanningCandidateImportBeanDefinitionRegistrar {

    /*** Default browser host address */
    public static final String DEFAULT_HTTP_BROWSER_HOST = "127.0.0.1:80";

    /*** Default browser host address */
    public static final String DEFAULT_BEAN_NAME_SUFFIX = "@sdk.proxy.bean";

    @Override
    public BeanDefinitionHolder createBeanDefinitionHolder(AnnotationAttributes markedAnnotationAttributes,
                                                           AnnotationMetadata markedAnnotationMetadata) {
        String className = markedAnnotationMetadata.getClassName();
        ProxyModel model = markedAnnotationAttributes.getEnum("model");
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
                markedAnnotationAttributes.getClass("proxyBeanType"));
        builder.addPropertyValue("host",
                getRequestHost(markedAnnotationAttributes.getString("hostProperty")));
        builder.addPropertyValue("proxyModel", model);
        builder.addPropertyValue("type", className);
        //@Since 2.2.5 Use @BeanProperty instead.
        AnnotationAttributes beanPropertyAttributes = markedAnnotationAttributes
                .getAnnotation("sdkProxyBeanProperty");
        String[] names = beanPropertyAttributes.getStringArray("name");
        //@since 2.0.7
        String beanName = SdkProxyBeanUtils.getTargetBeanName(BeanPropertyUtils.getBeanName(names), className);
        String[] alisaNames = BeanPropertyUtils.getAlisaNames(names);
        String scope = beanPropertyAttributes.getString("scope");
        Autowire autowire = beanPropertyAttributes.getEnum("autowire");
        //@since 2.0.9
        String initMethod = beanPropertyAttributes.getString("initMethod");
        String destroyMethod = beanPropertyAttributes.getString("destroyMethod");
        int role = beanPropertyAttributes.<Integer>getNumber("role");
        boolean lazyInit = beanPropertyAttributes.getBoolean("lazyInit");
        String description = beanPropertyAttributes.getString("description");
        //@Since 2.2.5
        boolean autowireCandidate = beanPropertyAttributes.getBoolean("autowireCandidate");
        builder.setScope(scope);
        builder.setAutowireMode(autowire.value());
        if (StringUtils.isNotBlank(initMethod)) builder.setInitMethodName(initMethod);
        if (StringUtils.isNotBlank(destroyMethod)) builder.setDestroyMethodName(destroyMethod);
        builder.setRole(role);
        builder.setLazyInit(lazyInit);
        if (StringUtils.isNotBlank(description)) builder.getBeanDefinition().setDescription(description);
        builder.getBeanDefinition().setAutowireCandidate(autowireCandidate);
        return SdkProxyBeanUtils.createBeanDefinitionHolderDistinguishScope(builder,
                getBeanDefinitionRegistry(),
                scope,
                b -> b.addConstructorArgValue(className),
                beanName,
                alisaNames);
    }

    @Override
    public Class<EnableSdkProxyRegister> getImportAnnotationType() {
        return EnableSdkProxyRegister.class;
    }

    @Override
    @NotNull
    public Class<Sdk> getFilterAnnotationType() {
        return Sdk.class;
    }

    /**
     * Create proxy classes only for interfaces and abstract classes. Regular
     * classes should use the annotations {@link org.springframework.stereotype.Component}
     * {@link org.springframework.stereotype.Service}  provided by Spring to
     * actively create beans, rather than using the proxies in this class.
     *
     * @param metadata the {@link AnnotationMetadata} provided by the
     *                 {@link AnnotatedBeanDefinition}.
     * @return Further filtering results from {@link SdkProxyBeanRegister} on class metadata.
     */
    @Override
    protected boolean isAvailableMarkedBeanDefinitionMetadata(AnnotationMetadata metadata) {
        return metadata.isInterface() || metadata.isAbstract();
    }

    /**
     * When no bean name is provided for the SDK proxy bean,
     * this method is used as an alternative.
     *
     * @return The name of the proxy bean.
     */
    private String generateBeanName(String className) {
        return className + DEFAULT_BEAN_NAME_SUFFIX;
    }

    /**
     * Based on the provided configuration name, obtain the
     * corresponding host address.
     * <p>The supported formats are as described in {@link Sdk#hostProperty()}.
     * <p>When the host configuration is not queried based
     * on properties, no exception will be thrown, and the
     * default {@link #DEFAULT_HTTP_BROWSER_HOST} will be used.
     * <p><strong>Checking Rule</strong></p>
     * <ul>
     *     <li>Prioritize using {@link Environment#getProperty(String)} to obtain</li>
     *     <li>The above is not obtained, consider whether it is in {@code ${...}} form,
     *     use {@link Environment#resolveRequiredPlaceholders(String)}, and after throwing
     *     an exception, use default.</li>
     *     <li>Provide host format verification, using the default host without verification.</li>
     * </ul>
     *
     * @param hostProperty Host configuration key.
     * @return Real host address,defaults to {@link #DEFAULT_HTTP_BROWSER_HOST}.
     * @see #is$PropertyGet(String)
     * @see #validationHost(String)
     */
    private String getRequestHost(String hostProperty) {
        if (StringUtils.isBlank(hostProperty)) {
            return DEFAULT_HTTP_BROWSER_HOST;
        }
        Environment environment = getEnvironment();
        Assert.notNull(environment, "Environment not capable");
        //Priority is given to using field query configurations directly.
        boolean useDefaultHost = false;
        String host;
        if (environment.containsProperty(hostProperty)) {
            host = environment.getProperty(hostProperty);
        } else {
            if (is$PropertyGet(hostProperty)) {
                try {
                    host = environment.resolveRequiredPlaceholders(hostProperty);
                } catch (IllegalArgumentException e) {
                    Console.warn("No configuration item with configuration key [{}] was found, " +
                            "so it defaults to [{}]", hostProperty, DEFAULT_HTTP_BROWSER_HOST);
                    //if catch IllegalArgumentException not found hostProperty
                    //use default host and logged console
                    host = DEFAULT_HTTP_BROWSER_HOST;
                    useDefaultHost = true;
                }
            } else {
                Console.warn("No configuration item with configuration key [{}] was found, " +
                        "so it defaults to [{}]", hostProperty, DEFAULT_HTTP_BROWSER_HOST);
                //If no relevant environment configuration for the host is
                // found, the default HTTP host address will be used.
                host = DEFAULT_HTTP_BROWSER_HOST;
                useDefaultHost = true;
            }
        }
        if (!useDefaultHost) {
            if (!validationHost(host)) {
                throw new IncorrectHostException(host);
            }
        }
        return host;
    }

    //@since 2.1.1
    private boolean validationHost(String host) {
        if (StringUtils.isBlank(host)) {
            return false;
        }
        boolean result;
        final String doMainRegex = "[a-zA-Z0-9][-a-zA-Z0-9]{0,62}(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+\\.?";
        final String ipRegex
                = "((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}";
        if (host.contains(VALUE_SEPARATOR)) {
            String[] hostAtt = host.split(VALUE_SEPARATOR);
            boolean $suffixIsInt;
            try {
                Integer.parseInt(hostAtt[1]);
                $suffixIsInt = true;
            } catch (NumberFormatException e) {
                $suffixIsInt = false;
            }
            result = hostAtt[0].matches(ipRegex) && $suffixIsInt;
        } else {
            result = host.matches(doMainRegex);
        }
        return result;
    }
}
