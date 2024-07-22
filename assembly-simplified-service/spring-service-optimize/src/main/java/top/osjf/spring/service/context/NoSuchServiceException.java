package top.osjf.spring.service.context;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * An exception thrown when the service is not queried.
 *
 * @author <a href="mailto:929160069@qq.com">zhangpengfei</a>
 * @since 1.0.0
 */
public class NoSuchServiceException extends NoSuchBeanDefinitionException {

    private static final long serialVersionUID = 922435911357767431L;
    public NoSuchServiceException(String serviceName, Class<?> requiredType) {
        super("Required type " + requiredType.getName() + " no service named " + serviceName +
                " was found from the service context");
    }
}
