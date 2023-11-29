package top.osjf.assembly.cache.autoconfigure;

/**
 * Regarding the public configuration of {@code assembly cache}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class CacheCommonsConfiguration {

    private final CacheProperties properties;

    public CacheCommonsConfiguration(CacheProperties properties) {
        this.properties = properties;
    }

    public final CacheProperties getProperties() {
        return this.properties;
    }
}
