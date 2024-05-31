package top.osjf.assembly.simplified;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.context.support.SimpleThreadScope;
import top.osjf.assembly.simplified.scope.ThreadScoped;
import top.osjf.assembly.simplified.scope.ThreadScopedExecutor;
import top.osjf.assembly.simplified.scope.ThreadScopedExecutorAgent;

import java.util.List;
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
public class SimplifiedAutoConfiguration {

    //———————————————————————————————————————— auto config

    private final ConfigurableListableBeanFactory beanFactory;

    private final TaskExecutionProperties properties;

    public SimplifiedAutoConfiguration(ConfigurableListableBeanFactory beanFactory,
                                       TaskExecutionProperties properties) {
        this.beanFactory = beanFactory;
        this.properties = properties;
    }

    @Bean
    public ThreadScopedExecutor threadScopedExecutor() {
        TaskExecutionProperties.Pool pool = properties.getPool();
        return new ThreadScopedExecutorAgent(
                pool.getCoreSize(),
                pool.getCoreSize() * 2,
                pool.getKeepAlive().getSeconds(),
                TimeUnit.SECONDS,
                pool.getCoreSize() * 100);
    }

    /**
     * To support annotation {@link top.osjf.assembly.simplified.scope.ThreadScope},
     * register scope {@link SimpleThreadScope} as support for {@code thread}.
     *
     * @return ThreadScopedExecutor
     */
    @Bean
    @ConditionalOnMissingBean
    public ThreadScoped threadScopedExecutor(ObjectProvider<List<ThreadScopedExecutor>> executor) {
        List<ThreadScopedExecutor> executors = executor.getIfAvailable();
        ThreadScoped threadScoped = new ThreadScoped(executors);
        beanFactory.registerScope("thread", threadScoped);
        return threadScoped;
    }
}
