package top.osjf.assembly.simplified.service.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * The help class for the service context.
 *
 * <p>Define common methods and variables here as service context
 * intermediate classes.
 * <p>Provide a default method to obtain beans from the spring context,
 * usually used for alias queries {@link #getService(String, Class)}.
 *
 * @author zpf
 * @since 2.0.6
 */
public abstract class AbstractServiceContext implements ServiceContext, ApplicationContextAware {

    private ApplicationContext context;

    @Override
    public void setApplicationContext(@NotNull ApplicationContext context) throws BeansException {
        this.context = context;
    }

    @Override
    public <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException {
        S service;
        try {
            //If the collected beans are not found,
            //Using spring's context lookup may be based on aliases.
            //However, when using aliases, please be aware of potential bean name duplication errors.
            service = context.getBean(serviceName, requiredType);
        } catch (BeansException e) {
            //Throw an exception that cannot be found.
            throw new NoSuchServiceException("No service named " + serviceName + " was found " +
                    "from the service context");
        }
        return service;
    }

    @Override
    public void reloadWithScanPackages(String... packages) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return context;
    }

    @Override
    public void close() {
    }
}
