/*
 * Copyright 2024-? the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package top.osjf.sdk.spring.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import top.osjf.sdk.core.support.NotNull;
import top.osjf.sdk.core.support.Nullable;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.proxy.ProxyModel;
import top.osjf.sdk.spring.beans.BeanPropertyUtils;
import top.osjf.sdk.spring.proxy.SdkProxyBeanUtils;
import top.osjf.sdk.spring.proxy.SdkProxyFactoryBean;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A {@code SdkBeanDefinitionRegistrar} is an SDK bean definition registry
 * used to automatically register SDK proxy beans in the context of Spring
 * applications.
 *
 * <p>This class implements the {@code ImportBeanDefinitionRegistrar} interface,
 * allowing for dynamic registration of Bean definitions to the Spring container
 * when using the {@link Import} annotation on the Spring configuration class.
 *
 * <p>At the same time, it also implements the {@code EnvironmentAware} and
 * {@code ResourceLoaderAware} interfaces,used to obtain Spring's environment
 * information and resource loader respectively, it can assist in {@link HelpProvider}
 * scanning the relevant proxy interfaces or abstract class under the specified
 * annotation {@link EnableSdkProxyRegister} class path.
 *
 * <p>The main function of a class is to scan the classes under a specified
 * package name and check if they meet specific conditions (such as whether
 * they are interfaces or abstract classes), creating and registering SDK
 * proxy beans for these classes. These proxy beans can support specific
 * SDK functionalities through callbacks.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.2
 */
public class SdkBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware,
        ResourceLoaderAware, Ordered {

    /**
     * Prefix for system property placeholders: "${"
     */
    private static final String PLACEHOLDER_PREFIX = "${";

    /**
     * Suffix for system property placeholders: "}".
     */
    private static final String PLACEHOLDER_SUFFIX = "}";

    /**
     * Value separator for system property placeholders: ":".
     */
    private static final String VALUE_SEPARATOR = ":";

    /**
     * Local browser host address.
     */
    public static final String LOCAL_HTTP_BROWSER_HOST = "localhost";

    /**
     * Regular expression for domain name validation.
     */
    private static final Pattern DOMAIN_PATTERN =
            Pattern.compile("^(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.[A-Za-z]{2,}$");

    /**
     * Regular expression for IP address validation.
     */
    private static final Pattern IP_PATTERN =
            Pattern.compile(
                    "((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}");

    /**
     * Spring Environment object.
     */
    private Environment environment;

    /**
     * Spring Resource Loader object.
     */
    private ResourceLoader resourceLoader;

    @Override
    public void setEnvironment(@NotNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(@NotNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1;
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry,
                                        @NonNull BeanNameGenerator beanNameGenerator) {
        this.registerBeanDefinitions(importingClassMetadata, registry);
    }

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata importingClassMetadata,
                                        @NonNull BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = importingClassMetadata
                .getAnnotationAttributes(EnableSdkProxyRegister.class.getCanonicalName());
        AnnotationAttributes annotationAttributesObj = AnnotationAttributes.fromMap(annotationAttributes);
        String[] scanningPackageNames;
        if (annotationAttributesObj != null) {
            scanningPackageNames = annotationAttributesObj.getStringArray("basePackages");
        } else {
            scanningPackageNames = new String[]{getPackageName(importingClassMetadata.getClassName())};
        }
        HelpProvider provider = new HelpProvider(environment, resourceLoader);
        for (String packageName : scanningPackageNames) {
            for (BeanDefinition beanDefinition : provider.findCandidateComponents(packageName)) {
                if (beanDefinition instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
                    AnnotationMetadata annotationMetadata = annotatedBeanDefinition.getMetadata();
                    if (annotationMetadata.isInterface() || annotationMetadata.isAbstract()) {
                        BeanDefinitionHolder holder = createBeanDefinitionHolder(annotatedBeanDefinition, registry);
                        if (holder != null)
                            BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
                    }
                }
            }
        }
    }

    /**
     * Extract the package name from the given class full name.
     *
     * <p>The method returns the package name by searching for the
     * position of the last dot {@code .} in the string, and then
     * truncating the part before that dot as the package name.
     *
     * <p>The purpose of this method is to provide a default path
     * when there is no path value for annotation {@code EnableSdkProxyRegister}.
     *
     * @param className a class name string.
     * @return Enter the truncated package name for the class name.
     */
    private String getPackageName(String className) {
        int theLastIndex = className.lastIndexOf(".");
        return className.substring(0, theLastIndex);
    }

    /**
     * Create a {@code BeanDefinitionHolder} instance based on annotation {@link Sdk}.
     *
     * <p>Retrieve AnnotationMetadata and attempt to extract the attributes of
     * {@code Sdk} annotations from it.
     * Based on the model enumeration value, host attribute value (after environment
     * variable parsing and validation), and other SpringBean configurations in the
     * annotation attributes, build a {@code BeanDefinitionBuilder} using {@code SdkProxyFactoryBean}
     * as beanClass, create a {@code BeanDefinition} instance, and finally, create a
     * {@code BeanDefinitionHolder} instance based on the Bean name, alias, and registry.
     * In the subsequent container refresh phase, build the true spring bean.
     *
     * <p>It is worth noting that it supports the setting of various scopes for Spring,
     * namely the {@code Scope} property of beans, which can be found in Method
     * {@link SdkProxyBeanUtils#createBeanDefinitionHolderDistinguishScope}.
     *
     * @param annotatedBeanDefinition {@code AnnotatedBeanDefinition} instances carrying
     *                                annotated metadata.
     * @param registry                the registry interface for beans.
     * @return The created {@code BeanDefinitionHolder} instance.
     */
    private BeanDefinitionHolder createBeanDefinitionHolder(AnnotatedBeanDefinition annotatedBeanDefinition,
                                                            BeanDefinitionRegistry registry) {
        AnnotationMetadata markedAnnotationMetadata = annotatedBeanDefinition.getMetadata();
        AnnotationAttributes annotationAttributes = AnnotationAttributes
                .fromMap(markedAnnotationMetadata.getAnnotationAttributes(Sdk.class.getCanonicalName()));
        if (annotationAttributes == null) return null;
        String className = markedAnnotationMetadata.getClassName();
        ProxyModel model = annotationAttributes.getEnum("model");
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
                SdkProxyFactoryBean.class);
        builder.addPropertyValue("host",
                getEnvHost(annotationAttributes.getString("hostProperty")));
        builder.addPropertyValue("proxyModel", model);
        builder.addConstructorArgValue(className);
        builder.addPropertyReference(SpringCallerConfiguration.SPRING_REQUEST_CALLER_FIELD_NAME,
                SpringCallerConfiguration.INTERNAL_SPRING_REQUEST_CALLER);
        AnnotationAttributes beanPropertyAttributes = annotationAttributes
                .getAnnotation("sdkProxyBeanProperty");
        String[] names = beanPropertyAttributes.getStringArray("name");
        String beanName = SdkProxyBeanUtils.getTargetBeanName(BeanPropertyUtils.getBeanName(names), className);
        String[] alisaNames = BeanPropertyUtils.getAlisaNames(names);
        BeanDefinition beanDefinition =
                BeanPropertyUtils.fullBeanDefinition(builder, markedAnnotationMetadata, beanPropertyAttributes);
        return SdkProxyBeanUtils.createBeanDefinitionHolderDistinguishScope(beanDefinition, beanName, alisaNames,
                registry);
    }

    /**
     * Retrieve the host name from the environment variable and perform necessary
     * parsing and validation.
     *
     * <p>Supports regular attribute definitions and el expressions for Spring.
     *
     * @param hostProperty the configuration attribute name of the host.
     * @return The configuration attribute values of the host.
     */
    @Nullable
    private String getEnvHost(String hostProperty) {
        if (StringUtils.isBlank(hostProperty)) return null;
        String host = null;
        if (environment.containsProperty(hostProperty)) {
            host = environment.getProperty(hostProperty);
        }
        if (StringUtils.isBlank(host)) {
            if (isResolveRequiredPlaceholdersProperty(hostProperty)) {
                host = environment.resolvePlaceholders(hostProperty);
            }
        }
        if (host != null && !validationHost(host)) {
            throw new IncorrectHostException(host);
        }
        return host;
    }

    /**
     * Determine whether the given attribute string needs to parse the
     * Spring EL expression rules for placeholders.
     *
     * <p>The judgment criteria are starting with {@link #PLACEHOLDER_PREFIX}
     * and ending with {@link #PLACEHOLDER_SUFFIX}.
     *
     * @param property configure attribute name.
     * @return If {@literal true}, it is the Spring EL expression specification,
     * otherwise it is not.
     */
    private boolean isResolveRequiredPlaceholdersProperty(String property) {
        if (StringUtils.isNotBlank(property)) {
            return property.startsWith(PLACEHOLDER_PREFIX) && property.endsWith(PLACEHOLDER_SUFFIX);
        }
        return false;
    }

    /**
     * Verify if the host name conforms to the expected format.
     *
     * @param host host name.
     * @return If it is {@literal true}, the validation passes,
     * and it is a valid hostname, otherwise it is not.
     */
    private boolean validationHost(String host) {
        //Firstly, check if the host name starts with the local HTTP browser
        // host localhost:, and if so, return true directly.
        if (host.startsWith(LOCAL_HTTP_BROWSER_HOST + ":")) {
            return true;
        }
        /*
         * If the host name contains a specific value delimiter (:),
         * split it and validate the host and port parts separately.
         * The host part needs to match the IP address mode or domain
         *  name mode, and the port part needs to be able to be resolved
         *  to integers
         * */
        boolean result;
        if (host.contains(VALUE_SEPARATOR)) {
            String[] hostAtt = host.split(VALUE_SEPARATOR);
            boolean $suffixIsInt;
            try {
                Integer.parseInt(hostAtt[1]);
                $suffixIsInt = true;
            } catch (NumberFormatException e) {
                $suffixIsInt = false;
            }
            result = (IP_PATTERN.matcher(hostAtt[0]).matches()
                    //Compatible with domain names and port numbers.
                    || DOMAIN_PATTERN.matcher(hostAtt[0]).matches()
            ) && $suffixIsInt;
        } else {
            //If the host name does not contain a value delimiter,
            // verify directly whether it matches the domain name pattern.
            result = DOMAIN_PATTERN.matcher(host).matches();
        }
        return result;
    }

    /**
     * The {@code HelpProvider} class is used to scan classes marked with
     * specific annotations {@code @Sdk} in Spring applications.
     *
     * <p>The classes marked with {@code @Sdk} annotations need to be
     * independent and not annotated or enumerated.
     */
    static class HelpProvider extends ClassPathScanningCandidateComponentProvider {

        public HelpProvider(Environment environment,
                            ResourceLoader resourceLoader) {
            super(false, environment);
            setResourceLoader(resourceLoader);
            addIncludeFilter(new AnnotationTypeFilter(Sdk.class));
        }

        @Override
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            AnnotationMetadata metadata = beanDefinition.getMetadata();
            return metadata.isIndependent()
                    &&
                    !metadata.isAnnotation()
                    &&
                    !Objects.equals(metadata.getSuperClassName(), Enum.class.getName());
        }
    }
}
