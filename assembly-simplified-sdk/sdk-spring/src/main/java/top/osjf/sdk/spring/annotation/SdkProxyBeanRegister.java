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
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import top.osjf.sdk.core.util.StringUtils;
import top.osjf.sdk.spring.beans.AnnotationTypeScanningCandidateImportBeanDefinitionRegistrar;
import top.osjf.sdk.spring.beans.BeanProperty;
import top.osjf.sdk.spring.beans.BeanPropertyUtils;
import top.osjf.sdk.spring.proxy.*;

import java.util.regex.Pattern;

/**
 * The proxy registration class of SDK scans the relevant interface
 * classes carrying {@link EnableSdkProxyRegister}.
 *
 * <p>Annotations based on the switch {@link Sdk}, automatically equips
 * the proxy implementation class , and calls the method of unified direction
 * {@link SdkJDKProxyBean} or {@link SdkCglibProxyBean}to achieve unified
 * deployment of SDK proxy encapsulation.
 *
 * <p>It mainly defines beans through {@link BeanDefinitionBuilder},
 * with variables including obtaining host addresses through spring's
 * environment variables, class objects of proxy interfaces, class aliases,
 * and scanning path names (from value or basePackages).
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class SdkProxyBeanRegister extends AnnotationTypeScanningCandidateImportBeanDefinitionRegistrar {

    /*** Default browser host address. */
    public static final String DEFAULT_HTTP_BROWSER_HOST = "127.0.0.1:80";

    /*** Local browser host address. */
    public static final String LOCAL_HTTP_BROWSER_HOST = "localhost";

    /*** Regular verification of domain names. */
    private static final Pattern DOMAIN_PATTERN =
            Pattern.compile("^(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.(?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.[A-Za-z]{2,}$");

    /*** Regular verification of IP.*/
    private static final Pattern IP_PATTERN =
            Pattern.compile(
                    "((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)(\\.((25[0-5])|(2[0-4]\\d)|(1\\d\\d)|([1-9]\\d)|\\d)){3}");

    /*** {@link Sdk#model()} annotation proxy model attr name.
     * */
    private static final String SDK_ATTR_PROXY_MODEL = "model";

    /*** {@link Sdk#hostProperty()} annotation host property attr name.
     * */
    private static final String SDK_ATTR_HOST_PROPERTY = "hostProperty";

    /*** {@link Sdk#model()} annotation sdk attr bean property attr name.
     * */
    private static final String SDK_ATTR_BEAN_PROPERTY = "sdkProxyBeanProperty";

    /*** {@link BeanProperty#name()} annotation bean name attr name.
     * */
    private static final String SDK_ATTR_BEAN_NAME = "name";

    /*** Sdk proxy bean host field name.
     * */
    private static final String SDK_PROXY_HOST_NAME = "host";

    /*** Sdk proxy bean proxyModel field name.
     * */
    private static final String SDK_PROXY_MODEL_NAME = "proxyModel";

    @Override
    public BeanDefinitionHolder createBeanDefinitionHolder(AnnotationAttributes markedAnnotationAttributes,
                                                           AnnotationMetadata markedAnnotationMetadata) {
        return createBeanDefinitionHolderInternal(markedAnnotationAttributes, markedAnnotationMetadata);
    }

    private BeanDefinitionHolder createBeanDefinitionHolderInternal(AnnotationAttributes markedAnnotationAttributes,
                                                             AnnotationMetadata markedAnnotationMetadata) {
        String className = markedAnnotationMetadata.getClassName();
        ProxyModel model = markedAnnotationAttributes.getEnum(SDK_ATTR_PROXY_MODEL);
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(
                SdkProxyBean.class);
        builder.addPropertyValue(SDK_PROXY_HOST_NAME,
                getRequestHost(markedAnnotationAttributes.getString(SDK_ATTR_HOST_PROPERTY)));
        builder.addPropertyValue(SDK_PROXY_MODEL_NAME, model);
        builder.addConstructorArgValue(className);
        AnnotationAttributes beanPropertyAttributes = markedAnnotationAttributes
                .getAnnotation(SDK_ATTR_BEAN_PROPERTY);
        String[] names = beanPropertyAttributes.getStringArray(SDK_ATTR_BEAN_NAME);
        String beanName = SdkProxyBeanUtils.getTargetBeanName(BeanPropertyUtils.getBeanName(names), className);
        String[] alisaNames = BeanPropertyUtils.getAlisaNames(names);
        BeanDefinition beanDefinition =
                BeanPropertyUtils.fullBeanDefinition(builder, markedAnnotationMetadata, beanPropertyAttributes);
        return SdkProxyBeanUtils.createBeanDefinitionHolderDistinguishScope(
                beanDefinition,
                beanName,
                alisaNames,
                getBeanDefinitionRegistry());
    }

    @Override
    public Class<EnableSdkProxyRegister> getImportAnnotationType() {
        return EnableSdkProxyRegister.class;
    }

    @Override
    @NonNull
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
                    log.error("No configuration item with configuration key [{}] was found, " +
                            "so it defaults to [{}]", hostProperty, DEFAULT_HTTP_BROWSER_HOST);
                    //if catch IllegalArgumentException not found hostProperty
                    //use default host and logged console
                    host = DEFAULT_HTTP_BROWSER_HOST;
                    useDefaultHost = true;
                }
            } else {
                if (log.isWarnEnabled()) {
                    log.warn("The host address {} attribute provides non configuration items and " +
                            "can be used directly as the host address.", hostProperty);
                }
                host = hostProperty;
            }
        }
        if (!useDefaultHost) {
            if (!validationHost(host)) {
                throw new IncorrectHostException(host);
            }
        }
        return host;
    }

    private boolean validationHost(String host) {
        if (StringUtils.isBlank(host)) {
            return false;
        }
        if (host.startsWith(LOCAL_HTTP_BROWSER_HOST + ":")) {
            return true;
        }
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
            result = DOMAIN_PATTERN.matcher(host).matches();
        }
        return result;
    }
}
