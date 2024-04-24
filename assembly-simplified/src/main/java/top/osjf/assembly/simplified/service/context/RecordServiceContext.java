package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.event.ContextRefreshedEvent;
import top.osjf.assembly.util.lang.CollectionUtils;

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

    @Override
    public void afterPropertiesSet() {
        List<String> recordBeanNames = getRecordBeanNames();
        if (CollectionUtils.isNotEmpty(recordBeanNames)) {
            for (String recordBeanName : recordBeanNames) {
                Object bean = getApplicationContext().getBean(recordBeanName);
                getContextMap().putIfAbsent(recordBeanName, bean);
                for (String aliasName : getBeanDefinitionRegistry().getAliases(recordBeanName)) {
                    getContextMap().putIfAbsent(aliasName, bean);
                }
            }
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        super.onApplicationEvent(event);
        clearCache();
        clearRecordBeanNames();
    }
}
