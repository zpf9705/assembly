package top.osjf.assembly.simplified.sdk.proxy;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The registration tool class for SDK proxy beans.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public abstract class SdkProxyBeanUtils {

    /***A collection of regular bean scopes.*/
    public static final List<String> ROUTINE_SCOPES = Stream.of(BeanDefinition.SCOPE_SINGLETON,
            BeanDefinition.SCOPE_PROTOTYPE, AbstractBeanDefinition.SCOPE_DEFAULT).collect(Collectors.toList());

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
}