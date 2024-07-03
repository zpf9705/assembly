package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import top.osjf.assembly.simplified.service.ServiceContextUtils;
import top.osjf.assembly.util.annotation.NotNull;
import top.osjf.assembly.util.lang.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Using the service registration record in {@link ServiceContextBeanNameGenerator#getRecordBeanNames()},
 * when the current class is loaded, retrieve the service registration record,
 * retrieve the corresponding bean object and alias information from the Spring
 * container, and register it in the container collected by the service.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @see top.osjf.assembly.simplified.service.annotation.EnableServiceCollection3
 * @see top.osjf.assembly.simplified.service.context.AbstractServiceContext.ServiceContextBeanNameGenerator
 * @since 2.2.0
 */
public class RecordServiceContext extends AbstractServiceContext implements InitializingBean {

    /**
     * Bottom line handling of beans that have not been changed scope.
     */
    @Override
    public void afterPropertiesSet() {
        for (String recordBeanName : getRecordBeanNames()) {
            Object bean = getApplicationContext().getBean(recordBeanName);
            getContextMap().putIfAbsent(recordBeanName, bean);
            for (String aliasName : getBeanDefinitionRegistry().getAliases(recordBeanName)) {
                getContextMap().putIfAbsent(aliasName, bean);
            }
        }
    }

    /**
     * Rewrite the service retrieval method to point to the beans saved in the specified
     * scope, whether to use the method of the parent class to retrieve beans that have
     * not changed the scope.
     * @see ServiceScope
     */
    @Override
    public <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException {
        ApplicationContext applicationContext = getApplicationContext();
        for (String name :
                ServiceContextUtils.getCandidateServiceNames(serviceName, requiredType, applicationContext.getId())) {
            try {
                return applicationContext.getBean(name, requiredType);
            } catch (BeansException e) {
                if (!(e instanceof NoSuchBeanDefinitionException)) throw new RuntimeException(e);
            } catch (Throwable e) {
                throw new IllegalStateException("Service acquisition failed", e);
            }
        }
        return super.getService(serviceName, requiredType);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        super.onApplicationEvent(event);
        clearCache();
        clearRecordBeanNames();
    }

    /**
     * Support modification of scope {@link ServiceContextUtils#SERVICE_SCOPE}.
     * @since 2.2.5
     */
    public static class ChangeScopePostProcessor implements BeanDefinitionRegistryPostProcessor {
        @Override
        public void postProcessBeanDefinitionRegistry(@NotNull BeanDefinitionRegistry registry) throws BeansException {
            List<String> recordBeanNames = ServiceContextBeanNameGenerator.getRecordBeanNames();
            List<String> updateRecordBeanNames = new ArrayList<>();
            for (String recordBeanName : recordBeanNames) {
                BeanDefinition beanDefinition;
                try {
                    beanDefinition = registry.getBeanDefinition(recordBeanName);
                } catch (NoSuchBeanDefinitionException e) {
                    continue;
                }
                beanDefinition.setScope(ServiceContextUtils.SERVICE_SCOPE);
                updateRecordBeanNames.add(recordBeanName);
            }
            if (CollectionUtils.isNotEmpty(updateRecordBeanNames)) {
                recordBeanNames.removeAll(updateRecordBeanNames);
            }
        }

        @Override
        public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory)
                throws BeansException {
        }
    }
}
