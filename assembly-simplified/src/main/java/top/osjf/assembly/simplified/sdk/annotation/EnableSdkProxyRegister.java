package top.osjf.assembly.simplified.sdk.annotation;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * This annotation is mainly annotated on the class where the Spring injection class annotation is located, which
 * can implement the interface class with automatic injection annotation {@link Sdk} and automatically create
 * the implementation class,it mainly relies on {@link SdkProxyBeanDefinitionProcessor}.
 * <p>
 *
 * @see top.osjf.assembly.simplified.support.AbstractProxyBeanInjectSupport
 * @see org.springframework.context.annotation.ImportBeanDefinitionRegistrar
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.core.env.Environment
 * @author zpf
 * @since 1.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({SdkProxyBeanDefinitionProcessor.class})
public @interface EnableSdkProxyRegister {

    /**
     * Carrying the path where the {@link Sdk} class is located.
     * <p>
     * If it is null, the default is to use springboot to start
     * the package path where the main class is located.
     *
     * @return alias for {{@link #basePackages()}}
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * His value shifts to {@link #value()}, consistent with it.
     * <p>
     * If it is null, the default is to use springboot to start
     * the package path where the main class is located.
     *
     * @return alias for {{@link #value()}}
     */
    @AliasFor("value")
    String[] basePackages() default {};
}
