package top.osjf.assembly.simplified.support;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Dynamically construct annotation identifiers for {@link BeanDefinition},
 * which include some essential attributes related to spring beans and also
 * provide some default values.
 *
 * <p>They inherit all related attributes of {@link Bean} and can be used
 * directly as {@link Bean}.
 *
 * <p>The basic purpose is to serve as a secondary attribute for special
 * bean registration services, such as setting properties for proxy beans
 * annotated with {@link top.osjf.assembly.simplified.sdk.annotation.Sdk}.
 *
 * @see BeanPropertyUtils
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Bean
public @interface BeanProperty {

    /**
     * Constant for the default scope name: {@code ""}, equivalent to singleton
     * status unless overridden from a parent bean definition (if applicable).
     */
    String SCOPE_DEFAULT = "";

    /**
     * Role hint indicating that a {@code BeanDefinition} is a major part
     * of the application. Typically corresponds to a user-defined bean.
     */
    int ROLE_APPLICATION = 0;

    /**
     * @return {@code org.springframework.context.annotation.Bean#value()}
     */
    @AliasFor(annotation = Bean.class)
    String[] value() default {};

    /**
     * @return {@code org.springframework.context.annotation.Bean#name()}
     */
    @AliasFor(annotation = Bean.class)
    String[] name() default {};

    /**
     * @return {@code org.springframework.context.annotation.Bean#autowire()}
     * @deprecated copy from Bean#autowire() explain as of 5.1, since {@code @Bean} factory method
     * argument resolution and {@code @Autowired} processing supersede name/type-based bean property
     * injection.
     */
    @Deprecated
    @AliasFor(annotation = Bean.class)
    Autowire autowire() default Autowire.NO;

    /**
     * @return {@code org.springframework.context.annotation.Bean#autowireCandidate()}
     */
    @AliasFor(annotation = Bean.class)
    boolean autowireCandidate() default true;

    /**
     * @return {@code org.springframework.context.annotation.Bean#initMethod()}
     */
    @AliasFor(annotation = Bean.class)
    String initMethod() default "";

    /**
     * @return {@code org.springframework.context.annotation.Bean#destroyMethod()}
     */
    @AliasFor(annotation = Bean.class)
    String destroyMethod() default AbstractBeanDefinition.INFER_METHOD;

    /**
     * The scope of the spring container bean.
     * The default is {@link AbstractBeanDefinition#SCOPE_DEFAULT},Equivalent to
     * {@link AbstractBeanDefinition#SCOPE_SINGLETON}
     *
     * @return Returns the scope range,defaults to singleton.
     * @see BeanDefinitionBuilder#setScope(String)
     * @see ConfigurableBeanFactory#SCOPE_PROTOTYPE
     * @see ConfigurableBeanFactory#SCOPE_SINGLETON
     * @see org.springframework.beans.factory.config.Scope
     * @see org.springframework.context.annotation.Scope
     * @see org.springframework.web.context.annotation.RequestScope
     * @see org.springframework.web.context.annotation.SessionScope
     * @see org.springframework.web.context.annotation.ApplicationScope
     */
    String scope() default SCOPE_DEFAULT;

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
     * @see org.springframework.context.annotation.Role
     * @see BeanDefinition#ROLE_APPLICATION
     * @see BeanDefinition#ROLE_INFRASTRUCTURE
     * @see BeanDefinition#ROLE_SUPPORT
     */
    int role() default ROLE_APPLICATION;

    /**
     * Set whether this bean should be lazily initialized.
     * <p>If {@code false}, the bean will get instantiated on startup by bean
     * factories that perform eager initialization of singletons.
     * <p>The explanation comes from {@link AbstractBeanDefinition#setLazyInit(boolean)}.
     *
     * @return A bean decide whether to lazily load,defaults not.
     * @see org.springframework.context.annotation.Lazy
     */
    boolean lazyInit() default false;

    /**
     * Set a human-readable description of this bean definition.
     * <p>The explanation comes from {@link AbstractBeanDefinition#setDescription(String)}.
     *
     * @return A bean with its description content,defaults blank.
     * @see org.springframework.context.annotation.Description
     */
    String description() default "";
}
