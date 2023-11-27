package top.osjf.assembly.simplified.sdk.annotation;

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
 * <p>Since 2.1.0,redirect {@link #value()} and {@link #basePackages()}
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
 *
 * @author zpf
 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 * @see top.osjf.assembly.simplified.support.AbstractImportBeanDefinitionRegistrar
 * @see top.osjf.assembly.simplified.support.BeanDefinitionRegisterBeforeRefresh
 * @see SdkProxyBeanRegister
 * @see ComponentScan
 * @since 1.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SdkProxyBeanRegister.class})
@ComponentScan
public @interface EnableSdkProxyRegister {

    /**
     * Carrying the path where the {@link Sdk} class is located.
     * <p>If it is null, the default is to use springboot to start
     * the package path where the main class is located.
     * <strong>_ _ _ _ Since 2.1.0 _ _ _ _</strong>
     * <p>Redirect this property to {@link ComponentScan#value()},
     * configure this property to display the injection source of the
     * bean during code editing, improve user experience.
     * <p>If this property is not configured, the injection source
     * will not be displayed, but the implementation class has already
     * been injected.
     *
     * @return Alias for {{@link #basePackages()}} for {@link ComponentScan}.
     */
    // update since 2.1.0
    @AliasFor(annotation = ComponentScan.class)
    String[] value() default {};

    /**
     * His value shifts to {@link #value()}, consistent with it.
     * <p>If it is null, the default is to use springboot to start
     * the package path where the main class is located.
     * <strong>_ _ _ _ Since 2.1.0 _ _ _ _</strong>
     * <p>Redirect this property to {@link ComponentScan#basePackages()},
     * configure this property to display the injection source of the
     * bean during code editing, improve user experience.
     * <p>If this property is not configured, the injection source
     * will not be displayed, but the implementation class has already
     * been injected.
     *
     * @return Alias for {{@link #value()}} for {@link ComponentScan}.
     */
    // update since 2.1.0
    @AliasFor(annotation = ComponentScan.class)
    String[] basePackages() default {};
}
