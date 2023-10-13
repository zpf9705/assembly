package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import top.osjf.assembly.simplified.service.annotation.ServiceCollection;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.data.ClassMap;
import top.osjf.assembly.util.lang.CollectionUtils;
import top.osjf.assembly.util.logger.Console;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class SimpleServiceContext extends AbstractServiceContext implements BeanPostProcessor, Ordered {

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

    public void addBeanPostProcessor(){
        getConfigurableApplicationContext()
                .getBeanFactory()
                .addBeanPostProcessor(this);
    }

    private void preServiceCollection(Object bean, String beanName) {
        ClassMap<String, Object> contextMap = getContextMap();
        // loaded direct return
        if (contextMap.containsKey(beanName)) {
            return;
        }
        List<Class<?>> classes = new ArrayList<>();
        Class<?> dela = bean.getClass();
        //If this bean directly contains service collection annotations and satisfies
        // the requirements of an interface or abstract class, then service collection
        // is directly carried out.
        if (ServiceContextUtils.isServiceCollectionParent(dela)) {
            classes.add(dela);
        } else {
            //Collect the interface collection and inheritance classes of this bean,
            // and search for service collection identification annotations.
            classes = ServiceContextUtils.getFilterServices(dela);
            if (CollectionUtils.isEmpty(classes)) return;
            Console.info("Service collection : {}", classes);
            for (Class<?> clazz : classes) {
                Map<String, ?> beansOfType;
                try {
                    //The collection of final services still relies on contextual objects.
                    beansOfType = getApplicationContext().getBeansOfType(clazz);
                } catch (BeansException e) {
                    return;
                }
                beansOfType.forEach(contextMap::putIfAbsent);
            }
        }
    }
}
