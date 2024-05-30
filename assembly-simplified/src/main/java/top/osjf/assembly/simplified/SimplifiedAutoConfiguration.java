package top.osjf.assembly.simplified;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import top.osjf.assembly.simplified.scope.ThreadScoped;
import top.osjf.assembly.simplified.scope.ThreadScopedExecutor;
import top.osjf.assembly.util.annotation.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for assembly-simplified.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 2.2.1
 */
@Configuration(proxyBeanMethods = false)
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
@EnableConfigurationProperties(TaskExecutionProperties.class)
public class SimplifiedAutoConfiguration implements BeanFactoryPostProcessor {

    //———————————————————————————————————————— auto config

    private ConfigurableListableBeanFactory beanFactory;

    private final TaskExecutionProperties properties;

    public SimplifiedAutoConfiguration(TaskExecutionProperties properties) {
        this.properties = properties;
    }

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory beanFactory)
            throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public ThreadScopedExecutor threadScopedExecutor() {
        TaskExecutionProperties.Pool pool = properties.getPool();
        ThreadScopedExecutor executor = new ThreadScopedExecutor(
                pool.getCoreSize(),
                pool.getMaxSize(),
                pool.getKeepAlive().getSeconds(),
                TimeUnit.SECONDS,
                pool.getQueueCapacity()
        );
        beanFactory.registerScope("thread", new ThreadScoped(executor));
        return executor;
    }
}
