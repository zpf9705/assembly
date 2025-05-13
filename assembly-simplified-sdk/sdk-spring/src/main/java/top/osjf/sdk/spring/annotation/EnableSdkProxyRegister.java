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

import org.springframework.aop.ClassFilter;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import top.osjf.sdk.spring.proxy.SdkProxyFactoryBean;

import java.lang.annotation.*;

/**
 * This annotation is mainly annotated on the class where the Spring
 * injection class annotation is located, which can implement the
 * interface class with automatic injection annotation {@link Sdk}
 * and automatically create the implementation class, it mainly relies
 * on {@link SdkBeanDefinitionRegistrar}.
 *
 * <p>Redirect {@link #value()} and {@link #basePackages()}to the
 * {@link ComponentScan} annotation, defined the path for SDK scanning
 * graffiti representation. If the path contains a regular class
 * {@link Sdk}without annotations, it will not be used to create
 * proxy beans. However,if it holds annotations created by Spring
 * container beans, such as {@link Component}, it will still be parsed
 * by Spring and added to the Spring container for use.
 *
 * <p>According to the framework dynamic registration configuration
 * {@link SdkBeanDefinitionRegistrar}, non interfaces and abstract
 * classes do not participate in the dynamic construction of SDK beans.
 * The annotation {@link Sdk} marks the {@link Component} annotation,
 * which is not created by the Spring container, but by the dynamic
 * configuration mentioned above. Therefore, the Spring bean prompt
 * is displayed in the editing software, only in the UI, not directly
 * created by the Spring annotation.
 *
 * <p><strong>Warn UI Support</strong>
 * <p>Please be cautious when merging annotation {@code org.springframework
 * .boot.autoconfigure.SpringBootApplication}, as it is used together with
 * {@link ComponentScan},it will cause the current scanning path of the main
 * startup annotation to become invalid.
 *
 * <p>Suggested for separate configuration classes, such as the following
 * example:
 * <p><u><strong>For example code:</strong></u>
 * <pre>
 *     &#064;Configuration(proxyBeanMethods = false)
 *     &#064;Role(BeanDefinition.ROLE_INFRASTRUCTURE)
 *     &#064;EnableSdkProxyRegister(Constance.SDK_SCAN_PATH)
 *     public class ExampleSdkRegisterConfiguration {
 * }
 * </pre>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SdkBeanDefinitionRegistrar.class, SpringCallerConfiguration.class})
@ComponentScan
public @interface EnableSdkProxyRegister {

    /**
     * Carrying the path where the {@link Sdk} class is located.
     * <p>If it is null, the default is to use springboot to start
     * the package path where the main class is located.
     * <p>Redirect this property to {@link ComponentScan#value()},
     * configure this property to display the injection source of the
     * bean during code editing, improve user experience.
     * <p>If this property is not configured, the injection source
     * will not be displayed, but the implementation class has already
     * been injected.
     *
     * @return Alias for {{@link #basePackages()}} for {@link ComponentScan}.
     */
    @AliasFor(annotation = ComponentScan.class)
    String[] value() default {};

    /**
     * His value shifts to {@link #value()}, consistent with it.
     * <p>If it is null, the default is to use springboot to start
     * the package path where the main class is located.
     * <p>Redirect this property to {@link ComponentScan#basePackages()},
     * configure this property to display the injection source of the
     * bean during code editing, improve user experience.
     * <p>If this property is not configured, the injection source
     * will not be displayed, but the implementation class has already
     * been injected.
     *
     * @return Alias for {{@link #value()}} for {@link ComponentScan}.
     */
    @AliasFor(annotation = ComponentScan.class)
    String[] basePackages() default {};

    /**
     * Type-safe alternative to {@link #basePackages} for specifying the packages
     * to scan for annotated components. The package of each class specified will be scanned.
     * <p>Consider creating a special no-op marker class or interface in each package
     * that serves no purpose other than being referenced by this attribute.
     *
     * @return The classes of alternative to {@link #basePackages} for specifying the packages
     * to scan for annotated components.
     * @since 1.0.3
     */
    @AliasFor(annotation = ComponentScan.class)
    Class<?>[] basePackageClasses() default {};

    /**
     * Controls the class files eligible for component detection.
     * The default value is {@code ** / *.class}, meaning all {@code .class} files in all
     * directories and subdirectories will be scanned. This attribute allows developers
     * to customize the scan pattern to more precisely control which classes should be
     * recognized as components by the Spring container. For instance, if only classes
     * in specific sub-packages are desired, this pattern can be adjusted.
     *
     * @return The string resource pattern of controls the class files eligible for component
     * detection.
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
     * @since 1.0.3
     */
    @AliasFor(annotation = ComponentScan.class)
    String resourcePattern() default "**/*.class";

    /**
     * The {@link BeanNameGenerator} class to be used for naming detected components within
     * the Spring container.
     *
     * @return the class of {@link BeanNameGenerator}
     * @see org.springframework.context.annotation.AnnotationBeanNameGenerator
     * @see org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator
     * @since 1.0.3
     */
    @AliasFor(annotation = ComponentScan.class)
    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

    /**
     * Specifies a custom {@code SdkProxyFactoryBean} to return a sdk proxy as spring bean.
     *
     * @return the class of {@code SdkProxyFactoryBean}.
     * @since 1.0.3
     */
    Class<? extends SdkProxyFactoryBean> factoryBean() default SdkProxyFactoryBean.class;

    /**
     * <p> This property allows specifying the implementation class that implements {@link ClassFilter}.
     * The specified filter will be applied to the classes marked {@link Sdk} discovered during scanning,
     * allowing fine-grained control over which classes are included or excluded based on custom criteria.
     *
     * <p>By default, this property is set to {@code ClassFilter.class}, which may indicate default or no
     * op filter implementation. Developers can override this default value to provide their own filtering
     * logic as needed.
     *
     * @return the class of {@code ClassFilter}.
     * @since 1.0.3
     */
    Class<? extends ClassFilter> classFilter() default ClassFilter.class;

    /**
     * Defines the regular expression pattern for domain names.
     * <p>
     * Verify the value obtained for {@link Sdk#hostProperty()}.
     *
     * @return A string representing the regular expression pattern for domain names.
     * @since 1.0.3
     */
    String domainPattern() default SdkManagementConfigUtils.DEFAULT_DOMAIN_PATTERN;

    /**
     * Defines the regular expression pattern for IP addresses.
     * <p>
     * Verify the value obtained for {@link Sdk#hostProperty()}.
     *
     * @return A string representing the regular expression pattern for IP addresses.
     * @since 1.0.3
     */
    String ipPattern() default SdkManagementConfigUtils.DEFAULT_IP_PATTERN;
}
