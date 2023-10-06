package top.osjf.assembly.simplified.sdk.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.lang.annotation.*;

/**
 * Dynamic injection of Spring container annotations, which only need to be annotated on the
 * interface class that needs dynamic injection, can be scanned by {@link SdkProxyBeanDefinitionProcessor}
 * and automatically create proxy objects based on annotation properties.
 *
 * <p>The class that wears this annotation can be injected and used in the container environment
 * of the spring boot project through {@link org.springframework.beans.factory.annotation.Autowired},
 * constructors, or set methods
 *
 * <p>Annotate properties based on viewing {@link BeanDefinitionBuilder}, and here only the
 * names, aliases, and injection modes of beans are listed, as mentioned above regarding the
 * bean injection properties of Spring.
 *
 * @author zpf
 * @since 1.1.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Sdk {

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
     *
     * @return Host configuration.
     */
    String hostProperty();

    /**
     * When dynamically registering spring beans, encapsulate them as an annotation based
     * on certain properties that are required and can be reasonably set for this project.
     *
     * <p>Refer to the registration class {@link BeanDefinitionBuilder}.
     *
     * @return Annotation attribute encapsulation.
     */
    BeanAttributes attributes();


    @interface BeanAttributes {

        /**
         * The unique ID of the spring container bean. If it is empty, it defaults to
         * the full path name of the registered class.
         *
         * @return Register the unique ID of the bean.
         * @see BeanDefinitionHolder#BeanDefinitionHolder(BeanDefinition, String, String[])
         */
        String beanName() default "";

        /**
         * The access aliases for spring container beans can be set multiple times and can be empty.
         *
         * @return Register the alisa array of the bean.
         * @see BeanDefinitionHolder#BeanDefinitionHolder(BeanDefinition, String, String[])
         */
        String[] alisa() default {};

        /**
         * The scope of the spring container bean.
         * The default is {@link AbstractBeanDefinition#SCOPE_DEFAULT},Equivalent to
         * {@link AbstractBeanDefinition#SCOPE_SINGLETON}
         *
         * @return Returns the scope range.
         * @see BeanDefinitionBuilder#setScope(String)
         * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
         * @see ConfigurableBeanFactory#SCOPE_SINGLETON
         */
        String scope() default AbstractBeanDefinition.SCOPE_DEFAULT;

        /**
         * Spring's bean injection method selection defaults to assembly by type.
         *
         * @return Returns the injection method.
         * @see GenericBeanDefinition#AUTOWIRE_BY_NAME
         * @see GenericBeanDefinition#AUTOWIRE_BY_TYPE
         * @see GenericBeanDefinition#AUTOWIRE_CONSTRUCTOR
         */
        int autowireMode() default GenericBeanDefinition.AUTOWIRE_BY_TYPE;
    }
}
