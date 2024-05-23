package top.osjf.assembly.simplified.sdk.annotation;

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import top.osjf.assembly.simplified.sdk.proxy.SdkCglibProxyBean;
import top.osjf.assembly.simplified.sdk.proxy.SdkJDKProxyBean;
import top.osjf.assembly.simplified.support.AnnotationTypeScanningCandidateImportBeanDefinitionRegistrar;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.encode.DigestUtils;
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

    @Override
    public BeanDefinitionHolder createBeanDefinitionHolder(AnnotationAttributes markedAnnotationAttributes,
                                                           AnnotationMetadata markedAnnotationMetadata) {
        String className = markedAnnotationMetadata.getClassName();
        Class<?> beanDefinitionType = markedAnnotationAttributes.getClass("beanDefinitionType");
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(beanDefinitionType);
        definition.addPropertyValue("host",
                getRequestHost(markedAnnotationAttributes.getString("hostProperty")));
        definition.addPropertyValue("type", className);
        //@since 2.0.7
        String beanName = markedAnnotationAttributes.getString("beanName");
        String[] alisa = markedAnnotationAttributes.getStringArray("alisa");
        String scope = markedAnnotationAttributes.getString("scope");
        int autowireMode = markedAnnotationAttributes.<Integer>getNumber("autowireMode");
        //@since 2.0.9
        String initMethod = markedAnnotationAttributes.getString("initMethod");
        String destroyMethod = markedAnnotationAttributes.getString("destroyMethod");
        int role = markedAnnotationAttributes.<Integer>getNumber("role");
        boolean lazyInit = markedAnnotationAttributes.getBoolean("lazyInit");
        String description = markedAnnotationAttributes.getString("description");
        //@Since 2.2.5
        boolean autowireCandidate = markedAnnotationAttributes.getBoolean("autowireCandidate");
        if (StringUtils.isBlank(beanName)) beanName = generateBeanName(className);
        definition.setScope(scope);
        definition.setAutowireMode(autowireMode);
        if (StringUtils.isNotBlank(initMethod)) definition.setInitMethodName(initMethod);
        if (StringUtils.isNotBlank(destroyMethod)) definition.setDestroyMethodName(destroyMethod);
        definition.setRole(role);
        definition.setLazyInit(lazyInit);
        if (StringUtils.isNotBlank(description)) definition.getBeanDefinition().setDescription(description);
        definition.getBeanDefinition().setAutowireCandidate(autowireCandidate);
        return new BeanDefinitionHolder(definition.getBeanDefinition(), beanName, alisa);
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

    @Override
    protected boolean isAvailableMarkedBeanDefinitionMetadata(AnnotationMetadata metadata) {
        return metadata.isInterface() || metadata.isAbstract();
    }

    /*** Default browser host address */
    static final String DEFAULT_HTTP_BROWSER_HOST = "127.0.0.1:80";

    /*** Default browser host address */
    static final String DEFAULT_BEAN_NAME_SUFFIX = "@sdk.proxy.bean";

    /**
     * When no bean name is provided for the SDK proxy bean,
     * this method is used as an alternative.
     * @return The name of the proxy bean.
     * */
    private String generateBeanName(String className) {
        return DigestUtils.md5Hex(className) + DEFAULT_BEAN_NAME_SUFFIX;
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
        Assert.hasText(hostProperty, "HostProperty no be null");
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
