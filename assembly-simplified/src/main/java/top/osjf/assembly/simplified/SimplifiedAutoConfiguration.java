package top.osjf.assembly.simplified;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

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
}
