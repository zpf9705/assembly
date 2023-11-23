package top.osjf.assembly.simplified.sdk.annotation;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * The combination of {@link EnableSdkProxyRegister} and {@link ComponentScan}
 * uses annotations with the aim of automatically injecting the SDK interface
 * into the implementation class and displaying the effect of the injection source.
 * <p><u><strong>For example code:</strong></u>
 * <pre>
 *     &#064;Configuration(proxyBeanMethods = false)
 *     &#064;Role(BeanDefinition.ROLE_INFRASTRUCTURE)
 *     public class InterfaceAutoConfiguration {
 *
 *     &#064;Configuration(proxyBeanMethods = false)
 *     &#064;Role(BeanDefinition.ROLE_INFRASTRUCTURE)
 *     &#064;EnableSdkClients(scanBasePackages = InterfaceConstance.SDK_SCAN_PATH,
 *             registerBasePackages = InterfaceConstance.SDK_SCAN_PATH)
 *     public static class SdkConfiguration {
 *     }
 * }
 * </pre>
 * <p>In principle, the paths of two attributes must correspond one by one to
 * achieve the above functions, so it is important to note that they should not
 * be used interchangeably.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.0.9
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan
@EnableSdkProxyRegister
public @interface EnableSdkClients {

    /**
     * Redirect to the attribute of {@link EnableSdkProxyRegister#basePackages()}.
     * <p>Entering this attribute will perform annotation assembly
     * for {@link EnableSdkProxyRegister}.
     * <p>For detailed usage of its attributes, please refer to
     * {@link EnableSdkProxyRegister#basePackages()}.
     *
     * @return Path array give Sdk scan.
     */
    @AliasFor(annotation = EnableSdkProxyRegister.class, attribute = "basePackages")
    String[] registerBasePackages() default {};

    /**
     * Redirect this property to {@link ComponentScan#basePackages()},
     * configure this property to display the injection source of the
     * bean during code editing, improve user experience.
     * <p>If this property is not configured, the injection source
     * will not be displayed, but the implementation class has already
     * been injected.
     *
     * @return Sdk interface scan array path.
     */
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};
}
