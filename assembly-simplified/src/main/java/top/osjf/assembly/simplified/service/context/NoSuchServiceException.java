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

    public NoSuchServiceException(String serviceName, Class<?> clazz) {
        super("Required type " + clazz.getName() + " no service named " + serviceName +
                " was found from the service context");
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }
}
