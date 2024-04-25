package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * To implement {@link ServiceContextAware}'s bean setting {@link ServiceContext},
 * the loading time should be after {@link ServiceContext} is loaded.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.1
 */
public class ServiceContextAwareBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware {

    /*** The service context that has already been loaded.*/
    private ServiceContext serviceContext;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        serviceContext = applicationContext.getBean(ServiceContext.class);
    }

    @CanNull
    @Override
    public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) throws BeansException {
        if (bean instanceof Aware) {
            if (bean instanceof ServiceContextAware) {
                ((ServiceContextAware) bean).setServiceContext(serviceContext);
            }
        }
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
