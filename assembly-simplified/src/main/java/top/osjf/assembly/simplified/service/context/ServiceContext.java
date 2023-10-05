package top.osjf.assembly.simplified.service.context;

import java.io.Closeable;

/**
 * The central interface for obtaining service context configuration.
 *
 * <p>The underlying implementation utilizes the bean query function method of spring
 * context central interface {@link org.springframework.context.ApplicationContext}.
 *
 * @author zpf
 * @since 2.0.4
 */
public interface ServiceContext extends Closeable {

    /**
     * Returns a service with a specified name, which can be shared or independent.
     * <p>By using a service name to query the corresponding service entity,
     * a container service hosted on Spring can be implemented, which is encapsulated
     * here for more convenient use.
     * <p>According to the specifications of Spring's beans, multiple alias queries are supported.
     *
     * @param serviceName the name of the service to retrieve.
     * @return an instance of the bean.
     * @throws NoSuchServiceException if there is no such service.
     */
    Object getService(String serviceName) throws NoSuchServiceException;

    /**
     * Returns a service with a specified name, which can be shared or independent.
     * <p>The operation is equivalent to {@link #getService(String)}, and on this basis,
     * the class needs to be transformed to convert the queried service entity into the
     * desired type for better operation and applicability to different scenarios.
     * <p>According to the specifications of Spring's beans, multiple alias queries are supported.
     *
     * @param serviceName  the name of the service to retrieve.
     * @param requiredType type the bean must match; can be an interface or superclass.
     * @param <S>          types of required.
     * @return an instance of the service.
     * @throws NoSuchServiceException if there is no such service.
     */
    <S> S getService(String serviceName, Class<S> requiredType) throws NoSuchServiceException;


    /**
     * Reloading the service context is generally used to respond to dynamic changes in the scanning path.
     *
     * @param packages Scan path array.
     */
    void reloadWithScanPackages(String... packages);

    @Override
    void close();
}
