package top.osjf.assembly.simplified.init;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.Map;

/**
 * Related tool classes for {@link Init}.
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class InitUtils implements ApplicationContextAware {

    /*** Configurable spring context..*/
    private ConfigurableApplicationContext applicationContext;

    /*** Configurable bean factory.*/
    private ConfigurableListableBeanFactory beanFactory;

    /*** The scope collection of beans that are not allowed to initiate initialization.*/
    private final String[] NOT_REQUIRED_SCOPE_NAMES = {
            WebApplicationContext.SCOPE_REQUEST,
            WebApplicationContext.SCOPE_APPLICATION,
            WebApplicationContext.SCOPE_SESSION
    };

    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
            this.beanFactory = this.applicationContext.getBeanFactory();
        }
    }

    /**
     * Initialize the execution {@link Init} and exclude the
     * scope within {@link #NOT_REQUIRED_SCOPE_NAMES}.
     *
     * <p>The scope of the filter bean is
     * {@link org.springframework.web.context.WebApplicationContext#SCOPE_APPLICATION}<br>
     * {@link org.springframework.web.context.WebApplicationContext#SCOPE_SESSION}<br>
     * {@link org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST}<br>,
     * and it is not initialized.
     *
     * @param initMap Key is the name of the bean, and
     *                value is the map type of the bean.
     */
    public void initWithoutWSAScopeBeans(Map<String, Init> initMap) {
        if (CollectionUtils.isEmpty(initMap)) {
            return;
        }
        for (String beanName : initMap.keySet()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
            //Does not include non executable scopes.
            if (!ArrayUtils.contains(NOT_REQUIRED_SCOPE_NAMES, beanDefinition.getScope())) {
                initMap.get(beanName).init();
            }
        }
    }
}
