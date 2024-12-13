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

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * This annotation is mainly annotated on the class where the
 * Spring injection class annotation is located, which can implement
 * the interface class with automatic injection annotation {@link Sdk}
 * and automatically create the implementation class, it mainly relies
 * on {@link SdkProxyBeanRegister}.
 * <p>Redirect {@link #value()} and {@link #basePackages()}
 * to the {@link ComponentScan} annotation.
 * <p><u><strong>For example code:</strong></u>
 * <pre>
 *     &#064;Configuration(proxyBeanMethods = false)
 *     &#064;Role(BeanDefinition.ROLE_INFRASTRUCTURE)
 *     public class AutoConfiguration {
 *
 *     &#064;Configuration(proxyBeanMethods = false)
 *     &#064;Role(BeanDefinition.ROLE_INFRASTRUCTURE)
 *     &#064;EnableSdkProxyRegister(Constance.SDK_SCAN_PATH)
 *     public static class Configuration {
 *     }
 * }
 * </pre>
 * <p>Please refer to its attribute explanation for specific explanations
 * and {@link #value()} and {@link #basePackages()}still maintain bidirectional
 * binding, which is specifically implemented in {@link ComponentScan}.
 * <p> <strong> Warn UI Support </strong>
 * <p>Please be cautious when merging {@code org.springframework.boot.autoconfigure
 * .SpringBootApplication}, as it is used together with {@link ComponentScan}.
 * <p>The former is to scan the beans in the specified path of this project in
 * seconds, while this annotation is to provide injected UI support for automatically
 * registering the SDK interface of the implementation class.
 * <p>The former has a higher priority and greater effect.
 * <p>Therefore, it is recommended to use this annotation and create a separate
 * configuration class.
 * <p><strong>If is forcibly merged, UI support will be invalidated.</strong>
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 * @see top.osjf.sdk.spring.beans.AbstractImportBeanDefinitionRegistrar
 * @see top.osjf.sdk.spring.beans.AnnotationTypeScanningCandidateImportBeanDefinitionRegistrar
 * @see SdkProxyBeanRegister
 * @see ComponentScan
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SdkProxyBeanRegister.class,
        SdkProxyBeanRegister.InternalConfiguration.class})
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
}
