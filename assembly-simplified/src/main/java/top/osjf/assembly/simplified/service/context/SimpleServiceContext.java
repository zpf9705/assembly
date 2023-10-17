package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.Ordered;
import top.osjf.assembly.simplified.service.ServiceContextUtils;
import top.osjf.assembly.simplified.service.annotation.ServiceCollection;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;

/**
 * Support implementation class for service context {@link ServiceContext}.
 *
 * <p>Compared to {@link ClassesServiceContext}, the loading process is simpler and does
 * not require a fixed package path.
 *
 * <p>The service loading process is as follows:
 * <p>The creation process of monitoring container beans depends on {@link BeanPostProcessor}.
 * <p>Firstly, check whether a single bean meets the service collection requirements.
 * <p>If it does not meet the requirements, then search for the service collection loading
 * of its interface class and parent class, and finally collect it into a thread safe
 * collection container {@link #getContextMap()}.
 *
 * <p>The basic requirement for service collection is to wear an annotated
 * {@link ServiceCollection} interface or abstract class, and satisfy such a
 * bean to be collected by the service.
 *
 * <p>The collection of this service configuration can only rely on
 * <pre>&#064;EnableServiceCollection2@type=SIMPLE.</pre>
 *
 * @author zpf
 * @since 2.0.6
 */
public class SimpleServiceContext extends AbstractServiceContext implements MergedBeanDefinitionPostProcessor, Ordered {

    public SimpleServiceContext() {
        //Manually add to the post processor collection of the bean.
        getConfigurableApplicationContext().getBeanFactory().addBeanPostProcessor(this);
    }

    @Override
    public void postProcessMergedBeanDefinition(@NotNull RootBeanDefinition beanDefinition, @NotNull Class<?> beanType,
                                                @NotNull String beanName) {
    }

    @CanNull
    @Override
    public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        preServiceCollection(bean, beanName);
        return bean;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE + 17;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        super.onApplicationEvent(event);
    }

    @Override
    public void close() {
        super.close();
    }

    private void preServiceCollection(Object bean, String beanName) {
        ApplicationContext context = getApplicationContext();
        if (context == null){
            context = getConfigurableApplicationContext();
        }
        if (ServiceContextUtils.isCollectionService(beanName, context.getId())) {
            getContextMap().putIfAbsent(beanName, bean);
            String[] aliases = getBeanDefinitionRegistry().getAliases(beanName);
            if (ArrayUtils.isNotEmpty(aliases)) {
                for (String alias : aliases) {
                    getContextMap().putIfAbsent(alias, bean);
                }
            }
        }
    }
}
