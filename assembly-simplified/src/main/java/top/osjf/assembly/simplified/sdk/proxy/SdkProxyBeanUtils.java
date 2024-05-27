package top.osjf.assembly.simplified.sdk.proxy;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import top.osjf.assembly.util.lang.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The registration tool class for SDK proxy beans.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public abstract class SdkProxyBeanUtils {

    /***A collection of regular bean scopes.*/
    public static final List<String> ROUTINE_SCOPES = Stream.of(BeanDefinition.SCOPE_SINGLETON,
            BeanDefinition.SCOPE_PROTOTYPE, AbstractBeanDefinition.SCOPE_DEFAULT).collect(Collectors.toList());

    /*** The name suffix of the SDK proxy bean.*/
    public static final String BEAN_NAME_SUFFIX = "@sdk.proxy.bean";

    /**
     * When dynamically registering a bean, it is classified and registered based on
     * the scope set by the registered bean. If the current scope is within the
     * {@link #ROUTINE_SCOPES} range, annotation creation will be performed for
     * {@link BeanDefinitionHolder}. If it is a special scope outside of this range,
     * a proxy bean will be created first to meet the special scope.
     *
     * @param builder                                     The builder of {@link BeanDefinitionBuilder}.
     * @param scope                                       The scope of the settings.
     * @param registry                                    the bean definition registry.
     * @param noRoutineScopeBeanDefinitionBuilderConsumer When the scope does not satisfy {@link #ROUTINE_SCOPES},
     *                                                    make some special consumption actions for
     *                                                    {@link BeanDefinitionBuilder}.
     * @param beanName                                    The name of the proxy bean.
     * @param alisa                                       The alias set of proxy beans.
     * @return The information registration body of {@link BeanDefinition}.
     * @see ScopedProxyUtils#createScopedProxy(BeanDefinitionHolder, BeanDefinitionRegistry, boolean)
     */
    public static BeanDefinitionHolder createBeanDefinitionHolderDistinguishScope(BeanDefinitionBuilder builder,
                                                                                  BeanDefinitionRegistry registry,
                                                                                  String scope,
                                                                                  Consumer<BeanDefinitionBuilder>
                                                                                          noRoutineScopeBeanDefinitionBuilderConsumer,
                                                                                  String beanName,
                                                                                  String[] alisa) {
        if (ROUTINE_SCOPES.stream().anyMatch(sc -> Objects.equals(sc, scope))) {
            return new BeanDefinitionHolder(builder.getBeanDefinition(), beanName, alisa);
        }
        noRoutineScopeBeanDefinitionBuilderConsumer.accept(builder);
        return ScopedProxyUtils.createScopedProxy(new BeanDefinitionHolder(builder.getBeanDefinition(),
                beanName, alisa), registry, true);
    }

    /**
     * When no bean name is provided for the SDK proxy bean,
     * this method is used as an alternative.
     *
     * @param beanName  The defined bean name.
     * @param className The fully qualified name of the target class.
     * @return The name of the proxy bean.
     */
    public static String getTargetBeanName(String beanName, String className) {
        if (StringUtils.isAllBlank(beanName, className)) {
            throw new IllegalArgumentException("When 'beanName' is not provided, please ensure that " +
                    "'className' is built as the default name and cannot be empty.");
        }
        if (StringUtils.isNotBlank(beanName)) {
            return beanName + BEAN_NAME_SUFFIX;
        }
        return className + BEAN_NAME_SUFFIX;
    }
}