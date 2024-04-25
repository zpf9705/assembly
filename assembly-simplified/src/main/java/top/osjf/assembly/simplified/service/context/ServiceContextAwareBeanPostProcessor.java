package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import top.osjf.assembly.util.annotation.CanNull;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.Objects;

/**
 * To implement {@link ServiceContextAware}'s bean setting {@link ServiceContext},
 * the loading time should be after {@link ServiceContext} is loaded.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.1
 */
public class ServiceContextAwareBeanPostProcessor implements BeanPostProcessor, Ordered {

    /*** The service context that has already been loaded.*/
    private final ServiceContext serviceContext;

    /**
     * The construction method of carrying service context.
     * @param serviceContext carrying service context
     * @since 2.2.2
     */
    public ServiceContextAwareBeanPostProcessor(ServiceContext serviceContext) {
        Objects.requireNonNull(serviceContext, "ServiceContext must not be null");
        this.serviceContext = serviceContext;
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

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 18;
    }
}
