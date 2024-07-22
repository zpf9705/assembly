package top.osjf.spring.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.Aware;
import org.springframework.lang.NonNull;

/**
 * Interface to be implemented by any bean that wishes to be
 * notified of the {@link ServiceContext} that it runs in.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public interface ServiceContextAware extends Aware {

    /**
     * Set the ServiceContext that this object runs in.
     * <p>Invoked after population of normal bean properties but before an init
     * callback like InitializingBean's afterPropertiesSet or a custom init-method.
     *
     * @param serviceContext service context to be used by this object
     * @throws BeansException if thrown by service context methods.
     */
    void setServiceContext(@NonNull ServiceContext serviceContext) throws BeansException;
}
