package top.osjf.assembly.simplified.init;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;

/**
 * Add initialization conditions to filter the initialization
 * of spring aop proxy objects.
 *
 * <p>The scope of the filter bean is
 * {@link org.springframework.web.context.WebApplicationContext#SCOPE_APPLICATION}<br>
 * {@link org.springframework.web.context.WebApplicationContext#SCOPE_SESSION}<br>
 * {@link org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST}<br>,
 * and it is not initialized.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see AopUtils
 * @see org.springframework.aop.framework.AopProxyUtils
 * @since 2.2.5
 */
public abstract class AbstractWithOutWSAScopeInit extends AbstractInit implements ApplicationContextAware,
        BeanNameAware {

    /*** Configurable spring context..*/
    private ConfigurableApplicationContext applicationContext;

    /*** The name of the initialization bean for this time.*/
    private String beanName;

    /*** The scope collection of beans that are not allowed to initiate initialization.*/
    private final String[] NOT_REQUIRED_SCOPE_NAMES = {
            WebApplicationContext.SCOPE_REQUEST,
            WebApplicationContext.SCOPE_APPLICATION,
            WebApplicationContext.SCOPE_SESSION
    };

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        }
    }

    @Override
    public void setBeanName(@NotNull String name) {
        this.beanName = name;
    }

    @Override
    protected boolean conditionalJudgment() {
        if (applicationContext == null) {
            return false;
        }
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
        return !ArrayUtils.contains(NOT_REQUIRED_SCOPE_NAMES, beanDefinition.getScope());
    }
}
