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

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;
import top.osjf.sdk.proxy.ProxyModel;
import top.osjf.sdk.spring.beans.BeanProperty;

import java.lang.annotation.*;

/**
 * Dynamic injection of Spring container annotations, which
 * only need to be annotated on the interface class that needs
 * dynamic injection, can be scanned by {@link SdkBeanDefinitionRegistrar}
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
 * <p>Added some attributes related to {@link BeanDefinitionBuilder}
 * and the remaining attributes that were not added will not be added,
 * making them useless for this component.
 *
 * <p>It is recommended to use interfaces for proxy, as proxy for abstract
 * classes can bring many problems.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component //only support ui and no significance
public @interface Sdk {

    /**
     * Alias for {@link #hostProperty()}.
     * <p>Intended to be used when no other attributes are needed, for example:
     * {@code @Sdk("${custom.setting}")}.
     *
     * @return {@link #hostProperty()}
     * @see #hostProperty()
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

    /**
     * When selecting the type of proxy object to generate for the
     * SDK tag target, you need to pay attention to whether your tag
     * type is an interface, abstract class, or regular type.
     *
     * <p>If it is an interface, use the default type.
     * <p>If it is a class level, then you may need to consider using
     * {@link ProxyModel#SPRING_CGLIB} to create a proxy class.
     *
     * @return The basic technical model for creating proxy classes.
     */
    ProxyModel model() default ProxyModel.JDK;

    /**
     * When creating and modifying SDK proxy classes, Annotate
     * the relevant properties of the bean, which can be referred to in
     * {@link org.springframework.context.annotation.Bean} and
     * {@link BeanDefinition}.
     *
     * <p>If you do not provide the {@link BeanProperty#name()} value
     * for this annotation method, the naming of the proxy bean will
     * be based on the fully qualified class name of the proxy type.
     *
     * @return Annotation level indication configuration items when
     * generating proxy beans.
     */
    BeanProperty property() default @BeanProperty;

    /**
     * Instruct the proxy type to create a proxy object when activated in
     * a specified environment, and compare {@code Profile#value()} with
     * {@code Environment#getActiveProfiles()}. Here, Spring's environment
     * condition annotation is used to provide developers with a more direct
     * understanding of usage.
     *
     * @return The specified environment configuration in annotation form
     * is invalid when it is {@code null}.
     * @since 1.0.3
     */
    Profile profile() default @Profile({});
}
