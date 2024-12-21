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

package top.osjf.sdk.spring.beans;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.AliasFor;
import top.osjf.sdk.spring.annotation.Sdk;

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
 * annotated with {@link Sdk}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see BeanPropertyUtils
 * @since 1.0.0
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
     * Alias for {@link #name}.
     * <p>Intended to be used when no other attributes are needed, for example:
     * {@code @Bean("customBeanName")}.
     *
     * @see #name
     */
    @AliasFor(annotation = Bean.class)
    String[] value() default {};

    /**
     * The name of this bean, or if several names, a primary bean name plus aliases.
     * <p>If left unspecified, the name of the bean is the name of the annotated method.
     * If specified, the method name is ignored.
     * <p>The bean name and aliases may also be configured via the {@link #value}
     * attribute if no other attributes are declared.
     *
     * @see #value
     */
    @AliasFor(annotation = Bean.class)
    String[] name() default {};

    /**
     * Are dependencies to be injected via convention-based autowiring by name or type?
     * <p>Note that this autowire mode is just about externally driven autowiring based
     * on bean property setter methods by convention, analogous to XML bean definitions.
     * <p>The default mode does allow for annotation-driven autowiring. "no" refers to
     * externally driven autowiring only, not affecting any autowiring demands that the
     * bean class itself expresses through annotations.
     *
     * @see Autowire#BY_NAME
     * @see Autowire#BY_TYPE
     * @deprecated as of 5.1, since {@code @Bean} factory method argument resolution and
     * {@code @Autowired} processing supersede name/type-based bean property injection
     */
    @Deprecated
    @AliasFor(annotation = Bean.class)
    Autowire autowire() default Autowire.NO;

    /**
     * Is this bean a candidate for getting autowired into some other bean?
     * <p>Default is {@code true}; set this to {@code false} for internal delegates
     * that are not meant to get in the way of beans of the same type in other places.
     */
    @AliasFor(annotation = Bean.class)
    boolean autowireCandidate() default true;

    /**
     * The optional name of a method to call on the bean instance during initialization.
     * Not commonly used, given that the method may be called programmatically directly
     * within the body of a Bean-annotated method.
     * <p>The default value is {@code ""}, indicating no init method to be called.
     * <p>
     * <h3>Remind</h3>
     * <p>If you want to use this annotation as a replacement for {@link Bean},
     * then this property will still be effective in its usage with {@link Bean},
     * but if you want to use it together with {@link Sdk}, it is invalid because
     * the proxy class is fixed.
     *
     * @return {@code org.springframework.context.annotation.Bean#initMethod()}
     * @see org.springframework.beans.factory.InitializingBean
     * @see org.springframework.context.ConfigurableApplicationContext#refresh()
     */
    @AliasFor(annotation = Bean.class)
    String initMethod() default "";

    /**
     * The optional name of a method to call on the bean instance upon closing the
     * application context, for example a {@code close()} method on a JDBC
     * {@code DataSource} implementation, or a Hibernate {@code SessionFactory} object.
     * The method must have no arguments but may throw any exception.
     * <p>As a convenience to the user, the container will attempt to infer a destroy
     * method against an object returned from the {@code @Bean} method. For example, given
     * an {@code @Bean} method returning an Apache Commons DBCP {@code BasicDataSource},
     * the container will notice the {@code close()} method available on that object and
     * automatically register it as the {@code destroyMethod}. This 'destroy method
     * inference' is currently limited to detecting only public, no-arg methods named
     * 'close' or 'shutdown'. The method may be declared at any level of the inheritance
     * hierarchy and will be detected regardless of the return type of the {@code @Bean}
     * method (i.e., detection occurs reflectively against the bean instance itself at
     * creation time).
     * <p>To disable destroy method inference for a particular {@code @Bean}, specify an
     * empty string as the value, e.g. {@code @Bean(destroyMethod="")}. Note that the
     * {@link org.springframework.beans.factory.DisposableBean} callback interface will
     * nevertheless get detected and the corresponding destroy method invoked: In other
     * words, {@code destroyMethod=""} only affects custom close/shutdown methods and
     * {@link java.io.Closeable}/{@link java.lang.AutoCloseable} declared close methods.
     * <p>Note: Only invoked on beans whose lifecycle is under the full control of the
     * factory, which is always the case for singletons but not guaranteed for any
     * other scope.
     * <p>
     * <h3>Remind</h3>
     * <p>If you want to use this annotation as a replacement for {@link Bean},
     * then this property will still be effective in its usage with {@link Bean},
     * but if you want to use it together with {@link Sdk}, it is invalid because
     * the proxy class is fixed.
     *
     * @return {@code org.springframework.context.annotation.Bean#destroyMethod()}
     * @see org.springframework.beans.factory.DisposableBean
     * @see org.springframework.context.ConfigurableApplicationContext#close()
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
