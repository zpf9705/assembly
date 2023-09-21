package top.osjf.assembly.cache.factory.expiremap;

import top.osjf.assembly.cache.center.ExpireMapCenter;
import top.osjf.assembly.cache.center.ExpiringMapCacheExecutor;
import top.osjf.assembly.cache.config.expiringmap.ExpiringMapClients;
import top.osjf.assembly.cache.factory.CacheExecutor;
import top.osjf.assembly.cache.factory.CacheExecutorFactory;
import top.osjf.assembly.cache.util.JdkProxyUtils;
import top.osjf.assembly.util.annotation.NotNull;

/**
 * The creation class of the cache execution factory for {@link net.jodah.expiringmap.ExpiringMap}.
 *
 * @author zpf
 * @since 1.0.0
 */
public class ExpiringMapCacheExecutorFactory implements CacheExecutorFactory {

    private final ExpiringMapCacheExecutor executor;

    public ExpiringMapCacheExecutorFactory(@NotNull ExpiringMapClients clients) {
        this.executor = doCreateExpiringMapExecutor(clients);
    }

    @Override
    @NotNull
    public CacheExecutor executor() {
        return this.executor;
    }

    public ExpiringMapCacheExecutor doCreateExpiringMapExecutor(ExpiringMapClients clients) {
        //Real object generated singleton operation
        ExpireMapCenter expireMapCenter = ExpireMapCenter.singletonWithConfiguration(clients);
        //To approach the processor
        return JdkProxyUtils.createProxy(
                new ExpiringMapPersistenceProcessor(new ExpiringMapCacheExecutorImpl(() -> expireMapCenter))
        );
    }
}
