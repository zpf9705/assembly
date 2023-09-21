package top.osjf.assembly.cache.autoconfigure;

/**
 * Callback interface that can be customized a {@link AssemblyCacheProperties} object generated on auto-configuration.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface ConfigurationCustomizer {

    /**
     * Customize the given a {@link AssemblyCacheProperties} object.
     *
     * @param properties the properties object to customize.
     */
    void customize(AssemblyCacheProperties properties);
}
