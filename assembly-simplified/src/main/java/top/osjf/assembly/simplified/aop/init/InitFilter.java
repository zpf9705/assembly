package top.osjf.assembly.simplified.aop.init;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * Execute the filter condition interface for initializing beans.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
@FunctionalInterface
@Deprecated
public interface InitFilter {

    /**
     * Return the test filtering results after executing the parameter logic.
     *
     * @param beanName           The name of this bean.
     * @param bean               This bean.
     * @param applicationContext The configurable context object of the Spring container.
     * @return If {@code true} is returned, it proves that it can be initialized for execution,
     *          otherwise it cannot.
     */
    boolean test(String beanName, Object bean, ConfigurableApplicationContext applicationContext);
}
