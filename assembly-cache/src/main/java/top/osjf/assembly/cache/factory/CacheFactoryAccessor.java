package top.osjf.assembly.cache.factory;

/**
 * Factory class accessor for {@link CacheFactory}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class CacheFactoryAccessor {

    private CacheFactory factory;

    public CacheFactoryAccessor(CacheFactory factory) {
        this.factory = factory;
    }

    public CacheFactory getCacheFactory() {
        return this.factory;
    }

    public void setCacheFactory(CacheFactory factory) {
        if (factory == null)
            throw new IllegalArgumentException("The settings for the cache factory cannot be empty.");
        this.factory = factory;
    }
}
