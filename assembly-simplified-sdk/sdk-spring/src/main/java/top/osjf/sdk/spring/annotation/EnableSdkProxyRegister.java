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
import org.springframework.stereotype.Component;

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
}
