package top.osjf.assembly.simplified.sdk.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import top.osjf.assembly.simplified.sdk.proxy.SdkProxyBean;
import top.osjf.assembly.simplified.support.BeanProperty;
import top.osjf.assembly.simplified.support.ProxyModel;

import java.lang.annotation.*;

/**
 * Dynamic injection of Spring container annotations, which
 * only need to be annotated on the interface class that needs
 * dynamic injection, can be scanned by {@link SdkProxyBeanRegister}
 * and automatically create proxy objects based on annotation properties.
 *
 * <p>The class that wears this annotation can be injected and
 * used in the container environment of the spring boot project
 * through {@link org.springframework.beans.factory.annotation.Autowired},
 * constructors, or set methods.
 *
 * <p>Annotate properties based on viewing {@link BeanDefinitionBuilder},
 * and here only the names, aliases, and injection modes of beans are
 * listed, as mentioned above regarding the bean injection properties of Spring.
 *
 * <p>Added some attributes related to {@link BeanDefinitionBuilder} from
 * version {@code 2.0.9}, and the remaining attributes that were not added
 * will not be added, making them useless for this component.
 *
 * <p>If you want to provide visible proxy classes{@link #proxyBeanType()},
 * then you need to understand {@link ImportBeanDefinitionRegistrar} registration
 * {@link BeanDefinition} and the usage of injection models.
 * <p>Please refer to the introduction of {@link BeanProperty#autowire()}.
 *
 * @author zpf
 * @since 1.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
//only support ui and no significance
//@since 2.0.7
@Component
public @interface Sdk {

    /**
     * Alias for {@link #hostProperty()}.
     * <p>Intended to be used when no other attributes are needed, for example:
     * {@code @Sdk("${custom.setting}")}.
     *
     * @see #hostProperty()
     * @since 2.2.5
     */
    @AliasFor("hostProperty")
    String value() default "";

    /**
     * The host domain name that can be configured.
     * <p>If not only the host domain name is variable, but also
     * the URL information needs to be variable, please configure
     * it together into the spring configuration file.
     * <p>The environment {@link org.springframework.core.env.Environment}
     * will be used to dynamically obtain it, and it will be included
     * in the bean's properties during dynamic creation.
     * <p>Regarding the format of obtaining, there are currently
     * two supported formats: el expressions and regular dot separated (xxx. xxx).
     *
     * @return Host name configuration name, cannot be {@literal null}.
     */
    @AliasFor("value")
    String hostProperty() default "";

    //The relevant bean properties of the spring proxy object created by oneself have default
    // values that can be selected according to needs.
    //Only the properties that may be used by the SDK proxy bean are listed.

    /**
     * Manually defining a class that can intuitively handle the {@link BeanDefinition}
     * lifecycle requires passing in the corresponding class object.
     * <p>If you have defined this class, you can intuitively use
     * {@link BeanProperty#autowire()} to define the assembly pattern of the
     * beans you introduce, and define the initialization and destruction
     * methods {@link BeanProperty#initMethod()} and {@link BeanProperty#destroyMethod()}
     * for {@link BeanDefinition}.
     *
     * <p>For clearer meaning, it was renamed 'proxyBeanType',represents
     * the type of proxy class.
     *
     * <p>Select the specific type of proxy object to generate based on {@link #model()},
     * if you provide this attribute, I hope you pay attention to the two construction
     * methods of {@link SdkProxyBean}.
     *
     * @return A proxy class , defaults to {@link SdkProxyBean}.
     * @since 2.0.9
     */
    Class<? extends SdkProxyBean> proxyBeanType() default SdkProxyBean.class;

    /**
     * When selecting the type of proxy object to generate for the
     * SDK tag target, you need to pay attention to whether your tag
     * type is an interface, abstract class, or regular type.
     *
     * <p>If it is an interface, use the default type.
     * <p>If it is a class level, then you may need to consider using
     * {@link ProxyModel#SPRING_CJ_LIB} to create a proxy class.
     *
     * @return The basic technical model for creating proxy classes.
     * @since 2.2.5
     */
    ProxyModel model() default ProxyModel.JDK;

    /**
     * When creating and modifying SDK proxy classes, Annotate
     * the relevant properties of the bean, which can be referred to in
     * {@link org.springframework.context.annotation.Bean} and
     * {@link BeanDefinition}.
     *
     * @return The bean properties of the SDK proxy class.
     * @since 2.2.5
     */
    BeanProperty sdkProxyBeanProperty() default @BeanProperty;
}
