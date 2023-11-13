package top.osjf.assembly.simplified.sdk.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.GenericBeanDefinition;

/**
 * When dynamically registering spring beans, encapsulate them
 * as an annotation based on certain properties that are required
 * and can be reasonably set for this project.
 * <p>Refer to the registration class {@link BeanDefinitionBuilder}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.0.6
 */
@Deprecated
public @interface BeanAttributes {

    /**
     * The unique ID of the spring container bean.
     * <p>If it is empty, it defaults to the full path name
     * of the registered class.
     *
     * @return Register the unique ID of the bean.
     * @see BeanDefinitionHolder#BeanDefinitionHolder(BeanDefinition, String, String[])
     */
    String beanName() default "";

    /**
     * The access aliases for spring container beans can be
     * set multiple times and can be empty.
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
     * Spring's bean injection method selection defaults to
     * assembly by type.
     *
     * @return Returns the injection method.
     * @see GenericBeanDefinition#AUTOWIRE_BY_NAME
     * @see GenericBeanDefinition#AUTOWIRE_BY_TYPE
     * @see GenericBeanDefinition#AUTOWIRE_CONSTRUCTOR
     */
    int autowireMode() default GenericBeanDefinition.AUTOWIRE_BY_TYPE;
}
