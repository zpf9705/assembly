package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;

/**
 * An exception thrown when the service is not queried.
 *
 * @author zpf
 * @since 2.0.4
 */
public class NoSuchServiceException extends BeansException {

    private static final long serialVersionUID = 922435911357767431L;

    private final String serviceName;

    private final Class<?> requiredType;

    public NoSuchServiceException(String serviceName, Class<?> requiredType) {
        super("Required type " + requiredType.getName() + " no service named " + serviceName +
                " was found from the service context");
        this.serviceName = serviceName;
        this.requiredType = requiredType;
    }

    /**
     * Return the service name of the parameter that did not find the service bean.
     * @return the service name of the parameter that did not find the service bean.
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Return the conversion type of parameters for service beans that were not found.
     * @return the conversion type of parameters for service beans that were not found.
     */
    public Class<?> getRequiredType() {
        return requiredType;
    }
}
