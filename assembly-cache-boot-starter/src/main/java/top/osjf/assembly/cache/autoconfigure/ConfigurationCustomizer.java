package top.osjf.assembly.cache.autoconfigure;

/**
 * Callback interface that can be customized a {@link CacheProperties} object generated on auto-configuration.
 *
 * @author zpf
 * @since 1.0.0
 */
public interface ConfigurationCustomizer {

    /**
     * Customize the given a {@link CacheProperties} object.
     *
     * @param properties the properties object to customize.
     */
    void customize(CacheProperties properties);
}
