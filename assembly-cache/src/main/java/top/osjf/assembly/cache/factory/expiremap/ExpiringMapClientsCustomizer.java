package top.osjf.assembly.cache.factory.expiremap;


/**
 * Callback interface that can be customized a {@link ExpiringMapClients.ExpiringMapClientsBuilder} object generated.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface ExpiringMapClientsCustomizer {

    /**
     * Customize the given a {@link ExpiringMapClients.ExpiringMapClientsBuilder} object.
     *
     * @param builder the properties object to customize.
     */
    void customize(ExpiringMapClients.ExpiringMapClientsBuilder builder);
}
