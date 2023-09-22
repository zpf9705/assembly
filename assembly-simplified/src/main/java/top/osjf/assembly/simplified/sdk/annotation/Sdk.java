package top.osjf.assembly.simplified.sdk.annotation;

import java.lang.annotation.*;

/**
 * Dynamic injection of Spring container annotations, which only need to be annotated on the interface class
 * that needs dynamic injection, can be scanned by {@link SdkProxyBeanDefinitionRegister} and automatically
 * create proxy objects based on annotation properties .
 *
 * <p>The class that wears this annotation can be injected and used in the container environment of the spring boot
 * project through {@link org.springframework.beans.factory.annotation.Autowired}, constructors, or set methods
 *
 * @author zpf
 * @since 1.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sdk {

    /**
     * The alias of the spring container bean, which can be used for bean lookup
     * if provided, can also be left blank. The default method for creating SDK beans
     * is to scan the full class name of the interface.
     *
     * @return can be {@literal null}.
     */
    String alisa() default "";

    /**
     * The host domain name that can be configured.
     *
     * <p>If not only the host domain name is variable, but also the URL information
     * needs to be variable, please configure it together into the spring configuration file.
     *
     * <p>The environment {@link org.springframework.core.env.Environment} will be used to
     * dynamically obtain it, and it will be included in the bean's properties during dynamic creation.
     *
     * <p>Regarding the format of obtaining, there are currently two supported formats:
     * el expressions and regular dot separated (xxx. xxx).</p>
     */
    String hostProperty();
}
