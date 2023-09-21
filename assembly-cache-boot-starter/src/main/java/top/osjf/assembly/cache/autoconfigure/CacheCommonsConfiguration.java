package top.osjf.assembly.cache.autoconfigure;

/**
 * Regarding the public configuration of {@code assembly cache}.
 *
 * @author zpf
 * @since 3.0.0
 */
public class CacheCommonsConfiguration {

    public final AssemblyCacheProperties properties;

    public CacheCommonsConfiguration(AssemblyCacheProperties properties) {
        this.properties = properties;
    }

    public final AssemblyCacheProperties getProperties() {
        return this.properties;
    }
}
