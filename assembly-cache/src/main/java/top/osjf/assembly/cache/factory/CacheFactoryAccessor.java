package top.osjf.assembly.cache.factory;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * The accessor of the cache factory performs not null validation of the cache factory value during
 * spring bean initialization to ensure smooth loading of the cache factory.
 *
 * @author zpf
 * @since 1.0.0
 */
public class CacheFactoryAccessor implements InitializingBean {

    private CacheExecutorFactory factory;

    @Override
    public void afterPropertiesSet() {
        Assert.isTrue(getCacheExecutorFactory() != null, "CacheExecutorFactory must not" +
                "be null");
    }

    public CacheExecutorFactory getCacheExecutorFactory() {
        return this.factory;
    }

    public void setCacheExecutorFactory(@NotNull CacheExecutorFactory factory) {
        this.factory = factory;
    }
}
