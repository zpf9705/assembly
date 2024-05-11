package top.osjf.assembly.simplified.init;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.web.context.WebApplicationContext;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.ArrayUtils;
import top.osjf.assembly.util.lang.StringUtils;

import java.util.Map;
import java.util.Objects;

/**
 * Related tool classes for {@link Init}.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.5
 */
public class InitUtils implements ApplicationContextAware {

    /*** Configurable spring context..*/
    private ConfigurableApplicationContext applicationContext;

    /*** Configurable bean factory.*/
    private ConfigurableListableBeanFactory beanFactory;

    /*** The scope collection of beans that are not allowed to initiate initialization.*/
    private static final String[] NOT_REQUIRED_SCOPE_NAMES = {
            WebApplicationContext.SCOPE_REQUEST,
            WebApplicationContext.SCOPE_APPLICATION,
            WebApplicationContext.SCOPE_SESSION
    };

    /*** Beans with default filtering scope in {@link #NOT_REQUIRED_SCOPE_NAMES} are not initialized.*/
    private final InitFilter withoutWSAScope = new WithoutWSAScopeInitFilter();

    public ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ConfigurableListableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public InitFilter getWithoutWSAScope() {
        return withoutWSAScope;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
            this.beanFactory = this.applicationContext.getBeanFactory();
        }
    }

    /**
     * Get a collection of type beans of a certain type, including
     * non singleton beans, and do not initialize beans that handle
     * proxy mode special scope names.
     *
     * @param beanType the class or interface to match, or {@code null} for all concrete beans
     * @param <T>      The type of {@link Init} or his word accumulation.
     * @return a Map with the matching beans, containing the bean names as
     * keys and the corresponding bean instances as values
     */
    public <T extends Init> Map<String, T> getBeansOfTypeNonAllowEagerInit(Class<T> beanType) {
        return applicationContext.getBeansOfType(beanType, true, false);
    }

    /**
     * Execute the relevant bean implementation for initializing {@link Init}.
     *
     * <p>Find the population of beans based on the provided type.
     *
     * @param <T>      The type of {@link Init} or his word accumulation.
     * @param beanType Initialize the class object of {@link Init} or subclasses.
     * @param filter   Initialize the collection to perform filtering conditions.
     */
    public <T extends Init> void initBeans(Class<T> beanType, @Nullable InitFilter filter) {
        Objects.requireNonNull(beanType, "beanType not be null");
        initBeans(getBeansOfTypeNonAllowEagerInit(beanType), filter);
    }

    /**
     * Execute the relevant bean implementation for initializing {@link Init}.
     *
     * @param <T>          The type of {@link Init} or his word accumulation.
     * @param initBeansMap Key is the name of the bean, and
     *                     value is the map type of the bean.
     * @param filter       Initialize the collection to perform filtering conditions.
     */
    public <T extends Init> void initBeans(Map<String, T> initBeansMap, @Nullable InitFilter filter) {
        Objects.requireNonNull(initBeansMap, "initBeansMap not be null");
        initBeansMap.forEach((beanName, bean) -> {
            if (filter != null) {
                if (filter.test(beanName, bean, getApplicationContext())) {
                    bean.init();
                }
            } else bean.init();
        });
    }

    /**
     * Initialize the execution {@link Init} and exclude the
     * scope within {@link #NOT_REQUIRED_SCOPE_NAMES}.
     *
     * <p>Find the population of beans based on the provided type.
     *
     * <p>The scope of the filter bean is
     * {@link org.springframework.web.context.WebApplicationContext#SCOPE_APPLICATION}<br>
     * {@link org.springframework.web.context.WebApplicationContext#SCOPE_SESSION}<br>
     * {@link org.springframework.web.context.WebApplicationContext#SCOPE_REQUEST}<br>,
     * and it is not initialized.
     *
     * @param <T>      The type of {@link Init} or his word accumulation.
     * @param beanType Initialize the class object of {@link Init} or subclasses.
     */
    public <T extends Init> void initWithoutWSAScopeBeans(Class<T> beanType) {
        Objects.requireNonNull(beanType, "beanType not be null");
        initWithoutWSAScopeBeans(getBeansOfTypeNonAllowEagerInit(beanType));
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
     * @param <T>          The type of {@link Init} or his word accumulation.
     * @param initBeansMap Key is the name of the bean, and
     *                     value is the map type of the bean.
     */
    public <T extends Init> void initWithoutWSAScopeBeans(Map<String, T> initBeansMap) {
        Objects.requireNonNull(initBeansMap, "initBeansMap not be null");
        initBeans(initBeansMap, withoutWSAScope);
    }

    /**
     * The example implementation of {@link Init} does not perform
     * initialization on beans with filtering scope in {@link #NOT_REQUIRED_SCOPE_NAMES}.
     */
    public static class WithoutWSAScopeInitFilter implements InitFilter {

        @Override
        public boolean test(String beanName, Object bean, ConfigurableApplicationContext applicationContext) {
            BeanDefinition beanDefinition = applicationContext.getBeanFactory().getBeanDefinition(beanName);
            //Does not include non executable scopes.
            String scope = beanDefinition.getScope();
            if (StringUtils.isBlank(scope)) {
                BeanDefinition originatingBeanDefinition = beanDefinition.getOriginatingBeanDefinition();
                if (originatingBeanDefinition != null) {
                    scope = originatingBeanDefinition.getScope();
                }
            }
            if (StringUtils.isBlank(scope)) return true;
            return !ArrayUtils.contains(NOT_REQUIRED_SCOPE_NAMES, scope);
        }
    }
}
