package top.osjf.assembly.simplified.sdk.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.stereotype.Component;
import top.osjf.assembly.simplified.sdk.AbstractSdkProxyInvoker;
import top.osjf.assembly.simplified.sdk.SdkProxyBeanDefinition;

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
 * <p>If you want to provide visible proxy classes{@link #beanDefinitionType()},
 * then you need to understand {@link ImportBeanDefinitionRegistrar} registration
 * {@link BeanDefinition} and the usage of injection models.
 * <p>Please refer to the introduction of {@link #autowireMode()}.
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
    String hostProperty();

    //The relevant bean properties of the spring proxy object created by oneself have default
    // values that can be selected according to needs.
    //Only the properties that may be used by the SDK proxy bean are listed.

    /**
     * Manually defining a class that can intuitively handle the {@link BeanDefinition}
     * lifecycle requires passing in the corresponding class object.
     * <p>If you have defined this class, you can intuitively use
     * {@link #autowireMode()} to define the assembly pattern of the
     * beans you introduce, and define the initialization and destruction
     * methods {@link #initMethod()} and {@link #destroyMethod()} for {@link BeanDefinition}.
     *
     * <p>For clarity of meaning, the name ‘beanDefinitionType’
     * was changed to version 2.2.5.
     *
     * @return A beanDefinition class , defaults to {@link SdkProxyBeanDefinition}.
     * @since 2.0.9
     */
    Class<? extends AbstractSdkProxyInvoker> beanDefinitionType() default SdkProxyBeanDefinition.class;

    /**
     * The unique ID of the spring container bean.
     * <p>If it is empty, it defaults to the full path name
     * of the registered class.
     *
     * @return Register the unique ID of the bean,defaults to {@link Class#getName()}.
     * @see BeanDefinitionHolder#BeanDefinitionHolder(BeanDefinition, String, String[])
     */
    String beanName() default "";

    /**
     * The access aliases for spring container beans can be
     * set multiple times and can be empty.
     *
     * @return Register the alisa array of the bean,defaults empty.
     * @see BeanDefinitionHolder#BeanDefinitionHolder(BeanDefinition, String, String[])
     */
    String[] alisa() default {};

    /**
     * The scope of the spring container bean.
     * The default is {@link AbstractBeanDefinition#SCOPE_DEFAULT},Equivalent to
     * {@link AbstractBeanDefinition#SCOPE_SINGLETON}
     *
     * @return Returns the scope range,defaults to singleton.
     * @see BeanDefinitionBuilder#setScope(String)
     * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
     * @see ConfigurableBeanFactory#SCOPE_SINGLETON
     */
    String scope() default AbstractBeanDefinition.SCOPE_DEFAULT;

    /**
     * Define the way the bean itself injects into other container classes.
     *
     * <p><strong>_____________________________________________</strong>
     * <p><strong>If you use {@link EnableSdkProxyRegister}</strong>
     * <p>You need to pay attention to the following statements.</p>
     * <p>It should be noted that if you choose the default {@link GenericBeanDefinition#AUTOWIRE_NO},
     * as the proxy implementation class of sdk is created through {@link ImportBeanDefinitionRegistrar},
     * Spring has not yet started creating beans in this registration stage, and since
     * beans have not been created, it will not parse the {@link Autowired}  or
     * {@link javax.annotation.Resource} annotation.
     * <p><strong>Therefore, if you provide a {@link #beanDefinitionType()} class,
     * then in this class do not use {@link Autowired}  or
     * {@link javax.annotation.Resource} annotation for bean injection.</strong>
     * <p>Of course, if you use {@link GenericBeanDefinition#AUTOWIRE_CONSTRUCTOR},
     * please ensure that your injection class has a default constructor.
     * <p><strong>_____________________________________________</strong>
     *
     * @return Returns the injection method,defaults to do not automatically assemble injection.
     * @see GenericBeanDefinition#AUTOWIRE_NO
     * @see GenericBeanDefinition#AUTOWIRE_BY_NAME
     * @see GenericBeanDefinition#AUTOWIRE_BY_TYPE
     * @see GenericBeanDefinition#AUTOWIRE_CONSTRUCTOR
     */
    @Deprecated
    int autowireMode() default GenericBeanDefinition.AUTOWIRE_NO;

    /**
     * Set whether this bean is a candidate for getting autowired into some other bean.
     * <p>Note that this flag is designed to only affect type-based autowiring.
     * It does not affect explicit references by name, which will get resolved even
     * if the specified bean is not marked as an autowire candidate. As a consequence,
     * autowiring by name will nevertheless inject a bean if the name matches.
     *
     * @return Return whether this bean is a candidate for getting autowired into some
     * other bean.
     * @see AbstractBeanDefinition#AUTOWIRE_BY_TYPE
     * @see AbstractBeanDefinition#AUTOWIRE_BY_NAME
     */
    boolean autowireCandidate() default true;

    /**
     * Set the name of the initializer method.
     * <p>The default is {@code null} in which case there is no initializer method.
     * <p>The explanation comes from {@link AbstractBeanDefinition#setInitMethodName(String)}}.
     *
     * @return A bean lifeStyle with init method name,defaults blank.
     * @since 2.0.9
     */
    String initMethod() default "";

    /**
     * Set the name of the destroy method.
     * <p>The default is {@code null} in which case there is no destroy method.
     * <p>The explanation comes from {@link AbstractBeanDefinition#setDestroyMethodName(String)}}.
     *
     * @return A bean lifeStyle with destroy method name,defaults blank.
     * @since 2.0.9
     */
    String destroyMethod() default "";

    /**
     * Set the role hint for this {@code BeanDefinition}.
     * <p>The explanation comes from {@link AbstractBeanDefinition#setRole(int)}}.
     * <p><strong>{@code role = 0}</strong>
     * <p>Role hint indicating that a {@code BeanDefinition} is a major part
     * of the application. Typically corresponds to a user-defined bean.
     * <p><strong>{@code role = 1}</strong>
     * <p>Role hint indicating that a {@code BeanDefinition} is a supporting
     * part of some larger configuration, typically an outer
     * {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
     * {@code SUPPORT} beans are considered important enough to be aware
     * of when looking more closely at a particular
     * {@link org.springframework.beans.factory.parsing.ComponentDefinition},
     * but not when looking at the overall configuration of an application.
     * <p><strong>{@code role = 2}</strong>
     * <p>Role hint indicating that a {@code BeanDefinition} is providing an
     * entirely background role and has no relevance to the end-user. This hint is
     * used when registering beans that are completely part of the internal workings
     * of a {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
     * <p>The explanation comes from {@link BeanDefinition}}.
     * <p>Generally, the proxy object automatically generated by SDK is user-defined
     * on the application side, that is, <strong>{@code role = 0}</strong>, which can
     * be set according to one's own needs.
     *
     * @return A bean lifeStyle role type,defaults user-yourself define.
     * @since 2.0.9
     */
    int role() default BeanDefinition.ROLE_APPLICATION;

    /**
     * Set whether this bean should be lazily initialized.
     * <p>If {@code false}, the bean will get instantiated on startup by bean
     * factories that perform eager initialization of singletons.
     * <p>The explanation comes from {@link AbstractBeanDefinition#setLazyInit(boolean)}.
     *
     * @return A bean decide whether to lazily load,defaults not.
     * @since 2.0.9
     */
    boolean lazyInit() default false;

    /**
     * Set a human-readable description of this bean definition.
     * <p>The explanation comes from {@link AbstractBeanDefinition#setDescription(String)}.
     *
     * @return A bean with its description content,defaults blank.
     * @since 2.0.9
     */
    String description() default "";
}
