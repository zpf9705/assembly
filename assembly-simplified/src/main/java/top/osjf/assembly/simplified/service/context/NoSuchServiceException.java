package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * An exception thrown when the service is not queried.
 *
 * @author zpf
 * @since 2.0.4
 */
public class NoSuchServiceException extends NoSuchBeanDefinitionException {

    private static final long serialVersionUID = 922435911357767431L;

    public NoSuchServiceException(String name) {
        super(name);
    }
}
